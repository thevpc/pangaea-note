/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.thevpc.common.iconset.IconSetConfig;
import net.thevpc.common.iconset.IconSets;
import net.thevpc.common.swing.NamedValue;
import net.thevpc.common.swing.icon.EmptyIcon;
import net.thevpc.pnote.gui.PangaeaNoteWindow;

/**
 *
 * @author vpc
 */
public class PangaeaNoteIconsList extends JPanel {

    private PangaeaNoteWindow win;
    private DefaultListModel dmodel;
    private JList list;

    public PangaeaNoteIconsList(PangaeaNoteWindow win) {
        this.win = win;
        dmodel = new DefaultListModel();
        dmodel.addElement(new NamedValue(false, "", win.app().i18n().getString("Icon.none"), null, 0));
        for (String icon : win.service().getAllIcons()) {
            dmodel.addElement(createIconValue(icon));
        }
        list = new JList(dmodel);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component e = super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
                NamedValue nv = (NamedValue) value;
                String icon = nv.getIcon();
                IconSets iconSets = win.app().iconSets();
                IconSetConfig c = iconSets.config().get();
                setIcon(PangaeaNoteIconsList.this.getIcon(nv.getIcon()));
                return e;
            }

        });
        add(new JScrollPane(list));
    }

    public void addChangeListener(ChangeListener changeListener) {
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                changeListener.stateChanged(new ChangeEvent(PangaeaNoteIconsList.this));
            }
        });
    }

    public void setSelectedIcon(String s) {
        if (s == null) {
            s = "";
        }
        for (int i = 0; i < dmodel.getSize(); i++) {
            NamedValue nv = (NamedValue) dmodel.get(i);
            String id = nv.getId();
            if (id == null) {
                id = "";
            }
            if (id.equals(s)) {
                list.setSelectedIndex(i);
                return;
            }
        }
    }

    public Icon getSelectedIcon() {
        return getIcon(getSelectedIconId());
    }

    public Icon getIcon(String icon) {
        IconSets iconSets = win.app().iconSets();
        IconSetConfig c = iconSets.config().get();
        return ((icon == null || icon.length() == 0) ? new EmptyIcon(c.getWidth(), c.getHeight())
                : iconSets.icon(icon).get());
    }

    public String getSelectedIconId() {
        NamedValue a = (NamedValue) list.getSelectedValue();
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
