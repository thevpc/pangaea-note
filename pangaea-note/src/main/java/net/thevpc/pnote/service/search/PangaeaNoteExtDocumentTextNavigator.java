/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.service.search.strsearch.DocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.gui.PangaeaNoteApp;

/**
 *
 * @author vpc
 */
public class PangaeaNoteExtDocumentTextNavigator implements DocumentTextNavigator {

    private PangaeaNote note;
    private PangaeaNoteApp app;

    public PangaeaNoteExtDocumentTextNavigator(PangaeaNote source, PangaeaNoteApp app) {
        note = source == null ? new PangaeaNote() : source;
        this.app=app;
    }

    @Override
    public Iterator<DocumentTextPart<PangaeaNote>> iterator() {
        List<Iterator<DocumentTextPart<PangaeaNote>>> parts = new ArrayList<>();
        parts.add(new StringDocumentTextNavigator("name", note, "name", note.getName()).iterator());
        parts.add(new StringDocumentTextNavigator("tags", note, "tags", String.join(" ", note.getTags())).iterator());
        PangaeaNoteMimeType ct = app.normalizeContentType(note.getContentType());
        List<? extends Iterator<DocumentTextPart<PangaeaNote>>> i = app.getContentTypeService(ct)
                .resolveTextNavigators(note);
        parts.addAll(i);
        List<DocumentTextPart<PangaeaNote>> li = new ArrayList<>();
        for (Iterator<DocumentTextPart<PangaeaNote>> o : parts) {
            o.forEachRemaining(li::add);
        }
        return li.iterator();
    }

    @Override
    public String toString() {
        return String.valueOf("note: "+note.getName());
    }

}
