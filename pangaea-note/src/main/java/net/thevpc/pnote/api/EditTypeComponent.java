/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author thevpc
 */
public interface EditTypeComponent extends AppComponent{
    void loadFrom(PangaeaNote note);
    void loadTo(PangaeaNote note);
}
