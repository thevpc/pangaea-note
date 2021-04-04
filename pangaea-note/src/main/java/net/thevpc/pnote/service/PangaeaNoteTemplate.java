/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import net.thevpc.pnote.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteTemplate {

    String getId();
    String getIcon();

    void prepare(PangaeaNote n, PangaeaNoteService sapp);

    public default String getLabel(PangaeaNoteService sapp) {
        return null;
    }
    
}
