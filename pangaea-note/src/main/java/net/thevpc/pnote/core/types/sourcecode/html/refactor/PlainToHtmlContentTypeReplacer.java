/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.html.refactor;

import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.gui.PangaeaNoteApp;

/**
 *
 * @author vpc
 */
public class PlainToHtmlContentTypeReplacer implements PangaeaContentTypeReplacer {


    public PlainToHtmlContentTypeReplacer() {

    }

    @Override
    public int getSupportLevel(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        if (PangaeaContentTypes.isSourceCode(oldContentType)) {
            if (PangaeaContentTypes.isSourceCode(newContentType)) {
                return 10;
            }
        }
        if (PangaeaContentTypes.isXmlLike(oldContentType)) {
            if (PangaeaContentTypes.isXmlLike(newContentType)) {
                return 10;
            }
        }
        if (PangaeaContentTypes.isFormattedText(oldContentType)) {
            if (PangaeaContentTypes.isFormattedText(newContentType)) {
                return 10;
            }
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app) {
        if (PangaeaContentTypes.isSourceCode(oldContentType)) {
            if (PangaeaContentTypes.isSourceCode(newContentType)) {
                toUpdate.setContentType(newContentType.toString());
                toUpdate.setEditorType(app.normalizeEditorType(newContentType, toUpdate.getEditorType()));
            }
        }
        if (PangaeaContentTypes.isXmlLike(oldContentType)) {
            if (PangaeaContentTypes.isXmlLike(newContentType)) {
                toUpdate.setContentType(newContentType.toString());
                toUpdate.setEditorType(app.normalizeEditorType(newContentType, toUpdate.getEditorType()));
            }
        }
        if (PangaeaContentTypes.isFormattedText(oldContentType)) {
            if (PangaeaContentTypes.isFormattedText(newContentType)) {
                toUpdate.setContentType(newContentType.toString());
                toUpdate.setEditorType(app.normalizeEditorType(newContentType, toUpdate.getEditorType()));
            }
        }
    }

}
