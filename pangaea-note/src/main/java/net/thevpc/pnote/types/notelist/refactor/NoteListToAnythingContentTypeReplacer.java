/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.notelist.refactor;

import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.refactor.PangaeaContentTypeReplacer;
import net.thevpc.pnote.types.notelist.PangaeaNoteListService;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class NoteListToAnythingContentTypeReplacer implements PangaeaContentTypeReplacer {

    private PangaeaNoteService service;

    public NoteListToAnythingContentTypeReplacer(PangaeaNoteService service) {
        this.service = service;
    }

    @Override
    public int getSupportLevel(PangaeaNoteExt toUpdate, PangaeaNoteContentType oldContentType, PangaeaNoteContentType newContentType) {
        if (PangaeaNoteListService.C_NOTE_LIST.equals(oldContentType)) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNoteExt toUpdate, PangaeaNoteContentType oldContentType, net.thevpc.pnote.model.PangaeaNoteContentType newContentType) {
        if (PangaeaNoteListService.C_NOTE_LIST.equals(oldContentType)) {
            toUpdate.setContent(service.getContentTypeService(newContentType).createDefaultContent());
            toUpdate.setContentType(newContentType.toString());
            toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
        }

    }

}
