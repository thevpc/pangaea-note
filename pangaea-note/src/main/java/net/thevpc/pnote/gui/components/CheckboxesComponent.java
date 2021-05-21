/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.common.i18n.Str;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.echo.*;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vpc
 */
public class CheckboxesComponent extends HorizontalPane implements FormComponent {

    private List<CheckBox> checkBoxes = new ArrayList<>();
    private Runnable callback;
    private PropertyListener itemListener;
    private boolean editable = true;

    public CheckboxesComponent(PangaeaNoteFrame win) {
        super(win.app());
        itemListener = new PropertyListener() {
            @Override
            public void propertyUpdated(PropertyEvent event) {
                callOnValueChanged();
            }
        };
    }

    private void callOnValueChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    public void setSelectValues(List<String> newValues) {
        while (checkBoxes.size() > newValues.size()) {
            CheckBox c = checkBoxes.remove(checkBoxes.size() - 1);
            c.listeners().remove(itemListener);
            children().remove(c);
        }
        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox c = checkBoxes.get(i);
            String s = newValues.get(i);
            if (s == null) {
                s = "";
            }
            s = s.trim();
            c.text().set(Str.of(s));
        }
        for (int i = checkBoxes.size(); i < newValues.size(); i++) {
            String s = newValues.get(i);
            if (s == null) {
                s = "";
            }
            s = s.trim();
            CheckBox cv = createCheckBox();
            cv.text().set(Str.of(s));
            cv.enabled().set(isEditable());
            cv.onChange(itemListener);
            checkBoxes.add(cv);
            children().add(cv);
        }
    }

    protected CheckBox createCheckBox() {
        CheckBox c = new CheckBox(Str.of("value"),app());
        ContextMenu p = new ContextMenu(app());
        p.children().add(new Button("copy",
                ()->{app().clipboard().putString(c.text().get().value());},
                app()));
        c.contextMenu().set(new ContextMenu(app()));
        return c;
    }

    @Override
    public String getContentString() {
        StringBuilder sb = new StringBuilder();
        for (CheckBox cb : checkBoxes) {
            if (cb.selected().get()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(cb.text().get());
            }
        }
        return (sb.toString());
    }

    @Override
    public void setContentString(String s) {
        Set<String> values = new HashSet<>();
        for (String v : s.split("\n")) {
            v = v.trim();
            if (v.length() > 0) {
                values.add(v);
            }
        }
        for (CheckBox cb : checkBoxes) {
            cb.selected().set(values.contains(cb.text().get().value().trim()));
        }
    }

    @Override
    public void uninstall() {
        callback = null;
    }

    public void install(Application app) {
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void setEditable(boolean b) {
        for (CheckBox checkBoxe : checkBoxes) {
            checkBoxe.enabled().set(isEditable());
        }
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

}
