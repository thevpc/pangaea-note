/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

/**
 *
 * @author vpc
 */
public class PasswordComponent extends HorizontalPane implements FormComponent {

    private PasswordField pf;
    private Runnable callback;

    public PasswordComponent(PangaeaNoteFrame win) {
        super(win.app());
        pf=new PasswordField(win.app());
        ContextMenu p = new ContextMenu(app());
        pf.contextMenu().set(p);
        p.children().add(new Button("copy",()-> app().clipboard().putString(pf.text().get().value()),app()));
        p.children().add(new Button("paste",()-> pf.text().set(Str.of(app().clipboard().getString())),app()));
        pf.text().onChange(x->textChanged());
        children().addAll(pf);
    }

    private void textChanged() {
        if(callback!=null){
            callback.run();
        }
    }

    @Override
    public void install(Application app) {
//        showPassword.setText(app.i18n().getString("Message.showPassword"));
    }


    @Override
    public void uninstall() {
    }

    @Override
    public String getContentString() {
        return new String(pf.text().get().value());
    }

    @Override
    public void setContentString(String s) {
        pf.text().set(Str.of(s));
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback=callback;
    }

    public void setEditable(boolean b) {
        pf.editable().set(b);
    }

    public boolean isEditable() {
        return pf.editable().get();
    }

}
