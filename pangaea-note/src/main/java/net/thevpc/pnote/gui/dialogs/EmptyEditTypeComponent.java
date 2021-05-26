/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Application;
import net.thevpc.echo.BorderPane;
import net.thevpc.echo.Label;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.api.EditTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public class EmptyEditTypeComponent extends BorderPane implements EditTypeComponent{

    public EmptyEditTypeComponent(Application application) {
        super(application);
        Label options = new Label(Str.of("No Options available"), application);
        options.enabled().set(false);
        children().add(options);
    }

    @Override
    public void loadFrom(PangaeaNote note) {
        //
    }

    @Override
    public void loadTo(PangaeaNote note) {
        //
    }
    
}
