/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.duke.archives.erprocessor.treeDisplay;

import edu.duke.archives.erprocessor.metadata.mets.METS;
import edu.duke.archives.erprocessor.metadata.mets.MetsFile;
import edu.duke.archives.erprocessor.metadata.mets.MetsNode;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 *
 * @author Seth Shaw
 */
public class DefaultTreeTransferHandler extends AbstractTreeTransferHandler {

    public DefaultTreeTransferHandler(JTree tree, int action) {
        super(tree, action, true);
    }

    public boolean canPerformAction(JTree target, TreePath draggedPath,
            int action, Point location) {

        //Collection is the only vaild drop tree.
        if (target.getModel().getRoot().toString().equalsIgnoreCase("accessions")) {
            return false;
        }

        //Highlight the target path if able
        TreePath pathTarget = target.getPathForLocation(location.x, location.y);
        if (pathTarget == null) {
            target.setSelectionPath(null);
            return false;
        }
        target.setSelectionPath(pathTarget);
        
        if(target.getSelectionPath().getLastPathComponent() instanceof MetsFile){
            return false;
        }

        //Allow copy
        if (action == DnDConstants.ACTION_COPY) {
            return (true);
        } //Question the move
        else if (action == DnDConstants.ACTION_MOVE) {
            if (draggedPath.getParentPath() == null || pathTarget ==
                    draggedPath.getParentPath() ||
                    draggedPath.isDescendant(pathTarget)) {
                return (false);
            } else {
                return (true);
            }
        } else {
            return (false);
        }
    }

    public boolean executeDrop(JTree target, TreePath draggedNode,
            TreePath newParentPath, int action) {
        boolean success = false;
        if (!(draggedNode.getLastPathComponent() instanceof MetsNode) ||
                !(newParentPath.getLastPathComponent() instanceof MetsNode)) {
            return false;
        }
        MetsNode dragged = (MetsNode) draggedNode.getLastPathComponent();
        MetsNode parent = (MetsNode) newParentPath.getLastPathComponent();
        try {
            if (draggedNode.getPathComponent(0).toString().equalsIgnoreCase(METS.ACCESSIONS_STRUCTMAP)) {
                success = parent.copyNodeInto(dragged, false);
            } else {
                success = parent.copyNodeInto(dragged, true);
            }
        } catch (Exception e) {
            System.err.println("Error in Copy/Move of " + draggedNode.toString() +
                    " to " + newParentPath.toString() + ": " + e.getMessage());
        }
        target.scrollPathToVisible(newParentPath);
        target.setSelectionPath(newParentPath);
        target.expandPath(newParentPath);
        target.updateUI();
        return (success);
    }
}
