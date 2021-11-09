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
import net.thevpc.pnote.api.AbstractPangaeaNoteTemplate;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author thevpc
 */
public class WifiConnectionTemplate extends AbstractPangaeaNoteTemplate {

    public WifiConnectionTemplate() {
        super("wifi-connection", "wifi");
    }

    @Override
    public void prepare(PangaeaNote n, PangaeaNoteApp app) {
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) app.getContentTypeService(PangaeaNoteFormsService.FORMS);
        PangaeaNoteObjectDocument doc = new PangaeaNoteObjectDocument().setDescriptor(new PangaeaNoteObjectDescriptor()
                .addField(new PangaeaNoteFieldDescriptor().setName(str("title", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("security", app)).setType(PangaeaNoteFieldType.COMBOBOX)
                        .addValue(str("security.wpa", app))
                        .addValue(str("security.wep", app))
                        .addValue(str("security.none", app))
                )
                .addField(new PangaeaNoteFieldDescriptor().setName(str("ssid", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("passphrase", app)).setType(PangaeaNoteFieldType.PASSWORD))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("ip", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("mask", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("dns", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("gateway", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("notes", app)).setType(PangaeaNoteFieldType.TEXTAREA)));
        n.setContentType(PangaeaNoteFormsService.FORMS.toString());
        n.setContent(s.getContentAsElement(doc.setValues(new ArrayList<>(Arrays.asList(doc.getDescriptor().createObject())))));
    }

}
