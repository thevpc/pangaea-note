package net.thevpc.pnote;

import java.util.logging.Handler;
import java.util.logging.Level;
import net.thevpc.nuts.NutsApplication;
import net.thevpc.nuts.NutsApplicationContext;
import net.thevpc.nuts.NutsArgument;
import net.thevpc.nuts.NutsCommandAliasConfig;
import net.thevpc.nuts.NutsCommandLine;
import net.thevpc.nuts.NutsConfirmationMode;
import net.thevpc.nuts.NutsId;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsWorkspace;
import net.thevpc.nuts.NutsWorkspaceCommandAlias;
import net.thevpc.pnote.gui.PangaeaNoteApp;

public class PangaeaNoteMain extends NutsApplication {
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

    private void runGui(NutsApplicationContext appContext) {
        new PangaeaNoteApp(appContext).run();
    }

    private void runInteractiveConsole(NutsApplicationContext appContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void runNonInteractiveConsole(NutsApplicationContext appContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private NutsWorkspaceCommandAlias findDefaultAlias(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        NutsId appId = applicationContext.getAppId();
        return ws.aliases().find(PREFERRED_ALIAS, appId, appId);
    }

    @Override
    protected void onUninstallApplication(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        NutsSession session = applicationContext.getSession();
        NutsWorkspaceCommandAlias a = findDefaultAlias(applicationContext);
        if (a != null) {
            ws.aliases().remove(PREFERRED_ALIAS);
        }
    }

    @Override
    protected void onUpdateApplication(NutsApplicationContext applicationContext) {
        onInstallApplication(applicationContext);
    }

    @Override
    protected void onInstallApplication(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        NutsSession session = applicationContext.getSession();
        NutsWorkspaceCommandAlias a = findDefaultAlias(applicationContext);
        boolean update = false;
        boolean add = false;
        if (a != null) {
            update = true;
        } else if (ws.aliases().find(PREFERRED_ALIAS) == null) {
            add = true;
        }
        if (update || add) {
            ws.aliases()
                    .setSession(update ? session.copy().setConfirm(NutsConfirmationMode.YES) : session)
                    .add(new NutsCommandAliasConfig()
                    .setName(PREFERRED_ALIAS)
                    .setOwner(applicationContext.getAppId())
                    .setCommand(applicationContext.getAppId().getShortName())
                    );
        }
    }
    
}
