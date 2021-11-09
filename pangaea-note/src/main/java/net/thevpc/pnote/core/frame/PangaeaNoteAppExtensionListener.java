/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame;

/**
 *
 * @author thevpc
 */
public interface PangaeaNoteAppExtensionListener {

    void onExtensionAdded(PangaeaNoteAppExtensionHandler extension);

    void onExtensionStatusChanged(PangaeaNoteAppExtensionHandler extension, PangaeaNoteAppExtensionStatus oldValue, PangaeaNoteAppExtensionStatus newValue);
}
