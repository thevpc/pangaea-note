/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.util;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import net.thevpc.common.swing.NamedValue;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.PangaeaNoteTypes;

/**
 *
 * @author vpc
 */
public class PangaeaNoteIconsCombobox extends JComboBox {

    private PangaeaNoteWindow sapp;

    public PangaeaNoteIconsCombobox(PangaeaNoteWindow sapp) {
        this.sapp = sapp;
        List<NamedValue> list = new ArrayList<>();
        list.add(new NamedValue(false, "", sapp.app().i18n().getString("Icon.none"), null,0));
        for (String icon : PangaeaNoteTypes.ALL_USER_ICONS) {
            list.add(createIconValue(icon));
        }
        ComboboxHelper.prepareCombobox(this, sapp.app(), list.toArray(new NamedValue[0]));
    }

    protected String getSelectedIcon() {
        NamedValue a = (NamedValue)getSelectedItem();
        return a==null?null:a.getId();
    }
    
    protected NamedValue createIconValue(String id) {
        return new NamedValue(false, id,
                sapp.app().i18n().getString("Icon." + id),
                id,0);
    }

}
