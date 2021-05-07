/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.refactor;

import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class EmptySourceContentTypeReplacer implements PangaeaContentTypeReplacer {

    public EmptySourceContentTypeReplacer() {
    }

    @Override
    public int getSupportLevel(PangaeaNoteExt toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteService service) {
        NutsElement c = toUpdate.getContent();
        if (service.getContentTypeService(oldContentType).isEmptyContent(c)) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNoteExt toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteService service) {
        toUpdate.setContentType(newContentType.toString());
        toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
    }

}
