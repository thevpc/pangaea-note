/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author thevpc
 */
public interface PangaeaNoteTypeServiceBase {

    PangaeaNoteMimeType getContentType();

    default EditTypeComponent createEditPanel(PangaeaNoteFrame win) {
        return null;
    }

}
