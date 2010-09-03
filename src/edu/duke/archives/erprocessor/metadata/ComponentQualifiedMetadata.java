/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.archives.erprocessor.metadata;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Seth Shaw
 */
public class ComponentQualifiedMetadata extends QualifiedMetadata{

    public JComponent component;

    public ComponentQualifiedMetadata() {
    }

    public ComponentQualifiedMetadata(JComponent jComponent) {
        this.component = jComponent;
    }

    public void clearComponentValue() {
        if ((component instanceof javax.swing.JTextField) ||
                (component instanceof javax.swing.JTextArea) ||
                (component instanceof javax.swing.JTextPane) ||
                (component instanceof javax.swing.JEditorPane)) {
            ((JTextComponent) component).setText("");
        } else if ((component instanceof javax.swing.JCheckBox) ||
                (component instanceof javax.swing.JRadioButton)) {
            ((JToggleButton) component).setSelected(false);
        } else if (component instanceof javax.swing.JComboBox) {
            ((JComboBox) component).setSelectedIndex(-1);
        } else if (component instanceof javax.swing.JList) {
            ((javax.swing.JList) component).clearSelection();
        }
    }

    public void setComponentValue(String[] textNormalize) {
        if ((component instanceof javax.swing.JTextField) ||
                (component instanceof javax.swing.JTextArea) ||
                (component instanceof javax.swing.JTextPane) ||
                (component instanceof javax.swing.JEditorPane)) {
            ((JTextComponent) component).setText(textNormalize[0]);
        } else if ((component instanceof javax.swing.JCheckBox) ||
                (component instanceof javax.swing.JRadioButton)) {
            if (textNormalize[0].equalsIgnoreCase("true")) {
                ((JToggleButton) component).setSelected(true);
            } else if (textNormalize[0].equalsIgnoreCase("false")) {
                ((JToggleButton) component).setSelected(false);
            }
        //Not a valid option, ignore
        } else if (component instanceof javax.swing.JComboBox) {
            ((JComboBox) component).setSelectedItem(textNormalize[0]);
        } else if (component instanceof javax.swing.JList) {
            ((javax.swing.JList) component).clearSelection();
            for (String textElement : textNormalize) {
                for (int i = 0; i < ((javax.swing.JList) component).getModel().
                        getSize(); i++) {
                    Object text = ((javax.swing.JList) component).getModel().
                            getElementAt(i);
                    if ((text instanceof String) &&
                            (((String) text).equalsIgnoreCase(textElement))) {
                        ((javax.swing.JList) component).addSelectionInterval(i,
                                i);
                    }
                }
            }
        }
    }

    /**
     * @return the value
     */
    @Override
    public String getValue() {
        String[] values = getValues();
        if (values.length == 0) {
            return "";
        } else {
            return getValues()[0];
        }
    }

    /**
     * @return the values
     */
    public String[] getValues() {
        if (component == null) {
            return new String[]{value};
        }
        List<String> values = new ArrayList<String>();
//        if ((component instanceof javax.swing.JTextField) ||
//                (component instanceof javax.swing.JTextArea) ||
//                (component instanceof javax.swing.JTextPane) ||
//                (component instanceof javax.swing.JEditorPane)) {
        if (component instanceof javax.swing.text.JTextComponent) {
//            String text = ((JTextComponent) component).getText();
            values.add(((JTextComponent) component).getText());
        } else if (component instanceof JToggleButton) {
            if (((JToggleButton) component).isSelected()) {
                values.add("true");
            } else {
                values.add("false");
            }
        } else if (component instanceof javax.swing.JComboBox) {
            for (Object obj : ((JComboBox) component).getSelectedObjects()) {
                if (obj instanceof String) {
                    values.add((String) obj);
                }
            }
        } else if (component instanceof javax.swing.JList) {
            for (int i = 0; i < ((javax.swing.JList) component).getModel().
                    getSize(); i++) {
                Object text = ((javax.swing.JList) component).getModel().
                        getElementAt(i);
                if (text instanceof String) {
                    values.add((String) text);
                }
            }
        }
        return values.toArray(new String[]{});
    }

    /**
     * @param value the value to set
     */
    @Override
    public void setValue(String value) {
        super.setValue(value);
        setComponentValue(new String[]{value});
    }

    /**
     * @param nameSpace
     * @param element
     * @param qualifier
     * @param value
     */
    public ComponentQualifiedMetadata(String nameSpace, String element,
            String qualifier, String value) {
        this.nameSpace = nameSpace;
        this.element = element;
        this.qualifier = qualifier;
        this.value = value;
    }
}

