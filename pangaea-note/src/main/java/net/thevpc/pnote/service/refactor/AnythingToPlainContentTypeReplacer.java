/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.refactor;

import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;

/**
 *
 * @author vpc
 */
public class AnythingToPlainContentTypeReplacer implements PangaeaContentTypeReplacer {

    private PangaeaNoteService service;

    public AnythingToPlainContentTypeReplacer(PangaeaNoteService service) {
        this.service = service;
    }

    @Override
    public int getSupportLevel(PangaeaNoteExt toUpdate, String oldContentType, String newContentType) {
        if (newContentType.startsWith("text/")) {
            return 10;
        }
        return -1;
    }

    @Override
    public void changeNoteContentType(PangaeaNoteExt toUpdate, String oldContentType, String newContentType) {
        if (newContentType.startsWith("text/")) {
            toUpdate.setContentType(newContentType);
            toUpdate.setEditorType(service.normalizeEditorType(newContentType, toUpdate.getEditorType()));
        }
        
    }

}
