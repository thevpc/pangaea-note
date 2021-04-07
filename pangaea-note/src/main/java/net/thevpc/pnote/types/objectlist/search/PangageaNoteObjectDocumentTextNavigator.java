/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.objectlist.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteField;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteObject;
import net.thevpc.pnote.types.objectlist.model.PangageaNoteObjectDocument;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.StringToPatternPortionImpl;
import net.thevpc.pnote.util.OtherUtils;
import net.thevpc.pnote.service.search.strsearch.DocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.types.objectlist.PangaeaObjectListService;

/**
 *
 * @author vpc
 */
public class PangageaNoteObjectDocumentTextNavigator implements DocumentTextNavigator<PangaeaNoteExt> {

    private PangaeaNoteService service;
    private PangageaNoteObjectDocument document;
    private PangaeaNoteExt note;

    public PangageaNoteObjectDocumentTextNavigator(PangaeaNoteService service, PangaeaNoteExt note, NutsElement source) {
        this.service = service;
        this.note = note;
        PangaeaObjectListService s = (PangaeaObjectListService) service.getContentTypeService(PangaeaObjectListService.OBJECT_LIST);
        document
                = source == null ? new PangageaNoteObjectDocument()
                        : s.getContentAsObject(source);
    }

    @Override
    public Iterator<DocumentTextPart<PangaeaNoteExt>> iterator() {
        List<DocumentTextPart<PangaeaNoteExt>> all = new ArrayList<>();
        for (PangaeaNoteFieldDescriptor value : document.getDescriptor().getFields()) {
            if (!OtherUtils.isBlank(value.getName())) {
                //String key, String text, T object, String stringValue
                all.add(new StringToPatternPortionImpl<PangaeaNoteExt>("fieldDescriptor", value.getName(), note, value, value.getName()));
            }
        }
        for (PangaeaNoteObject value : document.getValues()) {
            for (PangaeaNoteField field : value.getFields()) {
                String s = field.getValue();
                if (!OtherUtils.isBlank(s)) {
                    all.add(new StringToPatternPortionImpl("fieldValue", s, note, field, s));
                }
            }
        }
        return all.iterator();
    }

    @Override
    public String toString() {
        return "document: " + document.getDescriptor().getName();
    }

}
