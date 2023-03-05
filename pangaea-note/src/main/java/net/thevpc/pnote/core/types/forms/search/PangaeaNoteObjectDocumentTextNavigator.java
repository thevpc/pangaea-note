/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.thevpc.nuts.NBlankable;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteField;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObject;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.service.search.strsearch.StringToPatternPortionImpl;
import net.thevpc.pnote.service.search.strsearch.DocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteObjectDocumentTextNavigator implements DocumentTextNavigator<PangaeaNote> {

    private PangaeaNoteApp app;
    private PangaeaNoteObjectDocument document;
    private PangaeaNote note;

    public PangaeaNoteObjectDocumentTextNavigator(PangaeaNoteApp app, PangaeaNote note, NElement source) {
        this.app = app;
        this.note = note;
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) app.getContentTypeService(PangaeaNoteFormsService.FORMS);
        document
                = source == null ? new PangaeaNoteObjectDocument()
                        : s.getContentAsObject(source);
    }

    @Override
    public Iterator<DocumentTextPart<PangaeaNote>> iterator() {
        List<DocumentTextPart<PangaeaNote>> all = new ArrayList<>();
        if (document.getDescriptor().getFields() != null) {
            for (PangaeaNoteFieldDescriptor value : document.getDescriptor().getFields()) {
                if (!NBlankable.isBlank(value.getName())) {
                    //String key, String text, T object, String stringValue
                    all.add(new StringToPatternPortionImpl<PangaeaNote>("fieldDescriptor", value.getName(), note, value, value.getName()));
                }
            }
        }
        if (document.getValues() != null) {
            for (PangaeaNoteObject value : document.getValues()) {
                for (PangaeaNoteField field : value.getFields()) {
                    String s = field.getValue();
                    if (!NBlankable.isBlank(s)) {
                        all.add(new StringToPatternPortionImpl("fieldValue", s, note, field, s));
                    }
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
