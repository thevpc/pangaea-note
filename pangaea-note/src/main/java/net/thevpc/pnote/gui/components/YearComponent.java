/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.echo.Application;
import net.thevpc.echo.ComboBox;
import net.thevpc.echo.ContextMenu;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author vpc
 */
public class YearComponent extends ComboBox<String> implements FormComponent {

    private Runnable callback;

    public YearComponent(PangaeaNoteFrame frame) {
        super(String.class, frame.app());
        values().addCollection(IntStream.range(1970, 2070).mapToObj(x -> String.valueOf(x)).collect(Collectors.toList()));
        selection().onChange((e) -> callOnValueChanged());
    }

    private void callOnValueChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    public void setSelectValues(List<String> values) {
        //
    }

    @Override
    public String getContentString() {
        return selection().get();
    }

    @Override
    public void setContentString(String s) {
        selection().set(s);
    }

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
        enabled().set(b);
    }

    @Override
    public boolean isEditable() {
        return enabled().get();
    }

}
