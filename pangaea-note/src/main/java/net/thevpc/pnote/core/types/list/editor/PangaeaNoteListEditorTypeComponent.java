/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.echo.ScrollPane;
import net.thevpc.echo.*;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

/**
 * @author vpc
 */
public class PangaeaNoteListEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    //    private JComponentList<PangaeaNoteExt> componentList;
    private PangaeaNoteListEditorContainer componentList;
    private PangaeaNoteFrame frame;
    private boolean compactMode = true;

    public PangaeaNoteListEditorTypeComponent(boolean compactMode, PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        this.compactMode = compactMode;
        componentList = new PangaeaNoteListEditorContainer(frame);
        ScrollPane scrollPane = new ScrollPane(componentList);
        children().add(scrollPane);
    }

    @Override
    public void requestFocus() {
        componentList.requestFocus();
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNote note) {
        this.componentList.setNote(note, frame);
    }

//    public boolean setSelectedName(String name, boolean sel) {
//        return this.componentList.setSelectedName(name, sel);
//    }

    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public void setEditable(boolean b) {
        componentList.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return componentList.editable().get();
    }

}
