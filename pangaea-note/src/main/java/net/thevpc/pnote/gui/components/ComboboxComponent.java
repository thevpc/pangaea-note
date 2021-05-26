/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.echo.Application;
import net.thevpc.echo.ComboBox;
import net.thevpc.echo.HorizontalPane;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.List;

/**
 *
 * @author vpc
 */
public class ComboboxComponent extends HorizontalPane implements FormComponent {

    private ComboBox<String> cb;
    private Runnable callback;

    public ComboboxComponent(PangaeaNoteFrame frame) {
        super(frame.app());
        cb=new ComboBox<>(String.class,frame.app());
        children().add(cb);
        cb.selection().onChange((e)->callOnValueChanged());
    }

    private void callOnValueChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    public void setSelectValues(List<String> values) {
        cb.values().setAll(values.toArray(new String[0]));
    }

    @Override
    public String getContentString() {
        return cb.selection().get();
    }

    @Override
    public void setContentString(String s) {
        cb.selection().set(s);
    }

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
        cb.enabled().set(b);
    }

    @Override
    public boolean isEditable() {
        return cb.enabled().get();
    }

}
