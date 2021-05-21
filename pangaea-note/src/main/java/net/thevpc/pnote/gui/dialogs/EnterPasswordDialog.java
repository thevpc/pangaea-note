/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import java.awt.HeadlessException;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Alert;
import net.thevpc.echo.Label;
import net.thevpc.echo.Panel;
import net.thevpc.echo.VerticalPane;
import net.thevpc.echo.api.CancelException;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.components.PasswordComponent;
import net.thevpc.pnote.service.security.PasswordHandler;

/**
 *
 * @author vpc
 */
public class EnterPasswordDialog {

    private Panel panel;
    private PasswordComponent passwordComponent;
    private PasswordHandler ph;
    private PangaeaNoteFrame frame;

    private boolean ok = false;
    private String path;

    public EnterPasswordDialog(PangaeaNoteFrame frame, String path, PasswordHandler ph) throws HeadlessException {
        this.ph = ph;
        this.frame = frame;
        passwordComponent = new PasswordComponent(frame);
        passwordComponent.install(frame.app());
        panel=new VerticalPane(frame.app())
        .with(p->{
            p.children().addAll(
                    new Label(Str.i18n("Message.enter-password"),frame.app()),
                    new Label(Str.of(path),frame.app()),
                            passwordComponent

            );
        });
//        passwordComponent.setMinimumSize(new Dimension(50, 30));
//        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EnterPasswordDialog.class.getResource(
//                "/net/thevpc/pnote/forms/EnterPassword.gbl-form"
//        ));
//        gbs.bind("label", ));
//        gbs.bind("file", );
//        gbs.bind("pwd", );
//
//        panel = gbs.apply(new JPanel());
    }

    protected void install() {
        passwordComponent.install(frame.app());
    }

    protected void uninstall() {
        passwordComponent.uninstall();
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
            new Alert(frame.app())
                    .setTitle(Str.i18n("Message.password"))
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
            String s = passwordComponent.getContentString();
            if (s != null && s.trim().length() > 0) {
                return s;
            }
            throw new IllegalArgumentException("missing password");
        }
        return null;
    }

}
