/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import java.awt.Color;

import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.gui.util.PangaeaNoteError;
import java.awt.event.KeyEvent;

import net.thevpc.pnote.gui.tree.PangaeaNoteDocumentTree;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.thevpc.common.i18n.I18n;
import net.thevpc.common.props.Props;
import net.thevpc.common.props.WritableValue;
import net.thevpc.common.swing.label.DateTimeLabel;
import net.thevpc.common.swing.file.DefaultRecentFilesModel;
import net.thevpc.common.swing.file.ExtensionFileChooserFilter;
import net.thevpc.common.swing.file.RecentFileEvent;
import net.thevpc.common.swing.file.FileSelectedListener;
import net.thevpc.common.swing.splash.JSplashScreen;
import net.thevpc.common.swing.label.MemoryUseIconTray;
import net.thevpc.common.swing.file.RecentFilesMenu;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.echo.AppDockingWorkspace;
import net.thevpc.echo.AppToolAction;
import net.thevpc.echo.AppToolWindow;
import net.thevpc.echo.AppToolWindowAnchor;
import net.thevpc.echo.AppTools;
import net.thevpc.echo.AppWindowDisplayMode;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.SwingApplications;
import net.thevpc.echo.swing.mydoggy.MyDoggyAppDockingWorkspace;
import net.thevpc.nuts.NutsApplicationContext;
import net.thevpc.pnote.PangaeaSplashScreen;
import net.thevpc.pnote.gui.breadcrumb.PangaeaNodeBreadcrumb;
import net.thevpc.pnote.gui.dialogs.EnterNewPasswordDialog;
import net.thevpc.pnote.gui.dialogs.EnterPasswordDialog;
import net.thevpc.pnote.gui.search.SearchResultPanel;
import net.thevpc.pnote.gui.tree.PangaeaNotSupportedFileFormatsFileFilter;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.api.model.PangaeaNoteConfig;
import net.thevpc.pnote.service.security.PasswordHandler;
import net.thevpc.pnote.util.OtherUtils;
import net.thevpc.swing.plaf.UIPlaf;
import net.thevpc.swing.plaf.UIPlafManager;
import net.thevpc.common.swing.util.CancelException;
import net.thevpc.echo.AppDialogBuilder;
import net.thevpc.pnote.gui.search.SearchDialog;
import net.thevpc.pnote.gui.util.SecureJFileChooserImpl;
import net.thevpc.pnote.gui.dialogs.EditNoteDialog;
import net.thevpc.pnote.gui.dialogs.NewNoteDialog;
import net.thevpc.pnote.api.model.CypherInfo;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.model.ReturnType;
import net.thevpc.swing.plaf.UIPlafListener;
import net.thevpc.echo.AppDialogResult;
import net.thevpc.echo.AppState;
import net.thevpc.echo.AppTool;
import net.thevpc.echo.AppWindow;
import net.thevpc.echo.swing.core.SwingApplication;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.jeep.editor.ColorResource;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsWorkspace;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.model.HighlightType;
import net.thevpc.pnote.gui.util.UIPropsTool;
import net.thevpc.pnote.service.search.SearchQuery;
import net.thevpc.pnote.service.search.PangaeaNoteExtSearchResult;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.UnsupportedViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteWindow {

    private static boolean DEV_MODE_UI = false;
    private boolean splash;
    private PangaeaNoteApp papp;
    private SwingApplication app;
    private NutsApplicationContext appContext;
    private RecentFilesMenu recentFilesMenu;
    private PangaeaNoteDocumentTree tree;
    private PangaeaNoteEditor noteEditor;
    private SearchResultPanel searchResultsTool;
    private AppToolWindow documentTool;
    private List<String> recentSearchQueries = new ArrayList<>();
    private String currentFilePath;
    private long modificationsCount;
    private PangaeaNote lastSavedDocument;
    private WritableValue<SelectableElement> selectableElement = Props.of("selectableElement").valueOf(SelectableElement.class, null);
    private WritableValue<Boolean> mainWindow = Props.of("mainWindow").valueOf(Boolean.class, false);

    public PangaeaNoteWindow(NutsApplicationContext appContext, PangaeaNoteApp papp, boolean splash) {
        this.appContext = appContext;
        this.splash = splash;
        this.papp = papp;
        app = (SwingApplication) SwingApplications.Apps.Default();
        mainWindow.listeners().add(x -> {
            onChangeTitle();
        });
    }

    public NutsWorkspace getNutsWorkspace() {
        return appContext.getWorkspace();
    }

    public NutsSession getNutsSession() {
        return appContext.getSession();
    }

    public PangaeaNoteDocumentTree tree() {
        return tree;
    }

    public SearchResultPanel searchResultsTool() {
        return searchResultsTool;
    }

    public PangaeaNoteService service() {
        return papp.service();
    }

    public void resetModifications() {
        this.modificationsCount = 0;
        onChangePath(currentFilePath);
    }

    public void onChangePath(String newPath) {
        if (newPath == null || newPath.length() == 0) {
            this.currentFilePath = null;
        } else {
            recentFilesMenu.addFile(newPath);
            service().addRecentFile(newPath);
            this.currentFilePath = newPath;
            service().setLastOpenPath(newPath);
        }
        onChangeTitle();
    }

    public void onChangeTitle() {
        if (app.state().get() == AppState.NONE || app.state().get() == AppState.INIT) {
            return;
        }
        String modPrefix = "";//mainWindow().get() ? "(!!) " : "";
        String modSuffix = modificationsCount > 0 ? " (*)" : "";
        if (currentFilePath == null || currentFilePath.length() == 0) {
            appWindow().title().set(modPrefix + "Pangaea-Note: " + "<" + app.i18n().getString("Message.noName") + ">" + modSuffix);
        } else {
            appWindow().title().set(modPrefix + "Pangaea-Note: " + currentFilePath + modSuffix);
        }
    }

    protected AppWindow appWindow() {
        return app.mainWindow().get();
    }

    public void bindConfig() {
        PangaeaNoteConfig config = service().config();
        app.iconSets().listeners().add(x -> {
            config.setIconSet(app.iconSets().id().get());
            config.setIconSetSize(app.iconSets().config().get().getWidth());
            saveConfig();
        });
        app.i18n().locale().listeners().add(x -> {
            config.setLocale(((Locale) x.getNewValue()).toString());
            saveConfig();
        });
        appWindow().displayMode().listeners().add(x -> {
            config.setDisplayMode(((AppWindowDisplayMode) x.getNewValue()));
            saveConfig();
        });
        appWindow().toolBar().get().visible().listeners().add(x -> {
            config.setDisplayToolBar(((Boolean) x.getNewValue()));
            saveConfig();
        });
        appWindow().statusBar().get().visible().listeners().add(x -> {
            config.setDisplayStatusBar(((Boolean) x.getNewValue()));
            saveConfig();
        });
        recentFilesMenu.addFileSelectedListener(new FileSelectedListener() {
            @Override
            public void fileSelected(RecentFileEvent event) {
                openDocument(new File(event.getFile()), false);
            }
        });
    }

    public void saveConfig() {
        service().saveConfig();
    }

    public void applyConfigToUI() {
        PangaeaNoteConfig config = service().config();
        if (app.iconSets().containsKey(config.getIconSet())) {
            app.iconSets().id().set(config.getIconSet());
        }
        if (config.getIconSetSize() > 3) {
            app.iconSets().config().set(
                    app.iconSets().config().get().setSize(config.getIconSetSize())
            );
        }
        if (config.getLocale() != null && config.getLocale().length() > 0) {
            app.i18n().locale().set(new Locale(config.getLocale()));
        }
        if (config.getPlaf() != null && config.getPlaf().length() > 0) {
            SwingUtilities.invokeLater(() -> app.plaf().set(config.getPlaf()));
        }
        if (config.getDisplayMode() != null) {
            appWindow().displayMode().set(config.getDisplayMode());
        }
        if (config.getDisplayToolBar() != null) {
            appWindow().toolBar().get().visible().set(config.getDisplayToolBar());
        }
        if (config.getDisplayStatusBar() != null) {
            appWindow().statusBar().get().visible().set(config.getDisplayStatusBar());
        }
        recentFilesMenu.getRecentFilesModel().setFiles(
                (config.getRecentFiles() == null ? Collections.EMPTY_LIST : config.getRecentFiles())
        );
    }

    public void run() {
//        AppEditorThemes editorThemes = new AppEditorThemes();
        AppTools tools = app.tools();
        tools.config().configurableLargeIcon().set(false);
        tools.config().configurableTooltip().set(false);
        app.builder().mainWindowBuilder().get().workspaceFactory().set(MyDoggyAppDockingWorkspace.factory());
        ticSplash();
        papp.prepareApp(app);

        ticSplash();
        PangaeaNoteConfig config = service().config();
        //initialize UI from config before loading the window...
        if (app.iconSets().containsKey(config.getIconSet())) {
            app.iconSets().id().set(config.getIconSet());
        } else {
            app.iconSets().id().set(app.iconSets().values().get(0).getId());
        }
        if (config.getLocale() != null && config.getLocale().length() > 0) {
            app.i18n().locale().set(new Locale(config.getLocale()));
        }
        if (config.getPlaf() != null && config.getPlaf().length() > 0) {
            UIPlafManager.getCurrentManager().apply(config.getPlaf());
        }

        ticSplash();
        app.start();
        appWindow().centerOnDefaultMonitor();
        ticSplash();
        onChangeTitle();
        appWindow().icon().set(new ImageIcon(getClass().getResource("/net/thevpc/pnote/icon.png")));

        AppDockingWorkspace ws = (AppDockingWorkspace) appWindow().workspace().get();
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
        documentTool = ws.addTool("Tools.Document", tree, AppToolWindowAnchor.LEFT);
        if (DEV_MODE_UI) {
            ws.addTool("Tools.UI", new UIPropsTool(), AppToolWindowAnchor.RIGHT);
        }
//        ws.addTool("Tools.Favorites", favourites, AppToolWindowAnchor.LEFT);
//        ws.addTool("Open Documents", "", null, favourites, AppToolWindowAnchor.LEFT);
//        ws.addTool("Recent Documents", "", null, favourites, AppToolWindowAnchor.LEFT);
        AppToolWindow resultPanelTool = ws.addTool("Tools.SearchResults", searchResultsTool, AppToolWindowAnchor.BOTTOM);
        searchResultsTool.setResultPanelTool(resultPanelTool);

        ticSplash();
        ws.addContent("Content", noteEditor);

        ticSplash();
        ticSplash();
        tools.addHorizontalGlue(windowPath() + "/statusBar/Default/glue");
        ticSplash();
        tools.addCustomTool(windowPath() + "/statusBar/Default/calendar", context -> new DateTimeLabel().setDateTimeFormatter("yyy-MM-dd HH:mm:ss"));
        ticSplash();
        tools.addHorizontalSeparator(windowPath() + "/statusBar/Default/glue");
        tools.addCustomTool(windowPath() + "/statusBar/Default/memory", context -> new MemoryUseIconTray(true));
        ticSplash();

        tools.addFolder(windowPath() + "/menuBar/File");

        AppToolAction newfileAction = tools.addAction().bind(() -> openNewDocument(false))
                .path(windowPath() + "/menuBar/File/NewFile", windowPath() + "/toolBar/Default/NewFile")
                .tool();
        newfileAction.mnemonic().set(KeyEvent.VK_N);
        newfileAction.accelerator().set("control N");

//        newfileAction.mnemonic().set(KeyEvent.VK_N);
//        newfileAction.accelerator().set("control N");
        ticSplash();
        AppTool f = tools.getToolByPath(windowPath() + "/menuBar/File");
        f.mnemonic().set(KeyEvent.VK_F);
        AppToolAction reopenAction = tools.addAction().bind(() -> openLastDocument(false))
                .path(windowPath() + "/menuBar/File/OpenLastFile").tool();
        reopenAction.accelerator().set("control shift O");

        AppToolAction openAction = tools.addAction().bind(() -> openDocument(false))
                .path(windowPath() + "/menuBar/File/Open", windowPath() + "/toolBar/Default/Open")
                .tool();
        openAction.mnemonic().set(KeyEvent.VK_O);
        openAction.accelerator().set("control O");

        AppToolAction reloadAction = tools.addAction().bind(() -> reloadDocument(false))
                .path(windowPath() + "/menuBar/File/Reload", windowPath() + "/toolBar/Default/Reload")
                .tool();
        reloadAction.mnemonic().set(KeyEvent.VK_R);
        reloadAction.accelerator().set("control R");

        ticSplash();
        recentFilesMenu = new RecentFilesMenu(app.i18n().getString("Action.recentFiles"), new DefaultRecentFilesModel());
        SwingApplicationsUtils.registerButton(recentFilesMenu, null, "open", app);
        tools.addCustomTool(windowPath() + "/menuBar/File/LoadRecent", x -> recentFilesMenu);

        ticSplash();
        tools.addSeparator(windowPath() + "/menuBar/File/Separator1");
        ticSplash();
        AppToolAction saveAction = tools.addAction().bind(this::saveDocument)
                .path(windowPath() + "/menuBar/File/Save", windowPath() + "/toolBar/Default/Save")
                .tool();

//        tools.addAction(new PNoteAction("Save", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                UIPlafManager.getCurrentManager().resizeRelativeFonts(UIPlafManager.getCurrentManager().getAppFontRelative() + 0.1f);
//            }
//        }, windowPath()+"/menuBar/File/Fplus", windowPath()+"/toolBar/Default/Fplus");
//
//        tools.addAction(new PNoteAction("Save", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                UIPlafManager.getCurrentManager().resizeRelativeFonts(UIPlafManager.getCurrentManager().getAppFontRelative() - 0.1f);
//            }
//        }, windowPath()+"/menuBar/File/Fmoins", windowPath()+"/toolBar/Default/Fmoins");
        ticSplash();
        saveAction.mnemonic().set(KeyEvent.VK_S);
        saveAction.accelerator().set("control S");

//        tools.addAction(
//                new PNoteAction("SaveAll", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            }
//        },windowPath()+"/menuBar/File/SaveAll", windowPath()+"/toolBar/Default/SaveAll");
        AppToolAction saveAsAction = tools.addAction().bind(this::saveAsDocument)
                .path(windowPath() + "/menuBar/File/SaveAs"/*, windowPath()+"/toolBar/Default/SaveAs"*/)
                .tool();

//
//        tools.addSeparator(windowPath()+"/menuBar/File/LoadRecent/Separator1");
//        tools.addAction(new PNoteAction("ClearRecentFiles", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        }, windowPath()+"/menuBar/File/LoadRecent/Clear");
        ticSplash();
        tools.addSeparator(windowPath() + "/menuBar/File/Separator2");
//        tools.addAction(windowPath()+"/menuBar/File/Settings", windowPath()+"/toolBar/Default/Settings");
//        tools.addSeparator(windowPath()+"/menuBar/File/Separator3");
        ticSplash();

        AppToolAction newWindowAction = tools.addAction().bind(() -> papp.newWindow())
                .path(windowPath() + "/menuBar/File/NewWindow")
                .tool();

        tools.addSeparator(windowPath() + "/menuBar/File/Separator3");

        tools.addAction().bind(() -> openNewDocument(false))
                .path(windowPath() + "/menuBar/File/CloseDocument")
                .tool();
        AppToolAction closeWindowAction = tools.addAction().bind(() -> close())
                .path(windowPath() + "/menuBar/File/CloseWindow")
                .tool();

        AppToolAction exitAction = tools.addAction().bind(() -> {
            if (isModifiedDocument()) {
                saveDocument();
            }
            app.shutdown();
        }
        ).path(windowPath() + "/menuBar/File/Exit").tool();
        exitAction.mnemonic().set(KeyEvent.VK_X);
        exitAction.accelerator().set("control X");

        tools.addFolder(windowPath() + "/menuBar/Edit");
        tools.addSeparator(windowPath() + "/toolBar/Default/Separator1");
        AppToolAction a = tools.addAction().bind(() -> {
            try {
                addNote();
            } catch (Exception ex) {
                app().errors().add(ex);
            }
        })
                .path(windowPath() + "/menuBar/Edit/AddNote", windowPath() + "/toolBar/Default/AddNote")
                .tool();
        a.mnemonic().set(KeyEvent.VK_B);
        a.accelerator().set("control B");

        tools.addSeparator(windowPath() + "/menuBar/Edit/Separator1");
        a = tools.addAction().bind(() -> searchNote())
                .path(windowPath() + "/menuBar/Edit/Search", windowPath() + "/toolBar/Default/Search")
                .tool();
        a.mnemonic().set(KeyEvent.VK_F);
        a.accelerator().set("control shift F");

//        tools.addSeparator(windowPath()+"/toolBar/Default/Separator1");
//        tools.addAction(windowPath()+"/menuBar/Edit/Copy", windowPath()+"/toolBar/Default/Copy");
//        tools.addAction(windowPath()+"/menuBar/Edit/Cut", windowPath()+"/toolBar/Default/Cut");
//        tools.addAction(windowPath()+"/menuBar/Edit/Paste", windowPath()+"/toolBar/Default/Paste");
//        tools.addAction(windowPath()+"/menuBar/Edit/Undo", windowPath()+"/toolBar/Default/Undo");
//        tools.addAction(windowPath()+"/menuBar/Edit/Redo", windowPath()+"/toolBar/Default/Redo");
        ticSplash();
        SwingApplications.Helper.addViewToolActions(app);
        ticSplash();
        SwingApplications.Helper.addViewPlafActions(app);
        ticSplash();
//        SwingApplications.Helper.addViewFontSizeActions(app);
        ticSplash();
        SwingApplications.Helper.addViewLocaleActions(app, new Locale[]{Locale.ENGLISH, Locale.FRENCH});
        ticSplash();

        SwingApplications.Helper.addViewIconActions(app);
        ticSplash();
        SwingApplications.Helper.addViewAppearanceActions(app);
        ticSplash();
        tools.addFolder(windowPath() + "/menuBar/Help");
        tools.addAction().bind(() -> papp.showAbout()).path(windowPath() + "/menuBar/Help/About").tool();

        tools.addHorizontalGlue(windowPath() + "/toolBar/Default/Glue");

        tools.addCustomTool(windowPath() + "/toolBar/Default/Glue", (c) -> new PangaeaNodeBreadcrumb(PangaeaNoteWindow.this));

        ticSplash();
        documentTool.active().set(true);
        applyConfigToUI();
        ticSplash();
        bindConfig();
        ticSplash();
        openNewDocument(false);
//        folders.openDocument(service().createSampleDocumentNote());
        ticSplash();
        closeSplash();
//        frame().getRootPane().registerKeyboardAction(
//                (e) -> {
//                    AppWindow w = appWindow().get();
//                    AppWindowDisplayMode dm = w.displayMode().get();
//                    if (dm == null) {
//                        dm = AppWindowDisplayMode.NORMAL;
//                    }
//                    switch (dm) {
//                        case NORMAL: {
//                            w.displayMode().set(AppWindowDisplayMode.FULLSCREEN);
//                            break;
//                        }
//                        default: {
//                            w.displayMode().set(AppWindowDisplayMode.NORMAL);
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
        UIPlafManager.getCurrentManager().addListener(new UIPlafListener() {
            @Override
            public void plafChanged(UIPlaf plaf) {
                if (plaf.isDark()) {
                    String iconsetId = app.iconSets().id().get();
                    if (iconsetId != null) {
                        switch (iconsetId) {
                            case "feather-black": {
                                app.iconSets().id().set("feather-white");
                                break;
                            }
                            case "feather-blue": {
                                app.iconSets().id().set("feather-yellow");
                                break;
                            }
                        }
                    }
                } else if (plaf.isLight()) {
                    String iconsetId = app.iconSets().id().get();
                    if (iconsetId != null) {
                        switch (iconsetId) {
                            case "feather-white": {
                                app.iconSets().id().set("feather-black");
                                break;
                            }
                            case "feather-yellow": {
                                app.iconSets().id().set("feather-blue");
                                break;
                            }
                        }
                    }
                }
            }
        });

//        app.waitFor();
    }

    protected void ticSplash() {
        if (splash) {
            PangaeaSplashScreen.get().tic();
        }
    }

    public PangaeaNoteEditor noteEditor() {
        return noteEditor;
    }

    public AppToolWindow documentTool() {
        return documentTool;
    }

    public Application app() {
        return app;
    }

    public JFrame frame() {
        return (JFrame) appWindow().component();
    }

    public void showError(Exception e) {
        e.printStackTrace();
        newDialog()
                .setTitleId("Message.error")
                .setContent(new JLabel(e.getMessage()))
                .withOkOnlyButton(c -> c.getDialog().closeDialog())
                .build().showDialog();
    }

    public void showError(PangaeaNoteError e) {
        if (e != null) {
            if (e.getEx() != null) {
                showError(e.getEx());
            }
        }
    }

    public PasswordHandler wallet() {
        return new PasswordHandler() {
            @Override
            public String askForSavePassword(String path, String root) {
                String enteredPassword = papp().getOpenWallet().get(root, path);
                if (enteredPassword != null) {
                    return enteredPassword;
                }
                EnterNewPasswordDialog d = new EnterNewPasswordDialog(PangaeaNoteWindow.this, path, this);
                enteredPassword = d.showDialog();
                papp().getOpenWallet().store(root, path, enteredPassword);
                return enteredPassword;
            }

            @Override
            public String askForLoadPassword(String path, String root) {
                String enteredPassword = papp().getOpenWallet().get(root, path);
                if (enteredPassword != null) {
                    return enteredPassword;
                }
                EnterPasswordDialog d = new EnterPasswordDialog(PangaeaNoteWindow.this, path, this);
                enteredPassword = d.showDialog();
                papp().getOpenWallet().store(root, path, enteredPassword);
                return enteredPassword;
            }

            @Override
            public boolean reTypePasswordOnError() {
                papp().getOpenWallet().clear();
                return "yes".equals(newDialog()
                        .setTitleId("Message.invalidPassword.askRetype")
                        .setContentTextId("Message.invalidPassword.askRetype")
                        .withYesNoButtons()
                        .build().showDialog());
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

    public AppDialogBuilder newDialog() {
        return app().newDialog();
    }

    public javax.swing.filechooser.FileFilter createPangaeaDocumentSupportedFileFilter() {
        return new PangaeaNotSupportedFileFormatsFileFilter(this);
    }

    public javax.swing.filechooser.FileFilter createPangaeaDocumentFileFilter() {
        return new ExtensionFileChooserFilter(
                PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION,
                app().i18n().getString("Message.pnoteDocumentFileFilter")
        );
    }

    public PangaeaNoteExt getDocument() {
        return tree().getDocument();
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
            String s = newDialog()
                    .setTitleId("Message.askSaveDocument")
                    .setContentTextId("Message.askSaveDocument")
                    .withYesNoButtons()
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
            String oldPath = tree.getDocument().getContent().asString();
            papp().getOpenWallet().clear(oldPath);
        }
        SwingUtilities3.invokeLater(() -> {
            tree().setDocumentNote(PangaeaNoteExt.of(note));
            snapshotDocument();
            onChangePath(
                    ((PangaeaNoteEmbeddedService) service().getContentTypeService(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT))
                            .getContentValueAsPath(note.getContent())
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
            showError(ex);
            return ReturnType.FAIL;
        }
        if (n.error == null) {
            openNode(n, true);
            return ReturnType.SUCCESS;
        } else {
            showError(n.error);
            return ReturnType.FAIL;
        }
    }

    public ReturnType importFileInto(String... preferred) {
        return importFileInto(getSelectedNoteOrDocument(), preferred);
    }

    public ReturnType importFileInto(PangaeaNoteExt current, String... preferred) {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(service().getValidLastOpenPath()));
        if (preferred.length == 0) {
            jfc.addChoosableFileFilter(createPangaeaDocumentSupportedFileFilter());
        }
        Set<String> preferredSet = new HashSet<>(Arrays.asList(preferred));
        if (preferredSet.isEmpty() || preferredSet.contains(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString())) {
            jfc.addChoosableFileFilter(createPangaeaDocumentFileFilter());
        }
        for (String importExtension : service().getImportExtensions()) {
            if (preferredSet.isEmpty() || preferredSet.contains(importExtension)) {
                jfc.addChoosableFileFilter(new ExtensionFileChooserFilter(importExtension,
                        app().i18n().getString("Message.fileFilter." + importExtension)
                ));
            }
        }
        jfc.setAcceptAllFileFilterUsed(!preferredSet.isEmpty());
        if (jfc.showOpenDialog(frame()) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            service().setLastOpenPath(file.getPath());
            if (file.getName().endsWith("." + PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION)) {
                PangaeaNote n = service().loadDocument(file, wallet());
                for (PangaeaNote c : n.getChildren()) {
                    current.addChild(PangaeaNoteExt.of(c));
                }
            } else {
                String extension = OtherUtils.getFileExtension(file.getName());
                PangaeaNoteFileImporter imp = service().resolveFileImporter(extension);
                try (InputStream is = new FileInputStream(file)) {
                    PangaeaNote n = imp.loadNote(is, OtherUtils.getFileName(file.getPath()), extension, service());
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
        String c = service().getDocumentPath(selectedNote);
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
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(service().getValidLastOpenPath()));
        jfc.addChoosableFileFilter(createPangaeaDocumentFileFilter());
        jfc.setAcceptAllFileFilterUsed(false);
        if (jfc.showOpenDialog(frame()) == JFileChooser.APPROVE_OPTION) {
            return openDocument(jfc.getSelectedFile(), true);
        } else {
            return ReturnType.CANCEL;
        }
    }

    public ReturnType saveAsDocument() {
        SecureJFileChooserImpl jfc = new SecureJFileChooserImpl();
        jfc.setCurrentDirectory(new File(service().getValidLastOpenPath()));
        jfc.addChoosableFileFilter(createPangaeaDocumentFileFilter());
        jfc.setAcceptAllFileFilterUsed(false);
        boolean doSecureDocument = false;
        if (getDocument().getCypherInfo() == null) {
            jfc.getSecureCheckbox().setSelected(false);
            jfc.getSecureCheckbox().setVisible(true);
            jfc.getSecureCheckbox().setText(app().i18n().getString("Message.secureDocument"));
            doSecureDocument = true;
        } else {
            jfc.getSecureCheckbox().setSelected(isSecureAlgo(getDocument().getCypherInfo().getAlgo()));
            jfc.getSecureCheckbox().setVisible(true);
            jfc.getSecureCheckbox().setText(app().i18n().getString("Message.secureDocument"));
            doSecureDocument = false;
        }
        if (jfc.showSaveDialog(frame()) == JFileChooser.APPROVE_OPTION) {
            service().setLastOpenPath(jfc.getSelectedFile().getPath());
            if (doSecureDocument && jfc.getSecureCheckbox().isSelected()) {
                getDocument().setCypherInfo(new CypherInfo(PangaeaNoteService.SECURE_ALGO, ""));
            }
            try {
                String canonicalPath = jfc.getSelectedFile().getCanonicalPath();
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
                showError(ex);
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
                onChangePath(service().getDocumentPath(getDocument()));
                if (service().saveDocument(getDocument().toNote(), wallet())) {
                    snapshotDocument();
                }
                return ReturnType.SUCCESS;
            } catch (Exception ex) {
                showError(ex);
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

            dialog.setTitleId("Message.search.searchEverywhere");
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
            dialog.setTitleId("Message.search.searchInNode", sb);
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
            tree.invalidate();
            tree.repaint();
            tree().fireOnSelectedNote(getSelectedNote());
        }
    }

    public void renameNote() {
        PangaeaNoteExt vn = getSelectedNote();
        if (vn != null) {
            AppDialogResult r = newDialog().withOkCancelButtons()
                    .setTitleId("Message.renameNote")
                    .setInputTextFieldContent("Message.renameNote.label", vn.getName())
                    .showInputDialog();
            if (!r.isBlankValue() && r.isButton("ok")) {
                PangaeaNote a = vn.toNote().setName(r.<String>getValue());
                service().updateNoteProperties(vn, a);
                onDocumentChanged();
                tree.invalidate();
                tree.repaint();
                tree().fireOnSelectedNote(getSelectedNote());
            }
        }
    }

    public void addNodeAfter() {
        NewNoteDialog a = new NewNoteDialog(this);
        PangaeaNote n = a.showDialog(this::showError);
        if (n != null) {
            PangaeaNoteExt current = getSelectedNote();
            if (current != null) {
                PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
                service().prepareChildForInsertion(current, cc);
                current.addAfterThis(cc);
                onDocumentChanged();
                tree().updateTree();
                tree().setSelectedNote(cc);
            }
        }
    }

    public void addNoteBefore() {
        NewNoteDialog a = new NewNoteDialog(this);
        PangaeaNote n = a.showDialog(this::showError);
        if (n != null) {
            PangaeaNoteExt current = getSelectedNote();
            if (current != null) {
                PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
                service().prepareChildForInsertion(current, cc);
                current.addBeforeThis(cc);
                onDocumentChanged();
                tree().updateTree();
                tree().setSelectedNote(cc);
            }
        }
    }

    public void addNote() {
        NewNoteDialog a = new NewNoteDialog(this);
        PangaeaNote n = a.showDialog(this::showError);
        if (n != null) {
            PangaeaNoteExt current = getSelectedNoteOrDocument();
            PangaeaNoteExt cc = new PangaeaNoteExt().copyFrom(n);
            service().prepareChildForInsertion(current, cc);
            current.addChild(cc);
            onDocumentChanged();
            tree().updateTree();
            tree().setSelectedNote(cc);
        }
    }

    public PangaeaNoteExt getSelectedNote() {
        return tree().getSelectedNote();
    }

    public PangaeaNoteExt getSelectedNoteOrDocument() {
        return tree().getSelectedNoteOrDocument();
    }

    public void deleteSelectedNote() {
        PangaeaNoteExt n = getSelectedNote();
        if (n != null) {
            String s = newDialog()
                    .setTitleId("Message.warning")
                    .setContentTextId("Message.askDeleteNote")
                    .withYesNoButtons()
                    .build().showDialog();

            if ("yes".equals(s)) {
                n.delete();
                onDocumentChanged();
                tree().updateTree();
                tree().setSelectedNote(null);
            }
        }
    }

    public void duplicateNote() {
        PangaeaNoteExt current = getSelectedNote();
        if (current != null) {
            onDocumentChanged();
            tree().setSelectedNote(current.addDuplicate());
            tree().updateTree();
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
            });
            e.stream().forEach(x -> {
                resultPanel.appendResult(x);
            });
            resultPanel.setSearching(false);
            searchResultsTool().showResults();
        }).start();
    }

    public Color colorForHighlightType(HighlightType t) {
        switch (t) {
            case SEARCH: {
                return ColorResource.of("Objects.Yellow;OptionPane.warningDialog.titlePane.background;Component.warning.focusedBorderColor;FlameGraph.nativeBackground;ToolWindowScrollBarUI.arrow.background.end;Table.dropLineShortColor;#yellow").get();
            }
            case SEARCH_MAIN: {
                return ColorResource.of("Objects.YellowDark;OptionPane.warningDialog.titlePane.shadow;Component.warning.focusedBorderColor;Desktop.background;Table[Enabled+Selected].textBackground;ToggleButton.onBackground;#orange").get();
            }
            case CARET: {
                return ColorResource.of(
                        "Button.default.focusColor;OptionPane.questionDialog.titlePane.background;OptionPane.questionDialog.titlePane.background;#green").get();
            }
        }
        return Color.CYAN;
    }

    public WritableValue<SelectableElement> selectableElement() {
        return selectableElement;
    }

    public void close() {
        app().shutdown();
    }

    public String windowPath() {
        return "/mainWindow";
    }

    private void closeSplash() {
        if (splash) {
            PangaeaSplashScreen.get().closeSplash();
        }
    }

    public WritableValue<Boolean> mainWindow() {
        return mainWindow;
    }

    public PangaeaNoteApp papp() {
        return papp;
    }

    public URLViewerComponent getViewer(String path, URLViewer uviewer) {
        PangaeaNoteMimeType probedContentType = PangaeaNoteMimeType.of(OtherUtils.probeContentType(path));
        String extension = OtherUtils.getFileExtension(path);
        int bestScore = -1;
        PangaeaNoteFileViewerManager best = null;
        for (PangaeaNoteFileViewerManager viewer : papp().getViewers()) {
            int s = viewer.getSupport(path, extension, probedContentType, this);
            if (s > 0 && s > bestScore) {
                bestScore = s;
                best = viewer;
            }
        }
        if (best == null) {
            return new UnsupportedViewerComponent(path, extension, probedContentType, this, uviewer, () -> uviewer.fireSuccessfulLoading(path), uviewer::fireError);
        }
        return best.createComponent(path, extension, probedContentType, uviewer, this);
    }

    public I18n i18n() {
        return app().i18n();
    }

}
