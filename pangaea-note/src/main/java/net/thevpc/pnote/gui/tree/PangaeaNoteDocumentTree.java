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
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppTreeNode;
import net.thevpc.echo.impl.TreeNode;
import net.thevpc.echo.model.AppTreeMutator;
import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.api.model.ObservableNoteSelectionListener;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.thevpc.common.props.WritableValue;

import javax.swing.*;
import javax.swing.tree.TreeModel;

/**
 * @author vpc
 */
public class PangaeaNoteDocumentTree extends BorderPane {

    private final EnableOnAnyTreeSelection enableIfSelection;
    Tree<PangaeaNoteExt> tree;
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
        tree = new Tree<>(PangaeaNoteExt.class, app);
        enableIfSelection = new EnableOnAnyTreeSelection();

        tree.rootVisible().set(false);
        tree.childrenFactory().set(PangaeaNoteExt::getChildren);
        tree.mutator().set(new AppTreeMutator<PangaeaNoteExt>() {
            @Override
            public void addChild(AppTreeNode<PangaeaNoteExt> parent, Object child, int index) {
                addNodeChild(parent, child, index);
            }

            @Override
            public void removeChild(AppTreeNode<PangaeaNoteExt> parent, int childIndex) {
                removeNodeChild(parent,childIndex);
            }

            @Override
            public AppTreeNode<PangaeaNoteExt> copy(AppTreeNode<PangaeaNoteExt> node) {
                PangaeaNoteExt n = node.get();
                n = n.copy();
                return tree.nodeOf(n);
            }
        });
        tree.root().set(
                tree.nodeOf(PangaeaNoteExt.of(this.frame.service().newDocument()))
        );
        tree.selection().onChange(
                x -> {
                    AppTreeNode<PangaeaNoteExt> n = tree.selection().get();
                    fireOnSelectedNote(n == null ? null : n.get());
                }
        );
        tree.itemRenderer().set(new SimpleDefaultTreeCellRendererImpl(frame));
        treePopupMenu = new ContextMenu("TreeContextMenu",Str.i18n("TreeContextMenu"),app);
        tree.contextMenu().set(treePopupMenu);
        treePopupMenu.children().add(new Button("AddChildNote", () -> frame.addNote(), app));
        treePopupMenu.children().add(new Button("AddNoteBefore", () -> frame.addNoteBefore(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("AddNoteAfter", () -> frame.addNodeAfter(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("DuplicateNote", () -> frame.duplicateNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("RenameNote", () -> frame.renameNote(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("StrikeNote", () -> frame.strikeThroughNote(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("BoldNote", () -> frame.boldNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("Import.Any", () -> frame.importFileInto(), app), Path.of("/Import/*"));
        treePopupMenu.children().add(new Button("Import.PangaeaNote", ()
                -> frame.importFileInto(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString()),
                 app), Path.of("/Import/*"));

        for (PangaeaNoteFileImporter fileImporter : frame.service().getFileImporters()) {
            treePopupMenu.children().add(new Button("Import." + fileImporter.getName(), ()
                    -> frame.importFileInto(fileImporter.getSupportedFileExtensions()),
                     app), Path.of("/Import/*"));
        }
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("DeleteNote", () -> frame.deleteSelectedNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("SearchNote", () -> frame.searchNote(), app));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("NoteProperties", () -> frame.editNote(), app).with(enableIfSelection));
        PangaeaNoteExt sn = getSelectedNote();
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

    public void fireNoteChanged(PangaeaNoteExt note) {
        updateTree();
    }

    public void fireOnSelectedNote(PangaeaNoteExt note) {
        for (ObservableNoteSelectionListener listener : listeners) {
            listener.onSelectionChanged(note);
        }
    }

    public PangaeaNoteExt getDocument() {
        return (PangaeaNoteExt) tree.root().get().get();
    }

    public void updateTree() {
    }

    public PangaeaNoteExt getSelectedNoteOrDocument() {
        AppTreeNode<PangaeaNoteExt> p = tree.selection().get();
        if (p != null) {
            PangaeaNoteExt c = p.get();
            if (c != null) {
                return c;
            }
        }
        return tree.root().get().get();
    }

    public PangaeaNoteExt getSelectedNote() {
        AppTreeNode<PangaeaNoteExt> p = tree.selection().get();
        if (p != null) {
            PangaeaNoteExt c = p.get();
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public void setSelectedNote(AppTreeNode<PangaeaNoteExt> sel) {
        tree.selection().set(sel);
    }

    public void setSelectedNote(PangaeaNoteExt note) {
        List<PangaeaNoteExt> elems = new ArrayList<>();
        while (note != null) {
            elems.add(0, note);
            note = note.getParent();
        }
        if (elems.isEmpty()) {
            elems.add(getDocument());
        }
        tree.selection().set(tree.findNode(elems.toArray()));
    }

    public void setDocumentNote(PangaeaNoteExt e) {
        tree.root().set(tree.nodeOf(e));
    }

    public AppTreeNode<PangaeaNoteExt> addNodeChild(AppTreeNode<PangaeaNoteExt> parent, Object child, int index) {
        if (child instanceof TreeNode) {
            child = ((TreeNode<?>) child).get();
        }
        PangaeaNoteExt nc = null;
        if (child instanceof PangaeaNoteExt) {
            nc = (PangaeaNoteExt) child;
        } else if (child instanceof String) {
            PangaeaNote nc0 = frame.service().createNoteFromSnippet(child);
            if (nc0 != null) {
                nc = PangaeaNoteExt.of(nc0);
            }
        }
        if (nc != null) {
            if (index < 0) {
                index = parent.get().getChildren().size();
            }
            parent.get().addChild(index, nc);
            TreeNode<PangaeaNoteExt> v = tree.nodeOf(nc);
            parent.children().add(index, v);
            return v;
        }
        return null;
    }

    public void removeNodeChild(AppTreeNode<PangaeaNoteExt> node) {
        AppTreeNode<PangaeaNoteExt> p = node.parent().get();
        if (p != null) {
            int index = p.children().findFirstIndexOf(node);
            if (index >= 0) {
                removeNodeChild(p, index);
                if (node == tree().selection().get()) {
                    tree().selection().set(null);
                }
            }
        }
    }

    public void dumpAll() {
        dump(tree.root().get().get(),"[PangaeaNote    ] ");
        dump(tree.root().get()      ,"[PangaeaTreeNote] ");
        dump(((JTree)(tree.peer().toolkitComponent())).getModel(),((JTree)(tree.peer().toolkitComponent())).getModel().getRoot(),"[JTree          ] ");
    }

    public void removeNodeChild(AppTreeNode<PangaeaNoteExt> parent, int childIndex) {
//        dumpAll();
        parent.children().removeAt(childIndex);
        parent.get().removeChild(childIndex);
    }

    private void dump(TreeModel m, Object a,String prefix){
        AppTreeNode<PangaeaNoteExt> n=(AppTreeNode<PangaeaNoteExt>)a;
        System.out.println(prefix+""+n.get().getName());
        int s = m.getChildCount(a);
        for (int i = 0; i < s; i++) {
            dump(m,m.getChild(a,i),   prefix+"\t");
        }
    }

    private void dump(AppTreeNode<PangaeaNoteExt> e,String prefix){
        System.out.println(prefix+""+e.get().getName());
        for (WritableIndexedNode<PangaeaNoteExt> child : e.children()) {
            dump((AppTreeNode<PangaeaNoteExt>) child,prefix+"\t");
        }
    }
    private void dump(PangaeaNoteExt e,String prefix){
        System.out.println(prefix+""+e.getName());
        for (PangaeaNoteExt child : e.getChildren()) {
            dump(child,prefix+"\t");
        }
    }

    public Tree<PangaeaNoteExt> tree() {
        return tree;
    }

    public EnableOnAnyTreeSelection getEnableIfSelection() {
        return enableIfSelection;
    }

    private class EnableOnAnyTreeSelection implements Consumer<Button> {

        ObservableBoolean notNull = tree.selection().isNotNull();

        @Override
        public void accept(Button b) {
            b.enabled().bindSource(notNull);
            b.visible().set(!app.hideDisabled().get() || notNull.get());

            notNull.onChange(e -> {
                b.visible().set(
                        !app.hideDisabled().get() || notNull.get()
                );
            });
//            b.visible().set(
//                    !app.hideDisabled().get() || notNull
//            );
        }
    }
}
