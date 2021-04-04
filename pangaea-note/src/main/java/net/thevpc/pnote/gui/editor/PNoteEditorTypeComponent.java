/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor;

import javax.swing.JComponent;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public interface PNoteEditorTypeComponent {

    JComponent component();

    void uninstall();

    public void setNote(PangaeaNoteExt note, PangaeaNoteGuiApp sapp);

    public void setEditable(boolean b);

    public boolean isEditable();
}
