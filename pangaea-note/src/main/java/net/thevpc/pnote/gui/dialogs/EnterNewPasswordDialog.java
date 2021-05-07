/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.common.swing.util.CancelException;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.components.PasswordComponent;
import net.thevpc.pnote.gui.util.dialog.OkCancelDialog;
import net.thevpc.pnote.service.security.PasswordHandler;

import javax.swing.*;
import java.awt.*;

/**
 * @author vpc
 */
public class EnterNewPasswordDialog{

    private JPanel panel;
    private PasswordComponent passwordComponent1;
    private PasswordComponent passwordComponent2;

    private boolean ok = false;
    private String path;
    private PasswordHandler ph;
    private PangaeaNoteWindow win;

    public EnterNewPasswordDialog(PangaeaNoteWindow win, String path, PasswordHandler ph) throws HeadlessException {
        super();
        this.ph = ph;
        this.win = win;
        passwordComponent1 = new PasswordComponent(win);
        passwordComponent1.install(win.app());
        passwordComponent1.setMinimumSize(new Dimension(50, 30));
        passwordComponent2 = new PasswordComponent(win);
        passwordComponent2.install(win.app());
        passwordComponent2.setMinimumSize(new Dimension(50, 30));
        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EnterNewPasswordDialog.class.getResource(
                "/net/thevpc/pnote/forms/EnterNewPassword.gbl-form"
        ));
        gbs.bind("label", new JLabel(win.app().i18n().getString("Message.enter-password")));
        gbs.bind("file", new JLabel(path));
        gbs.bind("pwd1", passwordComponent1);
        gbs.bind("pwd2", passwordComponent2);
        panel = gbs.apply(new JPanel());
    }

    protected void install() {
        passwordComponent1.install(win.app());
        passwordComponent2.install(win.app());
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
            win.app().newDialog()
                    .setTitleId("Message.password")
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
                    .showDialog();
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
            throw new IllegalArgumentException(win.app().i18n().getString("Message.passwordsDoNotMatch"));
        }
        return null;
    }

}
