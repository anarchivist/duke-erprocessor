/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.archives.erprocessor.metadata.mets;

import edu.duke.archives.erprocessor.metadata.QualifiedMetadata;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.jdom.Element;

/**
 *
 * @author Seth Shaw
 */
public class MetsNode extends DefaultMutableTreeNode {

    String label = "";
    String dmdId = "";
    String id = "";
    METS mets;
    Element element = null;

    MetsNode(METS mets) {
        this.mets = mets;
    }

    public void loadChildren() {
        loadChildren(false);
    }

    public void loadChildren(boolean recurse) {
        if (element == null) {
            return;
        }
        for (Object child : element.getChildren()) {
            if (!(child instanceof Element)) {
                continue;
            }
            this.add(mets.loadNode((Element) child));
        }
    }

    /**
     * Returns the item's name. Called for tree display.
     * @return the item's name
     */
    @Override
    public String toString() {
        return label;
    }

    public List<QualifiedMetadata> getQualifiedMetadata() {
        List<QualifiedMetadata> qmList = new ArrayList<QualifiedMetadata>();
        if (!getDMDID().equalsIgnoreCase("")) {
            Element dmdBase = mets.getDMD(dmdId, "OTHER");
            for (Object metadata : dmdBase.getChildren()) {
                QualifiedMetadata qm = new QualifiedMetadata();
                qm.setElement(((Element) metadata).getName());
                qm.setNameSpace(((Element) metadata).getNamespacePrefix());
                qm.setValue(((Element) metadata).getValue());
                qm.setQualifier(((Element) metadata).getAttributeValue("qualifier"));
                qmList.add(qm);
            }
        }
        return qmList;
    }

    public void setQualifiedMetadata(List<QualifiedMetadata> qualifiedMetadataLst) {
        Element baseDMD = null;
        
        //What if none exist?
        if(getDMDID().equalsIgnoreCase("")){
            //and no metadata was given?
            if(qualifiedMetadataLst.isEmpty()){
                //Ignore it.
                return;
            }
            //Get an Id to make a new one.
//            dmdId = mets.nextID("DMD");    
            setDMDID(mets.nextID("dmdSec"));
        }
        baseDMD = mets.getDMD(dmdId, "OTHER", true);
        
        //Empty it out for new metadata
        baseDMD.removeContent();
        for(QualifiedMetadata qm : qualifiedMetadataLst){
            if(qm.getValue().equalsIgnoreCase("")) {
                continue;
            }
            Element qmElement = new Element(qm.getElement());
            qmElement.setText(qm.getValue());
            if(!qm.getQualifier().equalsIgnoreCase("")){
                qmElement.setAttribute("qualifier", qm.getQualifier());
            }
            baseDMD.addContent(qmElement);
        }
    }

//    private static String getQMString(List<QualifiedMetadata> qualifiedMetadataLst) {
//        String fullQMString = "";
//        return "<mets:xmlData>\n" + fullQMString + "</mets:xmlData>";
//    }

    public String getID() {
        return id;
    }

    protected void setDMDID(String dmdId){
        this.dmdId = dmdId;
        element.setAttribute("DMDID", dmdId);
    }
    
    public String getDMDID() {
        if (dmdId == null) {
            dmdId = "";
        }
        return dmdId;
    }
    
    public boolean addDiv(String label) {
        MetsNode child = new MetsDiv(mets, label);
        this.add(child);
        this.element.addContent(child.element);
        return true;
    }

    public boolean removeChild(Object childToRemove) {
        if (childToRemove instanceof MetsNode) {
            element.removeContent(((MetsNode) childToRemove).element);
            this.removeChild(childToRemove);
        }
        return true;
    }

    void addChild(Element newAccession) {
        MetsNode child = mets.loadNode(newAccession);
        this.add(child);
        this.element.addContent(child.element);
    }

    public boolean copyNodeInto(MetsNode copied, boolean detatch) {
        if (detatch) {//I.e. move.
            //Detatch copied from parent element
            org.jdom.Content detached = copied.element.detach();
            //Detatch copied from parent MetsNode
            copied.removeFromParent();
            if (detached instanceof Element) {
                this.addChild((Element) detached);
            }
        } else {//Copy
            //Clone node
            MetsNode clone = (MetsNode) copied.clone();
            clone.children = copied.children;
            //The element does not survive the clone so we clone it here.
            if(copied.element != null) {
                clone.element = (Element) copied.element.clone();
            }
            addChild(clone);
        }

        return true;
    }

    public METS getMETS() {
        return mets;
    }
    
    protected void addChild(MetsNode metsNode) {
        this.add(metsNode);
        //Avoid duplication
        if(! this.element.isAncestor(metsNode.element)) {
            this.element.addContent(metsNode.element);
        }
        
    }

    public void rename(String newName){
        if(newName != null && !newName.matches("^\\s*$")){
            label = newName;
        }
    }
    
    @Override
    public void remove(MutableTreeNode aChild) {
        super.remove(aChild);
        if(aChild instanceof MetsNode){
            element.removeContent(((MetsNode)aChild).element);
        }
    }
    
    
}
