/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.refactor;

import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author thevpc
 */
public class FormsToAnythingContentTypeReplacer implements PangaeaContentTypeReplacer {


    public FormsToAnythingContentTypeReplacer() {

    }

    @Override
    public int getScore(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        if (PangaeaNoteFormsService.FORMS.equals(oldContentType)) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        if (PangaeaNoteFormsService.FORMS.equals(oldContentType)) {
            toUpdate.setContent(app.getContentTypeService(newContentType).createDefaultContent()
            );
            toUpdate.setContentType(newContentType.toString());
            toUpdate.setEditorType(app.normalizeEditorType(newContentType, toUpdate.getEditorType()));
        }

    }

}
