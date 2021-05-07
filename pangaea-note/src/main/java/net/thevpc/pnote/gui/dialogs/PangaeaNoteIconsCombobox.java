/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import net.thevpc.common.swing.NamedValue;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.common.swing.combo.JComboBoxHelper;

/**
 *
 * @author vpc
 */
public class PangaeaNoteIconsCombobox extends JComboBox {

    private PangaeaNoteWindow win;

    public PangaeaNoteIconsCombobox(PangaeaNoteWindow win) {
        this.win = win;
        List<NamedValue> list = new ArrayList<>();
        list.add(new NamedValue(false, "", win.app().i18n().getString("Icon.none"), null, 0));
        for (String icon : win.service().getAllIcons()) {
            list.add(createIconValue(icon));
        }
        JComboBoxHelper.prepareCombobox(this, 
                icon->(icon != null && icon.length() > 0) ? win.app().iconSets().icon(icon).get() : null
                , list.toArray(new NamedValue[0]));
    }

    protected String getSelectedIcon() {
        NamedValue a = (NamedValue) getSelectedItem();
        return a == null ? null : a.getId();
    }

    protected NamedValue createIconValue(String id) {
        if (id.startsWith("content-type.")) {
            return new NamedValue(false, id,
                    win.app().i18n().getString(id),
                    id, 0);
        }
        if (id.startsWith("datatype.")) {
            return new NamedValue(false, id,
                    win.app().i18n().getString(id),
                    id, 0);
        }
        return new NamedValue(false, id,
                win.app().i18n().getString("Icon." + id),
                id, 0);
    }

}
