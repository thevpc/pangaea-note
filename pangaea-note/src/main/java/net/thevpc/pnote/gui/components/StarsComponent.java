/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.echo.*;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.List;

/**
 *
 * @author vpc
 */
public class StarsComponent extends StarsField<Integer> implements FormComponent {

    private Runnable callback;

    public StarsComponent(PangaeaNoteFrame win) {
        super(Integer.class, win.app());
        value().onChange(() -> {
            if (callback != null) {
                callback.run();
            }
        });
    }

    public void setSelectValues(List<String> newValues) {
        //
    }

    @Override
    public String getContentString() {
        return value().getOr(x -> x == null ? "" : x.toString());
    }

    @Override
    public void setContentString(String s) {
        int v = 0;
        try {
            v = Integer.parseInt(s);
        } catch (Exception ex) {

        }
        value().set(v);
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
