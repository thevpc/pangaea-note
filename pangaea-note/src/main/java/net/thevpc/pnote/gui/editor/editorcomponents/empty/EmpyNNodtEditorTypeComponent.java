/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.empty;

import javax.swing.JComponent;
import javax.swing.JPanel;
import net.thevpc.pnote.model.HighlightType;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

/**
 *
 * @author vpc
 */
public class EmpyNNodtEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent{

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public boolean isCompactMode() {
        return true;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNoteExt note,PangaeaNoteWindow sapp) {
    }

    @Override
    public void setEditable(boolean b) {
    
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    
}
