/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import net.thevpc.common.i18n.I18n;
import net.thevpc.common.i18n.Str;
import net.thevpc.common.props.*;
import net.thevpc.echo.FileFilter;
import net.thevpc.echo.*;
import net.thevpc.echo.api.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.api.components.AppMenu;
import net.thevpc.echo.api.components.AppTreeNode;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.echo.impl.Applications;
import net.thevpc.echo.impl.controls.ExtraControls;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsWorkspace;
import net.thevpc.pnote.PangaeaSplashScreen;
import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.model.*;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteDocumentInfo;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.gui.breadcrumb.PangaeaNodeBreadcrumb;
import net.thevpc.pnote.gui.dialogs.EditNoteDialog;
import net.thevpc.pnote.gui.dialogs.EnterNewPasswordDialog;
import net.thevpc.pnote.gui.dialogs.EnterPasswordDialog;
import net.thevpc.pnote.gui.dialogs.NewNoteDialog;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.UnsupportedViewerComponent;
import net.thevpc.pnote.gui.search.SearchDialog;
import net.thevpc.pnote.gui.search.SearchResultPanel;
import net.thevpc.pnote.gui.tree.PangaeaNoteDocumentTree;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.PangaeaNoteExtSearchResult;
import net.thevpc.pnote.service.search.SearchQuery;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.security.PasswordHandler;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author vpc
 */
public class PangaeaNoteFrame extends Frame {

    private static boolean DEV_MODE_UI = false;
    private static String DEFAULT_ICONSET = "svgrepo-color";
    private boolean splash;
    //    private RecentFilesMenu recentFilesMenu;
    private PangaeaNoteDocumentTree tree;
    private PangaeaNoteEditor noteEditor;
    private SearchResultPanel searchResultsTool;
    private List<String> recentSearchQueries = new ArrayList<>();
    private String currentFilePath;
    private long modificationsCount;
    private PangaeaNote lastSavedDocument;
    private WritableValue<SelectableElement> selectableElement = Props.of("selectableElement").valueOf(SelectableElement.class, null);
    private WritableBoolean mainFrame = Props.of("mainFrame").booleanOf(false);

    public PangaeaNoteFrame(String id,PangaeaNoteApp app, boolean splash) {
        super(id,app);
        this.splash = splash;

        prefSize().set(new Dimension(800, 600));
        mainFrame.onChange(x -> onChangeTitle());
        String ic0 = app.service().config().getIconSet();
        this.iconSet().set(ic0 == null ? DEFAULT_ICONSET : ic0);
        String loc0 = app.service().config().getLocale();
        this.locale().set(loc0 == null ? Locale.getDefault() : new Locale(loc0));
        this.iconSet().onChange(
                () -> {
                    PangaeaNoteExt d = getDocument();
                    PangaeaNoteDocumentInfo info = app().service().getDocumentInfo(d.toNote());
                    info.setIconSet(this.iconSet().get());
                    d.setContent(app().service().documentInfoToElement(info));
                    service().config().setIconSet(this.iconSet().get());
                    service().saveConfig();
                    app().iconSets().id().set(this.iconSet().get());
                }
        );
        this.locale().onChange(
                () -> {
                    PangaeaNoteExt d = getDocument();
                    PangaeaNoteDocumentInfo info = app().service().getDocumentInfo(d.toNote());
                    String loc = this.locale().get() == null ? null : this.locale().get().toString();
                    info.setLocale(loc);
                    d.setContent(app().service().documentInfoToElement(info));
                    service().config().setLocale(loc);
                    service().saveConfig();
                    app().i18n().locale().set(this.locale().get());
                }
        );
    }

    public NutsWorkspace getNutsWorkspace() {
        return app().getNutsWorkspace();
    }

    public NutsSession getNutsSession() {
        return app().getNutsSession();
    }

    public PangaeaNoteDocumentTree treePane() {
        return tree;
    }

    public SearchResultPanel searchResultsTool() {
        return searchResultsTool;
    }

    public PangaeaNoteService service() {
        return app().service();
    }

    public void resetModifications() {
        this.modificationsCount = 0;
        onChangePath(currentFilePath);
    }

    public void onChangePath(String newPath) {
        if (newPath == null || newPath.length() == 0) {
            this.currentFilePath = null;
        } else {
            service().addRecentFile(newPath);
            this.currentFilePath = newPath;
            service().setLastOpenPath(newPath);
            app().runUI(() -> {
                updateRecentFilesMenu();
            });
        }
        onChangeTitle();
    }

    public void onChangeTitle() {
        if (app().state().get() == AppState.NONE || app().state().get() == AppState.INIT) {
            return;
        }
        String modPrefix = "";//mainFrame().get() ? "(!!) " : "";
        String modSuffix = modificationsCount > 0 ? " (*)" : "";
        if (currentFilePath == null || currentFilePath.length() == 0) {
            this.title().set(Str.of(modPrefix + "Pangaea-Note v1.2 : " + "<" + app().i18n().getString("Message.noName") + ">" + modSuffix));
        } else {
            this.title().set(Str.of(modPrefix + "Pangaea-Note v1.2 : " + currentFilePath + modSuffix));
        }
    }

    public void bindConfig() {
        PangaeaNoteConfig config = service().config();
//        app().iconSets().onChange(x -> {
//            config.setIconSet(app().iconSets().id().get());
//            config.setIconSetSize(app().iconSets().config().get().getWidth());
//            saveConfig();
//        });
//        app().i18n().locale().onChange(x -> {
//            config.setLocale(((Locale) x.newValue()).toString());
//            saveConfig();
//        });
        this.displayMode().onChange(x -> {
            config.setDisplayMode(((FrameDisplayMode) x.newValue()));
            saveConfig();
        });
        this.toolBar().get().visible().onChange(x -> {
            config.setDisplayToolBar(((Boolean) x.newValue()));
            saveConfig();
        });
        this.statusBar().get().visible().onChange(x -> {
            config.setDisplayStatusBar(((Boolean) x.newValue()));
            saveConfig();
        });
//        recentFilesMenu.addFileSelectedListener(new FileSelectedListener() {
//            @Override
//            public void fileSelected(RecentFileEvent event) {
//                openDocument(new File(event.getFile()), false);
//            }
//        });
    }

    public void saveConfig() {
        service().saveConfig();
    }

    public void applyConfigToUI() {
        PangaeaNoteConfig config = service().config();
//        if (app().iconSets().containsKey(config.getIconSet())) {
//            app().iconSets().id().set(config.getIconSet());
//        }
//        if (config.getIconSetSize() > 3) {
//            app().iconSets().config().set(
//                    app().iconSets().config().get().setSize(config.getIconSetSize())
//            );
//        }
//        if (config.getLocale() != null && config.getLocale().length() > 0) {
//            app().i18n().locale().set(new Locale(config.getLocale()));
//        }
        if (config.getPlaf() != null && config.getPlaf().length() > 0) {
            app().toolkit().runUI(() -> app().plaf().set(config.getPlaf()));
        }
        if (config.getDisplayMode() != null) {
            this.displayMode().set(config.getDisplayMode());
        }
        if (config.getDisplayToolBar() != null) {
            this.toolBar().get().visible().set(config.getDisplayToolBar());
        }
        if (config.getDisplayStatusBar() != null) {
            this.statusBar().get().visible().set(config.getDisplayStatusBar());
        }
        updateRecentFilesMenu();
    }

    public void run() {
//        AppEditorThemes editorThemes = new AppEditorThemes();
        ticSplash();
        this.addDefaultMenus();
        this.smallIcon().set(new Image(
                getClass().getResource("/net/thevpc/pnote/icon.png"), app()
        ));
        this.content().set(new DockPane(app()));
//                f.toolBar().set(new ToolBarGroup(app));
//                f.statusBar().set(new ToolBarGroup(app));
//        app.components().addFolder(pathOf( "/menuBar/File"));
//        app.components().addFolder(pathOf( "/menuBar/Edit"));
//        app.components().addFolder(pathOf( "/menuBar/View"));
//        app.components().addFolder(pathOf( "/menuBar/Help"));
        app().components().addFolder(pathOf("/toolBar/Default"));
        app().components().addFolder(pathOf("/statusBar/Default"));

        ticSplash();

        this.open();
        ticSplash();
        this.centerOnDefaultMonitor();
        ticSplash();
        onChangeTitle();
        this.smallIcon().set(
                Image.of(getClass().getResource("/net/thevpc/pnote/icon.png"), app())
        );

        DockPane ws = workspace();
        ticSplash();

        tree = new PangaeaNoteDocumentTree(this);
        noteEditor = new PangaeaNoteEditor(this, false);
        ticSplash();
        tree.addNoteSelectionListener(n -> {
            try {
                noteEditor.setNote(n);
            } catch (Exception ex) {
                ex.printStackTrace();
                app().errors().add(ex);
            }
        });
        noteEditor.addListener(n -> tree.setSelectedNote(n));
        searchResultsTool = new SearchResultPanel(this);
//        PangaeaNoteDocumentTree favourites = new PangaeaNoteDocumentTree(this);
//        PangaeaNoteDocumentTree openFiles = new PangaeaNoteDocumentTree(app);
        ticSplash();
        tree.anchor().set(Anchor.LEFT);
        searchResultsTool.anchor().set(Anchor.BOTTOM);
        ws.children().add(tree);
        if (DEV_MODE_UI) {
//            ws.children().add(new Window(
//                    "Tools.UI", Str.of("Tools.UI"), Anchor.RIGHT, app.toolkit().createComponent(new UIPropsTool())
//            ));
        }
        ws.children().add(searchResultsTool);

        ticSplash();
        ws.children().add(noteEditor);

        ticSplash();
        ticSplash();
        ticSplash();
        app().components().add(ExtraControls.createDateLabel(app(), "yyy-MM-dd HH:mm:ss"), pathOf("/statusBar/Default/calendar"));
        ticSplash();
        app().components().addSeparator(pathOf("/statusBar/Default/*"));
        app().components().add(ExtraControls.createMemoryMonitorLabel(app()), pathOf("/statusBar/Default/calendar"));
        ticSplash();

        app().components().addFolder(pathOf("/menuBar/File"));

        app().components().addMulti(
                new Button("NewFile", this::closeDocumentNoDiscard, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyEvent.VK_N);
                            b.accelerator().set("control N");
                        }),
                pathOf("/menuBar/File/*"), pathOf("/toolBar/Default/NewFile"));

//        newfileAction.mnemonic().set(KeyEvent.VK_N);
//        newfileAction.accelerator().set("control N");
        ticSplash();
        Menu f = (Menu) app().components().get(pathOf("/menuBar/File"));
        f.mnemonic().set(KeyEvent.VK_F);

        app().components().add(new Button("OpenLastFile", this::openLastDiscardNoDiscard, app())
                        .with((Button b) -> {
                            b.accelerator().set("control shift O");
                        }),
                pathOf("/menuBar/File/*"));

        app().components().add(new Button("Open", this::openDocumentNoDiscard, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyEvent.VK_O);
                            b.accelerator().set("control O");
                        }),
                pathOf("/menuBar/File/*"));

        app().components().add(new Button("Reload", this::reloadDocumentNoDiscard, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyEvent.VK_R);
                            b.accelerator().set("control R");
                        }),
                pathOf("/menuBar/File/*"));

        ticSplash();
        app().components().addSeparator(pathOf("/menuBar/File/*"));
        updateRecentFilesMenu();

        app().components().addSeparator(pathOf("/menuBar/File/*"));
        ticSplash();

        app().components().add(new Button("Save", this::saveDocument, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyEvent.VK_S);
                            b.accelerator().set("control S");
                        }),
                pathOf("/menuBar/File/*"));

        ticSplash();

        app().components().add(new Button("SaveAs", this::saveAsDocument, app()), pathOf("/menuBar/File/*"));

        ticSplash();
        app().components().addSeparator(pathOf("/menuBar/File/*"));
        ticSplash();

        app().components().add(new Button("NewWindow", () -> app().newFrame(), app()), pathOf("/menuBar/File/*"));

        app().components().addSeparator(pathOf("/menuBar/File/*"));

        app().components().add(new Button("CloseDocument", this::closeDocumentNoDiscard, app()), pathOf("/menuBar/File/*"));
        app().components().add(new Button("CloseWindow", this::close, app()), pathOf("/menuBar/File/*"));

        app().components().add(new Button("Exit", this::exitApp, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyEvent.VK_X);
                            b.accelerator().set("control X");
                        }),
                pathOf("/menuBar/File/*")
        );

        app().components().addFolder(pathOf("/menuBar/Edit"));
        app().components().addSeparator(pathOf("/toolBar/Default/Separator1"));
        app().components().addMulti(new Button("AddNote", this::addNoteSafe, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyEvent.VK_B);
                            b.accelerator().set("control B");
                        }),
                pathOf("/menuBar/Edit/AddNote"), pathOf("/toolBar/Default/*")
        );

        app().components().addSeparator(pathOf("/menuBar/Edit/Separator1"));
        app().components().addMulti(new Button("Search", this::searchNote, app())
                        .with((Button b)
                                        -> {
                                    b.mnemonic().set(KeyEvent.VK_F);
                                    b.accelerator().set("control shift F");

                                }
                        ),
                pathOf("/menuBar/Edit/Search"), pathOf("/toolBar/Default/*"));

        app().components().addFolder(pathOf("/menuBar/Help"));
        app().components().add(new Button("About", () -> app().showAbout(), app()),
                pathOf("/menuBar/Help/*"));

        app().components().add(new Spacer(app()).expandX(), pathOf("/toolBar/Default/*"));

        app().components().add(new PangaeaNodeBreadcrumb(PangaeaNoteFrame.this),
                pathOf("/toolBar/Default/*")
        );

        ticSplash();
        treePane().active().set(true);
        applyConfigToUI();
        ticSplash();
        bindConfig();
        ticSplash();
        closeDocumentNoDiscard();
//        folders.openDocument(service().createSampleDocumentNote());
        ticSplash();
        closeSplash();
//        frame().getRootPane().registerKeyboardAction(
//                (e) -> {
//                    AppFrame w = appWindow().get();
//                    FrameDisplayMode dm = w.displayMode().get();
//                    if (dm == null) {
//                        dm = FrameDisplayMode.NORMAL;
//                    }
//                    switch (dm) {
//                        case NORMAL: {
//                            w.displayMode().set(FrameDisplayMode.FULLSCREEN);
//                            break;
//                        }
//                        default: {
//                            w.displayMode().set(FrameDisplayMode.NORMAL);
//                            break;
//                        }
//                    }
//                },
//                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_MASK + KeyEvent.SHIFT_MASK),
//                JComponent.WHEN_IN_FOCUSED_WINDOW
//        );
//        frame().getRootPane().registerKeyboardAction(
//                (e) -> {
//                    tree.addNote();
//                },
//                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK),
//                JComponent.WHEN_IN_FOCUSED_WINDOW
//        );
        app().plaf().onChange(event -> {
            AppUIPlaf plaf = app().toolkit().getPlaf(event.newValue());
            if (plaf != null) {
                if (plaf.isDark()) {
                    String iconsetId = iconSet().get();
                    if (iconsetId != null) {
                        switch (iconsetId) {
                            case "feather-black": {
                                iconSet().set("feather-white");
                                break;
                            }
                            case "feather-blue": {
                                iconSet().set("feather-yellow");
                                break;
                            }
                        }
                    }
                } else if (plaf.isLight()) {
                    String iconsetId = iconSet().get();
                    if (iconsetId != null) {
                        switch (iconsetId) {
                            case "feather-white": {
                                iconSet().set("feather-black");
                                break;
                            }
                            case "feather-yellow": {
                                iconSet().set("feather-blue");
                                break;
                            }
                        }
                    }
                }
            }
        });
//        app.waitFor();
    }

    private ReturnType reloadDocumentNoDiscard() {
        return reloadDocument(false);
    }

    private ReturnType openDocumentNoDiscard() {
        return openDocument(false);
    }

    private ReturnType openLastDiscardNoDiscard() {
        return openLastDocument(false);
    }

    private void addNoteSafe() {
        try {
            addNote();
        } catch (Exception ex) {
            app().errors().add(ex);
        }
    }

    private ReturnType closeDocumentNoDiscard() {
        return openNewDocument(false);
    }

    private void exitApp() {
        if (isModifiedDocument()) {
            saveDocument();
        }
        app().shutdown();
    }

    private DockPane workspace() {
        return (DockPane) this.content().get();
    }

    protected void updateRecentFilesMenu() {
        AppMenu a = (AppMenu) app().components().addFolder(pathOf("/menuBar/File/LoadRecent"));
        a.text().set(Str.i18n("Action.LoadRecent"));
        List<String> all = service().config().getRecentFiles();
        ObservableList<AppComponent> ac = a.children();
        while (ac.size() > 0) {
            int before = ac.size();
            a.children().remove(Path.of(ac.get(0).path().get().name()));
            int after = ac.size();
            if (after >= before) {
                //error
                return;
            }
        }
        int index = 0;
        for (String filePath : all) {
            Button t = new Button(
                    null, () -> {
                ReturnType returnType = openDocument(new File(filePath), false);
                if (returnType == ReturnType.FAIL) {
                    List<String> all2 = service().config().getRecentFiles();
                    if(all2.remove(filePath)){
                        service().config().setRecentFiles(all2);
                        service().saveConfig();
                    }
                    updateRecentFilesMenu();
                }
            }, app()
            );
            t.text().set(Str.of(filePath));
            //t.smallIcon().set((AppImage) null);
            app().components().add(t, pathOf("/menuBar/File/LoadRecent/" + (index + 1)));
        }
    }

    protected void ticSplash() {
        if (splash) {
            PangaeaSplashScreen.get().tic();
        }
    }

    public PangaeaNoteEditor noteEditor() {
        return noteEditor;
    }

    public PangaeaNoteApp app() {
        return (PangaeaNoteApp) super.app();
    }

    public PasswordHandler wallet() {
        return new PasswordHandler() {
            @Override
            public String askForSavePassword(String path, String root) {
                String enteredPassword = app().getOpenWallet().get(root, path);
                if (enteredPassword != null) {
                    return enteredPassword;
                }
                EnterNewPasswordDialog d = new EnterNewPasswordDialog(PangaeaNoteFrame.this, path, this);
                enteredPassword = d.showDialog();
                app().getOpenWallet().store(root, path, enteredPassword);
                return enteredPassword;
            }

            @Override
            public String askForLoadPassword(String path, String root) {
                String enteredPassword = app().getOpenWallet().get(root, path);
                if (enteredPassword != null) {
                    return enteredPassword;
                }
                EnterPasswordDialog d = new EnterPasswordDialog(PangaeaNoteFrame.this, path, this);
                enteredPassword = d.showDialog();
                app().getOpenWallet().store(root, path, enteredPassword);
                return enteredPassword;
            }

            @Override
            public boolean reTypePasswordOnError() {
                app().getOpenWallet().clear();
                return "yes".equals(
                        new Alert(app())
                                .with((Alert a) -> {
                                    a.title().set(Str.i18n("Message.invalidPassword.askRetype"));
                                    a.headerText().set(Str.i18n("Message.invalidPassword.askRetype"));
                                    a.headerIcon().set(Str.of("error"));
                                })
                                .setContentText(Str.i18n("Message.invalidPassword.askRetype"))
                                .withYesNoButtons()
                                .showDialog(null));
            }
        };
    }

    public PangaeaNoteConfig config() {
        return service().config();
    }

    public List<String> getRecentSearchQueries() {
        return recentSearchQueries;
    }

    public void addRecentSearchQuery(String query) {
        if (query != null && query.trim().length() > 0) {
            recentSearchQueries.add(0, query.trim());
            recentSearchQueries = new ArrayList<>(new LinkedHashSet<String>(recentSearchQueries));
        }
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public FileFilter createPangaeaDocumentSupportedFileFilter() {
        return new FileFilter(
                Str.i18n("Message.pnoteSupportedFileFilters"),
                "*.pnote", "*.cdt", "*.csv"
        );
    }

    public FileFilter createPangaeaDocumentFileFilter() {
        return new FileFilter(
                Str.i18n("Message.pnoteDocumentFileFilter"),
                "*.pnote"
        );
    }

    public PangaeaNoteExt getDocument() {
        return treePane().getDocument();
    }

    public boolean isModifiedDocument() {
        PangaeaNote newDoc = getDocument().toNote();
        boolean mod = lastSavedDocument != null && !lastSavedDocument.equals(newDoc);
        if (mod) {
        }
        return mod;
    }

    public void snapshotDocument() {
        lastSavedDocument = tree.getDocument().toNote();
        resetModifications();
    }

    public ReturnType trySaveChangesOrDiscard() {
        if (isModifiedDocument()) {
            String s = new Alert(app())
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.askSaveDocument"));
                        a.headerText().set(Str.i18n("Message.askSaveDocument"));
                        a.headerIcon().set(Str.of("save"));
                    })
                    .setContentText(Str.i18n("Message.askSaveDocument"))
                    .withYesNoButtons()
                    .showDialog(null);

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
        return openNode(service().newDocument(), discardChanges);
    }

    public ReturnType openNewDocument(boolean discardChanges) {
        return openNode(service().newDocument(), discardChanges);
    }

    private ReturnType openNode(PangaeaNote note, boolean discardChanges) {
        if (!discardChanges) {
            ReturnType s = trySaveChangesOrDiscard();
            if (s == ReturnType.CANCEL || s == ReturnType.FAIL) {
                return s;
            }
        }
        if (!PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(note.getContentType())) {
            throw new IllegalArgumentException("expected Document Note");
        }
        if (tree.getDocument() != null && tree.getDocument().getContent() != null) {
            PangaeaNoteDocumentInfo oldInfo = service().getDocumentInfo(tree.getDocument().toNote());
            String oldPath = oldInfo.getPath();
            app().getOpenWallet().clear(oldPath);
        }
        app().toolkit().runUI(() -> {
            treePane().setDocumentNote(PangaeaNoteExt.of(note));
            snapshotDocument();
            onChangePath(
                    ((PangaeaNoteEmbeddedService) service().getContentTypeService(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT))
                            .getContentValueAsInfo(note.getContent()).getPath()
            );
        });
        return ReturnType.SUCCESS;
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
            n = service().loadDocument(file, wallet());
        } catch (CancelException ex) {
            return ReturnType.CANCEL;
        } catch (Exception ex) {
            app().errors().add(ex);
            return ReturnType.FAIL;
        }
        if (n.error == null) {
            PangaeaNoteDocumentInfo info = service().getDocumentInfo(n);
            String loc = info.getLocale();
            if (loc == null) {
                loc = service().config().getLocale();
            }
            if (loc == null) {
                loc = Locale.getDefault().toString();
            }
            locale().set(new Locale(loc));
            String iconSet = info.getIconSet();
            if (iconSet == null) {
                iconSet = service().config().getIconSet();
            }
            if (iconSet == null) {
                iconSet = DEFAULT_ICONSET;
            }
            iconSet().set(iconSet);

            openNode(n, true);
            return ReturnType.SUCCESS;
        } else {
            if (n.error.getEx() != null) {
                app().errors().add(n.error.getEx());
            }
            return ReturnType.FAIL;
        }
    }

    public ReturnType importFileInto(String... preferred) {
        return importFileInto(getSelectedNoteOrDocument(), preferred);
    }

    public ReturnType importFileInto(PangaeaNoteExt current, String... preferred) {
        FileChooser jfc = new FileChooser(app());
        jfc.currentDirectory().set(service().getValidLastOpenPath());
        if (preferred.length == 0) {
            jfc.filters().add(createPangaeaDocumentSupportedFileFilter());
        }
        Set<String> preferredSet = new HashSet<>(Arrays.asList(preferred));
        if (preferredSet.isEmpty() || preferredSet.contains(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString())) {
            jfc.filters().add(createPangaeaDocumentFileFilter());
        }
        for (String importExtension : service().getImportExtensions()) {
            if (preferredSet.isEmpty() || preferredSet.contains(importExtension)) {
                jfc.filters().add(
                        new FileFilter(
                                Str.i18n("Message.fileFilter." + importExtension),
                                "*." + importExtension
                        )
                );
            }
        }
        if (jfc.showOpenDialog(null)) {
            String file = jfc.selection().get();
            service().setLastOpenPath(file);
            if (file.endsWith("." + PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION)) {
                PangaeaNote n = service().loadDocument(new File(file), wallet());
                for (PangaeaNote c : n.getChildren()) {
                    current.addChild(PangaeaNoteExt.of(c));
                }
            } else {
                String extension = Applications.getFileExtension(file);
                PangaeaNoteFileImporter imp = service().resolveFileImporter(extension);
                try (InputStream is = new FileInputStream(file)) {
                    PangaeaNote n = imp.loadNote(is, Applications.getFileName(file), extension, service());
                    if (n != null) {
                        current.addChild(PangaeaNoteExt.of(n));
                    }
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
            tree.updateTree();
            return ReturnType.CANCEL;
        } else {
            tree.updateTree();
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
        PangaeaNoteExt selectedNote = getSelectedNote();
        if (selectedNote == null) {
            return ReturnType.CANCEL;
        }
        if (!service().isDocumentNote(selectedNote.toNote())) {
            return ReturnType.CANCEL;
        }
        String c = service().getDocumentInfo(selectedNote.toNote()).getPath();
        if (c == null || c.length() == 0) {
            //openNewDocument(false);
            return ReturnType.CANCEL;
        } else {
            return openDocument(new File(c), true);
        }
    }

    public ReturnType openLastDocument(boolean discardChanges) {
        List<String> rf = config().getRecentFiles();
        if (rf != null && rf.size() > 0) {
            String file = rf.get(0);
            return openDocument(new File(file), discardChanges);
        }
        return ReturnType.FAIL;
    }

    public ReturnType openDocument(boolean discardChanges) {
        if (!discardChanges) {
            ReturnType s = trySaveChangesOrDiscard();
            if (s == ReturnType.CANCEL || s == ReturnType.FAIL) {
                return s;
            }
        }
        FileChooser jfc = new FileChooser(app());
        jfc.currentDirectory().set(service().getValidLastOpenPath());
        jfc.filters().add(createPangaeaDocumentFileFilter());
        if (jfc.showOpenDialog(null)) {
            return openDocument(new File(jfc.selection().get()), true);
        } else {
            return ReturnType.CANCEL;
        }
    }

    public ReturnType saveAsDocument() {
        boolean doSecureDocument = false;
        switch (new Alert(app())
                .with((Alert t) -> {
                    t.title().set(Str.i18n("Message.askSecureSaveTitle"));
                    t.headerText().set(Str.i18n("Message.askSecureSaveHeader"));
                    t.headerIcon().set(Str.of("lock"));
                })
                .withYesNoCancelButtons()
                .setContentText(Str.i18n("Message.askSecureSave"))
                .showDialog(null)) {
            case "cancel": {
                return ReturnType.CANCEL;
            }
            case "yes": {
                doSecureDocument = true;
            }
        }
        FileChooser jfc = new FileChooser(app());
        jfc.currentDirectory().set(service().getValidLastOpenPath());
        jfc.filters().add(createPangaeaDocumentFileFilter());
        if (jfc.showSaveDialog(null)) {
            service().setLastOpenPath(jfc.selection().get());
            if (doSecureDocument && doSecureDocument) {
                getDocument().setCypherInfo(new CypherInfo(PangaeaNoteService.SECURE_ALGO, ""));
            }
            try {
                String canonicalPath = new File(jfc.selection().get()).getCanonicalPath();
                if (!canonicalPath.endsWith("." + PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION) && !new File(canonicalPath).exists()) {
                    canonicalPath = canonicalPath + "." + PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION;
                }
                getDocument().setContent(service().stringToElement(canonicalPath));
                service().saveDocument(getDocument().toNote(), wallet());
                onChangePath(canonicalPath);
                snapshotDocument();
                config().addRecentFile(canonicalPath);
                saveConfig();
                return ReturnType.SUCCESS;
            } catch (Exception ex) {
                app().errors().add(ex);
                return ReturnType.FAIL;
            }
        }
        return ReturnType.CANCEL;
    }

    private boolean isSecureAlgo(String s) {
        return s != null && s.length() > 0;
    }

    public ReturnType saveDocument() {
        if (service().isEmptyContent(getDocument().getContent())) {
            return saveAsDocument();
        } else {
            try {
                onChangePath(service().getDocumentInfo(getDocument().toNote()).getPath());
                if (service().saveDocument(getDocument().toNote(), wallet())) {
                    snapshotDocument();
                }
                return ReturnType.SUCCESS;
            } catch (Exception ex) {
                app().errors().add(ex);
                return ReturnType.FAIL;
            }
        }
    }

    public void searchNote() {
        PangaeaNoteExt n = getSelectedNoteOrDocument();
        SearchDialog dialog = new SearchDialog(this);
        SelectableElement se = selectableElement().get();
        String searchText = null;
        if (se != null) {
            searchText = se.getSelectedText();
        }
        dialog.setSearchText(searchText);
        if (n.getParent() == null) {

            dialog.setTitle(Str.i18n("Message.search.searchEverywhere"));
        } else {
            StringBuilder sb = new StringBuilder();
            PangaeaNoteExt p = n;
            while (p.getParent() != null) {
                if (sb.length() > 0) {
                    sb.insert(0, "/");
                }
                sb.insert(0, p.getName());
                p = p.getParent();
            }
            dialog.setTitle(Str.i18nfmt("Message.search.searchInNode", sb));
        }
        SearchQuery s = dialog.showDialog();
        if (s != null) {
            searchInNodes(s, n);
        }
    }

    public void editNote() {
        PangaeaNote n = new EditNoteDialog(this, getSelectedNote()).showDialog();
        if (n != null) {
            onDocumentChanged();
//            tree.invalidate();
//            tree.repaint();
            treePane().fireOnSelectedNote(getSelectedNote());
        }
    }

    public void strikeThroughNote() {
        PangaeaNoteExt vn = getSelectedNote();
        if (vn != null) {
            PangaeaNote a = vn.toNote();
            a.setTitleStriked(!a.isTitleStriked());
            service().updateNoteProperties(vn, a, this);
            onDocumentChanged();
            treePane().fireOnSelectedNote(getSelectedNote());
        }
    }

    public void boldNote() {
        PangaeaNoteExt vn = getSelectedNote();
        if (vn != null) {
            PangaeaNote a = vn.toNote();
            a.setTitleBold(!a.isTitleBold());
            service().updateNoteProperties(vn, a, this);
            onDocumentChanged();
            treePane().fireOnSelectedNote(getSelectedNote());
        }
    }

    public void renameNote() {
        PangaeaNoteExt vn = getSelectedNote();
        if (vn != null) {
            AppDialogResult r = new Alert(app()).withOkCancelButtons()
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.renameNote"));
                        a.headerText().set(Str.i18n("Message.renameNote"));
                        a.headerIcon().set(Str.of("edit"));
                    })
                    .setInputTextFieldContent(Str.i18n("Message.renameNote.label"), Str.of(vn.getName()))
                    .showInputDialog(null);
            if (!r.isBlankValue() && r.isButton("ok")) {
                PangaeaNote a = vn.toNote().setName(r.<String>value());
                service().updateNoteProperties(vn, a, this);
                onDocumentChanged();
//                tree.invalidate();
//                tree.repaint();
                treePane().fireOnSelectedNote(getSelectedNote());
            }
        }
    }

    public void addNodeAfter() {
        NewNoteDialog a = new NewNoteDialog(this);
        PangaeaNote n = a.showDialog();
        if (n != null) {
            PangaeaNoteExt current = getSelectedNote();
            if (current != null) {
                PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
                service().prepareChildForInsertion(current, cc);
                current.addAfterThis(cc);
                onDocumentChanged();
                treePane().updateTree();
                treePane().setSelectedNote(cc);
            }
        }
    }

    public void addNoteBefore() {
        NewNoteDialog a = new NewNoteDialog(this);
        PangaeaNote n = a.showDialog();
        if (n != null) {
            PangaeaNoteExt current = getSelectedNote();
            if (current != null) {
                PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
                service().prepareChildForInsertion(current, cc);
                current.addBeforeThis(cc);
                onDocumentChanged();
                treePane().updateTree();
                treePane().setSelectedNote(cc);
            }
        }
    }

    public void addNote() {
        NewNoteDialog a = new NewNoteDialog(this);
        PangaeaNote n = a.showDialog();
        if (n != null) {
            AppTreeNode<PangaeaNoteExt> nn = treePane().tree().selection().get();
            if (nn == null) {
                nn = treePane().tree().root().get();
            }
//            PangaeaNoteExt current = getSelectedNoteOrDocument();
            PangaeaNoteExt current = nn.get();
            PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
            service().prepareChildForInsertion(current, cc);
//            treePane().updateTree();
            treePane().setSelectedNote(treePane().addNodeChild(nn, cc, -1));
            noteEditor.requestFocus();
            onDocumentChanged();
        }
    }

    public PangaeaNoteExt getSelectedNote() {
        return treePane().getSelectedNote();
    }

    public PangaeaNoteExt getSelectedNoteOrDocument() {
        return treePane().getSelectedNoteOrDocument();
    }

    public void deleteSelectedNote() {
        AppTreeNode<PangaeaNoteExt> n = treePane().tree().selection().get();
        if (n != null) {
            if (n != treePane().tree().root().get()) {
                String s = new Alert(app())
                        .with((Alert a) -> {
                            a.title().set(Str.i18n("Message.warning"));
                            a.headerText().set(Str.i18n("Message.warning"));
                            a.headerIcon().set(Str.of("delete"));
                        })
                        .setContentText(Str.i18n("Message.askDeleteNote"))
                        .withYesNoButtons()
                        .showDialog(null);

                if ("yes".equals(s)) {
                    treePane().removeNodeChild(n);
                    onDocumentChanged();
                }
            }
        }
    }

    public void duplicateNote() {
        PangaeaNoteExt current = getSelectedNote();
        if (current != null) {
            onDocumentChanged();
            treePane().setSelectedNote(current.addDuplicate());
            treePane().updateTree();
        }
    }

    public void onDocumentChanged() {
        modificationsCount++;
        onChangePath(currentFilePath);
    }

    public void searchInNodes(SearchQuery s, PangaeaNoteExt note) {
        if (s == null || note == null) {
            return;
        }
        new Thread(() -> {
            SearchResultPanel.SearchResultPanelItem resultPanel = searchResultsTool().createNewPanel();
            searchResultsTool().showResults();
            resultPanel.resetResults();
            resultPanel.setSearching(true);
            addRecentSearchQuery(s.getText());
            PangaeaNoteExtSearchResult e = service().search(note, s, new SearchProgressMonitor() {
                @Override
                public void startSearch() {
                    resultPanel.setSearchProgress(0, "searching...");
                }

                @Override
                public void searchProgress(Object current) {
                    if (current instanceof PangaeaNoteExt) {
                        resultPanel.setSearchProgress(0, "searching in " + ((PangaeaNoteExt) current).getName());
                    }
                }

                @Override
                public void completeSearch() {
                }
            }, this);
            e.stream().forEach(x -> {
                resultPanel.appendResult(x);
            });
            resultPanel.setSearching(false);
            searchResultsTool().showResults();
        }).start();
    }

    public AppColor colorForHighlightType(HighlightType t) {
        switch (t) {
            case SEARCH: {
                return new Color("Objects.Yellow;OptionPane.warningDialog.titlePane.background;Component.warning.focusedBorderColor;FlameGraph.nativeBackground;ToolWindowScrollBarUI.arrow.background.end;Table.dropLineShortColor;#yellow", app());
            }
            case SEARCH_MAIN: {
                return new Color("Objects.YellowDark;OptionPane.warningDialog.titlePane.shadow;Component.warning.focusedBorderColor;Desktop.background;Table[Enabled+Selected].textBackground;ToggleButton.onBackground;#orange", app());
            }
            case CARET: {
                return new Color(
                        "Button.default.focusColor;OptionPane.questionDialog.titlePane.background;OptionPane.questionDialog.titlePane.background;#green", app());
            }
        }
        return new Color("cyan", app());
    }

    public WritableValue<SelectableElement> selectableElement() {
        return selectableElement;
    }

    public void close() {
        app().shutdown();
    }

    public Path pathOf(String s) {
        return framePath().append(s);
    }

    public Path framePath() {
        return this.path().get();
    }

    private void closeSplash() {
        if (splash) {
            PangaeaSplashScreen.get().closeSplash();
        }
    }

    public WritableBoolean mainFrame() {
        return mainFrame;
    }

    public URLViewerComponent getViewer(String path, URLViewer uviewer) {
        PangaeaNoteMimeType probedContentType = PangaeaNoteMimeType.of(Applications.probeContentType(path));
        String extension = Applications.getFileExtension(path);
        int bestScore = -1;
        SupportSupplier<URLViewerComponent> best = null;
        for (PangaeaNoteFileViewerManager viewer : app().getViewers()) {
            SupportSupplier<URLViewerComponent> ss = viewer.getSupport(path, extension, probedContentType, uviewer, this);
            if (ss != null) {
                int s = ss.getSupportLevel();
                if (s > 0 && s > bestScore) {
                    bestScore = s;
                    best = ss;
                }
            }
        }
        if (best == null) {
            return new UnsupportedViewerComponent(path, extension, probedContentType, this, uviewer, () -> uviewer.fireSuccessfulLoading(path), uviewer::fireError);
        }
        return best.get();
    }

    public I18n i18n() {
        return app().i18n();
    }

    public ToolBar findOrCreateToolBar(String name, Consumer<ToolBar> initializer) {
        ToolBar editToolBar = (ToolBar) toolBar().get().children().get(name);
        if (editToolBar == null) {
            editToolBar = new ToolBar(name, Str.of(name), app());
            toolBar().get().children().add(editToolBar, name);
            initializer.accept(editToolBar);
        }
        return editToolBar;
    }
}
