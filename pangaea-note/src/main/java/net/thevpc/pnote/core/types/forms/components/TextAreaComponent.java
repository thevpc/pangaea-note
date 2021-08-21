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
 * @author vpc
 */
public class TextAreaComponent extends TextArea implements FormComponent {

    Runnable callback;

    public TextAreaComponent(PangaeaNoteFrame win) {
        super(win.app());
        ContextMenu p = new ContextMenu(app());
        contextMenu().set(p);
        p.children().add(new Button("copy", () -> app().clipboard().putString(text().get().value()), app()));
        p.children().add(new Button("paste", () -> text().set(Str.of(app().clipboard().getString())), app()));
        text().onChange(x -> textChanged());
    }

    public boolean isLargeComponent() {
        return true;
    }

    @Override
    public String getContentString() {
        return text().get().value();
    }

    @Override
    public void setContentString(String s) {
        text().set(Str.of(s));
    }

    @Override
    public void setContentType(String contentType) {
        if (contentType == null || contentType.length() == 0) {
            contentType = "text/plain";
        }
        textContentType().set(contentType);
    }

    @Override
    public void uninstall() {
        callback = null;
    }

    @Override
    public void install(Application app, ContextMenu contextMenu) {
    }

    private void textChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void setEditable(boolean b) {
        editable().set(b);
    }

    @Override
    public boolean isEditable() {
        return editable().get();
    }

}
