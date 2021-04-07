/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.refactor;

import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public interface PangaeaContentTypeReplacer {
    int getSupportLevel(PangaeaNoteExt toUpdate, PangaeaNoteContentType oldContentType, PangaeaNoteContentType newContentType);
    public void changeNoteContentType(PangaeaNoteExt toUpdate, PangaeaNoteContentType oldContentType, net.thevpc.pnote.model.PangaeaNoteContentType newContentType);
}
