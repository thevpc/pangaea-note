package net.thevpc.pnote;

import net.thevpc.nuts.app.*;
import net.thevpc.nuts.artifact.NId;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.command.NCustomCmd;
import net.thevpc.nuts.core.NWorkspace;
import net.thevpc.nuts.swing.NSwingUtils;
import net.thevpc.nuts.platform.NLauncherOptions;
import net.thevpc.nuts.util.NRef;
import net.thevpc.nuts.util.NSupportMode;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.splash.PangaeaSplashScreen;

@NApp
public class PangaeaNoteMain  {

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
        NApplication.builder(args).run();
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
        NId appId = NApplication.of().id().get();
        return NWorkspace.of().findCommand(PREFERRED_ALIAS, appId, appId);
    }

    @NAppInstall
    public void onInstallApplication() {
        NWorkspace.of().addLauncher(new NLauncherOptions()
                .id(NApplication.of().id().get())
                .alias(PREFERRED_ALIAS)
                .createAlias(true)
                .createMenuLauncher(NSupportMode.PREFERRED)
                .createDesktopLauncher(NSupportMode.PREFERRED)
        );
    }

    @NAppUpdate
    public void onUpdateApplication() {
        onInstallApplication();
    }

    @NAppUninstall
    public void onUninstallApplication() {
        NWorkspace.of().removeCommandIfExists(PREFERRED_ALIAS);
    }

    @NAppRun
    public void run() {
        NSwingUtils.setSharedWorkspaceInstance();
        PangaeaSplashScreen.get().tic();
        NCmdLine cmdLine = NApplication.of().cmdLine();
        NRef<Boolean> interactive = NRef.of(false);
        NRef<Boolean> console = NRef.of(false);
        NRef<Boolean> gui = NRef.of(false);
        NRef<Boolean> cui = NRef.of(false);
        while (!cmdLine.isEmpty()) {
            cmdLine.matcher()
                    .when( "-i", "--interactive").asFlag((v) -> interactive.set(v.booleanValue()))
                    .when("-w", "--gui").asFlag((v) -> gui.set(v.booleanValue()))
                    .when("--cui").asFlag((v) -> cui.set(v.booleanValue()))
                    .when("--scale").asTrueFlag((v) -> {})
                    .require();
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
