/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import java.awt.Color;
import java.util.concurrent.Semaphore;
import javax.swing.ImageIcon;
import net.thevpc.common.iconset.ColorIconTransform;
import net.thevpc.common.iconset.DefaultIconSet;
import net.thevpc.common.iconset.NoIconSet;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.common.props.Props;
import net.thevpc.common.props.WritableList;
import net.thevpc.common.swing.JSplashScreen;
import net.thevpc.common.swing.anim.AnimPoint;
import net.thevpc.common.swing.anim.Animators;
import net.thevpc.echo.AppState;
import net.thevpc.echo.swing.core.SwingApplication;
import net.thevpc.nuts.NutsApplicationContext;
import net.thevpc.pnote.PangaeaSplashScreen;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.swing.plaf.UIPlaf;
import net.thevpc.swing.plaf.UIPlafManager;

/**
 *
 * @author vpc
 */
public class PangaeaNoteApp {

    private NutsApplicationContext appContext;
    private SwingApplication app;
    private PangaeaNoteService service;
    private WritableList<PangaeaNoteWindow> windows = Props.of("windows").listOf(PangaeaNoteWindow.class);
    private Semaphore waitings = new Semaphore(1);

    public PangaeaNoteApp(NutsApplicationContext appContext) {
        this.appContext = appContext;
        service = new PangaeaNoteService(appContext, null);
    }

    public SwingApplication app() {
        return app;
    }

    public PangaeaNoteService service() {
        return service;
    }

    public WritableList<PangaeaNoteWindow> getWindows() {
        return windows;
    }

    public void newWindow() {
        newWindow(true, false);
    }

    private void newWindow(boolean withSplash, boolean main) {
        PangaeaNoteWindow w = new PangaeaNoteWindow(appContext, this, withSplash);
        w.mainWindow().set(main);
        windows.add(w);
        w.app().state().listeners().add(new PropertyListener() {
            @Override
            public void propertyUpdated(PropertyEvent event) {
                AppState s = w.app().state().get();
                if (s == AppState.CLOSED) {
                    windows.remove(w);
                    if (windows.size() == 0) {
                        quit0();
                    } else {
                        if (w.mainWindow().get()) {
                            windows.get(0).mainWindow().set(true);
                        }
                    }
                }
            }
        });
        w.run();
    }

    public void quit() {
        int size;
        while ((size = windows.size()) > 0) {
            PangaeaNoteWindow a = windows.get(0);
            a.close();
            int size2 = windows.size();
            if (size2 >= size) {
                break;
            }
        }
        quit0();
    }

    private void quit0() {
        waitings.release();
    }

    public void run() {
        System.out.println("loading config: " + service().getConfigFilePath());
        service().loadConfig();
        UIPlafManager.getCurrentManager().addListener(x -> {
            service().config().setPlaf(((UIPlaf) x).getId());
            service().saveConfig();
        });
        newWindow(true, true);
        try {
            waitings.acquire();
            waitings.acquire();
        } catch (InterruptedException ex) {
            //
        }
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

    void prepareApp(SwingApplication app) {
        app.i18n().bundles().add("net.thevpc.echo.swing.app-locale-independent");
        app.i18n().bundles().add("net.thevpc.echo.swing.app");
        app.i18n().bundles().add("net.thevpc.pnote.messages.pnote-locale-independent");
        app.i18n().bundles().add("net.thevpc.pnote.messages.pnote-messages");
        app.iconSets().add(new NoIconSet("no-icon"));
        app.iconSets().add(new DefaultIconSet("svgrepo-color", "/net/thevpc/pnote/iconsets/svgrepo-color", getClass().getClassLoader(), null));
        app.iconSets().add(new DefaultIconSet("feather-black", "/net/thevpc/pnote/iconsets/feather", getClass().getClassLoader(), null));
        app.iconSets().add(new DefaultIconSet("feather-white", "/net/thevpc/pnote/iconsets/feather", getClass().getClassLoader(), new ColorIconTransform(Color.BLACK, Color.white)));
        app.iconSets().add(new DefaultIconSet("feather-blue", "/net/thevpc/pnote/iconsets/feather", getClass().getClassLoader(), new ColorIconTransform(Color.BLACK, new Color(22, 60, 90))));
        app.iconSets().add(new DefaultIconSet("feather-cyan", "/net/thevpc/pnote/iconsets/feather", getClass().getClassLoader(), new ColorIconTransform(Color.BLACK, new Color(32, 99, 155))));
        app.iconSets().add(new DefaultIconSet("feather-green", "/net/thevpc/pnote/iconsets/feather", getClass().getClassLoader(), new ColorIconTransform(Color.BLACK, new Color(60, 174, 163))));
        app.iconSets().add(new DefaultIconSet("feather-yellow", "/net/thevpc/pnote/iconsets/feather", getClass().getClassLoader(), new ColorIconTransform(Color.BLACK, new Color(246, 213, 92))));
        app.iconSets().add(new DefaultIconSet("feather-red", "/net/thevpc/pnote/iconsets/feather", getClass().getClassLoader(), new ColorIconTransform(Color.BLACK, new Color(237, 85, 59))));
        if (service().i18n() == null) {
            service().setI18n(app.i18n());
        }

    }

}
