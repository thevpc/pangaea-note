/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.templates;

import net.thevpc.pnote.api.AbstractPangaeaNoteTemplate;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldType;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author thevpc
 */
public class ServerAccountTemplate extends AbstractPangaeaNoteTemplate {

    public ServerAccountTemplate() {
        super("server-account", "datatype.url");
    }

    @Override
    public void prepare(PangaeaNote n, PangaeaNoteApp app) {
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) app.getContentTypeService(PangaeaNoteFormsService.FORMS);
        PangaeaNoteObjectDocument doc = new PangaeaNoteObjectDocument().setDescriptor(new PangaeaNoteObjectDescriptor()
                .addField(new PangaeaNoteFieldDescriptor().setName(str("title", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("serverType", app)).setType(PangaeaNoteFieldType.COMBOBOX).setValues(
                        new ArrayList<>(
                                Arrays.asList(
                                        "SSH",
                                        "FTP",
                                        "MS TERM SVR"
                                )
                        )
                ))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("serverAddress", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("serverPort", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("userName", app)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("password", app)).setType(PangaeaNoteFieldType.PASSWORD))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("notes", app)).setType(PangaeaNoteFieldType.TEXTAREA)));
        n.setContentType(PangaeaNoteFormsService.FORMS.toString());
        n.setContent(s.getContentAsElement(doc.setValues(new ArrayList<>(Arrays.asList(doc.getDescriptor().createObject())))));
    }

}
