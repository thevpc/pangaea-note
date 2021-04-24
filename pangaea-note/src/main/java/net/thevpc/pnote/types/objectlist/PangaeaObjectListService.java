/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.objectlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.types.objectlist.editor.PangaeaNoteObjectDocumentComponent;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.types.objectlist.model.PangageaNoteObjectDocument;
import net.thevpc.pnote.types.objectlist.templates.BankAccountTemplate;
import net.thevpc.pnote.types.objectlist.templates.CreditCardAccountTemplate;
import net.thevpc.pnote.types.objectlist.templates.EthernetConnectionTemplate;
import net.thevpc.pnote.types.objectlist.templates.UrlBookmarkTemplate;
import net.thevpc.pnote.types.objectlist.templates.UrlCardTemplate;
import net.thevpc.pnote.types.objectlist.templates.WifiConnectionTemplate;
import net.thevpc.pnote.types.objectlist.refactor.ObjectListToAnythingContentTypeReplacer;
import net.thevpc.pnote.types.objectlist.search.PangageaNoteObjectDocumentTextNavigator;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaObjectListService implements PangaeaNoteTypeService {

    public static final PangaeaNoteContentType OBJECT_LIST = PangaeaNoteContentType.of("application/pangaea-object-list");

    private PangaeaNoteService service;

    public PangaeaObjectListService() {
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return OBJECT_LIST;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        this.service = service;
        service.installTypeReplacer(new ObjectListToAnythingContentTypeReplacer(service));
        service.register(new UrlCardTemplate());
        service.register(new EthernetConnectionTemplate());
        service.register(new WifiConnectionTemplate());
        service.register(new UrlBookmarkTemplate());
        service.register(new BankAccountTemplate());
        service.register(new CreditCardAccountTemplate());
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "pangaea-object-list";
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_OBJECT_LIST;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(
                new PangageaNoteObjectDocumentTextNavigator(service, note, note.getContent()).iterator()
        );
    }

    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteWindow sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_OBJECT_LIST:
                return new PangaeaNoteObjectDocumentComponent(compactMode, sapp);
        }
        return null;
    }

    @Override
    public NutsElement createDefaultContent() {
        return getContentAsElement(new PangageaNoteObjectDocument()
                .setDescriptor(new PangaeaNoteObjectDescriptor())
                .setValues(new ArrayList<>())
        );
    }

    public NutsElement getContentAsElement(PangageaNoteObjectDocument dynamicDocument) {
        return service.element().toElement(dynamicDocument);
    }

    public PangageaNoteObjectDocument getContentAsObject(NutsElement s) {
        if(s!=null && s.isString()){
            return service.element().parse(s.asString(), PangageaNoteObjectDocument.class);
        }
        return service.element().convert(s, PangageaNoteObjectDocument.class);
    }

    @Override
    public boolean isEmptyContent(NutsElement content) {
        PangageaNoteObjectDocument a = getContentAsObject(content);
        if (a == null) {
            return true;
        }
        return (a.getDescriptor() == null || a.getDescriptor().getFields() == null || a.getDescriptor().getFields().isEmpty())
                && (a.getValues() == null || a.getValues().isEmpty());
    }
}
