/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteTemplate extends PangaeaNoteTypeServiceBase{

    default String getGroup(){
        return "templates";
    }

    PangaeaNoteContentType getContentType();

    String getIcon();

    default int getOrder() {
        return 0;
    }

    void prepare(PangaeaNote n, PangaeaNoteService sapp);

    public default String getLabel(PangaeaNoteService sapp) {
        return null;
    }

}
