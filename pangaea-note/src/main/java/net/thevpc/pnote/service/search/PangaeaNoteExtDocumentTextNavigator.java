/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteExtDocumentTextNavigator implements DocumentTextNavigator {

    private PangaeaNoteService service;
    private PangaeaNoteExt note;
    private PangaeaNoteFrame frame;

    public PangaeaNoteExtDocumentTextNavigator(PangaeaNoteService service, PangaeaNoteExt source, PangaeaNoteFrame frame) {
        this.service = service;
        note = source == null ? new PangaeaNoteExt() : source;
        this.frame=frame;
    }

    @Override
    public Iterator<DocumentTextPart<PangaeaNoteExt>> iterator() {
        List<Iterator<DocumentTextPart<PangaeaNoteExt>>> parts = new ArrayList<>();
        parts.add(new StringDocumentTextNavigator("name", note, "name", note.getName()).iterator());
        parts.add(new StringDocumentTextNavigator("tags", note, "tags", String.join(" ", note.getTags())).iterator());
        PangaeaNoteMimeType ct = service.normalizeContentType(note.getContentType());
        List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> i = service.getContentTypeService(ct)
                .resolveTextNavigators(note, frame);
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
