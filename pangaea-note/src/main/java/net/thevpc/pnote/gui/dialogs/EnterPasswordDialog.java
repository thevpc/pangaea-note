/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Label;
import net.thevpc.echo.Panel;
import net.thevpc.echo.TextField;
import net.thevpc.echo.*;
import net.thevpc.echo.api.CancelException;
import net.thevpc.echo.constraints.AllFill;
import net.thevpc.echo.constraints.AllMargins;
import net.thevpc.echo.constraints.Grow;
import net.thevpc.echo.constraints.ContainerGrow;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.service.security.PasswordHandler;


/**
 * @author vpc
 */
public class EnterPasswordDialog {

    private Panel panel;
    private PasswordField passwordComponent;
    private PasswordHandler ph;
    private PangaeaNoteFrame frame;

    private boolean ok = false;
    private String path;

    public EnterPasswordDialog(PangaeaNoteFrame frame, String path, PasswordHandler ph) {
        this.ph = ph;
        this.frame = frame;
        passwordComponent = new PasswordField(frame.app());
        panel = new GridPane(1, frame.app())
                .with(p -> {
                    p.parentConstraints().addAll(AllMargins.of(3), AllFill.HORIZONTAL, ContainerGrow.TOP_ROW);
                    p.children().addAll(
                            new Label(Str.i18n("Message.enter-password"), frame.app()),
                            passwordComponent,
                            new Label(Str.i18n("Message.for-file"), frame.app()),
                            new TextField(frame.app())
                                    .with((TextField t) -> {
                                        t.editable().set(false);
                                        t.text().set(Str.of(path));
                                        t.childConstraints().add(Grow.HORIZONTAL);
                                    })
                            );
                });
    }

    protected void install() {
    }

    protected void uninstall() {

    }

    protected void ok() {
        uninstall();
        this.ok = true;
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
    }

    public String showDialog() {
        while (true) {
            install();
            this.ok = false;
            new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.password"));
                        a.headerText().set(Str.i18n("Message.password"));
                        a.headerIcon().set(Str.of("unlock"));
                    })
                    .setContent(panel)
                    .withOkCancelButtons(
                            (a) -> {
                                ok();
                                a.getDialog().closeDialog();
                            },
                            (a) -> {
                                cancel();
                                a.getDialog().closeDialog();
                            }
                    )
                    .showDialog(null);
            try {
                return get();
            } catch (Exception ex) {
                if (!ph.reTypePasswordOnError()) {
                    throw new CancelException();
                }
            }
        }
    }

    public String get() {
        if (ok) {
            String s = passwordComponent.text().get().value();
            if (s != null && s.trim().length() > 0) {
                return s;
            }
            throw new IllegalArgumentException("missing password");
        }
        return null;
    }

}
