/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.model;

import net.thevpc.pnote.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public interface ObservableNoteSelectionListener {

    void onSelectionChanged(PangaeaNoteExt note);
    
}