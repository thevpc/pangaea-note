/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
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
public class TextFieldComponent extends JPanel implements FormComponent {

    private JTextField textField = new JTextField();
    private AnyDocumentListener listener;
    private SwingApplicationsHelper.Tracker tracker;

    public TextFieldComponent(PangaeaNoteGuiApp sapp) {
        tracker = new SwingApplicationsHelper.Tracker(sapp.app());
        new GridBagLayoutSupport("[txt-===]; insets(2)")
                .bind("txt", textField)
                .apply(this);
        JPopupMenu p = new JPopupMenu();
        p.add(new JMenuItem(
                tracker.registerStandardAction(
                        () -> {
                            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                            try {
                                clip.setContents(new StringSelection(textField.getText()), null);
                            } catch (Exception ex) {
                                //ex.printStackTrace();
                            }
                        }, "copy"
                )));
        p.add(new JMenuItem(
                tracker.registerStandardAction(
                        () -> {
                            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                            try {
                                Transferable content = clip.getContents(textField);
                                String txt = content.getTransferData(
                                        new DataFlavor(String.class, "String")).toString();
                                textField.setText(txt);
                            } catch (Exception ex) {
                                //ex.printStackTrace();
                            }
                        }, "paste"
                )));
        textField.setComponentPopupMenu(p);
    }

    @Override
    public void install(Application app) {
    }

    public JTextField getTextField() {
        return textField;
    }

    @Override
    public void uninstall() {
        if (listener != null) {
            textField.getDocument().removeDocumentListener(listener);
            listener = null;
        }
        tracker.unregisterAll();
    }

    @Override
    public String getContentString() {
        return new String(textField.getText());
    }

    @Override
    public void setContentString(String s) {
        textField.setText(s);
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
            textField.getDocument().addDocumentListener(listener);
        }
    }

    public void setEditable(boolean b) {
        textField.setEditable(b);
    }

    public boolean isEditable() {
        return textField.isEditable();
    }

}