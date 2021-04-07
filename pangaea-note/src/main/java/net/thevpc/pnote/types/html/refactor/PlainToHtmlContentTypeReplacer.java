/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.html.refactor;

import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.refactor.PangaeaContentTypeReplacer;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PlainToHtmlContentTypeReplacer implements PangaeaContentTypeReplacer {

    private PangaeaNoteService service;

    public PlainToHtmlContentTypeReplacer(PangaeaNoteService service) {
        this.service = service;
    }

    @Override
    public int getSupportLevel(PangaeaNoteExt toUpdate, PangaeaNoteContentType oldContentType, PangaeaNoteContentType newContentType) {
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
    public void changeNoteContentType(PangaeaNoteExt toUpdate, PangaeaNoteContentType oldContentType, net.thevpc.pnote.model.PangaeaNoteContentType newContentType) {
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
