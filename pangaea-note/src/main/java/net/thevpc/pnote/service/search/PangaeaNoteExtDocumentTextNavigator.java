/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service.search;

import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;

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
        parts.add(new TextStringToPatternHandler("name", note, "name", note.getName()).iterator());
        parts.add(new TextStringToPatternHandler("tags", note, "tags", String.join(" ", note.getTags())).iterator());
        String ct = note.getContentType();
        if (ct == null) {
            ct = PangaeaNoteTypes.PLAIN;
        }
        switch (ct) {
            case PangaeaNoteTypes.OBJECT_LIST: {
                parts.add(new PangageaNoteObjectDocumentTextNavigator(service, note, note.getContent()).iterator());
                break;
            }
            default: {
                parts.add(new TextStringToPatternHandler("content", note, "content", note.getContent()).iterator());
            }
        }
        List<DocumentTextPart<PangaeaNoteExt>> li = new ArrayList<>();
        for (Iterator<DocumentTextPart<PangaeaNoteExt>> o : parts) {
            o.forEachRemaining(li::add);
        }
        return li.iterator();
    }

}
