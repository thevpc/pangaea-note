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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DropMode;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import net.thevpc.common.swing.ExtensionFileChooserFilter;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.common.swing.tree.TreeTransferHandler;
import net.thevpc.common.swing.util.CancelException;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.core.swing.SwingApplicationsHelper;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.search.SearchDialog;
import net.thevpc.pnote.gui.tree.dialogs.EditNoteDialog;
import net.thevpc.pnote.gui.tree.dialogs.NewNoteDialog;
import net.thevpc.pnote.model.CypherInfo;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.ReturnType;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.model.ObservableNoteTreeModel;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.util.OtherUtils;
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
    private PangaeaNote lastSavedDocument;
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
                onAddChild();
                //
            }
        });
        treePopupMenu.add(new TreeAction("AddNoteBefore", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddChildBefore();
            }

            @Override
            protected void onSelectedNote(PangaeaNoteExt note) {
                requireSelectedNote(note);
            }
        });
        treePopupMenu.add(new TreeAction("AddNoteAfter", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddChildAfter();
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
                PangaeaNoteExt current = getSelectedNote();
                if (current != null) {
                    setSelectedNote(current.addDuplicate());
                    updateTree();
                }
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
                PangaeaNoteExt current = getSelectedNote();
                if (current == null) {
                    current = (PangaeaNoteExt) tree.getModel().getRoot();
                }
                importFileInto(current);
                updateTree();
            }
        });
        importCustomMenu.add(new TreeAction("ImportPangaeaNote", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                PangaeaNoteExt current = getSelectedNote();
                if (current == null) {
                    current = (PangaeaNoteExt) tree.getModel().getRoot();
                }
                importFileInto(current, PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT);
                updateTree();
            }
        });
        importCustomMenu.add(new TreeAction("ImportCherryTree", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                PangaeaNoteExt current = getSelectedNote();
                if (current == null) {
                    current = (PangaeaNoteExt) tree.getModel().getRoot();
                }
                importFileInto(current, "ctd");
                updateTree();
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
                PangaeaNoteExt n = getSelectedNote();
                if (n != null) {
                    n.delete();
                    updateTree();
                    setSelectedNote(null);
                }
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
                onSearch();
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
                onEditNote();
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

    public ReturnType trySaveChangesOrDiscard() {
        if (isModifiedDocument()) {
            String s = sapp.newDialog()
                    .setTitleId("Message.askSaveDocument")
                    .setContentTextId("Message.askSaveDocument")
                    .withYesNoButtons(c -> c.closeDialog(), c -> c.closeDialog())
                    .build().showDialog();
            
            
            if ("yes".equals(s)) {
                return saveDocument();
            } else if ("no".equals(s)) {
                //DISCARD
                return ReturnType.SUCCESS;
            } else {
                return ReturnType.CANCEL;
            }
        }
        return ReturnType.SUCCESS;
    }

    public ReturnType closeDocument(boolean discardChanges) {
        return openNode(PangaeaNote.newDocument(), discardChanges);
    }

    public ReturnType openNewDocument(boolean discardChanges) {
        return openNode(PangaeaNote.newDocument(), discardChanges);
    }

    private ReturnType openNode(PangaeaNote note, boolean discardChanges) {
        if (!discardChanges) {
            ReturnType s = trySaveChangesOrDiscard();
            if (s == ReturnType.CANCEL || s == ReturnType.FAIL) {
                return s;
            }
        }
        if (!PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT.equals(note.getContentType())) {
            throw new IllegalArgumentException("expected Document Note");
        }

        SwingUtilities3.invokeLater(() -> {
            model.setRoot(PangaeaNoteExt.of(note));
            snapshotDocument();
            sapp.onChangePath(note.getContent());
        });
        return ReturnType.SUCCESS;
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

    private void fireOnSelectedNote(PangaeaNoteExt note) {
        for (TreeAction action : actions) {
            action.onSelectedNote(note);
        }
        for (ObservableNoteSelectionListener listener : listeners) {
            listener.onSelectionChanged(note);
        }
    }

    public boolean isModifiedDocument() {
        PangaeaNote newDoc = getDocument().toNote();
        boolean mod = lastSavedDocument != null && !lastSavedDocument.equals(newDoc);
        if (mod) {
//            System.out.println("modified: " + newDoc + "\nexpected: " + lastSavedDocument);
        }
        return mod;
    }

    public void snapshotDocument() {
        lastSavedDocument = getDocument().toNote();
//        System.out.println("snapshotted:" + lastSavedDocument);
    }

    public ReturnType openDocument(File file, boolean discardChanges) {
        if (!discardChanges) {
            ReturnType s = trySaveChangesOrDiscard();
            if (s == ReturnType.CANCEL || s == ReturnType.FAIL) {
                return s;
            }
        }
        PangaeaNote n = null;
        try {
            n = sapp.service().loadDocument(file, sapp.wallet());
        } catch (CancelException ex) {
            return ReturnType.CANCEL;
        } catch (Exception ex) {
            sapp.showError(ex);
            return ReturnType.FAIL;
        }
        if (n.error == null) {
            openNode(n, true);
            return ReturnType.SUCCESS;
        } else {
            sapp.showError(n.error);
            return ReturnType.FAIL;
        }
    }

    public ReturnType importFileInto(PangaeaNoteExt current, String... preferred) {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(sapp.getValidLastOpenPath()));
        if (preferred.length == 0) {
            jfc.addChoosableFileFilter(sapp.createPangaeaDocumentSupportedFileFilter());
        }
        Set<String> preferredSet = new HashSet<>(Arrays.asList(preferred));
        if (preferredSet.isEmpty() || preferredSet.contains(PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT)) {
            jfc.addChoosableFileFilter(sapp.createPangaeaDocumentFileFilter());
        }
        if (preferredSet.isEmpty() || preferredSet.contains("ctd")) {
            jfc.addChoosableFileFilter(new ExtensionFileChooserFilter("ctd", sapp.app().i18n().getString("Message.ctdDocumentFileFilter")));
        }
        jfc.setAcceptAllFileFilterUsed(!preferredSet.isEmpty());
        if (jfc.showOpenDialog(sapp.frame()) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            sapp.setLastOpenPath(file.getPath());
            if (file.getName().endsWith("."+PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION)) {
                PangaeaNote n = sapp.service().loadDocument(file, sapp.wallet());
                for (PangaeaNote c : n.getChildren()) {
                    current.addChild(PangaeaNoteExt.of(c));
                }
            } else if (file.getName().endsWith(".ctd")) {
                PangaeaNote n = sapp.service().loadCherryTreeXmlFile(file);
                for (PangaeaNote c : n.getChildren()) {
                    current.addChild(PangaeaNoteExt.of(c));
                }
            }
            return ReturnType.CANCEL;
        } else {
            return ReturnType.CANCEL;
        }
    }

    public ReturnType reloadDocument(boolean discardChanges) {
        if (!discardChanges) {
            ReturnType s = trySaveChangesOrDiscard();
            if (s == ReturnType.CANCEL || s == ReturnType.FAIL) {
                return s;
            }
        }
        String c = getSelectedNote().getContent();
        if (c == null || c.length() == 0) {
            //openNewDocument(false);
            return ReturnType.CANCEL;
        } else {
            return openDocument(new File(c), true);
        }
    }

    public ReturnType openDocument(boolean discardChanges) {
        if (!discardChanges) {
            ReturnType s = trySaveChangesOrDiscard();
            if (s == ReturnType.CANCEL || s == ReturnType.FAIL) {
                return s;
            }
        }
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(sapp.getValidLastOpenPath()));
        jfc.addChoosableFileFilter(sapp.createPangaeaDocumentFileFilter());
        jfc.setAcceptAllFileFilterUsed(false);
        if (jfc.showOpenDialog(sapp.frame()) == JFileChooser.APPROVE_OPTION) {
            return openDocument(jfc.getSelectedFile(), true);
        } else {
            return ReturnType.CANCEL;
        }
    }

    public PangaeaNoteExt getDocument() {
        return (PangaeaNoteExt) model.getRoot();
    }

    public ReturnType saveAsDocument() {
        SecureJFileChooserImpl jfc = new SecureJFileChooserImpl(this);
        jfc.setCurrentDirectory(new File(sapp.getValidLastOpenPath()));
        jfc.addChoosableFileFilter(sapp.createPangaeaDocumentFileFilter());
        jfc.setAcceptAllFileFilterUsed(false);
        boolean doSecureDocument = false;
        if (getDocument().getCypherInfo() == null) {
            jfc.getSecureCheckbox().setSelected(false);
            jfc.getSecureCheckbox().setVisible(true);
            jfc.getSecureCheckbox().setText(sapp.app().i18n().getString("Message.secureDocument"));
            doSecureDocument = true;
        } else {
            jfc.getSecureCheckbox().setSelected(isSecureAlgo(getDocument().getCypherInfo().getAlgo()));
            jfc.getSecureCheckbox().setVisible(true);
            jfc.getSecureCheckbox().setText(sapp.app().i18n().getString("Message.secureDocument"));
            doSecureDocument = false;
        }
        if (jfc.showSaveDialog(sapp.frame()) == JFileChooser.APPROVE_OPTION) {
            sapp.setLastOpenPath(jfc.getSelectedFile().getPath());
            if (doSecureDocument && jfc.getSecureCheckbox().isSelected()) {
                getDocument().setCypherInfo(new CypherInfo(PangaeaNoteService.SECURE_ALGO, ""));
            }
            try {
                String canonicalPath = jfc.getSelectedFile().getCanonicalPath();
                if (!canonicalPath.endsWith("."+PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION) && !new File(canonicalPath).exists()) {
                    canonicalPath = canonicalPath + "."+PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION;
                }
                getDocument().setContent(canonicalPath);
                sapp.service().saveDocument(getDocument().toNote(), sapp.wallet());
                sapp.onChangePath(canonicalPath);
                snapshotDocument();
                sapp.config().addRecentFile(canonicalPath);
                sapp.saveConfig();
                return ReturnType.SUCCESS;
            } catch (Exception ex) {
                sapp.showError(ex);
                return ReturnType.FAIL;
            }
        }
        return ReturnType.CANCEL;
    }

    private boolean isSecureAlgo(String s) {
        return s != null && s.length() > 0;
    }

    public ReturnType saveDocument() {
        if (OtherUtils.isBlank(getDocument().getContent())) {
            return saveAsDocument();
        } else {
            try {
                sapp.onChangePath(getDocument().getContent());
                if (sapp.service().saveDocument(getDocument().toNote(), sapp.wallet())) {
                    snapshotDocument();
                }
                return ReturnType.SUCCESS;
            } catch (Exception ex) {
                sapp.showError(ex);
                return ReturnType.FAIL;
            }
        }
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

    public void onAddChildAfter() {
        NewNoteDialog a = new NewNoteDialog(sapp);
        PangaeaNote n = a.showDialog(sapp::showError);
        if (n != null) {
            PangaeaNoteExt current = getSelectedNote();
            if (current != null) {
                PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
                sapp.service().prepareChildForInsertion(current, cc);
                current.addAfterThis(cc);
                updateTree();
                setSelectedNote(cc);
            }
        }
    }

    public void onAddChildBefore() {
        NewNoteDialog a = new NewNoteDialog(sapp);
        PangaeaNote n = a.showDialog(sapp::showError);
        if (n != null) {
            PangaeaNoteExt current = getSelectedNote();
            if (current != null) {
                PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
                sapp.service().prepareChildForInsertion(current, cc);
                current.addBeforeThis(cc);
                updateTree();
                setSelectedNote(cc);
            }
        }
    }

    public void onAddChild() {
        NewNoteDialog a = new NewNoteDialog(sapp);
        PangaeaNote n = a.showDialog(sapp::showError);
        if (n != null) {
            PangaeaNoteExt current = getSelectedNoteOrDocument();
            PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
            sapp.service().prepareChildForInsertion(current, cc);
            current.addChild(cc);
            updateTree();
            setSelectedNote(cc);
        }
    }

    public void onEditNote() {
        PangaeaNote n = new EditNoteDialog(sapp, getSelectedNote()).showDialog();
        if (n != null) {
            tree.invalidate();
            tree.repaint();
            fireOnSelectedNote(getSelectedNote());
        }
    }

    public void onSearch() {
        SearchDialog dialog = new SearchDialog(sapp);
        dialog.showDialogAndSearch(getSelectedNoteOrDocument());
    }

}
