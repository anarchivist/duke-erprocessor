/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.archives.erprocessor.metadata.mets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 *
 * @author Seth Shaw
 */
public class METS {

    public static final String ACCESSIONS_STRUCTMAP = "Accessions";
    public static final String COLLECTION_STRUCTMAP = "Collection";
    
    Document metsDoc;
    protected List<Namespace> namespaces = new ArrayList<Namespace>();
    protected Namespace metsNamespace = Namespace.getNamespace("mets",
            "http://www.loc.gov/METS/");
    protected Namespace xlinkNamespace = Namespace.getNamespace("xlink",
            "http://www.w3.org/1999/xlink");
    protected Namespace premisNamespace = Namespace.getNamespace("p1",
            "http://www.loc.gov/standards/premis/v1/");
    private HashMap<String, Long> ids = new HashMap<String, Long>(); //An ID cache for creating new IDs so we don't have to keep running an XPath over the whole document to find an ID of a certain type.
    private MetsNode accessionMap = null;
    private MetsNode collectionMap = null;

    public METS() {
        metsDoc = new Document(new Element("mets", metsNamespace));
        loadKnownNamespaces();
    }

    public METS(Document mets) {
        metsDoc = mets;
        loadKnownNamespaces();
    }

    public Document getDoc() {
        return metsDoc;
    }

    /**
     * Converts a given accession metadata file into the METS form we need
     * it in for use.
     * 
     * @param accessionFile is the file to be loaded
     */
    public void loadAccession(File accessionFile) {
        try {

            //FIXME: This doesn't work.
            Document accessionDoc =
                    new SAXBuilder().build(accessionFile);

            /** Add accession to METS doc **/
            Element sourceAccession =
                    accessionDoc.getRootElement().getChild("accession");
            //Append new METS accession
            Element metsAccessionElement = new Element("div",
                    metsNamespace);
            metsAccessionElement.setAttribute("LABEL",
                    sourceAccession.getAttributeValue("number"));
//            getStructMap("Accessions").addChild(metsAccessionElement);
            //Load children
            loadAccessionChildren(sourceAccession, metsAccessionElement,
                    sourceAccession.getAttributeValue("number"));

            /** Load new METS into Tree **/
            getStructMap(ACCESSIONS_STRUCTMAP).addChild(loadNode(metsAccessionElement));

        } catch (Exception ex) {
            Logger.getLogger(METS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String nextID(String type) {
        long highest = 0;
        if (ids.containsKey(type)) {
            highest = (Long) ids.get(type);
            highest++;
        } else {
            highest = loadNextID(type);
        }
        ids.put(type, highest);
        String value = Long.toString(highest);
        return type + ("00000000" + value).substring(value.length());
    }

    private long loadNextID(String type) {
        try {

            long highest = 0;
            Pattern pattern =
                    Pattern.compile(type,
                    Pattern.CASE_INSENSITIVE);
            for (Object id : XPath.selectNodes(metsDoc.getRootElement(),
                    "//mets:" + type + "/@ID")) {
                String value = ((Attribute) id).getValue();
                Matcher matcher = pattern.matcher(value);
                try {
                    long number =
                            Long.parseLong(matcher.replaceFirst(""));
                    highest = (highest > number) ? highest : number;
                } catch (NumberFormatException nfe) {
                //Do nothing and move along.
                }
            }
            return highest + 1;
//            return type + ("00000000" + value).substring(value.length());
        } catch (JDOMException ex) {
            Logger.getLogger(METS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean addDiv(String parentID, String label) {
        try {
            Element parent = null;
            if (parentID != null) {
                parent = (Element) XPath.selectSingleNode(metsDoc.getRootElement(),
                        "/mets:mets/mets:structMap//mets:div[@ID='" + parentID +
                        "']");
            } else {
                parent = (Element) XPath.selectSingleNode(metsDoc.getRootElement(),
                        "/mets:mets/mets:structMap[@TYPE='Collection']");
            }
            if (parent == null) {
                return false;
            }
            Element newDiv = new Element("div", metsNamespace);
            newDiv.setAttribute("ID", nextID("div"));
            newDiv.setAttribute("LABEL", label);
            parent.addContent(newDiv);
        } catch (JDOMException ex) {
            Logger.getLogger(METS.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public Element getItemAMD(Element item, String amdType, String schema,
            boolean create) {
        String id = null;
        if (item.getName().equalsIgnoreCase("fptr")) {
            MetsFile file = getFileByID(item.getAttributeValue("FILEID"));
            id = file.getAMDID();
        } else {
            id = item.getAttributeValue("ADMID");
        }

        if (id == null) {
            if (item.getAttributeValue("ID") != null) {
                id = amdType + item.getAttributeValue("ID");
            } else {
                return null;
            }
        }
        return getAMD(id, amdType, schema, create);
    }

    public Element getItemDMD(Element item, String mdType, boolean create) {
        String id = null;
        id = item.getAttributeValue("DMDID");
        if (id == null) {
            String idType = "ID";
            if (item.getName().equalsIgnoreCase("fptr")) {
                idType = "FILEID";
            }
            if (item.getAttributeValue(idType) != null && create) {
                id = nextID("dmdSec");
                item.setAttribute("DMDID", id);
            }
        }
        return getDMD(id, mdType, create);
    }

    public Element getDMD(String id, String mdType) {
        return getDMD(id, mdType, false);
    }

    /*
     * I admit that the getDMD & createDMD are a bit of misnomers. Instead,
     * they really create and return xmlData elements which holds the 
     * content we actually care about. The fullXPath, on the other hand, holds 
     * the identifying information. The user doesn't need to know that 
     * there are two wrapper elements (mdWrap & xmlData) between the ID 
     * and the content.
     */
    public Element getDMD(String id, String mdType, boolean create) {
        //Does it already exisit?
        Element xmlData = getMDWrap(id, mdType, "");
        if (xmlData == null && create) {
            xmlData = new Element("xmlData", metsNamespace);
            //mdWrap & xmlData tags
            Element mdWrap = new Element("mdWrap", metsNamespace);
            mdWrap.setAttribute("MDTYPE", mdType);
            mdWrap.addContent(xmlData);

            //New dmdSec
            Element dmd = new Element("dmdSec", metsNamespace);
            dmd.setAttribute("ID", id);
            dmd.addContent(mdWrap);
            metsDoc.getRootElement().addContent(dmd);
        }
        return xmlData;
    }

    public String createDMD(String mdType) {
        Element xmlData = new Element("xmlData", metsNamespace);
        //mdWrap & xmlData tags
        Element mdWrap = new Element("mdWrap", metsNamespace);
        mdWrap.setAttribute("MDTYPE", mdType);
        mdWrap.addContent(xmlData);

        //New dmdSec
        Element dmd = new Element("dmdSec", metsNamespace);
        String id = nextID("dmdSec");
        dmd.setAttribute("ID", id);
        dmd.addContent(mdWrap);
        metsDoc.getRootElement().addContent(dmd);

        return id;
    }

    public void removeItemDMD(Element describedItem) {
        String dmdID = describedItem.getAttributeValue("DMDID");
        if ((dmdID == null) && (describedItem.getAttributeValue("ID") != null)) {
            dmdID = "DMD" + describedItem.getAttributeValue("ID");

        } else {
            return;
        }
        try {
            //How many elements reference this DMD? Could be nearly anywhere.
            List nodes = XPath.selectNodes(metsDoc.getRootElement(), "//@DMDID='" +
                    dmdID + "'");
            if (nodes.size() == 1) {
                //Remove the DMD element since no one uses it anymore.
                Element itemDMD =
                        (Element) XPath.selectSingleNode(metsDoc.getRootElement(), "/mets:mets/mets:dmdSec[@ID='" +
                        dmdID + "']");
                if (itemDMD != null) {
                    itemDMD.detach();
                }
            }
        } catch (JDOMException ex) {
            Logger.getLogger(METS.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    public MetsNode getStructMap(String type, String label) {
        MetsNode map = getStructMap(type);
        map.label = label;
        return map;
    }

    public MetsNode getStructMap(String type) {
        //Cached map?
        if (type.equalsIgnoreCase(ACCESSIONS_STRUCTMAP) && accessionMap != null) {
            return accessionMap;
        } else if (type.equalsIgnoreCase(COLLECTION_STRUCTMAP) && collectionMap != null) {
            return collectionMap;
        }
        
        //Load the map from METS?
        MetsNode map = null;
        try {
            map = loadNode((Element) XPath.selectSingleNode(metsDoc.getRootElement(),
                    "/mets:mets/mets:structMap[@TYPE='" + type + "']"));
            if (type.equals(ACCESSIONS_STRUCTMAP) && map != null) {
                accessionMap = map;
            } else if (type.equals(COLLECTION_STRUCTMAP) && map !=
                    null) {
                collectionMap = map;
            }
            
            if (map != null){
                return map;
            }
        } catch (JDOMException ex) {
            System.err.println("Failed to get StructMap type='" + type + "' : " +
                    ex.getMessage());
        }

        //No preexisting structmap, lets make one
        Element element = new Element("structMap", metsDoc.getRootElement().
                getNamespace());
        element.setAttribute("TYPE", type);
        map = new MetsDiv(this, element);

        this.metsDoc.getRootElement().addContent(element);

        if (type.equals(ACCESSIONS_STRUCTMAP)) {
            accessionMap = map;
        } else if (type.equals(COLLECTION_STRUCTMAP)) {
            collectionMap = map;
        }

        return map;
    }

    public synchronized MetsFile getFileByID(String id) {
        return new MetsFile(this, id);
    }

    public Namespace[] getNamespaces() {
        return namespaces.toArray(new Namespace[]{});
    }

    public Element getAMD(String id, String amdType, String schema,
            boolean create) {
        //Does it already exisit?
        Element xmlData = getMDWrap(id, schema, "");
        if (xmlData == null && create) {
            try {
                xmlData =
                        new Element("xmlData", metsNamespace);

                Element mdWrap =
                        new Element("mdWrap", metsNamespace);
                mdWrap.setAttribute("MDTYPE", schema);
                mdWrap.addContent(xmlData);


                Element dmd =
                        new Element(amdType, metsNamespace);
                dmd.setAttribute("ID", id);
                dmd.addContent(mdWrap);
                Element amdSec =
                        (Element) XPath.selectSingleNode(metsDoc.getRootElement(),
                        "/mets:mets/mets:amdSec");
                if (amdSec == null) {
                    amdSec = new Element("amdSec", metsNamespace);
                    this.metsDoc.getRootElement().addContent(amdSec);
                }
                amdSec.addContent(dmd);
            } catch (JDOMException ex) {
                Logger.getLogger(METS.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }
        return xmlData;
    }

    private synchronized void loadAccessionChildren(Element sourceParent,
            Element metsParent, String path) {
        try {
            HashMap<String, String> schema =
                    new HashMap<String, String>();
            schema.put("jhove", "JHOVE");
            schema.put("FileCollection", "DROID");
            schema.put("object", "PREMIS");
            Element fileSet =
                    (Element) XPath.selectSingleNode(metsDoc.getRootElement(),
                    "/mets:mets/mets:fileSec/mets:fileGrp[@USE='SOURCE']");
            for (Object sourceChild : sourceParent.getChildren()) {
                if (sourceChild instanceof Element) {

                    if (((Element) sourceChild).getName().
                            equalsIgnoreCase("folder")) {
                        Element div =
                                new Element("div", metsNamespace);
                        div.setAttribute("ID", nextID("div"));
                        div.setAttribute("LABEL",
                                ((Element) sourceChild).getAttributeValue("name"));
                        metsParent.addContent(div);
                        loadAccessionChildren((Element) sourceChild, div,
                                path + "/" +
                                ((Element) sourceChild).getAttributeValue("name"));
                    } else if (((Element) sourceChild).getName().
                            equalsIgnoreCase("file")) {
                        //METS File Element
                        Element metsFile = new Element("file", metsNamespace);
                        String id = nextID("file");
                        metsFile.setAttribute("ID", id);
                        if (((Element) sourceChild).getAttributeValue("size") !=
                                null) {
                            metsFile.setAttribute("SIZE",
                                    ((Element) sourceChild).getAttributeValue("size"));
                        }
                        if (((Element) sourceChild).getAttributeValue("last_modified") !=
                                null) {
                            metsFile.setAttribute("CREATED",
                                    ((Element) sourceChild).getAttributeValue("last_modified"));
                        }
                        if (((Element) sourceChild).getAttributeValue("MD5") !=
                                null) {
                            metsFile.setAttribute("CHECKSUM",
                                    ((Element) sourceChild).getAttributeValue("MD5"));
                            metsFile.setAttribute("CHECKSUMTYPE", "MD5");
                        }

                        if (fileSet == null) {
                            fileSet = makeFileSec();

                        }
                        fileSet.addContent(metsFile);

                        //File location
                        Element flocat =
                                new Element("FLocat", metsNamespace);
                        flocat.setAttribute("LOCTYPE", "OTHER");
                        flocat.setAttribute("OTHERLOCTYPE", "TRANSFORM-URI");
                        flocat.setAttribute(new Attribute("href",
                                "file://" + path + "/" +
                                ((Element) sourceChild).getAttributeValue("name"),
                                xlinkNamespace));
                        metsFile.addContent(flocat);
                        //AMD - PREMIS
                        String amdID = nextID("techMD");
                        metsFile.setAttribute("ADMID", amdID);
                        Element amd =
                                getAMD(amdID, "techMD", "PREMIS", true);
                        Element object =
                                amd.getChild("object", premisNamespace);
                        if (object == null) {
                            object = new Element("object", premisNamespace);
                            amd.addContent(object);
                        }
                        Element name =
                                object.getChild("originalName", premisNamespace);
                        if (name == null) {
                            name = new Element("originalName", premisNamespace);
                            name.setText(((Element) sourceChild).getAttributeValue("name"));
                            object.addContent(name);
                        }
                        //File pointer
                        Element fptr =
                                new Element("fptr", metsNamespace);
                        fptr.setAttribute("FILEID", id);
                        metsParent.addContent(fptr);
                        loadAccessionChildren((Element) sourceChild, metsFile,
                                path + "/" +
                                ((Element) sourceChild).getAttributeValue("name"));
                    } else {
                        String childName =
                                ((Element) sourceChild).getName();
                        //SOURCE Jhove & Droid
                        if (metsParent.getName().equalsIgnoreCase("file") &&
                                (childName.equalsIgnoreCase("jhove") ||
                                childName.equalsIgnoreCase("FileCollection"))) {
//                        getItemAMD(parent, "techMD", schema.get(childName), true).addContent((Element) ((Element) sourceChild).clone());
                        } //SOURCE PREMIS
                        else if (childName.equalsIgnoreCase("object")) {
                            getItemAMD(metsParent, "techMD",
                                    schema.get(childName),
                                    true).
                                    addContent((Element) ((Element) sourceChild).clone());
                        } else {
                            getItemDMD(metsParent, "OTHER", true).
                                    addContent((Element) ((Element) sourceChild).clone());
                        }
                    }
                }
            }
        } catch (JDOMException ex) {
            Logger.getLogger(METS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadKnownNamespaces() {
        namespaces.add(metsNamespace);
        namespaces.add(xlinkNamespace);
        namespaces.add(premisNamespace);
    }

    public synchronized Element getMDWrap(String id, String mdType,
            String xPath) {
        try {
            //Try DMD first
            String fullXPath = "/mets:mets/mets:dmdSec[@ID='" + id +
                    "']/mets:mdWrap[@MDTYPE='" + mdType +
                    "']/mets:xmlData" + xPath;
            XPath xP = XPath.newInstance(fullXPath);
            for (Namespace ns : namespaces) {
                xP.addNamespace(ns);
            }
            Element returned =
                    (Element) xP.selectSingleNode(metsDoc.getRootElement());
            if (returned == null) { //No DMD with that ID found, try AMD
                fullXPath = "/mets:mets/mets:amdSec/mets:*[@ID='" + id +
                        "']/mets:mdWrap[@MDTYPE='" + mdType +
                        "']/mets:xmlData" + xPath;
                xP = XPath.newInstance(fullXPath);
                for (Namespace ns : namespaces) {
                    xP.addNamespace(ns);
                }
                returned =
                        (Element) xP.selectSingleNode(metsDoc.getRootElement());
            }

            return returned;

        } catch (JDOMException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    private Element makeFileSec() {
        Element fileSec = new Element("fileSec", metsNamespace);
        Element fileGroup = new Element("fileGrp", metsNamespace);
        fileGroup.setAttribute("USE", "SOURCE");
        fileSec.addContent(fileGroup);
        this.metsDoc.getRootElement().addContent(fileSec);
        return fileGroup;
    }

    protected MetsNode loadNode(Element element) {
        if (element == null || !(element instanceof Element)) {
            return null;
        }
        MetsNode newNode = null;
        if (element.getName().equalsIgnoreCase("fptr")) {
            newNode = new MetsFile(this, element.getAttributeValue("FILEID"));
        } else {
            newNode = new MetsDiv(this, element);
        }
        newNode.loadChildren(true);
        return newNode;
    }
}
