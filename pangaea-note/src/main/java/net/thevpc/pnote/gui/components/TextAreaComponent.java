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
import net.thevpc.pnote.gui.util.AnyDocumentListener;
import net.thevpc.pnote.gui.util.GuiHelper;

/**
 *
 * @author vpc
 */
public class TextAreaComponent extends BorderPane implements FormComponent {
    TextArea textArea;
    Runnable callback;
    public TextAreaComponent(PangaeaNoteFrame win) {
        super(win.app());

        children.add(new ScrollPane(textArea=new TextArea(Str.empty(),app())));
        this.prefSize().set(new Dimension(200, 100));
//        setMinimumSize(new Dimension(100, 100));
//        GuiHelper.installUndoRedoManager(textArea);
        ContextMenu p = new ContextMenu(app());
        textArea.contextMenu().set(p);
        p.children().add(new Button("copy",()-> app().clipboard().putString(textArea.text().get().value()),app()));
        p.children().add(new Button("paste",()-> textArea.text().set(Str.of(app().clipboard().getString())),app()));
        textArea.text().onChange(x->textChanged());
    }


    @Override
    public String getContentString() {
        return textArea.text().get().value();
    }

    @Override
    public void setContentString(String s) {
        textArea.text().set(Str.of(s));
    }

    public void uninstall() {
        callback=null;
    }

    public void install(Application app) {
    }
    private void textChanged() {
            if(callback!=null){
                callback.run();
            }
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback=callback;
    }

    @Override
    public void setEditable(boolean b) {
        textArea.editable().set(b);
    }

    @Override
    public boolean isEditable() {
        return textArea.editable().get();
    }
}
