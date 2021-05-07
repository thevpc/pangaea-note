/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import javax.swing.JComponent;
import net.thevpc.pnote.api.model.HighlightType;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteEditorTypeComponent {

    JComponent component();

    void uninstall();

    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow win);

    public void setEditable(boolean b);

    public boolean isEditable();

    public boolean isCompactMode();

    default public void moveTo(int pos){
        
    }

    default void removeHighlights(HighlightType hightlightType) {
    }

    default void highlight(int fromm, int too, HighlightType hightlightType) {
    }
}
