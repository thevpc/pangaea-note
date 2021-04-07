/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.objectlist.templates;

import java.util.ArrayList;
import java.util.Arrays;
import net.thevpc.pnote.types.objectlist.model.PangageaNoteObjectDocument;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteFieldType;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.templates.AbstractPangaeaNoteTemplate;
import net.thevpc.pnote.types.objectlist.PangaeaObjectListService;

/**
 *
 * @author vpc
 */
public class BankAccountTemplate extends AbstractPangaeaNoteTemplate {

    public BankAccountTemplate() {
        super("bank-account", "datatype-money");
    }

    @Override
    public void prepare(PangaeaNote n, PangaeaNoteService service) {
        PangaeaObjectListService s = (PangaeaObjectListService)service.getContentTypeService(PangaeaObjectListService.OBJECT_LIST);
        PangageaNoteObjectDocument doc = new PangageaNoteObjectDocument().setDescriptor(new PangaeaNoteObjectDescriptor()
                .addField(new PangaeaNoteFieldDescriptor().setName(str("title", service)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("agency", service)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("agencyAddress", service)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("rib", service)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("iban", service)).setType(PangaeaNoteFieldType.TEXT))
                .addField(new PangaeaNoteFieldDescriptor().setName(str("notes", service)).setType(PangaeaNoteFieldType.TEXTAREA)));
        n.setContentType(PangaeaObjectListService.OBJECT_LIST.toString());
        n.setContent(s.getContentAsElement(doc.setValues(new ArrayList<>(Arrays.asList(doc.getDescriptor().createObject())))));
    }

}