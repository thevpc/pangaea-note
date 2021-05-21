/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteAppExtension {

    default void onLoad(PangaeaNoteApp app) {
    }

    default void onDisable(PangaeaNoteApp app) {
    }

    default void onEnable(PangaeaNoteApp app) {
    }

    default void uninstallNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteFrame win) {
        
    }
    default void installNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteFrame win) {
        
    }
}
