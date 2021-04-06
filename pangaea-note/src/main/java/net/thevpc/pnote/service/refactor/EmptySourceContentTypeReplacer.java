/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.refactor;

import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;

/**
 *
 * @author vpc
 */
public class EmptySourceContentTypeReplacer implements PangaeaContentTypeReplacer {

    private PangaeaNoteService service;

    public EmptySourceContentTypeReplacer(PangaeaNoteService service) {
        this.service = service;
    }

    @Override
    public int getSupportLevel(PangaeaNoteExt toUpdate, String oldContentType, String newContentType) {
        String c = toUpdate.getContent();
        if (c == null || c.trim().length() == 0) {
            return 10;
        }
        PangaeaNoteTypeService cs = service.getContentTypeService(service.normalizeContentType(oldContentType));
        boolean empty = cs.isEmptyContent(c);
        if (empty) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNoteExt toUpdate, String oldContentType, String newContentType) {
        toUpdate.setContentType(newContentType);
        toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
    }

}
