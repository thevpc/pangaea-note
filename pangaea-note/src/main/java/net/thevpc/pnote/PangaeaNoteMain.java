package net.thevpc.pnote;

import net.thevpc.nuts.*;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.splash.PangaeaSplashScreen;

public class PangaeaNoteMain implements NutsApplication {
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

    private void runGui(NutsApplicationContext appContext) {
        new PangaeaNoteApp(appContext).run();
    }

    private void runInteractiveConsole(NutsApplicationContext appContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void runNonInteractiveConsole(NutsApplicationContext appContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private NutsWorkspaceCustomCommand findDefaultAlias(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        NutsId appId = applicationContext.getAppId();
        return ws.commands().findCommand(PREFERRED_ALIAS, appId, appId);
    }

    @Override
    public void onInstallApplication(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        ws.env().addLauncher(new NutsLauncherOptions()
                .setId(applicationContext.getAppId())
                .setAlias(PREFERRED_ALIAS)
                .setCreateAlias(true)
                .setCreateMenuShortcut(NutsSupportCondition.PREFERRED)
                .setCreateDesktopShortcut(NutsSupportCondition.PREFERRED)
        );
    }

    @Override
    public void onUpdateApplication(NutsApplicationContext applicationContext) {
        onInstallApplication(applicationContext);
    }

    @Override
    public void onUninstallApplication(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        ws.commands().removeCommandIfExists(PREFERRED_ALIAS);
    }

    @Override
    public void run(NutsApplicationContext appContext) {
        PangaeaSplashScreen.get().tic();
        NutsCommandLine cmdLine = appContext.getCommandLine();
        NutsArgument a;
        boolean interactive = false;
        boolean console = false;
        boolean gui = false;
        boolean cui = false;
        while (!cmdLine.isEmpty()) {
            if (appContext.configureFirst(cmdLine)) {
                //
            } else if ((a = cmdLine.nextBoolean("-i", "--interactive")) != null) {
                if (a.isEnabled()) {
                    interactive = a.getBooleanValue();
                }
            } else if ((a = cmdLine.nextBoolean("-w", "--gui")) != null) {
                if (a.isEnabled()) {
                    gui = a.getBooleanValue();
                }
            } else if ((a = cmdLine.nextBoolean("--cui")) != null) {
                if (a.isEnabled()) {
                    cui = a.getBooleanValue();
                }
            } else {
                cmdLine.unexpectedArgument();
            }
        }
        if (interactive) {
            console = true;
        }
        if (!console && !gui && !cui) {
            console = true;
        }
        gui = true;//force for now
        PangaeaSplashScreen.get().tic();
        if (cui || gui) {
            runGui(appContext);
        } else if (interactive) {
            runInteractiveConsole(appContext);
        } else {
            runNonInteractiveConsole(appContext);
        }
    }

}
