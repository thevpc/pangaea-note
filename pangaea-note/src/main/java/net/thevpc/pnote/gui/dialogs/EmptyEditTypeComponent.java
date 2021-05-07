/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.pnote.api.EditTypeComponent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public class EmptyEditTypeComponent extends JPanel implements EditTypeComponent{

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void loadFrom(PangaeaNote note) {
        //
    }

    @Override
    public void loadTo(PangaeaNote note) {
        //
    }
    
}
