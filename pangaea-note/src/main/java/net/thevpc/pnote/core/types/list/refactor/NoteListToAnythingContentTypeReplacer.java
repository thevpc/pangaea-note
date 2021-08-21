/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.list.refactor;

import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author vpc
 */
public class NoteListToAnythingContentTypeReplacer implements PangaeaContentTypeReplacer {

    public NoteListToAnythingContentTypeReplacer() {
    }

    @Override
    public int getSupportLevel(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        if (PangaeaNoteListService.LIST.equals(oldContentType)) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        if (PangaeaNoteListService.LIST.equals(oldContentType)) {
            toUpdate.setContent(app.getContentTypeService(newContentType).createDefaultContent());
            toUpdate.setContentType(newContentType.toString());
            toUpdate.setEditorType(app.normalizeEditorType(newContentType, toUpdate.getEditorType()));
        }

    }

}
