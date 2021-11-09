/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.components;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldOptions;

/**
 *
 * @author thevpc
 */
public class URLComponent extends TextField implements FormComponent {
    private Runnable callback;

    public URLComponent(PangaeaNoteFrame win) {
        super(win.app());
        ContextMenu p = new ContextMenu(app());
        contextMenu().set(p);
        p.children().add(new Button("copy", () -> app().clipboard().putString(text().get().value()), app()));
        p.children().add(new Button("paste", () -> text().set(Str.of(app().clipboard().getString())), app()));
        text().onChange(x -> textChanged());
    }

    private void textChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    @Override
    public void install(Application app, ContextMenu contextMenu) {
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void uninstall() {
        this.callback=null;
    }

    @Override
    public String getContentString() {
        return text().get().value();
    }

    @Override
    public void setContentString(String s) {
        text().set(Str.of(s));
    }

    public void setEditable(boolean b) {
        editable().set(b);
    }

    public boolean isEditable() {
        return editable().get();
    }

}
