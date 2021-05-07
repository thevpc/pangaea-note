/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.templates;

import java.util.ArrayList;
import java.util.Arrays;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldType;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.api.AbstractPangaeaNoteTemplate;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;

/**
 *
 * @author vpc
 */
public class UrlBookmarkTemplate extends AbstractPangaeaNoteTemplate {

    public UrlBookmarkTemplate() {
        super("web-url", "url");
    }

    @Override
    public void prepare(PangaeaNote n, PangaeaNoteService win) {
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) win.getContentTypeService(PangaeaNoteFormsService.FORMS);
        PangaeaNoteObjectDocument doc = new PangaeaNoteObjectDocument().setDescriptor(new PangaeaNoteObjectDescriptor()
                        .addField(new PangaeaNoteFieldDescriptor().setName(str("title", win)).setType(PangaeaNoteFieldType.TEXT))
                        .addField(new PangaeaNoteFieldDescriptor().setName(str("url", win)).setType(PangaeaNoteFieldType.URL))
                        .addField(new PangaeaNoteFieldDescriptor().setName(str("notes", win)).setType(PangaeaNoteFieldType.TEXTAREA)));
        n.setContentType(PangaeaNoteFormsService.FORMS.toString());
        n.setContent(s.getContentAsElement(doc.setValues(new ArrayList<>(Arrays.asList(doc.getDescriptor().createObject())))));
    }
}
