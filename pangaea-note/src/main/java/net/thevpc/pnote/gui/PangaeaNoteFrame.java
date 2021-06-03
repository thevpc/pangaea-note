/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import net.thevpc.common.i18n.I18n;
import net.thevpc.common.i18n.Str;
import net.thevpc.common.props.*;
import net.thevpc.echo.Button;
import net.thevpc.echo.Color;
import net.thevpc.echo.Dimension;
import net.thevpc.echo.FileFilter;
import net.thevpc.echo.*;
import net.thevpc.echo.Frame;
import net.thevpc.echo.Image;
import net.thevpc.echo.Menu;
import net.thevpc.echo.api.*;
import net.thevpc.echo.api.components.*;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.echo.iconset.IconConfig;
import net.thevpc.echo.impl.Applications;
import net.thevpc.echo.impl.controls.ExtraControls;
import net.thevpc.echo.util.ClipboardHelper;
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
import net.thevpc.pnote.service.search.PangaeaNoteExtSearchResult;
import net.thevpc.pnote.service.search.SearchQuery;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.security.PasswordHandler;

import java.io.*;
import java.util.*;
import java.util.List;

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
    private BreadCrumb<PangaeaNote> breadCrumb;

    public PangaeaNoteFrame(String id, PangaeaNoteApp app, boolean splash) {
        super(id, app);
        this.splash = splash;

        prefSize().set(new Dimension(800, 600));
        mainFrame.onChange(x -> onChangeTitle());
        String ic0 = app.config().getIconSet();
        this.iconSet().set(ic0 == null ? DEFAULT_ICONSET : ic0);
        String loc0 = app.config().getLocale();
        this.locale().set(loc0 == null ? Locale.getDefault() : new Locale(loc0));
        int iconSize0 = app.config().getIconSize();
        if(iconSize0>=8){
            this.iconConfig().set(new IconConfig(iconSize0));
        }
        this.iconSet().onChange(
                () -> {
                    PangaeaNote d = getDocument();
                    PangaeaNoteDocumentInfo info = app().getDocumentInfo(d);
                    info.setIconSet(this.iconSet().get());
                    d.setContent(app().documentInfoToElement(info));
                    config().setIconSet(this.iconSet().get());
                    saveConfig();
                    app().iconSets().id().set(this.iconSet().get());
                }
        );
        this.iconConfig().onChange(
                () -> {
                    PangaeaNote d = getDocument();
                    PangaeaNoteDocumentInfo info = app().getDocumentInfo(d);
                    IconConfig c = this.iconConfig().get();
                    info.setIconSize(c==null?16:c.getWidth());
                    d.setContent(app().documentInfoToElement(info));
                    config().setIconSize(info.getIconSize());
                    saveConfig();
                    app().iconSets().config().set(new IconConfig(info.getIconSize()));
                }
        );
        this.locale().onChange(
                () -> {
                    PangaeaNote d = getDocument();
                    PangaeaNoteDocumentInfo info = app().getDocumentInfo(d);
                    String loc = this.locale().get() == null ? null : this.locale().get().toString();
                    info.setLocale(loc);
                    d.setContent(app().documentInfoToElement(info));
                    config().setLocale(loc);
                    saveConfig();
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

    public void resetModifications() {
        this.modificationsCount = 0;
        onChangePath(currentFilePath);
    }

    public void onChangePath(String newPath) {
        if (newPath == null || newPath.length() == 0) {
            this.currentFilePath = null;
        } else {
            app().addRecentFile(newPath);
            this.currentFilePath = newPath;
            app().setLastOpenPath(newPath);
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
        String pv=app().appContext().getAppVersion().toString();
        if (currentFilePath == null || currentFilePath.length() == 0) {
            this.title().set(Str.of(modPrefix + "Pangaea-Note v"+pv+" : " + "<" + app().i18n().getString("Message.noName") + ">" + modSuffix));
        } else {
            this.title().set(Str.of(modPrefix + "Pangaea-Note v"+pv+" : " + currentFilePath + modSuffix));
        }
    }

    public void bindConfig() {
        PangaeaNoteConfig config = app().config();
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
        app().saveConfig();
    }

    public void applyConfigToUI() {
        PangaeaNoteConfig config = app().config();
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
        this.icon().unset();
        this.icon().set(new Image(
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

        DockPane ws = workspace();
        ticSplash();

        tree = new PangaeaNoteDocumentTree(this);
        noteEditor = new PangaeaNoteEditor(this, false);

        breadCrumb = new BreadCrumb<>(PangaeaNote.class, app());
        breadCrumb.anchor().set(Anchor.TOP);
        breadCrumb.itemRenderer().set(context -> {
            context.setText(context.getValue().getName());
            context.setIcon(Str.of(app().getNoteIcon(context.getValue())));
        });
        children().add(breadCrumb);


        ticSplash();
        tree.addNoteSelectionListener(n -> {
            try {
                noteEditor.setNote(n);
                noteEditor.requestFocus();
            } catch (Exception ex) {
                app().errors().add(ex);
            }
            if (n == null) {
                breadCrumb.values().clear();
            } else {
                breadCrumb.values().setAll(app().nodePath(n, false));
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
        searchResultsTool.active().set(false);
        ticSplash();
        ws.children().add(new BorderPane(app())
                .with((BorderPane p) -> {
                    p.anchor().set(Anchor.CENTER);
                    p.children().add(breadCrumb);
                    p.children().add(noteEditor);
                })
        );

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
                            b.mnemonic().set(KeyCode.N);
                            b.accelerator().set("control N");
                        }),
                pathOf("/menuBar/File/*")
                //                , pathOf("/toolBar/Default/NewFile")
        );

//        newfileAction.mnemonic().set(KeyEvent.VK_N);
//        newfileAction.accelerator().set("control N");
        ticSplash();
        Menu f = (Menu) app().components().get(pathOf("/menuBar/File"));
        f.mnemonic().set(KeyCode.F);

        app().components().add(new Button("OpenLastFile", this::openLastDiscardNoDiscard, app())
                        .with((Button b) -> {
                            b.accelerator().set("control shift O");
                        }),
                pathOf("/menuBar/File/*"));

        app().components().add(new Button("Open", this::openDocumentNoDiscard, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyCode.O);
                            b.accelerator().set("control O");
                        }),
                pathOf("/menuBar/File/*"));

        app().components().add(new Button("Reload", this::reloadDocumentNoDiscard, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyCode.R);
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
                            b.mnemonic().set(KeyCode.S);
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
                            b.mnemonic().set(KeyCode.X);
                            b.accelerator().set("control X");
                        }),
                pathOf("/menuBar/File/*")
        );

        app().components().addFolder(pathOf("/menuBar/Edit"));
        ClipboardHelper.prepareMenu(this);
        app().components().addSeparator(pathOf("/menuBar/Edit/*"));
        app().components().addMulti(new Button("AddNote", this::addNote, app())
                        .with((Button b) -> {
                            b.mnemonic().set(KeyCode.B);
                            b.accelerator().set("control B");
                        }),
                pathOf("/menuBar/Edit/AddNote"), pathOf("/toolBar/Default/*")
        );
        app().components().addSeparator(pathOf("/menuBar/Edit/*"));

        app().components().addSeparator(pathOf("/toolBar/Default/*"));
        ClipboardHelper.prepareToolBar(this);

        app().components().addMulti(new Button("Search", this::searchNote, app())
                        .with((Button b)
                                        -> {
                                    b.mnemonic().set(KeyCode.F);
                                    b.accelerator().set("control shift F");

                                }
                        ),
                pathOf("/menuBar/Edit/Search"), pathOf("/toolBar/Default/*"));

        app().components().addFolder(pathOf("/menuBar/Help"));
        app().components().add(new Button("About", () -> showAbout(), app()),
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
        List<String> all = app().config().getRecentFiles();
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
                    List<String> all2 = app().config().getRecentFiles();
                    if (all2.remove(filePath)) {
                        app().config().setRecentFiles(all2);
                        app().saveConfig();
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
                        new Alert(PangaeaNoteFrame.this)
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
        return app().config();
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

    public PangaeaNote getDocument() {
        return treePane().getDocument();
    }

    public boolean isModifiedDocument() {
        PangaeaNote newDoc = getDocument();
        boolean mod = lastSavedDocument != null && !lastSavedDocument.equals(newDoc);
        if (mod) {
        }
        return mod;
    }

    public void snapshotDocument() {
        lastSavedDocument = tree.getDocument().copy();
        resetModifications();
    }

    public ReturnType trySaveChangesOrDiscard() {
        if (isModifiedDocument()) {
            String s = new Alert(this)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.askSaveDocument"));
                        a.headerText().set(Str.i18n("Message.askSaveDocument"));
                        a.headerIcon().set(Str.of("save"));
                    })
                    .setContentText(Str.i18n("Message.askSaveDocument"))
                    .withYesNoCancelButtons()
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
        return openNode(app().newDocument(), discardChanges);
    }

    public ReturnType openNewDocument(boolean discardChanges) {
        return openNode(app().newDocument(), discardChanges);
    }

    private ReturnType openNode(PangaeaNote note, boolean discardChanges) {
        if (!discardChanges) {
            ReturnType s = trySaveChangesOrDiscard();
            if (s == ReturnType.CANCEL || s == ReturnType.FAIL) {
                return s;
            }
        }
        if (!app().isDocumentNote(note)) {
            throw new IllegalArgumentException("expected Document Note");
        }
        if (tree.getDocument() != null && tree.getDocument().getContent() != null) {
            PangaeaNoteDocumentInfo oldInfo = app().getDocumentInfo(tree.getDocument());
            String oldPath = oldInfo.getPath();
            app().getOpenWallet().clear(oldPath);
        }
        app().toolkit().runUI(() -> {
            treePane().setDocumentNote(note);
            snapshotDocument();
            PangaeaNoteDocumentInfo contentValueAsInfo = ((PangaeaNoteEmbeddedService) app().getContentTypeService(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT))
                    .getContentValueAsInfo(note.getContent());
            onChangePath(contentValueAsInfo == null ? null : contentValueAsInfo.getPath());
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
            n = app().loadDocument(file, wallet());
        } catch (CancelException ex) {
            return ReturnType.CANCEL;
        } catch (Exception ex) {
            app().errors().add(ex);
            return ReturnType.FAIL;
        }
        if (n.error == null) {
            PangaeaNoteDocumentInfo info = app().getDocumentInfo(n);
            String loc = info == null ? null : info.getLocale();
            if (loc == null) {
                loc = app().config().getLocale();
            }
            if (loc == null) {
                loc = Locale.getDefault().toString();
            }
            locale().set(new Locale(loc));
            String iconSet = info == null ? null : info.getIconSet();
            if (iconSet == null) {
                iconSet = app().config().getIconSet();
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

    public ReturnType importFileInto(PangaeaNote current, String... preferred) {
        FileChooser jfc = new FileChooser(app());
        jfc.currentDirectory().set(app().getValidLastOpenPath());
        if (preferred.length == 0) {
            jfc.filters().add(createPangaeaDocumentSupportedFileFilter());
        }
        Set<String> preferredSet = new HashSet<>(Arrays.asList(preferred));
        if (preferredSet.isEmpty() || preferredSet.contains(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString())) {
            jfc.filters().add(createPangaeaDocumentFileFilter());
        }
        for (String importExtension : app().getImportExtensions()) {
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
            app().setLastOpenPath(file);
            if (file.endsWith("." + PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION)) {
                PangaeaNote n = app().loadDocument(new File(file), wallet());
                for (PangaeaNote c : n.getChildren()) {
                    app().addChild(current, c, -1);
                }
            } else {
                String extension = Applications.getFileExtension(file);
                PangaeaNoteFileImporter imp = app().resolveFileImporter(extension);
                try (InputStream is = new FileInputStream(file)) {
                    PangaeaNote n = imp.loadNote(is, Applications.getFileName(file), extension, app());
                    if (n != null) {
                        app().addChild(current, n, -1);
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
        PangaeaNote selectedNote = getSelectedNote();
        if (selectedNote == null) {
            return ReturnType.CANCEL;
        }
        if (!app().isDocumentNote(selectedNote)) {
            return ReturnType.CANCEL;
        }
        String c = app().getDocumentInfo(selectedNote).getPath();
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
        jfc.currentDirectory().set(app().getValidLastOpenPath());
        jfc.filters().add(createPangaeaDocumentFileFilter());
        if (jfc.showOpenDialog(null)) {
            return openDocument(new File(jfc.selection().get()), true);
        } else {
            return ReturnType.CANCEL;
        }
    }

    public ReturnType saveAsDocument() {
        boolean doSecureDocument;
        switch (new Alert(this)
                .with((Alert t) -> {
                    t.title().set(Str.i18n("Message.askSecureSaveTitle"));
                    t.headerText().set(Str.i18n("Message.askSecureSaveHeader"));
                    t.headerIcon().set(Str.of("lock"));
                    t.withYesNoCancelButtons();
                    t.setContentText(Str.i18n("Message.askSecureSave"));
                })
                .showDialog()) {
            case "yes": {
                doSecureDocument = true;
                break;
            }
            case "no": {
                doSecureDocument = false;
                break;
            }
            default: {
                return ReturnType.CANCEL;
            }
        }
        FileChooser jfc = new FileChooser(app());
        jfc.currentDirectory().set(app().getValidLastOpenPath());
        jfc.filters().add(createPangaeaDocumentFileFilter());
        if (jfc.showSaveDialog()) {
            app().setLastOpenPath(jfc.selection().get());
            if (doSecureDocument) {
                getDocument().setCypherInfo(new CypherInfo(PangaeaNoteApp.SECURE_ALGO, ""));
            }
            try {
                String canonicalPath = new File(jfc.selection().get()).getCanonicalPath();
                if (!canonicalPath.endsWith("." + PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION) && !new File(canonicalPath).exists()) {
                    canonicalPath = canonicalPath + "." + PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION;
                }
                getDocument().setContent(app().stringToElement(canonicalPath));
                app().saveDocument(getDocument(), wallet());
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
        PangaeaNote currentDocument = getDocument();
        if (app().isNewDocumentNote(currentDocument)) {
            return saveAsDocument();
        } else {
            try {
                onChangePath(app().getDocumentInfo(currentDocument).getPath());
                if (app().saveDocument(currentDocument, wallet())) {
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
        PangaeaNote n = getSelectedNoteOrDocument();
        SearchDialog dialog = new SearchDialog(this);
        SelectableElement se = selectableElement().get();
        String searchText = null;
        if (se != null) {
            searchText = se.getSelectedText();
        }
        dialog.setSearchTextElseClipboard(searchText);
        if (app().getParent(n) == null) {

            dialog.setTitle(Str.i18n("Message.search.searchEverywhere"));
        } else {
            StringBuilder sb = new StringBuilder();
            PangaeaNote p = n;
            while (app().getParent(p) != null) {
                if (sb.length() > 0) {
                    sb.insert(0, "/");
                }
                sb.insert(0, p.getName());
                p = app().getParent(p);
            }
            dialog.setTitle(Str.i18nfmt("Message.search.searchInNode", sb));
        }
        SearchQuery s = dialog.showDialog();
        if (s != null) {
            searchInNodes(s, n);
        }
    }

    public void printNote() {
        PangaeaNote n = getSelectedNote();
        if(n!=null) {
            app().printerService().createJob().printComponent(
                    noteEditor().getCurrentEditor()
            );
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
        PangaeaNote vn = getSelectedNote();
        if (vn != null) {
            PangaeaNote a = vn;
            a.setTitleStriked(!a.isTitleStriked());
            app().updateNoteProperties(vn, a, this);
            onDocumentChanged();
            treePane().fireOnSelectedNote(getSelectedNote());
        }
    }

    public void boldNote() {
        PangaeaNote vn = getSelectedNote();
        if (vn != null) {
            PangaeaNote a = vn;
            a.setTitleBold(!a.isTitleBold());
            app().updateNoteProperties(vn, a, this);
            onDocumentChanged();
            treePane().fireOnSelectedNote(getSelectedNote());
        }
    }

    public void renameNote() {
        PangaeaNote vn = getSelectedNote();
        if (vn != null) {
            AppDialogResult r = new Alert(this).withOkCancelButtons()
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.renameNote"));
                        a.headerText().set(Str.i18n("Message.renameNote"));
                        a.headerIcon().set(Str.of("edit"));
                    })
                    .setInputTextFieldContent(Str.i18n("Message.renameNote.label"), Str.of(vn.getName()))
                    .showInputDialog(null);
            if (!r.isBlankValue() && r.isButton("ok")) {
                PangaeaNote a = vn.setName(r.<String>value());
                app().updateNoteProperties(vn, a, this);
                app().treeNodeOf(a).fireContentUpdated();
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
            PangaeaNote current = getSelectedNote();
            if (current != null) {
                PangaeaNote cc = new PangaeaNote();
                cc.copyFrom(n);
                app().prepareChildForInsertion(current, cc);
                app().addAfterThis(current, cc);
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
            PangaeaNote current = getSelectedNote();
            if (current != null) {
                PangaeaNote cc = new PangaeaNote();
                cc.copyFrom(n);
                app().prepareChildForInsertion(current, cc);
                app().addBeforeThis(current, cc);
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
            AppTreeNode<PangaeaNote> nn = treePane().tree().selection().get();
            if (nn == null) {
                nn = treePane().tree().root().get();
            }
//            PangaeaNoteExt current = getSelectedNoteOrDocument();
            PangaeaNote current = nn.get();
            PangaeaNote cc = new PangaeaNote();
            cc.copyFrom(n);
            app().prepareChildForInsertion(current, cc);
//            treePane().updateTree();
            treePane().setSelectedNote(treePane().addNodeChild(nn, cc, -1));
            noteEditor.requestFocus();
            onDocumentChanged();
        }
    }

    public PangaeaNote getSelectedNote() {
        return treePane().getSelectedNote();
    }

    public PangaeaNote getSelectedNoteOrDocument() {
        return treePane().getSelectedNoteOrDocument();
    }

    public void deleteSelectedNote() {
        AppTreeNode<PangaeaNote> n = treePane().tree().selection().get();
        if (n != null) {
            if (n != treePane().tree().root().get()) {
                String s = new Alert(this)
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
        PangaeaNote current = getSelectedNote();
        if (current != null) {
            onDocumentChanged();
            treePane().setSelectedNote(app().addDuplicateSiblingNote(current, -1));
            treePane().updateTree();
        }
    }

    public void onDocumentChanged() {
        modificationsCount++;
        onChangePath(currentFilePath);
    }

    public void searchInNodes(SearchQuery s, PangaeaNote note) {
        if (s == null || note == null) {
            return;
        }
        new Thread(() -> {
            SearchResultPanel.SearchResultPanelItem resultPanel = searchResultsTool().createNewPanel();
            searchResultsTool().showResults();
            resultPanel.resetResults();
            resultPanel.setSearching(true);
            addRecentSearchQuery(s.getText());
            PangaeaNoteExtSearchResult e = app().search(note, s, new SearchProgressMonitor() {
                @Override
                public void startSearch() {
                    resultPanel.setSearchProgress(0, "searching...");
                }

                @Override
                public void searchProgress(Object current) {
                    if (current instanceof PangaeaNote) {
                        resultPanel.setSearchProgress(0, "searching in " + ((PangaeaNote) current).getName());
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

    public void showAbout() {
        Alert alert = new Alert(this)
                .with(a -> {
                    a.headerIcon().set(new Image(getClass().getResource("/net/thevpc/pnote/icon.png"), app()));
                    a.withOkOnlyButton();
                    a.title().set(Str.i18n("About.title"));
                    a.headerText().set(Str.i18n("About.header"));
                    a.setContent(new PangaeaAboutPane(app()));
                    a.prefSize().set(new Dimension(500, 400));
                });
        alert.showDialog(null);
    }

}