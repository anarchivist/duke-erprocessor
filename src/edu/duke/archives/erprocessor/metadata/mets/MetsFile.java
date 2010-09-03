/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.archives.erprocessor.metadata.mets;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.*;

/**
 *
 * @author Seth Shaw
 */
public class MetsFile extends MetsNode {

    private String fileID;
    //Cache values
    private long size = -1;
    private String lastModified;
    private String AMDID;
    private String MIMETYPE;
    private String CHECKSUM;
    private String CHECKSUMTYPE;
    private String fileType;
    private String filePath;
    private Element file = null;

    MetsFile(METS mets, String id) {
        super(mets);
        try {
            fileID = id;
            file =
                    (Element) XPath.selectSingleNode(mets.metsDoc,
                    "/mets:mets/mets:fileSec/mets:fileGrp[@USE='SOURCE']/mets:file[@ID='" +
                    fileID + "']");

            if (file == null) {
                return;
            }

            //We already have the element here... why not load up?
            String sizeString = file.getAttributeValue("SIZE");
            if (sizeString != null && !sizeString.equalsIgnoreCase("") &&
                    sizeString.matches("^\\d$")) {
                size = Long.parseLong(sizeString);
            }
            lastModified = file.getAttributeValue("CREATED");
            dmdId = file.getAttributeValue("DMDID");
            AMDID = file.getAttributeValue("AMDID");
            MIMETYPE = file.getAttributeValue("MIMETYPE");
            CHECKSUM = file.getAttributeValue("CHECKSUM");
            CHECKSUMTYPE = file.getAttributeValue("CHECKSUMTYPE");

            //PREMIS NAME
            if (label == null || label.equalsIgnoreCase("")) {
                Element originalName = getAMD("techMD", "PREMIS",
                        "//p1:originalName");
                if (originalName != null) {
                    label = originalName.getTextNormalize();
                } else {
                    label = "[name unknown]";
                }
            }

            //PREMIS TYPE
            if (fileType == null || fileType.equalsIgnoreCase("")) {
                fileType = "";
                Element format = getAMD("techMD", "PREMIS",
                        "//p1:format");
                if (format != null) {
                    for (Object type : XPath.selectNodes(format,
                            "//p1:formatName")) {
                        if (fileType.length() > 0) {
                            fileType += "; ";
                        }
                        fileType += ((Element) type).getTextNormalize();
                    }
                }
            }

            //Save memory, perhaps
            file = null;

        } catch (Exception ex) {
            Logger.getLogger(MetsFile.class.getName()).log(Level.SEVERE,
                    "Failed to load file: " + fileID,
                    ex);
        }
    }

    public String getAMDID() {
        if (AMDID == null) {
            AMDID = getFileAttribute("ADMID");
        }
        return AMDID;
    }

    public String getCHECKSUM() {
        if (CHECKSUM == null) {
            CHECKSUM = getFileAttribute("CHECKSUM");
        }
        return CHECKSUM;
    }

    public String getCHECKSUMTYPE() {
        if (CHECKSUMTYPE == null) {
            CHECKSUMTYPE = getFileAttribute("CHECKSUMTYPE");
        }
        return CHECKSUMTYPE;
    }

    @Override
    public String getDMDID() {
        if (dmdId == null || dmdId.equalsIgnoreCase("")) {
            //Check to see if one has been set.
            dmdId = getFileAttribute("DMDID");
            if (dmdId == null) {
                return "";
            }
        }
        return dmdId;
    }

    public String getFilePath() {
        if (filePath == null || filePath.equalsIgnoreCase("")) {
            try {
//                String xpath = "/mets:mets/mets:fileSec/mets:fileGrp[@USE='SOURCE']/mets:file[@ID='" +
//                        fileID + "']/mets:FLocat/@xlink:href";
//                Attribute att = (Attribute) XPath.selectSingleNode(mets.metsDoc,
//                        xpath);
//                if (att == null) { //File not found (essentially)
//                    System.err.println("Could not find xlink:href for " + this.toString() +
//                            " with fileID " + fileID);
//                    filePath = ""; //Nothing we can do about it
//                } else {
//                    filePath = att.getValue();
//                }
                file =
                    (Element) XPath.selectSingleNode(mets.metsDoc,
                    "/mets:mets/mets:fileSec/mets:fileGrp[@USE='SOURCE']/mets:file[@ID='" +
                    fileID + "']");
                Element fLocat = file.getChild("FLocat", mets.metsNamespace);
                Attribute att = fLocat.getAttribute("href", mets.xlinkNamespace);
                if (att == null) { //File not found (essentially)
                    System.err.println("Could not find xlink:href for " +
                            this.toString() +
                            " with fileID " + fileID);
                    filePath = ""; //Nothing we can do about it
                } else {
                    filePath = att.getValue();
                }
            } catch (Exception ex) {
                Logger.getLogger(MetsFile.class.getName()).log(Level.WARNING,
                        null, ex);
            }
        }
        return filePath;
    }

    @Override
    protected void setDMDID(String dmdId) {
        this.dmdId = dmdId;
        try {
            if (file == null) {
                file = (Element) XPath.selectSingleNode(mets.metsDoc,
                        "/mets:mets/mets:fileSec/mets:fileGrp[@USE='SOURCE']/mets:file[@ID='" +
                        fileID + "']");
                if (file == null) { //File not found (essentially)
                    System.err.println("Could not find "+this.toString()+" with fileID "+fileID);
                    return; //Nothing we can do about it
                }
            } 
            file.setAttribute("DMDID", dmdId);
        } catch (Exception ex) {
            Logger.getLogger(MetsFile.class.getName()).log(Level.WARNING, null,
                    ex);
        }
    }

    public String getMIMETYPE() {
        if (MIMETYPE == null) {
            MIMETYPE = getFileAttribute("MIMETYPE");
        }
        return MIMETYPE;
    }

    public String getLastModified() {
        if (lastModified == null) {
            lastModified = getFileAttribute("CREATED");
        }
        return lastModified;
    }

    public long getSize() {
        if (size < 0) { //May not be set
            String sizeString = getFileAttribute("SIZE");
            if (sizeString != null && !sizeString.equalsIgnoreCase("") &&
                    sizeString.matches("^\\d$")) {
                size = Long.parseLong(sizeString);
            }
        }
        return size;
    }

    private String getFileAttribute(String attribute) {
        try {
            if (file == null) {
                file = (Element) XPath.selectSingleNode(mets.metsDoc,
                        "/mets:mets/mets:fileSec/mets:fileGrp[@USE='SOURCE']/mets:file[@ID='" +
                        fileID + "']");
                if (file == null) { //File not found (essentially)
                    return ""; //Nothing we can do about it
                }
            }
            String value = file.getAttributeValue(attribute);
            return (value != null) ? value : "";
        } catch (Exception ex) {
            Logger.getLogger(MetsFile.class.getName()).log(Level.WARNING,
                    null,
                    ex);
            return "";
        }
    }

    public Element getDMD(String mdType, String xPath) {
        return mets.getMDWrap(getDMDID(), mdType, xPath);
    }

    public Element getDMD(String mdType) {
        return getDMD(mdType, "");
    }

    public Element getAMD(String type, String mdType) {
        return getAMD(type, mdType, "");
    }

    /*
     * There is only one type of AMD right now (techMD) and file only has one
     * AMD pointer. I kept "type" just in case we need it later.
     */
    public Element getAMD(String type, String mdType, String xPath) {
        return mets.getMDWrap(getAMDID(), mdType, xPath);
    }

    public String getFileType() {
        String filetype = "";
        Element amd = getAMD("techMD", "PREMIS");
        if (amd == null) {
            return "";
        }
        try {
            for (Object formatDesignation : XPath.selectNodes(amd,
                    "//p1:formatDesignation")) {
                if (!(formatDesignation instanceof Element)) {
                    continue;
                }
                Element name =
                        ((Element) formatDesignation).getChild("formatName");
                if (name != null) {
                    if (!filetype.equalsIgnoreCase("")) {
                        filetype += "; ";
                    }
                    filetype += name.getTextNormalize();
                }
            }
        } catch (JDOMException ex) {/*Do nothing*/
        }
        return filetype;
    }

    public String getName() {
        return label;
    }

    @Override
    public void rename(String newName) {
    //Do nothing, we don't permit renaming files here
    //Instead, rely on Alternate Name metadata
    }

    @Override
    public void loadChildren() {
        //Files have no children to load
        return;
    }

    @Override
    public void loadChildren(boolean recurse) {
        //Files have no children to load
        return;
    }
}
