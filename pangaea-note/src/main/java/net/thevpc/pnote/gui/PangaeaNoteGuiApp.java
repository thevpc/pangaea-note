/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import net.thevpc.pnote.service.security.OpenWallet;
import net.thevpc.pnote.gui.util.PangaeaNoteError;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import net.thevpc.pnote.gui.tree.PangaeaNoteDocumentTree;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import net.thevpc.common.iconset.ResourcesIconSet;
import net.thevpc.common.swing.DateTimeLabel;
import net.thevpc.common.swing.DefaultRecentFilesModel;
import net.thevpc.common.swing.ExtensionFileChooserFilter;
import net.thevpc.common.swing.RecentFileEvent;
import net.thevpc.common.swing.FileSelectedListener;
import net.thevpc.common.swing.JSplashScreen;
import net.thevpc.common.swing.MemoryUseIconTray;
import net.thevpc.common.swing.RecentFilesMenu;
import net.thevpc.common.swing.anim.Animators;
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
import net.thevpc.pnote.gui.actions.PNoteAction;
import net.thevpc.pnote.gui.breadcrumb.PangaeaNodeBreadcrumb;
import net.thevpc.pnote.gui.dialogs.EnterNewPasswordDialog;
import net.thevpc.pnote.gui.dialogs.EnterPasswordDialog;
import net.thevpc.pnote.gui.search.SearchResultPanel;
import net.thevpc.pnote.gui.tree.PangaeaNotSupportedFileFormatsFileFilter;
import net.thevpc.pnote.gui.util.echoapp.AppDialog;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.model.PangaeaNoteConfig;
import net.thevpc.pnote.service.security.PasswordHandler;
import net.thevpc.pnote.util.OtherUtils;
import net.thevpc.swing.plaf.UIPlaf;
import net.thevpc.swing.plaf.UIPlafManager;
import net.thevpc.common.swing.anim.AnimPoint;
import net.thevpc.pnote.gui.extensions.Tess4JPangaeaNoteAppExtension;
import net.thevpc.swing.plaf.UIPlafListener;

/**
 *
 * @author vpc
 */
public class PangaeaNoteGuiApp {

    private NutsApplicationContext appContext;
    private PangaeaNoteService service;
    private PangaeaNoteConfig config;
    private Application app;
    private RecentFilesMenu recentFilesMenu;
    private PangaeaNoteDocumentTree tree;
    private JSplashScreen jSplashScreen;
    private SearchResultPanel searchResultsTool;
    private AppToolWindow documentTool;
    private List<String> recentSearchQueries = new ArrayList<>();
    private List<PangaeaNoteAppExtensionHandler> appExtensions = new ArrayList<>();
    private List<PangaeaNoteAppExtensionListener> appExtensionsListeners = new ArrayList<>();
    private String currentFilePath;
    private OpenWallet openWallet = new OpenWallet();

    public PangaeaNoteGuiApp(NutsApplicationContext appContext) {
        this.appContext = appContext;
        addExtension(() -> new Tess4JPangaeaNoteAppExtension());
    }

    public PangaeaNoteDocumentTree tree() {
        return tree;
    }

    public SearchResultPanel searchResultsTool() {
        return searchResultsTool;
    }

    public PangaeaNoteService service() {
        return service;
    }

    public void setLastOpenPath(String path) {
        if (path != null) {
            File f = new File(path);
            if (f.isDirectory()) {
                config.setLastOpenPath(f.getPath());
                saveConfig();
            } else {
                File p = f.getParentFile();
                if (p != null) {
                    config.setLastOpenPath(p.getPath());
                    saveConfig();
                }
            }
        } else {
            config.setLastOpenPath(null);
            saveConfig();
        }
    }

    public void onChangePath(String newPath) {
        if (newPath == null || newPath.length() == 0) {
            this.currentFilePath = null;
            app.mainWindow().get().title().set("Pangaea-Note: " + "<" + app.i18n().getString("Message.noName") + ">");
        } else {
            recentFilesMenu.addFile(newPath);
            config.addRecentFile(newPath);
            this.currentFilePath = newPath;
            app.mainWindow().get().title().set("Pangaea-Note: " + newPath);
            setLastOpenPath(newPath);
        }
    }

    public String getValidLastOpenPath() {
        String p = config.getLastOpenPath();
        if (!OtherUtils.isBlank(p)) {
            File f = new File(p);
            if (f.isDirectory()) {
                return f.getPath();
            }
            if (f.isFile()) {
                File parentFile = f.getParentFile();
                if (parentFile != null) {
                    return parentFile.getPath();
                }
            }
        }
        return service().getDefaultDocumentsFolder().getPath();
    }

    public void bindConfig() {
        app.iconSet().id().listeners().add(x -> {
            config.setIconSet(((String) x.getNewValue()));
            saveConfig();
        });
        app.i18n().locale().listeners().add(x -> {
            config.setLocale(((Locale) x.getNewValue()).toString());
            saveConfig();
        });
        app.mainWindow().get().displayMode().listeners().add(x -> {
            config.setDisplayMode(((AppWindowDisplayMode) x.getNewValue()));
            saveConfig();
        });
        app.mainWindow().get().toolBar().get().visible().listeners().add(x -> {
            config.setDisplayToolBar(((Boolean) x.getNewValue()));
            saveConfig();
        });
        app.mainWindow().get().statusBar().get().visible().listeners().add(x -> {
            config.setDisplayStatusBar(((Boolean) x.getNewValue()));
            saveConfig();
        });
        UIPlafManager.getCurrentManager().addListener(x -> {
            config.setPlaf(((UIPlaf) x).getId());
            saveConfig();
        });
        recentFilesMenu.addFileSelectedListener(new FileSelectedListener() {
            @Override
            public void fileSelected(RecentFileEvent event) {
                tree.openDocument(new File(event.getFile()), false);
            }
        });
    }

    public void saveConfig() {
        service.saveConfig(config);
    }

    public void loadConfig() {
        config = service.loadConfig(() -> {
            //default config...
            PangaeaNoteConfig c = new PangaeaNoteConfig();
            c.setIconSet("feather-black-16");
            c.setPlaf("FlatLight");
            return c;
        });
    }

    public void applyConfigToUI() {
        if (app.iconSets().containsKey(config.getIconSet())) {
            app.iconSet().id().set(config.getIconSet());
        }
        if (config.getLocale() != null && config.getLocale().length() > 0) {
            app.i18n().locale().set(new Locale(config.getLocale()));
        }
        if (config.getPlaf() != null && config.getPlaf().length() > 0) {
            SwingUtilities.invokeLater(() -> UIPlafManager.INSTANCE.apply(config.getPlaf()));
        }
        if (config.getDisplayMode() != null) {
            app.mainWindow().get().displayMode().set(config.getDisplayMode());
        }
        if (config.getDisplayToolBar() != null) {
            app.mainWindow().get().toolBar().get().visible().set(config.getDisplayToolBar());
        }
        if (config.getDisplayStatusBar() != null) {
            app.mainWindow().get().statusBar().get().visible().set(config.getDisplayStatusBar());
        }
        recentFilesMenu.getRecentFilesModel().setFiles(
                (config.getRecentFiles() == null ? Collections.EMPTY_LIST : config.getRecentFiles())
        );
    }

    public void run() {
//        AppEditorThemes editorThemes = new AppEditorThemes();
        app = SwingApplications.Apps.Default();
        AppTools tools = app.tools();
        tools.config().configurableLargeIcon().set(false);
        tools.config().configurableTooltip().set(false);
        PangaeaSplashScreen.get().tic();
        app.builder().mainWindowBuilder().get().workspaceFactory().set(MyDoggyAppDockingWorkspace.factory());
        app.i18n().bundles().add("net.thevpc.echo.swing.app-locale-independent");
        app.i18n().bundles().add("net.thevpc.echo.swing.app");
        app.i18n().bundles().add("net.thevpc.pnote.messages.pnote-locale-independent");
        app.i18n().bundles().add("net.thevpc.pnote.messages.pnote-messages");
        for (String iconSetId : new String[]{"feather-black", "feather-white"}) {
            for (int iconSize : new int[]{16, 24, 32}) {
                app.iconSets().add(new ResourcesIconSet(
                        iconSetId + "-" + iconSize, iconSize,
                        "/net/thevpc/pnote/iconsets/" + iconSetId,
                        getClass().getClassLoader()));
            }
        }
        PangaeaSplashScreen.get().tic();
        service = new PangaeaNoteService(appContext, app.i18n());
        System.out.println("loading config: " + service.getConfigFilePath());

        loadConfig();
        //initialize UI from config before loading the window...
        if (app.iconSets().containsKey(config.getIconSet())) {
            app.iconSet().id().set(config.getIconSet());
        } else {
            app.iconSet().id().set(app.iconSets().values().get(0).getId());
        }
        if (config.getLocale() != null && config.getLocale().length() > 0) {
            app.i18n().locale().set(new Locale(config.getLocale()));
        }
        if (config.getPlaf() != null && config.getPlaf().length() > 0) {
            UIPlafManager.getCurrentManager().apply(config.getPlaf());
        }

        PangaeaSplashScreen.get().tic();
        app.start();
        app.mainWindow().get().centerOnDefaultMonitor();
        PangaeaSplashScreen.get().tic();
        app.mainWindow().get().title().set("Pangaea-Note");
        app.mainWindow().get().icon().set(new ImageIcon(getClass().getResource("/net/thevpc/pnote/icon.png")));

        AppDockingWorkspace ws = (AppDockingWorkspace) app.mainWindow().get().workspace().get();
        PangaeaSplashScreen.get().tic();

        tree = new PangaeaNoteDocumentTree(this);
        PangaeaNoteEditor content = new PangaeaNoteEditor(this, false);
        PangaeaSplashScreen.get().tic();
        tree.addNoteSelectionListener(n -> content.setNote(n));
        content.addListener(n -> tree.setSelectedNote(n));
        searchResultsTool = new SearchResultPanel(this);

//        PangaeaNoteDocumentTree favourites = new PangaeaNoteDocumentTree(this);
//        PangaeaNoteDocumentTree openFiles = new PangaeaNoteDocumentTree(app);
        PangaeaSplashScreen.get().tic();
        documentTool = ws.addTool("Tools.Document", tree, AppToolWindowAnchor.LEFT);
//        ws.addTool("Tools.Favorites", favourites, AppToolWindowAnchor.LEFT);
//        ws.addTool("Open Documents", "", null, favourites, AppToolWindowAnchor.LEFT);
//        ws.addTool("Recent Documents", "", null, favourites, AppToolWindowAnchor.LEFT);
        AppToolWindow resultPanelTool = ws.addTool("Tools.SearchResults", searchResultsTool, AppToolWindowAnchor.BOTTOM);
        searchResultsTool.setResultPanelTool(resultPanelTool);

        PangaeaSplashScreen.get().tic();
        ws.addContent("Content", content);

        PangaeaSplashScreen.get().tic();

        PangaeaSplashScreen.get().tic();
        tools.addHorizontalGlue("/mainWindow/statusBar/Default/glue");
        PangaeaSplashScreen.get().tic();
        tools.addCustomTool("/mainWindow/statusBar/Default/calendar", context -> new DateTimeLabel().setDateTimeFormatter("yyy-MM-dd HH:mm:ss"));
        PangaeaSplashScreen.get().tic();
        tools.addHorizontalSeparator("/mainWindow/statusBar/Default/glue");
        tools.addCustomTool("/mainWindow/statusBar/Default/memory", context -> new MemoryUseIconTray(true));
        PangaeaSplashScreen.get().tic();

        tools.addFolder("/mainWindow/menuBar/File");

        AppToolAction newfileAction = tools.addAction(new PNoteAction("NewFile", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.openNewDocument(false);
            }
        }, "/mainWindow/menuBar/File/NewFile", "/mainWindow/toolBar/Default/NewFile");
//        newfileAction.mnemonic().set(KeyEvent.VK_N);
//        newfileAction.accelerator().set("control N");

        PangaeaSplashScreen.get().tic();

        AppToolAction openAction = tools.addAction(new PNoteAction("OpenFile", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.openDocument(false);
            }
        }, "/mainWindow/menuBar/File/Open", "/mainWindow/toolBar/Default/Open");
        openAction.mnemonic().set(KeyEvent.VK_O);
        openAction.accelerator().set("control O");

        AppToolAction reloadAction = tools.addAction(new PNoteAction("ReloadFile", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.reloadDocument(false);
            }
        }, "/mainWindow/menuBar/File/Reload", "/mainWindow/toolBar/Default/Reload");
        reloadAction.mnemonic().set(KeyEvent.VK_R);
        reloadAction.accelerator().set("control R");

        PangaeaSplashScreen.get().tic();
        AppToolAction saveAction = tools.addAction(new PNoteAction("Save", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.saveDocument();
            }
        }, "/mainWindow/menuBar/File/Save", "/mainWindow/toolBar/Default/Save");
        PangaeaSplashScreen.get().tic();
        saveAction.mnemonic().set(KeyEvent.VK_S);
        saveAction.accelerator().set("control S");

//        tools.addAction(
//                new PNoteAction("SaveAll", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            }
//        },"/mainWindow/menuBar/File/SaveAll", "/mainWindow/toolBar/Default/SaveAll");
        AppToolAction saveAsAction = tools.addAction(new PNoteAction("SaveAs", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.saveAsDocument();
            }
        }, "/mainWindow/menuBar/File/SaveAs"/*, "/mainWindow/toolBar/Default/SaveAs"*/);

        tools.addAction(new PNoteAction("CloseDocument", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.openNewDocument(false); // close is same as new!!
            }
        }, "/mainWindow/menuBar/File/CloseDocument", "/mainWindow/toolBar/Default/CloseDocument");
        PangaeaSplashScreen.get().tic();
        tools.addSeparator("/mainWindow/menuBar/File/Separator1");

        PangaeaSplashScreen.get().tic();
        recentFilesMenu = new RecentFilesMenu(app.i18n().getString("Action.recentFiles"), new DefaultRecentFilesModel());

        PangaeaSplashScreen.get().tic();
        tools.addCustomTool("/mainWindow/menuBar/File/LoadRecent", x -> recentFilesMenu);
//
//        tools.addSeparator("/mainWindow/menuBar/File/LoadRecent/Separator1");
//        tools.addAction(new PNoteAction("ClearRecentFiles", this) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//            }
//        }, "/mainWindow/menuBar/File/LoadRecent/Clear");
        PangaeaSplashScreen.get().tic();
        tools.addSeparator("/mainWindow/menuBar/File/Separator2");
//        tools.addAction("/mainWindow/menuBar/File/Settings", "/mainWindow/toolBar/Default/Settings");
//        tools.addSeparator("/mainWindow/menuBar/File/Separator3");
        PangaeaSplashScreen.get().tic();

        AppToolAction exitAction = tools.addAction(new PNoteAction("Exit", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tree.isModifiedDocument()) {
                    tree.saveDocument();
                }
                app.shutdown();
            }
        },
                "/mainWindow/menuBar/File/Exit");
        exitAction.mnemonic().set(KeyEvent.VK_X);
        exitAction.accelerator().set("control X");

        tools.addFolder("/mainWindow/menuBar/Edit");
        AppToolAction a = tools.addAction(new PNoteAction("Search", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.onAddChild();
            }
        }, "/mainWindow/menuBar/Edit/AddNote"/*, "/mainWindow/toolBar/Default/SaveAs"*/);
        a.mnemonic().set(KeyEvent.VK_N);
        a.accelerator().set("control N");
        tools.addSeparator("/mainWindow/menuBar/Edit/Separator1");
        a = tools.addAction(new PNoteAction("Search", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tree.onSearch();
            }
        }, "/mainWindow/menuBar/Edit/Search"/*, "/mainWindow/toolBar/Default/SaveAs"*/);
        a.mnemonic().set(KeyEvent.VK_F);
        a.accelerator().set("control shift F");

//        tools.addSeparator("/mainWindow/toolBar/Default/Separator1");
//        tools.addAction("/mainWindow/menuBar/Edit/Copy", "/mainWindow/toolBar/Default/Copy");
//        tools.addAction("/mainWindow/menuBar/Edit/Cut", "/mainWindow/toolBar/Default/Cut");
//        tools.addAction("/mainWindow/menuBar/Edit/Paste", "/mainWindow/toolBar/Default/Paste");
//        tools.addAction("/mainWindow/menuBar/Edit/Undo", "/mainWindow/toolBar/Default/Undo");
//        tools.addAction("/mainWindow/menuBar/Edit/Redo", "/mainWindow/toolBar/Default/Redo");
        PangaeaSplashScreen.get().tic();
        SwingApplications.Helper.addViewToolActions(app);
        PangaeaSplashScreen.get().tic();
        SwingApplications.Helper.addViewPlafActions(app);
        PangaeaSplashScreen.get().tic();
        SwingApplications.Helper.addViewLocaleActions(app, new Locale[]{Locale.ENGLISH, Locale.FRENCH});
        PangaeaSplashScreen.get().tic();

        SwingApplications.Helper.addViewIconActions(app);
        PangaeaSplashScreen.get().tic();
        SwingApplications.Helper.addViewAppearanceActions(app);
        PangaeaSplashScreen.get().tic();
        tools.addFolder("/mainWindow/menuBar/Help");
        tools.addAction(new PNoteAction("About", this) {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        },
                "/mainWindow/menuBar/Help/About");

        tools.addHorizontalGlue("/mainWindow/toolBar/Default/Glue");

        tools.addCustomTool("/mainWindow/toolBar/Default/Glue", (c) -> new PangaeaNodeBreadcrumb(PangaeaNoteGuiApp.this));

        PangaeaSplashScreen.get().tic();
        documentTool.active().set(true);
        applyConfigToUI();
        PangaeaSplashScreen.get().tic();
        bindConfig();
        PangaeaSplashScreen.get().tic();
        tree.openNewDocument(false);
//        folders.openDocument(service().createSampleDocumentNote());
        PangaeaSplashScreen.get().tic();
        PangaeaSplashScreen.get().closeSplash();
//        frame().getRootPane().registerKeyboardAction(
//                (e) -> {
//                    AppWindow w = app.mainWindow().get();
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
//                    tree.onAddChild();
//                },
//                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK),
//                JComponent.WHEN_IN_FOCUSED_WINDOW
//        );
        UIPlafManager.getCurrentManager().addListener(new UIPlafListener() {
            @Override
            public void plafChanged(UIPlaf plaf) {
                if (plaf.isDark()) {
                    String iconsetId = app.iconSet().id().get();
                    if (iconsetId != null) {
                        switch (iconsetId) {
                            case "feather-black-16": {
                                app.iconSet().id().set("feather-white-16");
                                break;
                            }
                            case "feather-black-24": {
                                app.iconSet().id().set("feather-white-24");
                                break;
                            }
                            case "feather-black-32": {
                                app.iconSet().id().set("feather-white-32");
                                break;
                            }
                        }
                    }
                } else if (plaf.isLight()) {
                    String iconsetId = app.iconSet().id().get();
                    if (iconsetId != null) {
                        switch (iconsetId) {
                            case "feather-white-16": {
                                app.iconSet().id().set("feather-black-16");
                                break;
                            }
                            case "feather-white-24": {
                                app.iconSet().id().set("feather-black-24");
                                break;
                            }
                            case "feather-white-32": {
                                app.iconSet().id().set("feather-black-32");
                                break;
                            }
                        }
                    }
                }
            }
        });

        app.waitFor();
    }

    public AppToolWindow documentTool() {
        return documentTool;
    }

    public Application app() {
        return app;
    }

    public JFrame frame() {
        return (JFrame) app.mainWindow().get().component();
    }

    public void showAbout() {
        JSplashScreen ss = new JSplashScreen(new ImageIcon(PangaeaSplashScreen.class.getResource("/net/thevpc/pnote/splash-screen.png")), null);
        ss.addMessage(new JSplashScreen.Message(JSplashScreen.Type.INFO, "https://github.com/thevpc/pangaea-note",
                Animators.linear(
                        AnimPoint.of(-100, 200),
                        AnimPoint.of(90, 200),
                        200
                )
        ));
        ss.addMessage(new JSplashScreen.Message(JSplashScreen.Type.INFO, "version 1.0.0",
                Animators.linear(AnimPoint.of(-100, 220),
                        AnimPoint.of(165, 220), 200
                )
        ));
//        ss.addMessage(new JSplashScreen.Message(JSplashScreen.Type.INFO, ""));
        ss.setTextHeightExact(16);
        ss.setTextYmax(300);
        ss.setShowProgress(false);
        ss.setTextY(220);
        ss.setHideOnClick(true);
        ss.setTimeout(30000);
        ss.animateText();
        ss.openSplash();
    }

    public void showError(Exception e) {
        e.printStackTrace();
        newDialog()
                .setTitleId("Message.error")
                .setContent(new JLabel(e.getMessage()))
                .withOkOnlyButton(c -> c.closeDialog())
                .build().setVisible(true);
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
                String enteredPassword = openWallet.get(root, path);
                if (enteredPassword != null) {
                    return enteredPassword;
                }
                EnterNewPasswordDialog d = new EnterNewPasswordDialog(PangaeaNoteGuiApp.this, path, this);
                enteredPassword = d.showDialog();
                openWallet.store(root, path, enteredPassword);
                return enteredPassword;
            }

            @Override
            public String askForLoadPassword(String path, String root) {
                String enteredPassword = openWallet.get(root, path);
                if (enteredPassword != null) {
                    return enteredPassword;
                }
                EnterPasswordDialog d = new EnterPasswordDialog(PangaeaNoteGuiApp.this, path, this);
                enteredPassword = d.showDialog();
                openWallet.store(root, path, enteredPassword);
                return enteredPassword;
            }

            @Override
            public boolean reTypePasswordOnError() {
                return "yes".equals(newDialog()
                        .setTitleId("Message.invalidPassword.askRetype")
                        .setContentTextId("Message.invalidPassword.askRetype")
                        .withYesNoButtons(c -> c.closeDialog(), c -> c.closeDialog())
                        .build().showDialog());
            }
        };
    }

    public PangaeaNoteConfig config() {
        return config;
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

    public AppDialog.Builder newDialog() {
        return AppDialog.of(app());
    }

    public javax.swing.filechooser.FileFilter createPangaeaDocumentSupportedFileFilter() {
        return new PangaeaNotSupportedFileFormatsFileFilter(this);
    }

    public javax.swing.filechooser.FileFilter createPangaeaDocumentFileFilter() {
        return new ExtensionFileChooserFilter(
                PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION,
                app().i18n().getString("Message.pnoteDocumentFileFilter")
        );
    }

    public void addExtension(Supplier<PangaeaNoteAppExtension> extension) {
        PangaeaNoteAppExtensionHandlerImpl a = new PangaeaNoteAppExtensionHandlerImpl(this, extension);
        appExtensions.add(a);
        a.addListener("status", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                for (PangaeaNoteAppExtensionListener li : appExtensionsListeners) {
                    li.onExtensionStatusChanged(a, (PangaeaNoteAppExtensionStatus) evt.getOldValue(), (PangaeaNoteAppExtensionStatus) evt.getNewValue());
                }
            }
        });
        for (PangaeaNoteAppExtensionListener li : appExtensionsListeners) {
            li.onExtensionAdded(a);
        }
    }

    public List<PangaeaNoteAppExtensionHandler> getLoadedAppExtensions() {
        return getAppExtensions().stream().filter(x -> x.checkLoaded()).collect(Collectors.toList());
    }

    public List<PangaeaNoteAppExtensionHandler> getAppExtensions() {
        return appExtensions;
    }

}
