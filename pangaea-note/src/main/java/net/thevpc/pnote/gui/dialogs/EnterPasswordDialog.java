/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.pnote.gui.util.dialog.OkCancelDialog;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.common.swing.util.CancelException;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.components.PasswordComponent;
import net.thevpc.pnote.service.security.PasswordHandler;

/**
 *
 * @author vpc
 */
public class EnterPasswordDialog {

    private JPanel panel;
    private PasswordComponent passwordComponent;
    private PasswordHandler ph;
    private PangaeaNoteWindow win;

    private boolean ok = false;
    private String path;

    public EnterPasswordDialog(PangaeaNoteWindow win, String path, PasswordHandler ph) throws HeadlessException {
        this.ph = ph;
        this.win = win;
        passwordComponent = new PasswordComponent(win);
        passwordComponent.install(win.app());
        passwordComponent.setMinimumSize(new Dimension(50, 30));
        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EnterPasswordDialog.class.getResource(
                "/net/thevpc/pnote/forms/EnterPassword.gbl-form"
        ));
        gbs.bind("label", new JLabel(win.app().i18n().getString("Message.enter-password")));
        gbs.bind("file", new JLabel(path));
        gbs.bind("pwd", passwordComponent);

        panel = gbs.apply(new JPanel());
    }

    protected void install() {
        passwordComponent.install(win.app());
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
