/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Alert;
import net.thevpc.echo.Panel;
import net.thevpc.echo.VerticalPane;
import net.thevpc.echo.api.CancelException;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.components.PasswordComponent;
import net.thevpc.pnote.service.security.PasswordHandler;

import javax.swing.*;
import java.awt.*;

/**
 * @author vpc
 */
public class EnterNewPasswordDialog{

    private Panel panel;
    private PasswordComponent passwordComponent1;
    private PasswordComponent passwordComponent2;

    private boolean ok = false;
    private String path;
    private PasswordHandler ph;
    private PangaeaNoteFrame frame;

    public EnterNewPasswordDialog(PangaeaNoteFrame frame, String path, PasswordHandler ph) throws HeadlessException {
        super();
        this.ph = ph;
        this.frame = frame;
        passwordComponent1 = new PasswordComponent(frame);
        passwordComponent1.install(frame.app());
//        passwordComponent1.setMinimumSize(new Dimension(50, 30));
        passwordComponent2 = new PasswordComponent(frame);
        passwordComponent2.install(frame.app());

        panel=new VerticalPane(frame.app())
                .with(p->{
                    p.children().addAll(
                            new net.thevpc.echo.Label(Str.i18n("Message.enter-password"),frame.app()),
                            new net.thevpc.echo.Label(Str.of(path),frame.app()),
                            passwordComponent1,
                            passwordComponent2
                    );
                });


//        passwordComponent2.setMinimumSize(new Dimension(50, 30));
//        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EnterNewPasswordDialog.class.getResource(
//                "/net/thevpc/pnote/forms/EnterNewPassword.gbl-form"
//        ));
//        gbs.bind("label", new JLabel(win.app().i18n().getString("Message.enter-password")));
//        gbs.bind("file", new JLabel(path));
//        gbs.bind("pwd1", passwordComponent1);
//        gbs.bind("pwd2", passwordComponent2);
//        panel = gbs.apply(new JPanel());
    }

    protected void install() {
        passwordComponent1.install(frame.app());
        passwordComponent2.install(frame.app());
    }

    protected void uninstall() {
        passwordComponent1.uninstall();
        passwordComponent2.uninstall();
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
                //exHandler.accept(ex);
            }
        }
    }

    public String get() {
        if (ok) {
            String s1 = passwordComponent1.getContentString();
            String s2 = passwordComponent2.getContentString();
            if (s1 != null && s1.trim().length() > 0 && s1.equals(s2)) {
                return s1;
            }
            throw new IllegalArgumentException(frame.app().i18n().getString("Message.passwordsDoNotMatch"));
        }
        return null;
    }

}
