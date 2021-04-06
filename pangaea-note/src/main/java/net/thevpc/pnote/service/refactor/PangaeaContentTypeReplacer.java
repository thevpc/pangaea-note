/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.refactor;

import net.thevpc.pnote.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public interface PangaeaContentTypeReplacer {
    int getSupportLevel(PangaeaNoteExt toUpdate, String oldContentType, String newContentType);
    public void changeNoteContentType(PangaeaNoteExt toUpdate, String oldContentType, String newContentType);
}
