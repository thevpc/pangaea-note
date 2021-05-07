/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.html.refactor;

import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PlainToHtmlContentTypeReplacer implements PangaeaContentTypeReplacer {


    public PlainToHtmlContentTypeReplacer() {

    }

    @Override
    public int getSupportLevel(PangaeaNoteExt toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteService service) {
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
    public void changeNoteContentType(PangaeaNoteExt toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteService service) {
        if (PangaeaContentTypes.isSourceCode(oldContentType)) {
            if (PangaeaContentTypes.isSourceCode(newContentType)) {
                toUpdate.setContentType(newContentType.toString());
                toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
            }
        }
        if (PangaeaContentTypes.isXmlLike(oldContentType)) {
            if (PangaeaContentTypes.isXmlLike(newContentType)) {
                toUpdate.setContentType(newContentType.toString());
                toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
            }
        }
        if (PangaeaContentTypes.isFormattedText(oldContentType)) {
            if (PangaeaContentTypes.isFormattedText(newContentType)) {
                toUpdate.setContentType(newContentType.toString());
                toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
            }
        }
    }

}
