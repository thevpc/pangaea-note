/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.api.model.HighlightType;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author thevpc
 */
public interface PangaeaNoteEditorTypeComponent extends AppComponent {

    void uninstall();

    void setNote(PangaeaNote note);

    void setEditable(boolean b);

    boolean isEditable();

    boolean isCompactMode();

    default void moveTo(int pos) {

    }

    default void removeHighlights(HighlightType highlightType) {
    }

    default void highlight(int fromm, int too, HighlightType hightlightType) {
    }
}
