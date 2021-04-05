/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.pnote.gui.components.FormComponent;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.core.swing.SwingApplicationsHelper;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.AnyDocumentListener;

/**
 *
 * @author vpc
 */
public class PasswordComponent extends JPanel implements FormComponent {

    private JPasswordField pf = new JPasswordField();
    private JCheckBox showPassword;
    private AnyDocumentListener listener;
    private SwingApplicationsHelper.Tracker tracker;

    public PasswordComponent(PangaeaNoteGuiApp sapp) {
        tracker = new SwingApplicationsHelper.Tracker(sapp.app());
        showPassword = new JCheckBox("Message.showPassword");
        showPassword.addActionListener((e)
                -> pf.setEchoChar(showPassword.isSelected() ? '\0' : '*')
        );
        new GridBagLayoutSupport("[pwd-===][check] ; insets(2)")
                .bind("pwd", pf)
                .bind("check", showPassword)
                .apply(this);
        JPopupMenu p = new JPopupMenu();
        p.add(new JMenuItem(
                tracker.registerStandardAction(
                        () -> {
                            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                            try {
                                clip.setContents(new StringSelection(new String(pf.getPassword())), null);
                            } catch (Exception ex) {
                                //ex.printStackTrace();
                            }
                        }, "copy"
                )));
        pf.setComponentPopupMenu(p);
    }

    @Override
    public void install(Application app) {
        showPassword.setText(app.i18n().getString("Message.showPassword"));
    }

    public JPasswordField getPasswordField() {
        return pf;
    }

    @Override
    public void uninstall() {
        if (listener != null) {
            pf.getDocument().removeDocumentListener(listener);
            listener = null;
        }
        tracker.unregisterAll();
    }

    @Override
    public String getContentString() {
        return new String(pf.getPassword());
    }

    @Override
    public void setContentString(String s) {
        pf.setText(s);
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        if (listener == null) {
            listener = new AnyDocumentListener() {
                @Override
                public void anyChange(DocumentEvent e) {
                    callback.run();
                }
            };
            pf.getDocument().addDocumentListener(listener);
        }
    }

    public void setEditable(boolean b) {
        pf.setEditable(b);
    }

    public boolean isEditable() {
        return pf.isEditable();
    }

}
