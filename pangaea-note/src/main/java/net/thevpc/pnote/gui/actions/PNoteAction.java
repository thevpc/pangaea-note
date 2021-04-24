/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.actions;

import javax.swing.AbstractAction;
import net.thevpc.pnote.gui.PangaeaNoteWindow;

/**
 *
 * @author vpc
 */
public abstract class PNoteAction extends AbstractAction {

    private PangaeaNoteWindow napp;

    public PNoteAction(String id, PangaeaNoteWindow napp) {
        this.napp = napp;
    }
}
