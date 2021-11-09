/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame;

import net.thevpc.pnote.api.PangaeaNoteAppExtension;

/**
 *
 * @author thevpc
 */
public interface PangaeaNoteAppExtensionHandler {

    PangaeaNoteAppExtension getExtension();

    PangaeaNoteAppExtensionStatus getState();

    void setDisabled(boolean b);

    boolean checkLoaded();
    
}
