/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.duke.archives.erprocessor.metadata;

import java.util.List;

/**
 *
 * @author Seth Shaw
 */
public class QualifiedMetadata {
    protected String nameSpace;
    protected String element = "";
    protected String qualifier = "";
    protected String value = "";

    public String getElement() {
        if (element == null){
            this.element = "";
        }
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getNameSpace() {
        if (nameSpace == null){
            this.nameSpace = "";
        }
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getQualifier() {
        if (qualifier == null){
            this.qualifier = "";
        }
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getValue() {
        if (value == null){
            this.value = "";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public static QualifiedMetadata getQM(List<QualifiedMetadata> qmList, String element, String qualifier){
        qualifier = (qualifier == null) ? "" : qualifier;
        for (QualifiedMetadata qm : qmList) {
            if (qm.element.equalsIgnoreCase(element) && qm.getQualifier().
                    equalsIgnoreCase(qualifier)) {
                return qm;
            }
        }
        return new QualifiedMetadata();
    }

    @Override
    public String toString(){
        String qm = "<";
        if(nameSpace != null){
            qm+=nameSpace+":";
        }
        qm += "<"+getElement();
        if(this.qualifier != null){
            qm += " qualifier=\""+qualifier+"\" ";
        }
        qm+=">"+getValue()+"</";
        if(nameSpace != null){
            qm+=nameSpace+":";
        }
        qm+=getElement()+">";
        
        return qm;
    }
}
