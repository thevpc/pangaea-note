/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.core.swing.SwingApplicationsHelper;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;

/**
 *
 * @author vpc
 */
public class TextAreaComponent extends JPanel implements FormComponent {

    private JTextArea textArea = new JTextArea();
    private AnyDocumentListener listener;
    private SwingApplicationsHelper.Tracker tracker;

    public TextAreaComponent(PangaeaNoteGuiApp sapp) {
        super(new BorderLayout());
        tracker = new SwingApplicationsHelper.Tracker(sapp.app());
        add(new JScrollPane(textArea));
        setPreferredSize(new Dimension(200, 100));
        setMinimumSize(new Dimension(100, 100));
        GuiHelper.installUndoRedoManager(textArea);
        JPopupMenu p = new JPopupMenu();
        p.add(new JMenuItem(
                tracker.registerStandardAction(
                        () -> {
                            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                            try {
                                clip.setContents(new StringSelection(textArea.getText()), null);
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
                                Transferable content = clip.getContents(textArea);
                                String txt = content.getTransferData(
                                        new DataFlavor(String.class, "String")).toString();
                                textArea.setText(txt);
                            } catch (Exception ex) {
                                //ex.printStackTrace();
                            }
                        }, "paste"
                )));
        textArea.setComponentPopupMenu(p);
    }

    @Override
    public String getContentString() {
        return textArea.getText();
    }

    @Override
    public void setContentString(String s) {
        textArea.setText(s);
    }

    public void uninstall() {
        if (listener != null) {
            textArea.getDocument().removeDocumentListener(listener);
            listener = null;
        }
        tracker.unregisterAll();
    }

    public void install(Application app) {
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
            textArea.getDocument().addDocumentListener(listener);
        }
    }

    @Override
    public void setEditable(boolean b) {
        textArea.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return textArea.isEditable();
    }
}
