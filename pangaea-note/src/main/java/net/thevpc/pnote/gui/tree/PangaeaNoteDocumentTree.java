/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.tree;

import net.thevpc.pnote.model.ObservableNoteSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DropMode;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import net.thevpc.common.swing.tree.TreeTransferHandler;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.core.swing.SwingApplicationsHelper;
import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.model.ObservableNoteTreeModel;
import net.thevpc.pnote.types.pnodetembedded.PangaeaNoteEmbeddedService;
import net.thevpc.swing.plaf.UIPlafManager;

/**
 *
 * @author vpc
 */
public class PangaeaNoteDocumentTree extends JPanel {

    JTree tree;
    private JPopupMenu treePopupMenu;
    private List<ObservableNoteSelectionListener> listeners = new ArrayList<>();
    private PangaeaNoteGuiApp sapp;
    Application app;
    private ObservableNoteTreeModel model;
    List<TreeAction> actions = new ArrayList<>();

    public PangaeaNoteDocumentTree(PangaeaNoteGuiApp sapp) {
        super(new BorderLayout());
        this.sapp = sapp;
        this.app = sapp.app();
        model = new ObservableNoteTreeModel(new PangaeaNoteExt().newDocument());
        tree = new JTree(model);
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler(PangaeaNoteExt.class, model));
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath p = e.getNewLeadSelectionPath();
                if (p == null) {
                    fireOnSelectedNote(null);
                } else {
                    fireOnSelectedNote((PangaeaNoteExt) p.getLastPathComponent());
                }

            }
        });
        UIPlafManager.getCurrentManager().addListener((p) -> tree.setCellRenderer(new SimpleDefaultTreeCellRendererImpl(sapp)));
        tree.setCellRenderer(new SimpleDefaultTreeCellRendererImpl(sapp));
        tree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
//                    System.out.println("right click " + selRow + "  " + selPath);
                    if (selRow != -1) {
                        PangaeaNoteExt selectedNote = ((PangaeaNoteExt) selPath.getLastPathComponent());
                        tree.setSelectionPath(selPath);
                    } else {
                        tree.clearSelection();
//                        tree.setSelectionPath(
//                                null
////                                new TreePath(tree.getModel().getRoot())
//                        );
                    }
                    if (tree.isShowing()) {
                        treePopupMenu.show(tree, e.getX(), e.getY());
                    }
                } else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
//                    System.out.println("left click " + selRow + "  " + selPath);
                    if (selRow != -1) {
                        PangaeaNoteExt selectedNote = ((PangaeaNoteExt) selPath.getLastPathComponent());
                        tree.setSelectionPath(selPath);
                    } else {
                        tree.clearSelection();
//                        tree.setSelectionPath(
//                                null
////                                new TreePath(tree.getModel().getRoot())
//                        );
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        treePopupMenu = new JPopupMenu();
        tree.addPropertyChangeListener("UI", (p) -> SwingUtilities.updateComponentTreeUI(treePopupMenu));
//        tree.setComponentPopupMenu(treePopupMenu);
        treePopupMenu.add(new TreeAction("AddChildNote", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.addNote();
                //
            }
        });
        treePopupMenu.add(new TreeAction("AddNoteBefore", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.addNoteBefore();
            }

            @Override
            protected void onSelectedNote(PangaeaNoteExt note) {
                requireSelectedNote(note);
            }
        });
        treePopupMenu.add(new TreeAction("AddNoteAfter", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.addNodeAfter();
            }

            @Override
            protected void onSelectedNote(PangaeaNoteExt note) {
                requireSelectedNote(note);
            }
        });
//        JMenu addCustomMenu = new JMenu();
//        SwingApplicationsHelper.registerButton(addCustomMenu, "Action.AddCustom", "$Action.AddCustom", app);
//        treePopupMenu.add(addCustomMenu);
//        addCustomMenu.add(new TreeAction("AddTodayNote") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                NewNoteDialog a = new NewNoteDialog(sapp);
//                PangaeaNote n = a.showDialog(PangaeaNoteDocumentTree::showError);
//                if (n != null) {
//                    //
//                }
//                //
//            }
//        });
        treePopupMenu.add(new TreeAction("DuplicateNote", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.duplicateNote();
            }

            @Override
            protected void onSelectedNote(PangaeaNoteExt note) {
                requireSelectedNote(note);
            }
        });
        treePopupMenu.addSeparator();
        treePopupMenu.add(new TreeAction("RenameNote", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.renameNote();
                //
            }

            @Override
            protected void onSelectedNote(PangaeaNoteExt note) {
                requireSelectedNote(note);
            }
        });
        treePopupMenu.addSeparator();
        JMenu importCustomMenu = new JMenu();
        SwingApplicationsHelper.registerButton(importCustomMenu, "Action.Import", "$Action.Import.icon", app);
        treePopupMenu.add(importCustomMenu);
        importCustomMenu.add(new TreeAction("ImportAny", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.importFileInto();
            }
        });
        importCustomMenu.add(new TreeAction("ImportPangaeaNote", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.importFileInto(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT);
            }
        });
        importCustomMenu.add(new TreeAction("ImportCherryTree", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.importFileInto("ctd");
            }

        });
//        JMenu exportMenu = new JMenu();
//        SwingApplicationsHelper.registerButton(exportMenu, "Action.Export", "$Action.Export.icon", app);
//        treePopupMenu.add(exportMenu);
//        exportMenu.add(new TreeAction("ExportPangaeaNote", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//
//            @Override
//            protected void onSelectedNote(PangaeaNoteExt note) {
//                requireSelectedNote(note);
//            }
//
//        });
//        exportMenu.add(new TreeAction("ExportCherryTree", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//
//            @Override
//            protected void onSelectedNote(PangaeaNoteExt note) {
//                requireSelectedNote(note);
//            }
//
//        });
        treePopupMenu.addSeparator();
        treePopupMenu.add(new TreeAction("DeleteNote", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.deleteSelectedNote();
            }

            @Override
            protected void onSelectedNote(PangaeaNoteExt note) {
                requireSelectedNote(note);
            }

        });
//        treePopupMenu.addSeparator();
//        JMenu moveMenu = new JMenu();
//        SwingApplicationsHelper.registerButton(moveMenu, "Action.Move", "$Action.Move.icon", app);
//        treePopupMenu.add(moveMenu);
//        moveMenu.add(new TreeAction("MoveUp", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//
//            @Override
//            protected void onSelectedNote(PangaeaNoteExt note) {
//                requireSelectedNote(note);
//            }
//
//        });
//        moveMenu.add(new TreeAction("MoveDown", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//
//            @Override
//            protected void onSelectedNote(PangaeaNoteExt note) {
//                requireSelectedNote(note);
//            }
//
//        });
//        moveMenu.add(new TreeAction("MoveLeft", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//
//            @Override
//            protected void onSelectedNote(PangaeaNoteExt note) {
//                requireSelectedNote(note);
//            }
//
//        });
//        moveMenu.add(new TreeAction("MoveRight", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//
//            @Override
//            protected void onSelectedNote(PangaeaNoteExt note) {
//                requireSelectedNote(note);
//            }
//
//        });
//        moveMenu.add(new TreeAction("SortNoteAsc", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//        });
//        moveMenu.add(new TreeAction("SortNoteDesc", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//        });

        treePopupMenu.addSeparator();
        treePopupMenu.add(new TreeAction("SearchNote", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.searchNote();
            }

            @Override
            protected void onSelectedNote(PangaeaNoteExt note) {

            }
        });
//        treePopupMenu.add(new TreeAction("SearchAndReplaceNote", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //
//            }
//
//            @Override
//            protected void onSelectedNote(PangaeaNoteExt note) {
//                requireSelectedNote(note);
//            }
//        });
        treePopupMenu.addSeparator();
        treePopupMenu.add(new TreeAction("NoteProperties", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                sapp.editNote();
            }

            @Override
            protected void onSelectedNote(PangaeaNoteExt note) {
                requireSelectedNote(note);
            }

        });

        PangaeaNoteExt sn = getSelectedNote();
        for (TreeAction action : actions) {
            action.onSelectedNote(sn);
        }
        add(new JScrollPane(tree));
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
        TreePath tPath = new TreePath(elems.toArray());
        tree.setSelectionPath(tPath);
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
        for (TreeAction action : actions) {
            action.onSelectedNote(note);
        }
        for (ObservableNoteSelectionListener listener : listeners) {
            listener.onSelectionChanged(note);
        }
    }

    public PangaeaNoteExt getDocument() {
        return (PangaeaNoteExt) model.getRoot();
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
        TreePath o = tree.getSelectionPath();
        model.treeStructureChanged();
        tree.invalidate();
        tree.revalidate();
        tree.setSelectionPath(o);
    }

    public PangaeaNoteExt getSelectedNoteOrDocument() {
        TreePath p = tree.getSelectionPath();
        if (p != null) {
            PangaeaNoteExt c = (PangaeaNoteExt) p.getLastPathComponent();
            if (c != null) {
                return c;
            }
        }
        return (PangaeaNoteExt) tree.getModel().getRoot();
    }

    public PangaeaNoteExt getSelectedNote() {
        TreePath p = tree.getSelectionPath();
        if (p != null) {
            PangaeaNoteExt c = (PangaeaNoteExt) p.getLastPathComponent();
            if (c != null && c != tree.getModel().getRoot()) {
                return c;
            }
        }
        return null;
    }

    public void setDocumentNote(PangaeaNoteExt e) {
        model.setRoot(e);
    }

}
