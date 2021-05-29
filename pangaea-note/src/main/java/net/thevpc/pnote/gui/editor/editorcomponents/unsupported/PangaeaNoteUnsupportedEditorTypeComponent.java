/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.unsupported;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Application;
import net.thevpc.echo.BorderPane;
import net.thevpc.echo.Label;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public class PangaeaNoteUnsupportedEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent{
    Label notSupported;

    public PangaeaNoteUnsupportedEditorTypeComponent(Application app) {
        super(app);
        notSupported = new Label(Str.i18n("Message.NotSupported"),app);
        children().add(notSupported);
    }

    @Override
    public boolean isCompactMode() {
        return true;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNote note) {
        notSupported.text().set(Str.of("Not supported "+note.getContentType()));
    }

    @Override
    public void setEditable(boolean b) {
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
