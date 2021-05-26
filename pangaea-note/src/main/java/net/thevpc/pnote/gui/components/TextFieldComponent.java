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
 * @author vpc
 */
public class TextFieldComponent extends HorizontalPane implements FormComponent {

    private TextField textField;
    private Runnable callback;

    public TextFieldComponent(PangaeaNoteFrame win) {
        super(win.app());
        textField = new TextField(Str.empty(), win.app());
        children().add(textField);
        ContextMenu p = new ContextMenu(app());
        textField.contextMenu().set(p);
        p.children().add(new Button("copy", () -> app().clipboard().putString(textField.text().get().value()), app()));
        p.children().add(new Button("paste", () -> textField.text().set(Str.of(app().clipboard().getString())), app()));
        textField.text().onChange(x -> textChanged());
    }

    private void textChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    @Override
    public void install(Application app) {
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
        return textField.text().get().value();
    }

    @Override
    public void setContentString(String s) {
        textField.text().set(Str.of(s));
    }

    public TextField getTextField() {
        return textField;
    }

    public void setEditable(boolean b) {
        textField.editable().set(b);
    }

    public boolean isEditable() {
        return textField.editable().get();
    }

}
