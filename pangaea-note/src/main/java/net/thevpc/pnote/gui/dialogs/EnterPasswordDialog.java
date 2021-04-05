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
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.common.swing.util.CancelException;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.components.PasswordComponent;
import net.thevpc.pnote.service.security.PasswordHandler;

/**
 *
 * @author vpc
 */
public class EnterPasswordDialog extends OkCancelDialog {

    private PasswordComponent passwordComponent;
    private PasswordHandler ph;

    private boolean ok = false;
    private String path;

    public EnterPasswordDialog(PangaeaNoteGuiApp sapp, String path, PasswordHandler ph) throws HeadlessException {
        super(sapp, "Message.password");
        this.ph = ph;
        passwordComponent = new PasswordComponent(sapp);
        passwordComponent.install(sapp.app());
        passwordComponent.setMinimumSize(new Dimension(50, 30));
        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EnterPasswordDialog.class.getResource(
                "/net/thevpc/pnote/forms/EnterPassword.gbl-form"
        ));
        gbs.bind("label", new JLabel(sapp.app().i18n().getString("Message.enter-password")));
        gbs.bind("file", new JLabel(path));
        gbs.bind("pwd", passwordComponent);

        build(gbs.apply(new JPanel()), this::ok, this::cancel);
    }

    protected void install() {
        passwordComponent.install(sapp.app());
    }

    protected void uninstall() {
        passwordComponent.uninstall();
    }

    protected void ok() {
        uninstall();
        this.ok = true;
        setVisible(false);
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
        setVisible(false);
    }

    public String showDialog() {
        while (true) {
            install();
            this.ok = false;
            pack();
            setLocationRelativeTo((JFrame) sapp.app().mainWindow().get().component());
            setVisible(true);
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
