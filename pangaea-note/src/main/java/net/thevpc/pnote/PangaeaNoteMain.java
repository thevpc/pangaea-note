package net.thevpc.pnote;

import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.nswing.NSwingUtils;
import net.thevpc.nuts.util.NRef;
import net.thevpc.nuts.util.NSupportMode;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.splash.PangaeaSplashScreen;

import javax.swing.*;

public class PangaeaNoteMain implements NApplication {

    String PREFERRED_ALIAS = "pnote";

    public static void main(String[] args) {
        NSwingUtils.prepareUI(args);
        PangaeaSplashScreen.get();
//        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
////        rootLogger.setLevel(Level.FINEST);
//        for (Handler handler : rootLogger.getHandlers()) {
//            handler.setLevel(Level.FINEST);
//        }
//        rootLogger = java.util.logging.Logger.getLogger("net.thevpc");
//        rootLogger.setLevel(Level.FINEST);
//        for (Handler handler : rootLogger.getHandlers()) {
//            handler.setLevel(Level.FINEST);
//        }
        PangaeaSplashScreen.get().tic();
        new PangaeaNoteMain().main(NMainArgs.of(args));
    }



    private void runGui() {
        new PangaeaNoteApp().run();
    }

    private void runInteractiveConsole() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void runNonInteractiveConsole() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private NCustomCmd findDefaultAlias() {
        NId appId = NApp.of().getId().get();
        return NWorkspace.of().findCommand(PREFERRED_ALIAS, appId, appId);
    }

    @Override
    public void onInstallApplication() {
        NWorkspace.of().addLauncher(new NLauncherOptions()
                .setId(NApp.of().getId().get())
                .setAlias(PREFERRED_ALIAS)
                .setCreateAlias(true)
                .setCreateMenuLauncher(NSupportMode.PREFERRED)
                .setCreateDesktopLauncher(NSupportMode.PREFERRED)
        );
    }

    @Override
    public void onUpdateApplication() {
        onInstallApplication();
    }

    @Override
    public void onUninstallApplication() {
        NWorkspace.of().removeCommandIfExists(PREFERRED_ALIAS);
    }

    @Override
    public void run() {
        NSwingUtils.setSharedWorkspaceInstance();
        PangaeaSplashScreen.get().tic();
        NCmdLine cmdLine = NApp.of().getCmdLine();
        NRef<Boolean> interactive = NRef.of(false);
        NRef<Boolean> console = NRef.of(false);
        NRef<Boolean> gui = NRef.of(false);
        NRef<Boolean> cui = NRef.of(false);
        while (!cmdLine.isEmpty()) {
            if (!cmdLine.withNextFlag((v, a) -> interactive.set(v), "-i", "--interactive")) {
                if (!cmdLine.withNextFlag((v, a) -> gui.set(v), "-w", "--gui")) {
                    if (!cmdLine.withNextFlag((v, a) -> cui.set(v), "--cui")) {
                        if (!cmdLine.withNextFlag((v, a) -> {}, "--scale")) {
                            cmdLine.throwUnexpectedArgument();
                        }
                    }
                }
            }
        }
        if (interactive.get()) {
            console.set(true);
        }
        if (!console.get() && !gui.get() && !cui.get()) {
            console.set(true);
        }
        gui.set(true);//force for now
        PangaeaSplashScreen.get().tic();
        if (cui.get() || gui.get()) {
            runGui();
        } else if (interactive.get()) {
            runInteractiveConsole();
        } else {
            runNonInteractiveConsole();
        }
    }

}
