package net.thevpc.pnote.core.frame;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Button;
import net.thevpc.echo.GridPane;
import net.thevpc.echo.Label;
import net.thevpc.echo.ProgressBar;
import net.thevpc.echo.constraints.AllFill;
import net.thevpc.echo.constraints.AllMargins;
import net.thevpc.nuts.NId;
import net.thevpc.nuts.NSearchCommand;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.NVersion;
import net.thevpc.nuts.concurrent.NScheduler;

public class PangaeaCheckUpdatesPane extends GridPane {
    private int minSeconds = 4;
    private NVersion nextVersion;
    private Label label;
    private Button button;
    private ProgressBar progressBar;
    private boolean startCheckVersion;
    private boolean allowRecheck = true;

    public PangaeaCheckUpdatesPane(PangaeaNoteApp app) {
        super(1, app);
        label = new Label(Str.of(""), app());
        button = new Button("checkForUpdatesNow", this::checkVersion, app());
        progressBar = new ProgressBar(Integer.class, app());
        parentConstraints().addAll(AllFill.HORIZONTAL, AllMargins.of(10));
        children().addAll(
                label,
                progressBar,
                button
        );
        checkVersion();
    }

    private void checkVersion() {
        //if (nextVersion == null) {
        if (!startCheckVersion) {
            startCheckVersion = true;
            nextVersion = null;
            updateMessage();
            NScheduler.of(session()).executorService().submit(
                    () -> {
                        long start = System.currentTimeMillis();
                        try {
                            NId q = NSearchCommand.of(session())
                                    .setId(session().getAppId().builder().setVersion("").build())
                                    .setLatest(true)
                                    .getResultIds().first();
                            if (q != null) {
                                nextVersion = q.getVersion();
                            }
                            long end = System.currentTimeMillis();
                            if (end - start < minSeconds * 1000L) {
                                long remaining = minSeconds * 1000L - (end - start);
                                if (remaining >= 500) {
                                    Thread.sleep(remaining);
                                }
                            }
                        } catch (Exception ex) {
                            long end = System.currentTimeMillis();
                            if (end - start < minSeconds * 1000L) {
                                long remaining = minSeconds * 1000L - (end - start);
                                if (remaining >= 500) {
                                    try {
                                        Thread.sleep(remaining);
                                    } catch (Exception ex2) {
                                        //
                                    }
                                }
                            }
                            app().errors().add(ex);
                        } finally {
                            startCheckVersion = false;
                            updateMessage();
                        }
                    }
            );
        }
        //}
    }

    private void updateMessage() {
        if (nextVersion == null) {
            if (startCheckVersion) {
                progressBar.indeterminate().set(true);
                progressBar.visible().set(true);
                button.enabled().set(false);
                label.text().set(Str.i18n("PangaeaCheckUpdatesPane.checkingForVersion"));
            } else {
                button.enabled().set(true);
                progressBar.indeterminate().set(false);
                progressBar.visible().set(false);
                label.text().set(Str.i18n("PangaeaCheckUpdatesPane.clickToCheckVersion"));
            }
        } else {
            button.enabled().set(allowRecheck);
            progressBar.indeterminate().set(false);
            progressBar.visible().set(false);
            NSession c = session();
            int x = c.getAppId().getVersion().compareTo(nextVersion);
            if (x < 0) {
                label.text().set(Str.i18nfmt("PangaeaCheckUpdatesPane.newVersionAvailable", nextVersion));
            } else if (x == 0) {
                label.text().set(Str.i18nfmt("PangaeaCheckUpdatesPane.latestVersionIsBeingUsed", nextVersion));
            } else if (x > 0) {
                label.text().set(Str.i18nfmt("PangaeaCheckUpdatesPane.currentVersionIsNewerThanRemote",
                        c.getAppId().getVersion(),
                        nextVersion
                ));
            }
        }
    }

    private NSession session() {
        return ((PangaeaNoteApp) app()).session();
    }
}
