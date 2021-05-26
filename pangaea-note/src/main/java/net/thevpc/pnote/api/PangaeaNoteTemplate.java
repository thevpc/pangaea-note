/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.common.i18n.Str;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.PangaeaNoteService;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteTemplate extends PangaeaNoteTypeServiceBase {

    default String getGroup() {
        return "templates";
    }

    PangaeaNoteMimeType getContentType();

    String getIcon();

    default int getOrder() {
        return 0;
    }

    void prepare(PangaeaNote n, PangaeaNoteService win);

    public default Str getLabel(PangaeaNoteService win) {
        return null;
    }

}
