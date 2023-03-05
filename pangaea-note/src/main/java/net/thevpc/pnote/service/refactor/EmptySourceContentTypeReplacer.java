/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.refactor;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author thevpc
 */
public class EmptySourceContentTypeReplacer implements PangaeaContentTypeReplacer {

    public EmptySourceContentTypeReplacer() {
    }

    @Override
    public int getSupportLevel(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        NElement c = toUpdate.getContent();
        if (app.getContentTypeService(oldContentType).isEmptyContent(c)) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        toUpdate.setContentType(newContentType.toString());
        toUpdate.setEditorType(app.normalizeEditorType(newContentType, toUpdate.getEditorType()));
    }

}
