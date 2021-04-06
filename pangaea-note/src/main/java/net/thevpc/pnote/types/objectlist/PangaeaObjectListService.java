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
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
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

/**
 *
 * @author vpc
 */
public class PangaeaObjectListService implements PangaeaNoteTypeService {

    public static final String OBJECT_LIST = "application/pangaea-object-list";

    private PangaeaNoteService service;

    public PangaeaObjectListService() {
    }

    @Override
    public ContentTypeSelector[] getContentTypeSelectors() {
        return new ContentTypeSelector[]{
            new ContentTypeSelector(getContentType(), getContentType(), PangaeaNoteTypes.EDITOR_OBJECT_LIST, "simple-documents", 0)
        };
    }

    @Override
    public String getContentType() {
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

    public String[] normalizeEditorTypes(String editorType) {
        return new String[]{PangaeaNoteTypes.EDITOR_OBJECT_LIST};
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(
                new PangageaNoteObjectDocumentTextNavigator(service, note, note.getContent()).iterator()
        );
    }

    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteGuiApp sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_OBJECT_LIST:
                return new PangaeaNoteObjectDocumentComponent(compactMode, sapp);
        }
        return null;
    }

    @Override
    public String createDefaultContent() {
        return stringifyDescriptor(new PangageaNoteObjectDocument()
                .setDescriptor(new PangaeaNoteObjectDescriptor())
                .setValues(new ArrayList<>())
        );
    }

    public String stringifyDescriptor(PangageaNoteObjectDocument dynamicDocument) {
        return service.stringifyAny(dynamicDocument);
    }

    public PangageaNoteObjectDocument parseObjectDocument(String s) {
        return service.parseAny(s, PangageaNoteObjectDocument.class);
    }

    @Override
    public boolean isEmptyContent(String content) {
        if (content == null || content.trim().length() == 0) {
            return true;
        }
        PangageaNoteObjectDocument a = parseObjectDocument(content);
        if (a == null) {
            return true;
        }
        return (a.getDescriptor() == null || a.getDescriptor().getFields() == null || a.getDescriptor().getFields().isEmpty())
                && (a.getValues() == null || a.getValues().isEmpty());
    }
}
