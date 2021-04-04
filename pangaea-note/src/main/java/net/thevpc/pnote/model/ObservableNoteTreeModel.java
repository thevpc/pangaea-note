/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.model;

import net.thevpc.common.swing.tree.AbstractTreeModel;

/**
 *
 * @author vpc
 */
public class ObservableNoteTreeModel extends AbstractTreeModel {

    private PangaeaNoteExt root;

    public ObservableNoteTreeModel(PangaeaNoteExt root) {
        this.root = root;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((PangaeaNoteExt) parent).getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((PangaeaNoteExt) parent).getChildren().size();
    }

    @Override
    public boolean isLeaf(Object note) {
        return ((PangaeaNoteExt) note).getChildren().size() == 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((PangaeaNoteExt) parent).getChildren().indexOf(child);
    }

    public void treeStructureChanged() {
        fireTreeStructureChanged(this, getPathToRoot((PangaeaNoteExt) getRoot()), null, null);
    }

    public void nodeStructureChanged(PangaeaNoteExt note) {
        if (note != null) {
            fireTreeStructureChanged(this, getPathToRoot(note), null, null);
        }
    }

    public PangaeaNoteExt[] getPathToRoot(PangaeaNoteExt note) {
        return getPathToRoot(note, 0);
    }

    protected PangaeaNoteExt[] getPathToRoot(PangaeaNoteExt note, int depth) {
        PangaeaNoteExt[] retNotes;
        if (note == null) {
            if (depth == 0) {
                return null;
            } else {
                retNotes = new PangaeaNoteExt[depth];
            }
        } else {
            depth++;
            if (note == getRoot()) {
                retNotes = new PangaeaNoteExt[depth];
            } else {
                retNotes = getPathToRoot(note.getParent(), depth);
            }
            retNotes[retNotes.length - depth] = note;
        }
        return retNotes;
    }

    public void setRoot(PangaeaNoteExt copyFrom) {
        this.root = copyFrom;
        treeStructureChanged();

    }

    @Override
    protected void insertNodeIntoImpl(Object parent, Object newChild, int index) {
        if (newChild instanceof PangaeaNoteExt) {
            ((PangaeaNoteExt) parent).addChild(index, ((PangaeaNoteExt) newChild));
        } else if (newChild instanceof String) {
            ((PangaeaNoteExt) parent).addChild(index, PangaeaNoteExt.of(new PangaeaNote().setName((String)newChild)));
        }
    }

    @Override
    protected void removeNodeFromParentImpl(Object parent, int childIndex) {
        if (childIndex >= 0) {
            ((PangaeaNoteExt) parent).removeChild(childIndex);
        }
    }

    @Override
    public Object getParent(Object target) {
        return ((PangaeaNoteExt) target).getParent();
    }

    @Override
    public Object copyNode(Object note) {
        return ((PangaeaNoteExt) note).copy();
    }

}
