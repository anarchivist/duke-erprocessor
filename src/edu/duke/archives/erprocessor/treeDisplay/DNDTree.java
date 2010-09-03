/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.archives.erprocessor.treeDisplay;

import edu.duke.archives.erprocessor.ERProcessor;
import edu.duke.archives.erprocessor.metadata.mets.MetsDiv;
import edu.duke.archives.erprocessor.metadata.mets.MetsFile;
import edu.duke.archives.erprocessor.metadata.mets.MetsNode;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

/**
 *
 * @author Seth Shaw
 */
public class DNDTree extends JTree {

    Insets autoscrollInsets = new Insets(20, 20, 20, 20); // insets

    public DNDTree(TreeModel model) {
        super(model);
        setAutoscrolls(true);
//        setLargeModel(true);
        setRootVisible(false);
        setShowsRootHandles(true);
        putClientProperty("JTree.lineStyle", "Angled");
        this.setCellRenderer(new TreeRenderer());
        new DefaultTreeTransferHandler(this, DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void autoscroll(Point cursorLocation) {
        Insets insets = getAutoscrollInsets();
        Rectangle outer = getVisibleRect();
        Rectangle inner = new Rectangle(outer.x + insets.left, outer.y +
                insets.top, outer.width - (insets.left + insets.right),
                outer.height - (insets.top + insets.bottom));
        if (!inner.contains(cursorLocation)) {
            Rectangle scrollRect = new Rectangle(cursorLocation.x - insets.left,
                    cursorLocation.y - insets.top, insets.left + insets.right,
                    insets.top + insets.bottom);
            scrollRectToVisible(scrollRect);
        }
    }

    public Insets getAutoscrollInsets() {
        return (autoscrollInsets);
    }

    public static DefaultMutableTreeNode makeDeepCopy(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode copy =
                new DefaultMutableTreeNode(node.getUserObject());
        for (Enumeration e = node.children(); e.hasMoreElements();) {
            copy.add(makeDeepCopy((DefaultMutableTreeNode) e.nextElement()));
        }
        return (copy);
    }
    
    private class TreeRenderer extends DefaultTreeCellRenderer{
        private javax.swing.Icon fileIcon = null;
        private javax.swing.Icon folderIcon = null;
        public TreeRenderer(){
            super();
            fileIcon = new javax.swing.ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().
                getResource("resources/page.gif")));
            folderIcon = new javax.swing.ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().
                getResource("resources/folder.gif")));
        }
        
        @Override
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            
            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);
            setToolTipText("");
            if (!(value instanceof MetsNode)) {
                return this;
            } 
            if((value instanceof MetsFile) && fileIcon != null){
                setIcon(fileIcon);
                setToolTipText(value.toString()+
                        "\nSize: "+ERProcessor.prettySize(((MetsFile)value).getSize())+
                        "Last Modified: "+((MetsFile)value).getLastModified());
            }else if((value instanceof MetsDiv) && fileIcon != null){
                setIcon(folderIcon);
            }
            
            return this;
        }

        @Override
        public String getToolTipText(MouseEvent evt) {
            // Let the renderer supply the tooltip
            String tip = super.getToolTipText(evt);

            // If it did not, return the tree's tip
            return tip != null ? tip : getToolTipText();
        }
    }
}
