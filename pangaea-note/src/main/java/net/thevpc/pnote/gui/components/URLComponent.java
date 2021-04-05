/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import net.thevpc.echo.Application;
import net.thevpc.pnote.gui.util.AnyDocumentListener;
import net.thevpc.pnote.gui.util.GuiHelper;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class URLComponent extends JPanel implements FormComponent {

    private JTextField jtf = new JTextField();
    private AnyDocumentListener listener;

    public URLComponent() {
        super(new BorderLayout());
        add(jtf);
        GuiHelper.installUndoRedoManager(jtf);
    }

    @Override
    public String getContentString() {
        return jtf.getText();
    }

    @Override
    public void setContentString(String s) {
        jtf.setText(s);
    }

    public void uninstall() {
        if (listener != null) {
            jtf.getDocument().removeDocumentListener(listener);
            listener = null;
        }
    }

    public void install(Application app) {
    }

    public JTextField getTextField() {
        return jtf;
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
            jtf.getDocument().addDocumentListener(listener);
        }
    }

    @Override
    public void setEditable(boolean b) {
        jtf.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return jtf.isEditable();
    }
}
