/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.components;

import net.thevpc.echo.*;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldOptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author vpc
 */
public class ToggleButtonComponent extends ToggleButtonGroup<String> implements FormComponent {

    private Runnable callback;

    public ToggleButtonComponent(PangaeaNoteFrame win) {
        super(String.class, win.app());
        selection().onChange(()->{
            if(callback!=null){
                callback.run();
            }
        });
    }

    public void setSelectValues(List<String> newValues) {
        super.values().setCollection(newValues.stream().filter(x->x!=null && x.length()>0).collect(Collectors.toList()));
    }

    @Override
    public String getContentString() {
        
        StringBuilder sb = new StringBuilder();
        for (String val : selection()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(val);
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
        selection().setCollection(values);
    }

    @Override
    public void uninstall() {
        callback = null;
    }

    public void install(Application app, ContextMenu contextMenu) {
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void setEditable(boolean b) {
        editable().set(b);
        enabled().set(b);
    }

    @Override
    public boolean isEditable() {
        return enabled().get();
    }

}
