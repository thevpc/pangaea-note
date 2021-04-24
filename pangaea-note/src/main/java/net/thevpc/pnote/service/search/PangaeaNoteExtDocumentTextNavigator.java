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
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteExtDocumentTextNavigator implements DocumentTextNavigator {

    private PangaeaNoteService service;
    private PangaeaNoteExt note;

    public PangaeaNoteExtDocumentTextNavigator(PangaeaNoteService service, PangaeaNoteExt source) {
        this.service = service;
        note = source == null ? new PangaeaNoteExt() : source;
    }

    @Override
    public Iterator<DocumentTextPart<PangaeaNoteExt>> iterator() {
        List<Iterator<DocumentTextPart<PangaeaNoteExt>>> parts = new ArrayList<>();
        parts.add(new StringDocumentTextNavigator("name", note, "name", note.getName()).iterator());
        parts.add(new StringDocumentTextNavigator("tags", note, "tags", String.join(" ", note.getTags())).iterator());
        PangaeaNoteContentType ct = service.normalizeContentType(note.getContentType());
        List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> i = service.getContentTypeService(ct)
                .resolveTextNavigators(note);
        parts.addAll(i);
        List<DocumentTextPart<PangaeaNoteExt>> li = new ArrayList<>();
        for (Iterator<DocumentTextPart<PangaeaNoteExt>> o : parts) {
            o.forEachRemaining(li::add);
        }
        return li.iterator();
    }

    @Override
    public String toString() {
        return String.valueOf("note: "+note.getName());
    }

}
