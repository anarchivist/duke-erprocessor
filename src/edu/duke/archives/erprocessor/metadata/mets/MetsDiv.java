/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.duke.archives.erprocessor.metadata.mets;

import org.jdom.Element;

/**
 *
 * @author Seth Shaw
 */
public class MetsDiv extends MetsNode {

    public MetsDiv(METS mets){
        super(mets);
    }

    MetsDiv(METS mets, Element element) {
        super(mets);
        this.element = element;
        
        String metsType = element.getAttributeValue("TYPE");
        if(metsType != null && !metsType.equalsIgnoreCase("")){
            label = metsType;
        }
        
        String metsLabel = element.getAttributeValue("LABEL");
        if(metsLabel != null && !metsLabel.equalsIgnoreCase("")){
            label = metsLabel;
        }
        
        String metsDMDid = element.getAttributeValue("DMDID");
        if(metsDMDid != null && !metsDMDid.equalsIgnoreCase("")){
            dmdId = metsDMDid;
        }
        
    }

    MetsDiv(METS mets, String label) {
        this(mets);
        element = new Element("div", mets.metsNamespace);
        this.label = label;
        element.setAttribute("LABEL", label);
    }
    
    @Override
    public void rename(String newName){
        if(newName != null && !newName.matches("^\\s*$")){
            label = newName;
            element.setAttribute("LABEL", newName);
        }
    }
}
