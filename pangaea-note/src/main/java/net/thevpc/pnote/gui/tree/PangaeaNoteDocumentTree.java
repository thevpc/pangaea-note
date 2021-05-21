/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.tree;

import net.thevpc.common.i18n.Str;
import net.thevpc.common.props.ObservableBoolean;
import net.thevpc.common.props.Path;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppTreeItemContext;
import net.thevpc.echo.api.components.AppTreeItemRenderer;
import net.thevpc.echo.api.components.AppTreeNode;
import net.thevpc.echo.impl.TreeNode;
import net.thevpc.echo.model.AppTreeMutator;
import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.api.model.ObservableNoteSelectionListener;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author vpc
 */
public class PangaeaNoteDocumentTree extends BorderPane {

    private final EnableOnAnyTreeSelection enableIfSelection;
    Tree<PangaeaNoteExt> tree;
    Application app;
    //    List<TreeAction> actions = new ArrayList<>();
    private ContextMenu treePopupMenu;
    private List<ObservableNoteSelectionListener> listeners = new ArrayList<>();
    private PangaeaNoteFrame frame;
    //    private ObservableNoteTreeModel model;

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
                    parent.get().addChild(index, nc);
                    parent.children().add(index, tree.nodeOf(nc));
                }
            }

            @Override
            public void removeChild(AppTreeNode<PangaeaNoteExt> parent, int childIndex) {
                parent.get().removeChild(childIndex);
                parent.children().removeAt(childIndex);
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
//        tree.setDragEnabled(true);
//        tree.setDropMode(DropMode.ON_OR_INSERT);
//        tree.setTransferHandler(new TreeTransferHandler(PangaeaNoteExt.class, model));
//        tree.addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent e) {
//                TreePath p = e.getNewLeadSelectionPath();
//                if (p == null) {
//                    fireOnSelectedNote(null);
//                } else {
//                    fireOnSelectedNote((PangaeaNoteExt) p.getLastPathComponent());
//                }
//
//            }
//        });
        tree.selection().onChange(
                x -> {
                    AppTreeNode<PangaeaNoteExt> n = tree.selection().get();
                    fireOnSelectedNote(n == null ? null : n.get());
                }
        );
//        UIPlafManager.getCurrentManager().addListener((p) -> tree.setCellRenderer(new SimpleDefaultTreeCellRendererImpl(win)));

        tree.itemRenderer().set(new SimpleDefaultTreeCellRendererImpl(frame));

//        tree.addMouseListener(new MouseListener() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
//                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
//                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
//                    if (selRow != -1) {
//                        PangaeaNoteExt selectedNote = ((PangaeaNoteExt) selPath.getLastPathComponent());
//                        tree.setSelectionPath(selPath);
//                    } else {
//                        tree.clearSelection();
////                        tree.setSelectionPath(
////                                null
//////                                new TreePath(tree.getModel().getRoot())
////                        );
//                    }
//                    if (tree.isShowing()) {
//                        treePopupMenu.show(tree, e.getX(), e.getY());
//                    }
//                } else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
//                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
//                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
//                    if (selRow != -1) {
//                        PangaeaNoteExt selectedNote = ((PangaeaNoteExt) selPath.getLastPathComponent());
//                        tree.setSelectionPath(selPath);
//                    } else {
//                        tree.clearSelection();
////                        tree.setSelectionPath(
////                                null
//////                                new TreePath(tree.getModel().getRoot())
////                        );
//                    }
//                }
//            }
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
//
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//
//            }
//        });
        treePopupMenu = new ContextMenu(app);
//        tree.addPropertyChangeListener("UI", (p) -> SwingUtilities.updateComponentTreeUI(treePopupMenu));
//        tree.setComponentPopupMenu(treePopupMenu);
        tree.contextMenu().set(treePopupMenu);
        treePopupMenu.children().add(new Button("AddChildNote", () -> frame.addNote(), app));
        treePopupMenu.children().add(new Button("AddNoteBefore", () -> frame.addNoteBefore(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("AddNoteAfter", () -> frame.addNodeAfter(), app).with(enableIfSelection));
        treePopupMenu.children().add(new Button("DuplicateNote", () -> frame.duplicateNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("RenameNote", () -> frame.renameNote(), app).with(enableIfSelection));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("Import.Any", () -> frame.importFileInto(), app), Path.of("/Import/*"));
        treePopupMenu.children().add(new Button("Import.PangaeaNote", () ->
                frame.importFileInto(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString())
                , app), Path.of("/Import/*"));

        for (PangaeaNoteFileImporter fileImporter : frame.service().getFileImporters()) {
            treePopupMenu.children().add(new Button("Import." + fileImporter.getName(), () ->
                    frame.importFileInto(fileImporter.getSupportedFileExtensions())
                    , app), Path.of("/Import/*"));
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
        //model.nodeChanged(note);
//        for (TreeAction action : actions) {
//            action.onSelectedNote(note);
//        }
        for (ObservableNoteSelectionListener listener : listeners) {
            listener.onSelectionChanged(note);
        }
    }

    public PangaeaNoteExt getDocument() {
        return (PangaeaNoteExt) tree.root().get().get();
    }

    //    protected Icon resolveIcon(String name) {
//        if (name == null || name.length() == 0) {
//            return null;
//        }
//        return app.iconSet().icon(name).get();
//    }
    public void updateTree() {
//        model = new ObservableNoteTreeModel((PangaeaNoteExt) model.getRoot());
//        tree = new JTree(model);
//        TreePath o = tree.getSelectionPath();
//        model.treeStructureChanged();
//        tree.invalidate();
//        tree.revalidate();
//        tree.setSelectionPath(o);
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

    public void removeNodeChild(AppTreeNode<PangaeaNoteExt> parent, int childIndex) {
        parent.get().removeChild(childIndex);
        parent.children().removeAt(childIndex);
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
            b.visible().bindSource(notNull);
        }
    }
}
