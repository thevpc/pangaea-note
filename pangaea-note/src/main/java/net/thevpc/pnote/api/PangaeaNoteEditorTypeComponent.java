/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import javax.swing.JComponent;

import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.api.model.HighlightType;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.api.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteEditorTypeComponent {

    AppComponent component();

    void uninstall();

    void setNote(PangaeaNoteExt note, PangaeaNoteFrame win);

    void setEditable(boolean b);

    boolean isEditable();

    boolean isCompactMode();

    default public void moveTo(int pos){
        
    }

    default void removeHighlights(HighlightType hightlightType) {
    }

    default void highlight(int fromm, int too, HighlightType hightlightType) {
    }
}
