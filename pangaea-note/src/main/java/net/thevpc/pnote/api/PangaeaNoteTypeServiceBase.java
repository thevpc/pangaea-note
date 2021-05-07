/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.EditTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteTypeServiceBase {

    PangaeaNoteMimeType getContentType();

    default EditTypeComponent createEditPanel(PangaeaNoteWindow win) {
        return null;
    }

}
