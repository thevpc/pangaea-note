/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.refactor;

import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class FormsToAnythingContentTypeReplacer implements PangaeaContentTypeReplacer {


    public FormsToAnythingContentTypeReplacer() {

    }

    @Override
    public int getSupportLevel(PangaeaNoteExt toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteService service, PangaeaNoteFrame frame) {
        if (PangaeaNoteFormsService.FORMS.equals(oldContentType)) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNoteExt toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteService service) {
        if (PangaeaNoteFormsService.FORMS.equals(oldContentType)) {
            toUpdate.setContent(
                    service.getContentTypeService(newContentType).createDefaultContent()
            );
            toUpdate.setContentType(newContentType.toString());
            toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
        }

    }

}
