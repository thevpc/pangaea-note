/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import net.thevpc.pnote.api.PangaeaNoteAppExtension;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteAppExtensionHandler {

    PangaeaNoteAppExtension getExtension();

    PangaeaNoteAppExtensionStatus getState();

    void setDisabled(boolean b);

    boolean checkLoaded();
    
}
