/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author vpc
 */
public interface PangaeaContentTypeReplacer {

    int getSupportLevel(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app);

    void changeNoteContentType(PangaeaNote toUpdate, PangaeaNoteMimeType oldContentType, PangaeaNoteMimeType newContentType, PangaeaNoteApp app);
}
