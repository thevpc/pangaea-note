/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Application;
import net.thevpc.echo.BorderPane;
import net.thevpc.echo.Label;
import net.thevpc.pnote.api.EditTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author thevpc
 */
public class EmptyEditTypeComponent extends BorderPane implements EditTypeComponent{

    public EmptyEditTypeComponent(Application application) {
        super(application);
        Label options = new Label(Str.i18n("Message.NoOptions"), application);
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
