/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.echo.iconset.IconSetConfig;
import net.thevpc.echo.iconset.IconSets;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

/**
 * @author vpc
 */
public class PangaeaNoteIconsList extends BorderPane {

    private PangaeaNoteFrame frame;
    private ChoiceList<String> list;

    public PangaeaNoteIconsList(PangaeaNoteFrame frame) {
        super(frame.app());
        this.title().set(Str.i18n("PangaeaNoteListSettingsComponent.iconsLabel"));
        this.frame = frame;
        list=new ChoiceList<>(String.class, frame.app());
        list.values().add("");
        for (String icon : frame.service().getAllIcons()) {
            list.values().add(icon);
        }
        list.itemRenderer().set(context -> {
            context.setText("");
            context.setIcon(getIcon(context.getValue()));
//            context.setValue(getIcon(context.getValue()));
            context.renderDefault();
        });
        list.parentConstraints().addAll(Layout.FLOW);
        children().add(new ScrollPane(list));
        list().selection().onChange(ee -> {
            smallIcon().set(getSelectedIcon());
        });

    }

//    public void addChangeListener(ChangeListener changeListener) {
//        list.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                changeListener.stateChanged(new ChangeEvent(PangaeaNoteIconsList.this));
//            }
//        });
//    }

    public ChoiceList<String> list() {
        return list;
    }

    public AppImage getSelectedIcon() {
        return getIcon(getSelectedIconId());
    }

    public void setSelectedIcon(String s) {
        if (s == null) {
            s = "";
        }
        list.selection().set(s);
//        for (int i = 0; i < dmodel.getSize(); i++) {
//            SimpleItem nv = (SimpleItem) dmodel.get(i);
//            String id = nv.getId();
//            if (id == null) {
//                id = "";
//            }
//            if (id.equals(s)) {
//                list.setSelectedIndex(i);
//                return;
//            }
//        }
    }

    public AppImage getIcon(String icon) {
        IconSets iconSets = frame.app().iconSets();
        IconSetConfig c = iconSets.config().get();
        return ((icon == null || icon.length() == 0) ? new Image(c.getWidth(), c.getHeight(), null, app())
                : iconSets.icon(icon,iconSet().get()) == null ? null :
                iconSets.icon(icon,iconSet().get())
        );
    }

    public String getSelectedIconId() {
        return list.selection().get();
    }

//    protected SimpleItem createIconValue(String id) {
//        if (id.startsWith("content-type.")) {
//            return new SimpleItem(false, id,
//                    win.getApplication().i18n().getString(id),
//                    id, 0);
//        }
//        if (id.startsWith("datatype.")) {
//            return new SimpleItem(false, id,
//                    win.getApplication().i18n().getString(id),
//                    id, 0);
//        }
//        return new SimpleItem(false, id,
//                win.getApplication().i18n().getString("Icon." + id),
//                id, 0);
//    }

}
