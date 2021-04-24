/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui;

import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteAppExtension {

    default public void onLoad(PangaeaNoteWindow sapp) {
    }

    default public void onDisable(PangaeaNoteWindow sapp) {
    }

    default public void onEnable(PangaeaNoteWindow sapp) {
    }

    default void uninstallNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteWindow sapp) {
        
    }
    default void installNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteWindow sapp) {
        
    }
}
