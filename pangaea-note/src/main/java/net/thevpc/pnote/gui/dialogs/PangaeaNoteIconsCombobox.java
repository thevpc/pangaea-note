///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.thevpc.pnote.gui.dialogs;
//
//import java.util.ArrayList;
//import java.util.List;
//import javax.swing.JComboBox;
//import net.thevpc.common.swing.NamedValue;
//import net.thevpc.echo.SimpleItem;
//import net.thevpc.echo.swing.icons.SwingAppImage;
//import net.thevpc.pnote.gui.PangaeaNoteFrame;
//import net.thevpc.common.swing.combo.JComboBoxHelper;
//
///**
// *
// * @author vpc
// */
//public class PangaeaNoteIconsCombobox extends JComboBox {
//
//    private PangaeaNoteFrame win;
//
//    public PangaeaNoteIconsCombobox(PangaeaNoteFrame win) {
//        this.win = win;
//        List<SimpleItem> list = new ArrayList<>();
//        list.add(new SimpleItem(false, "", win.app().i18n().getString("Icon.none"), null, 0));
//        for (String icon : win.service().getAllIcons()) {
//            list.add(createIconValue(icon));
//        }
//        JComboBoxHelper.prepareCombobox(this,
//                icon->(icon != null && icon.length() > 0) ? SwingAppImage.imageIconOf(win.app().iconSets().icon(icon).get()) : null
//                , list.toArray(new SimpleItem[0]));
//    }
//
//    protected String getSelectedIcon() {
//        SimpleItem a = (SimpleItem) getSelectedItem();
//        return a == null ? null : a.getId();
//    }
//
//    protected SimpleItem createIconValue(String id) {
//        if (id.startsWith("content-type.")) {
//            return new SimpleItem(false, id,
//                    win.app().i18n().getString(id),
//                    id, 0);
//        }
//        if (id.startsWith("datatype.")) {
//            return new SimpleItem(false, id,
//                    win.app().i18n().getString(id),
//                    id, 0);
//        }
//        return new SimpleItem(false, id,
//                win.app().i18n().getString("Icon." + id),
//                id, 0);
//    }
//
//}
