/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

/**
 *
 * @author vpc
 */
public class PasswordComponent extends PasswordField implements FormComponent {

    private Runnable callback;

    public PasswordComponent(PangaeaNoteFrame frame) {
        super(frame.app());
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
//        showPassword.setText(app.i18n().getString("Message.showPassword"));
    }

    @Override
    public void uninstall() {
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
    public void setFormChangeListener(Runnable callback) {
        this.callback = callback;
    }

    public void setEditable(boolean b) {
        editable().set(b);
    }

    public boolean isEditable() {
        return editable().get();
    }

}
