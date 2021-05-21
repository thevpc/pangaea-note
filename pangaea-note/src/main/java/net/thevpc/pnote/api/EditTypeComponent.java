/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import javax.swing.JComponent;

import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public interface EditTypeComponent {
    AppComponent component();
    void loadFrom(PangaeaNote note);
    void loadTo(PangaeaNote note);
}
