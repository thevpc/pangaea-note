/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.refactor;

import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.model.PangaeaNoteContentType;

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
    public int getSupportLevel(PangaeaNoteExt toUpdate, PangaeaNoteContentType oldContentType, PangaeaNoteContentType newContentType) {
        NutsElement c = toUpdate.getContent();
        if (service.getContentTypeService(oldContentType).isEmptyContent(c)) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNoteExt toUpdate, PangaeaNoteContentType oldContentType, net.thevpc.pnote.model.PangaeaNoteContentType newContentType) {
        toUpdate.setContentType(newContentType.toString());
        toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
    }

}
