/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.source;

import net.thevpc.jeep.editor.JEditorPaneBuilder;
import net.thevpc.pnote.gui.PangaeaNoteWindow;

/**
 *
 * @author vpc
 */
public interface SourceEditorPaneExtension {

    public void uninstall(JEditorPaneBuilder editorBuilder, PangaeaNoteWindow sapp);
    
    public void prepareEditor(JEditorPaneBuilder editorBuilder, boolean compactMode, PangaeaNoteWindow sapp);

}
