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
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import net.thevpc.echo.api.components.AppMenu;
import net.thevpc.echo.api.components.AppTree;

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
            public void addChild(AppTreeNode<PangaeaNote> parent, Object child, int index) {
                addNodeChild(parent, child, index);
            }

            @Override
            public void removeChild(AppTreeNode<PangaeaNote> parent, int childIndex) {
                removeNodeChild(parent, childIndex);
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
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("RenameNote", () -> frame.renameNote(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("StrikeNote", () -> frame.strikeThroughNote(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("BoldNote", () -> frame.boldNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator();
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
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("DeleteNote", () -> frame.deleteSelectedNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("SearchNote", () -> frame.searchNote(), app));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("PrintNote", () -> frame.printNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator();
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

    public void fireNoteChanged(PangaeaNote note) {
        updateTree();
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
            note = app.getParent(note);
        }
        if (elems.isEmpty()) {
            elems.add(getDocument());
        }
        tree.selection().set(tree.findNode(elems.toArray()));
    }

    public void setDocumentNote(PangaeaNote e) {
        if(e.guiNode==null){
            e.guiNode=tree.nodeOf(e);
        }
        tree.root().set(e.guiNode);
    }

    public AppTreeNode<PangaeaNote> addNodeChild(AppTreeNode<PangaeaNote> parent, Object child, int index) {
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
            app.addChild(parent.get(), nc, index);
            TreeNode<PangaeaNote> v = app.treeNodeOf(nc);
            return v;
        }
        return null;
    }

    public void removeNodeChild(AppTreeNode<PangaeaNote> node) {
        PangaeaNote parentNote = node.parent().get().get();
        app().removeChildNote(parentNote,this.app.indexOfNote(parentNote, node.get()));
    }

    @Override
    public PangaeaNoteApp app() {
        return (PangaeaNoteApp)super.app();
    }
    

    public void dumpAll() {
        dump(tree.root().get().get(), "[PangaeaNote    ] ");
        dump(tree.root().get(), "[PangaeaTreeNote] ");
        dump(((JTree) (tree.peer().toolkitComponent())).getModel(), ((JTree) (tree.peer().toolkitComponent())).getModel().getRoot(), "[JTree          ] ");
    }

    public void removeNodeChild(AppTreeNode<PangaeaNote> parent, int childIndex) {
//        dumpAll();
        app.removeChildNote(parent.get(), childIndex);
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
