/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.tree;

import net.thevpc.common.i18n.Str;
import net.thevpc.common.props.ObservableBoolean;
import net.thevpc.common.props.Path;
import net.thevpc.common.props.WritableIndexedNode;
import net.thevpc.common.props.WritableList;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.*;
import net.thevpc.echo.impl.TreeNode;
import net.thevpc.echo.model.AppTreeMutator;
import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.api.model.ObservableNoteSelectionListener;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author vpc
 */
public class PangaeaNoteDocumentTree extends BorderPane {

    private final EnableOnAnyTreeSelection enableIfSelection;
    Tree<PangaeaNote> tree;
    PangaeaNoteApp app;
    private ContextMenu treePopupMenu;
    private List<ObservableNoteSelectionListener> listeners = new ArrayList<>();
    private PangaeaNoteFrame frame;

    public PangaeaNoteDocumentTree(PangaeaNoteFrame frame) {
        super("DocumentTree", frame.app());
        title().set(Str.i18n("Tools.Document"));
        this.frame = frame;
        this.app = frame.app();
//        model = new ObservableNoteTreeModel(PangaeaNoteExt.of(this.win.service().newDocument()), this.win.service());
        tree = new Tree<>(PangaeaNote.class, app);
        enableIfSelection = new EnableOnAnyTreeSelection();

        tree.rootVisible().set(false);
        tree.nodeFactory().set(new TreeNodeFactory<PangaeaNote>() {
            @Override
            public TreeNode<PangaeaNote> createNode(PangaeaNote value, AppTree<PangaeaNote> tree) {
                TreeNode<PangaeaNote> t = new TreeNode<>(value, tree);
                value.guiNode = t;
                return t;
            }
        });
        tree.childrenFactory().set(PangaeaNote::getChildren);
        tree.mutator().set(new AppTreeMutator<PangaeaNote>() {
            @Override
            public void updateChild(AppTreeNode<PangaeaNote> parent, int index, AppTreeCallBack<PangaeaNote> callback) {
                callback.nodesWereUpdated(parent,new AppTreeIndexedChild<>((AppTreeNode<PangaeaNote>) parent.children().get(index),index));
            }

            @Override
            public AppTreeNode<PangaeaNote> addChild(AppTreeNode<PangaeaNote> parent, Object child, int index, AppTreeCallBack<PangaeaNote> callback) {
                if (child instanceof TreeNode) {
                    child = ((TreeNode<?>) child).get();
                }
                PangaeaNote nc = null;
                if (child instanceof PangaeaNote) {
                    nc = (PangaeaNote) child;
                } else if (child instanceof String) {
                    PangaeaNote nc0 = frame.app().createNoteFromSnippet(child);
                    if (nc0 != null) {
                        nc = nc0;
                    }
                }
                if (nc != null) {
                    AppTreeNode<PangaeaNote> otherNode;
                    if (nc.guiNode != null) {
                        otherNode = nc.guiNode;
                    } else {
                        otherNode = tree().nodeOf(nc);
                    }
                    if (index < 0) {
                        index= parent.get().getChildren().size();
                        parent.get().getChildren().add(nc);
                        parent.children().add(otherNode);
                    } else {
                        parent.get().getChildren().add(index, nc);
                        parent.children().add(index, otherNode);
                    }

                    TreeNode<PangaeaNote> v = treeNodeOf(nc);
                    callback.nodesWereInserted(parent,new AppTreeIndexedChild<>(otherNode,index));
                    frame.onDocumentChanged();
                    return v;
                }
                return null;
            }

            @Override
            public AppTreeNode<PangaeaNote> removeChild(AppTreeNode<PangaeaNote> parent, int childIndex, AppTreeCallBack<PangaeaNote> callBack) {
                PangaeaNote parentNote = parent.get();
                AppTreeNode<PangaeaNote> child = (AppTreeNode<PangaeaNote>) parent.children().get(childIndex);
                parentNote.getChildren().remove(childIndex);
                parent.children().removeAt(childIndex);
                callBack.nodesWereDeleted(parent,new AppTreeIndexedChild<>(child,childIndex));
                frame.onDocumentChanged();
                return child;
            }

            @Override
            public AppTreeNode<PangaeaNote> copy(AppTreeNode<PangaeaNote> node) {
                PangaeaNote n = node.get();
                n = n.copy();
                return tree.nodeOf(n);
            }
        });
        tree.root().set(
                tree.nodeOf(this.frame.app().newDocument())
        );
        tree.selection().onChange(
                x -> {
                    AppTreeNode<PangaeaNote> n = tree.selection().get();
                    fireOnSelectedNote(n == null ? null : n.get());
                }
        );
        tree.itemRenderer().set(new SimpleDefaultTreeCellRendererImpl(frame));
        treePopupMenu = new ContextMenu("TreeContextMenu", Str.i18n("TreeContextMenu"), app);
        tree.contextMenu().set(treePopupMenu);
        treePopupMenu.children().add(new Button("AddChildNote", () -> frame.addNote(), app));
        treePopupMenu.children().add(new Button("AddNoteBefore", () -> frame.addNoteBefore(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("AddNoteAfter", () -> frame.addNodeAfter(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("DuplicateNote", () -> frame.duplicateNote(), app).with(enableIfSelection));
        AppMenu importMenu = (AppMenu) treePopupMenu.children().addFolder(Path.of("Import"));
        importMenu.text().set(Str.i18n("Import"));
        importMenu.icon().unset();

        treePopupMenu.children().add(new Button("Import.Any", () -> frame.importFileInto(), app), Path.of("/Import/*"));
        treePopupMenu.children().add(new Button("Import.PangaeaNote", ()
                -> frame.importFileInto(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString()),
                app), Path.of("/Import/*"));

        for (PangaeaNoteFileImporter fileImporter : frame.app().getFileImporters()) {
            treePopupMenu.children().add(new Button("Import." + fileImporter.getName(), ()
                    -> frame.importFileInto(fileImporter.getSupportedFileExtensions()),
                    app), Path.of("/Import/*"));
        }
        treePopupMenu.children().addSeparator().with(enableIfSelection);
        treePopupMenu.children().add(new Button("RenameNote", () -> frame.renameNote(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("StrikeNote", () -> frame.strikeThroughNote(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("BoldNote", () -> frame.boldNote(), app).with(enableIfSelection));
//        treePopupMenu.children().addSeparator();
        treePopupMenu.children().addSeparator().with(enableIfSelection);
        treePopupMenu.children().add(new Button("moveUpInObjectList", () -> moveUp(getSelectedNote()), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("moveDownInObjectList", () -> moveDown(getSelectedNote()), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("moveFirstInObjectList", () -> moveFirst(getSelectedNote()), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("moveLastInObjectList", () -> moveLast(getSelectedNote()), app).with(enableIfSelection));

        treePopupMenu.children().addSeparator().with(enableIfSelection);
        treePopupMenu.children().add(new Button("SearchNote", () -> frame.searchNote(), app));
//        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("PrintNote", () -> frame.printNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator().with(enableIfSelection);
        treePopupMenu.children().add(new Button("DeleteNote", () -> frame.deleteSelectedNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator().with(enableIfSelection);
        treePopupMenu.children().add(new Button("NoteProperties", () -> frame.editNote(), app).with(enableIfSelection));
        PangaeaNote sn = getSelectedNote();
//        for (TreeAction action : actions) {
//            action.onSelectedNote(sn);
//        }
        children.add(new ScrollPane(tree));

//        tree.selection().onChange(this::dumpAll);
    }

    public PangaeaNoteDocumentTree addNoteSelectionListener(ObservableNoteSelectionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
        return this;
    }

    public void fireOnSelectedNote(PangaeaNote note) {
        for (ObservableNoteSelectionListener listener : listeners) {
            listener.onSelectionChanged(note);
        }
    }

    public PangaeaNote getDocument() {
        return (PangaeaNote) tree.root().get().get();
    }

    public void updateTree() {
    }

    public PangaeaNote getSelectedNoteOrDocument() {
        AppTreeNode<PangaeaNote> p = tree.selection().get();
        if (p != null) {
            PangaeaNote c = p.get();
            if (c != null) {
                return c;
            }
        }
        return tree.root().get().get();
    }

    public PangaeaNote getSelectedNote() {
        AppTreeNode<PangaeaNote> p = tree.selection().get();
        if (p != null) {
            PangaeaNote c = p.get();
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public void setSelectedNote(AppTreeNode<PangaeaNote> sel) {
        tree.selection().set(sel);
    }

    public void setSelectedNote(PangaeaNote note) {
        List<PangaeaNote> elems = new ArrayList<>();
        while (note != null) {
            elems.add(0, note);
            note = getParent(note);
        }
        if (elems.isEmpty()) {
            elems.add(getDocument());
        }
        tree.selection().set(tree.findNode(elems.toArray()));
    }

    public void setDocumentNote(PangaeaNote e) {
        if (e.guiNode == null) {
            e.guiNode = tree.nodeOf(e);
        }
        tree.root().set(e.guiNode);
    }


    public void removeNodeChild(PangaeaNote parent,int pos) {
        tree().removeChild(treeNodeOf(parent),pos);
    }

    public void removeNodeChild(AppTreeNode<PangaeaNote> node) {
        PangaeaNote parentNote = node.parent().get().get();
        tree().removeChild(treeNodeOf(parentNote),indexOfNote(parentNote, node.get()));
    }

    @Override
    public PangaeaNoteApp app() {
        return (PangaeaNoteApp) super.app();
    }


    public void dumpAll() {
        dump(tree.root().get().get(), "[PangaeaNote    ] ");
        dump(tree.root().get(), "[PangaeaTreeNote] ");
        dump(((JTree) (tree.peer().toolkitComponent())).getModel(), ((JTree) (tree.peer().toolkitComponent())).getModel().getRoot(), "[JTree          ] ");
    }


    private void dump(TreeModel m, Object a, String prefix) {
        AppTreeNode<PangaeaNote> n = (AppTreeNode<PangaeaNote>) a;
        System.out.println(prefix + "" + n.get().getName());
        int s = m.getChildCount(a);
        for (int i = 0; i < s; i++) {
            dump(m, m.getChild(a, i), prefix + "\t");
        }
    }

    private void dump(AppTreeNode<PangaeaNote> e, String prefix) {
        System.out.println(prefix + "" + e.get().getName());
        for (WritableIndexedNode<PangaeaNote> child : e.children()) {
            dump((AppTreeNode<PangaeaNote>) child, prefix + "\t");
        }
    }

    private void dump(PangaeaNote e, String prefix) {
        System.out.println(prefix + "" + e.getName());
        for (PangaeaNote child : e.getChildren()) {
            dump(child, prefix + "\t");
        }
    }

    public Tree<PangaeaNote> tree() {
        return tree;
    }

    public EnableOnAnyTreeSelection getEnableIfSelection() {
        return enableIfSelection;
    }

    private class EnableOnAnyTreeSelection implements Consumer<AppComponent> {

        ObservableBoolean notNull = tree.selection().isNotNull();

        @Override
        public void accept(AppComponent b) {
            b.enabled().bindSource(notNull);
            b.visible().set(!app.hideDisabled().get() || notNull.get());

            notNull.onChange(e -> {
                b.visible().set(
                        !app.hideDisabled().get() || notNull.get()
                );
            });
        }
    }

    public TreeNode<PangaeaNote>[] treeNodePath(PangaeaNote thisNode, boolean includeRoot) {
        TreeNode<PangaeaNote> t = treeNodeOf(thisNode);
        List<TreeNode> all = new ArrayList<>();
        while (t != null) {
            if (!includeRoot && t.parent().get() == null) {
                break;
            }
            all.add(0, t);
            t = (TreeNode<PangaeaNote>) t.parent().get();
        }
        return all.toArray(new TreeNode[0]);
    }


    public TreeNode<PangaeaNote> treeNodeOf(PangaeaNote thisNode) {
        if (thisNode == null) {
            return null;
        }
        if (thisNode.guiNode == null) {
            throw new IllegalArgumentException("missing tree node");
        }
        return thisNode.guiNode;
    }

    public void addNoteChild(PangaeaNote parent, PangaeaNote other, int index) {
        AppTreeNode<PangaeaNote> parentNode = treeNodeOf(parent);
        AppTreeNode<PangaeaNote> otherNode;
        if (other.guiNode != null) {
            otherNode = other.guiNode;
        } else {
            otherNode = parentNode.tree().nodeOf(other);
        }
        tree().addChild(parentNode,otherNode,index);
    }

    /**
     * add new sibling (same level) as duplicate of this node
     *
     * @param thisNode the note to duplicate
     * @param index    the new position (or -1) of the sibling
     * @return the sibling
     */
    public PangaeaNote addDuplicateSiblingNote(PangaeaNote thisNode, int index) {
        PangaeaNote dup = thisNode.copy();
        PangaeaNote parentNote = treeNodeOf(thisNode).parent().get().get();
        addNoteChild(parentNote, dup, index);
        return dup;
    }

//    public void removeChildNote(PangaeaNote parentNote, int childIndex) {
//    }

    public <T> boolean switchChildNotes(PangaeaNote parent, int index1, int index2) {
        if (parent != null) {
            List<PangaeaNote> tchildren = parent.getChildren();
            int size = tchildren.size();
            if (index1 >= 0 && index1 < size) {
                if (index2 >= 0 && index2 < size) {
                    if (index1 != index2) {
                        // switch notes
                        PangaeaNote a = tchildren.get(index1);
                        tchildren.set(index1, tchildren.get(index2));
                        tchildren.set(index2, a);
                        // switch nodes
                        AppTreeNode<PangaeaNote> pnode = treeNodeOf(parent);
                        WritableList<WritableIndexedNode<PangaeaNote>> nchildren = pnode.children();
                        AppTreeNode<PangaeaNote> b = (AppTreeNode<PangaeaNote>) nchildren.get(index1);
                        nchildren.set(index1, nchildren.get(index2));
                        nchildren.set(index2, b);
                        tree().updateChild(pnode,index1);
                        tree().updateChild(pnode,index2);
                    }
                }
            }
        }
        return false;
    }

    public int indexOfChild(PangaeaNote thisNode) {
        AppTreeNode<PangaeaNote> p = treeNodeOf(thisNode).parent().get();
        if (p == null) {
            return -1;
        }
        return p.get().getChildren().indexOf(thisNode);
    }

    public void updateNote(PangaeaNote thisNode) {
        AppTreeNode<PangaeaNote> p = treeNodeOf(thisNode).parent().get();
        if (p == null) {
            return;
        }
        PangaeaNote parent = p.get();
        int i = parent.getChildren().indexOf(thisNode);
        if (i >= 0) {
            tree().updateChild(p,i);
            if(thisNode==getSelectedNote()){
                frame.updateSelectedNote();
            }
        }
    }

    public void moveUp(PangaeaNote thisNode) {
        AppTreeNode<PangaeaNote> p = treeNodeOf(thisNode).parent().get();
        if (p == null) {
            return;
        }
        PangaeaNote parent = p.get();
        int i = parent.getChildren().indexOf(thisNode);
        if (i >= 0) {
            moveUp(parent, i);
        }
    }

    public void moveDown(PangaeaNote thisNode) {
        AppTreeNode<PangaeaNote> p = treeNodeOf(thisNode).parent().get();
        if (p == null) {
            return;
        }
        PangaeaNote parent = p.get();
        int i = parent.getChildren().indexOf(thisNode);
        if (i >= 0) {
            moveDown(parent, i);
        }
    }

    public void moveFirst(PangaeaNote thisNode) {
        AppTreeNode<PangaeaNote> p = treeNodeOf(thisNode).parent().get();
        if (p == null) {
            return;
        }
        PangaeaNote parent = p.get();
        int i = parent.getChildren().indexOf(thisNode);
        if (i >= 0) {
            moveFirst(parent, i);
        }
    }

    public void moveLast(PangaeaNote thisNode) {
        AppTreeNode<PangaeaNote> p = treeNodeOf(thisNode).parent().get();
        if (p == null) {
            return;
        }
        PangaeaNote parent = p.get();
        int i = parent.getChildren().indexOf(thisNode);
        if (i >= 0) {
            moveLast(parent, i);
        }
    }

    public void moveUp(PangaeaNote thisNode, int index) {
        switchChildNotes(thisNode, index, index - 1);
    }

    public void moveDown(PangaeaNote thisNode, int index) {
        switchChildNotes(thisNode, index, index + 1);
    }

    public boolean moveFirst(PangaeaNote thisNode, int from) {
        if (from > 0 && from <= thisNode.getChildren().size() - 1) {
            //remove from position
            PangaeaNote ta = thisNode.getChildren().remove(from);
            WritableIndexedNode<PangaeaNote> na = thisNode.guiNode.children().removeAt(from);

            //remove add to 0 position
            thisNode.getChildren().add(0, ta);
            thisNode.guiNode.children().add(0, na);
            return true;
        }
        return false;
    }

    public boolean moveLast(PangaeaNote thisNode, int from) {
        if (from > 0 && from <= thisNode.getChildren().size() - 1) {
            //remove from position
            PangaeaNote ta = thisNode.getChildren().remove(from);
            WritableIndexedNode<PangaeaNote> na = thisNode.guiNode.children().removeAt(from);

            //remove add to 0 position
            thisNode.getChildren().add(ta);
            thisNode.guiNode.children().add(na);
            return true;
        }
        return false;
    }

    public PangaeaNote getParent(PangaeaNote node) {
        AppTreeNode<PangaeaNote> parentNode = treeNodeOf(node).parent().get();
        return parentNode == null ? null : parentNode.get();
    }

    public int indexOfNote(PangaeaNote parent, PangaeaNote child) {
        TreeNode<PangaeaNote> p = treeNodeOf(parent);
        int index = 0;
        for (WritableIndexedNode<PangaeaNote> c : p.children()) {
            PangaeaNote cc = c.get();
            if (Objects.equals(cc, child)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public boolean addBeforeThis(PangaeaNote thisNode, PangaeaNote other) {
        PangaeaNote p = getParent(thisNode);
        if (p != null) {
            int i = indexOfNote(p, other);
            if (i >= 0) {
                addNoteChild(p, other, i);
                return true;
            }
        }
        return false;
    }

    public boolean addAfterThis(PangaeaNote thisNode, PangaeaNote other) {
        PangaeaNote p = getParent(thisNode);
        if (p != null) {
            int i = indexOfNote(p, other);
            if (i >= 0) {
                addNoteChild(p, other, i + 1);
                return true;
            }
        }
        return false;
    }

    public PangaeaNote[] nodePath(PangaeaNote thisNode, boolean includeRoot) {
        TreeNode<PangaeaNote> t = treeNodeOf(thisNode);
        List<PangaeaNote> all = new ArrayList<>();
        while (t != null) {
            if (!includeRoot && t.parent().get() == null) {
                break;
            }
            all.add(0, t.get());
            t = (TreeNode<PangaeaNote>) t.parent().get();
        }
        return all.toArray(new PangaeaNote[0]);
    }
}
