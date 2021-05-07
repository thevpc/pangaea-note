/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.unsupported;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class UnsupportedEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent{
    JLabel notSupported = new JLabel("Not supported");

    public UnsupportedEditorTypeComponent() {
        add(notSupported);
    }

    @Override
    public boolean isCompactMode() {
        return true;
    }

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNoteExt note,PangaeaNoteWindow win) {
        notSupported.setText("Not supported "+note.getContentType());
    }

    @Override
    public void setEditable(boolean b) {
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
