package net.thevpc.pnote;

import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NArg;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.util.NRef;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.splash.PangaeaSplashScreen;

public class PangaeaNoteMain implements NApplication {
    String PREFERRED_ALIAS = "pnote";

    public static void main(String[] args) {
//        System.out.println("<<complied>> : Tue Jun  8 01:43:11 PM CET 2021");
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
        new PangaeaNoteMain().runAndExit(args);
    }

    private void runGui(NApplicationContext appContext) {
        new PangaeaNoteApp(appContext).run();
    }

    private void runInteractiveConsole(NApplicationContext appContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void runNonInteractiveConsole(NApplicationContext appContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private NCustomCommand findDefaultAlias(NApplicationContext applicationContext) {
        NSession session = applicationContext.getSession();
        NId appId = applicationContext.getAppId();
        return NCommands.of(session).findCommand(PREFERRED_ALIAS, appId, appId);
    }

    @Override
    public void onInstallApplication(NApplicationContext applicationContext) {
        NSession session = applicationContext.getSession();
        NEnvs.of(session).addLauncher(new NLauncherOptions()
                .setId(applicationContext.getAppId())
                .setAlias(PREFERRED_ALIAS)
                .setCreateAlias(true)
                .setCreateMenuLauncher(NSupportMode.PREFERRED)
                .setCreateDesktopLauncher(NSupportMode.PREFERRED)
        );
    }

    @Override
    public void onUpdateApplication(NApplicationContext applicationContext) {
        onInstallApplication(applicationContext);
    }

    @Override
    public void onUninstallApplication(NApplicationContext applicationContext) {
        NSession session = applicationContext.getSession();
        NCommands.of(session).removeCommandIfExists(PREFERRED_ALIAS);
    }

    @Override
    public void run(NApplicationContext appContext) {
        PangaeaSplashScreen.get().tic();
        NCmdLine cmdLine = appContext.getCommandLine();
        NRef<Boolean> interactive = NRef.of(false);
        NRef<Boolean> console = NRef.of(false);
        NRef<Boolean> gui = NRef.of(false);
        NRef<Boolean> cui = NRef.of(false);
        while (!cmdLine.isEmpty()) {
            if (!cmdLine.withNextFlag((v, a, s) -> interactive.set(v), "-i", "--interactive")) {
                if (!cmdLine.withNextFlag((v, a, s) -> gui.set(v), "-w", "--gui")) {
                    if (!cmdLine.withNextFlag((v, a, s) -> cui.set(v), "--cui")) {
                        cmdLine.throwUnexpectedArgument();
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
            runGui(appContext);
        } else if (interactive.get()) {
            runInteractiveConsole(appContext);
        } else {
            runNonInteractiveConsole(appContext);
        }
    }

}
