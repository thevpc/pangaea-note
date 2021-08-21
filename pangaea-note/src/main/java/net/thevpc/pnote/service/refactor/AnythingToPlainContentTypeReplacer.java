/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.refactor;

import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author vpc
 */
public class AnythingToPlainContentTypeReplacer implements PangaeaContentTypeReplacer {


    public AnythingToPlainContentTypeReplacer() {

    }

    @Override
    public int getSupportLevel(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        if (newContentType.getMajor().endsWith("text")) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        if (newContentType.getMajor().endsWith("text")) {
            toUpdate.setContentType(newContentType.toString());
            toUpdate.setEditorType(app.normalizeEditorType(newContentType, toUpdate.getEditorType()));
        }
        
    }

}
