/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

//import java.awt.Color;

import net.thevpc.common.props.Props;
import net.thevpc.common.props.WritableList;
import net.thevpc.echo.AppState;
import net.thevpc.echo.Application;
import net.thevpc.echo.Color;
import net.thevpc.echo.iconset.NoIconSet;
import net.thevpc.echo.impl.DefaultApplication;
import net.thevpc.nuts.NutsApplicationContext;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsWorkspace;
import net.thevpc.pnote.api.PangaeaNoteAppExtension;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.model.PangaeaNoteConfig;
import net.thevpc.pnote.core.CorePangaeaNoteApp;
import net.thevpc.pnote.extensions.CherryTreeExtension;
import net.thevpc.pnote.extensions.CsvExtension;
import net.thevpc.pnote.extensions.Tess4JPangaeaNoteAppExtension;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.security.OpenWallet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class PangaeaNoteApp extends DefaultApplication{

    private NutsApplicationContext appContext;
    private PangaeaNoteService service;
    private WritableList<PangaeaNoteFrame> windows = Props.of("windows").listOf(PangaeaNoteFrame.class);
    private CorePangaeaNoteApp core = new CorePangaeaNoteApp();
    private List<PangaeaNoteAppExtensionHandler> appExtensions = new ArrayList<>();
    private List<PangaeaNoteAppExtensionListener> appExtensionsListeners = new ArrayList<>();
    private List<PangaeaNoteEditorService> editorServices = new ArrayList<>();
    private List<PangaeaNoteFileViewerManager> viewers = new ArrayList<>();
    private OpenWallet openWallet = new OpenWallet();
//    private Application app;

    public PangaeaNoteApp(NutsApplicationContext appContext) {
        super("swing");
        this.appContext = appContext;
        this.service = new PangaeaNoteService(appContext, null, this);
        this.appExtensions.add(new PangaeaNoteAppExtensionHandlerImpl(this, () -> core.asExtension()) {
            {
                checkLoaded();
            }

            @Override
            public void setDisabled(boolean b) {
                //
            }
        });
        addExtension(() -> new Tess4JPangaeaNoteAppExtension());
        addExtension(() -> new CherryTreeExtension());
        addExtension(() -> new CsvExtension());
        for (PangaeaNoteAppExtensionHandler appExtension : appExtensions) {
            appExtension.checkLoaded();
        }
    }



    public void installEditorService(PangaeaNoteEditorService s) {
        editorServices.add(s);
        s.onInstall(this);
    }

    public PangaeaNoteService service() {
        return service;
    }

    public WritableList<PangaeaNoteFrame> getWindows() {
        return windows;
    }

    public void newFrame() {
        newFrame(true, false);
    }

    private PangaeaNoteFrame newFrame(boolean withSplash, boolean main) {
        PangaeaNoteFrame w = new PangaeaNoteFrame(this, withSplash,app());
        w.mainFrame().set(main);
        if(main) {
            app().mainFrame().set(w.frameWidget());
        }
        windows.add(w);
        w.app().state().onChange(event -> {
            AppState s = w.app().state().get();
            if (s == AppState.CLOSED) {
                windows.remove(w);
                if (windows.size() == 0) {
//                    quit0();
                } else {
                    if (w.mainFrame().get()) {
                        windows.get(0).mainFrame().set(true);
                    }
                }
            }
        });
        w.run();
        return w;
    }

    public void quit() {
        int size;
        while ((size = windows.size()) > 0) {
            PangaeaNoteFrame a = windows.get(0);
            a.close();
            int size2 = windows.size();
            if (size2 >= size) {
                break;
            }
        }
//        quit0();
    }


    public void showAbout() {
//        JSplashScreen ss = new JSplashScreen(new ImageIcon(PangaeaSplashScreen.class.getResource("/net/thevpc/pnote/splash-screen.png")), null);
//        ss.addMessage(new JSplashScreen.Message(JSplashScreen.Type.INFO, "https://github.com/thevpc/pangaea-note",
//                Animators.linear(
//                        AnimPoint.of(-100, 200),
//                        AnimPoint.of(90, 200),
//                        200
//                )
//        ));
//        ss.addMessage(new JSplashScreen.Message(JSplashScreen.Type.INFO, "version 1.0.0",
//                Animators.linear(AnimPoint.of(-100, 220),
//                        AnimPoint.of(165, 220), 200
//                )
//        ));
////        ss.addMessage(new JSplashScreen.Message(JSplashScreen.Type.INFO, ""));
//        ss.setTextHeightExact(16);
//        ss.setTextYmax(300);
//        ss.setShowProgress(false);
//        ss.setTextY(220);
//        ss.setHideOnClick(true);
//        ss.setTimeout(30000);
//        ss.animateText();
//        ss.openSplash();
    }

    public void run() {
        System.out.println("loading config: " + service().getConfigFilePath());
        service().loadConfig();
//        app = new DefaultApplication("swing");
//        new AppSwingxConfigurator().configure(app);
        app().i18n().locales().addAll(Locale.ENGLISH, Locale.FRENCH);
        app().i18n().bundles().add("net.thevpc.pnote.messages.pnote-locale-independent");
        app().i18n().bundles().add("net.thevpc.pnote.messages.pnote-messages");
        app().i18n().bundles().add("net.thevpc.echo.app-locale-independent");
        app().i18n().bundles().add("net.thevpc.echo.app");
        app().iconSets().add(new NoIconSet("no-icon"));
        app().iconSets().add().name("svgrepo-color").path("/net/thevpc/pnote/iconsets/svgrepo-color").build();
        app().iconSets().add().name("feather-black").path("/net/thevpc/pnote/iconsets/feather").build();
        for (Object[] r : new Object[][]{
                {"white", Color.WHITE(app())},
                {"white", Color.WHITE(app())},
                {"blue", new Color(22, 60, 90, app())},
                {"cyan", new Color(32, 99, 155, app())},
                {"green", new Color(60, 174, 163, app())},
                {"yellow", new Color(246, 213, 92, app())},
                {"red", new Color(237, 85, 59, app())},}) {
            String n = (String) r[0];
            Color c = (Color) r[1];
            app().iconSets().add().name("feather-" + n).path("/net/thevpc/pnote/iconsets/feather")
                    .replaceColor(Color.BLACK(app()), c).build();
        }
        if (service().i18n() == null) {
            service().setI18n(app().i18n());
        }
        PangaeaNoteConfig config = service().config();
        //initialize UI from config before loading the window...
        if (app().iconSets().containsKey(config.getIconSet())) {
            app().iconSets().id().set(config.getIconSet());
        } else {
            app().iconSets().id().set(app().iconSets().values().get(0).getId());
        }
        if (config.getLocale() != null && config.getLocale().length() > 0) {
            app().i18n().locale().set(new Locale(config.getLocale()));
        }
        if (config.getPlaf() != null && config.getPlaf().length() > 0) {
            app().toolkit().applyPlaf(config.getPlaf());
        }
        app().plaf().onChange(x -> {
            //update config only if we are main window
            //if(mainFrame().get()) {
            service().config().setPlaf(x.newValue());
            service().saveConfig();
            //}
        });
        app().start();
        newFrame(true,true);
        app().waitFor();
//        try {
//            waitings.acquire();
//            waitings.acquire();
//        } catch (InterruptedException ex) {
//            //
//        }
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

    public NutsApplicationContext nutsAppContext() {
        return appContext;
    }

    public NutsWorkspace getNutsWorkspace() {
        return appContext.getWorkspace();
    }

    public NutsSession getNutsSession() {
        return appContext.getSession();
    }

    public List<PangaeaNoteAppExtensionHandler> getAppExtensions() {
        return appExtensions;
    }

    public List<PangaeaNoteEditorService> getEditorServices() {
        return editorServices;
    }

    public List<PangaeaNoteFileViewerManager> getViewers() {
        return viewers;
    }

    public void installViewer(PangaeaNoteFileViewerManager v) {
        viewers.add(v);
    }

    public OpenWallet getOpenWallet() {
        return openWallet;
    }

    public Application app() {
        return this;
    }
}
