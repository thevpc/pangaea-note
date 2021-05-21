/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.core.types.forms.editor.PangaeaNoteObjectDocumentComponent;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.core.types.forms.templates.BankAccountTemplate;
import net.thevpc.pnote.core.types.forms.templates.CreditCardAccountTemplate;
import net.thevpc.pnote.core.types.forms.templates.EthernetConnectionTemplate;
import net.thevpc.pnote.core.types.forms.templates.UrlBookmarkTemplate;
import net.thevpc.pnote.core.types.forms.templates.UrlCardTemplate;
import net.thevpc.pnote.core.types.forms.templates.WifiConnectionTemplate;
import net.thevpc.pnote.core.types.forms.refactor.FormsToAnythingContentTypeReplacer;
import net.thevpc.pnote.core.types.forms.search.PangaeaNoteObjectDocumentTextNavigator;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteFormsService extends AbstractPangaeaNoteTypeService {
    public static final PangaeaNoteMimeType FORMS = PangaeaNoteMimeType.of("application/x-pangaea-note-forms");

    public PangaeaNoteFormsService() {
        super(FORMS);
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "composed-documents", 0);
    }

    @Override
    public PangaeaNoteMimeType getContentType() {
        return FORMS;
    }

    @Override
    public void onInstall(PangaeaNoteService service, PangaeaNoteApp app) {
        super.onInstall(service, app);
        service.installTypeReplacer(new FormsToAnythingContentTypeReplacer());
        service.register(new UrlCardTemplate());
        service.register(new EthernetConnectionTemplate());
        service.register(new WifiConnectionTemplate());
        service.register(new UrlBookmarkTemplate());
        service.register(new BankAccountTemplate());
        service.register(new CreditCardAccountTemplate());
        app.installEditorService(new PangaeaNoteEditorService() {
            @Override
            public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
                switch (name) {
                    case PangaeaNoteTypes.EDITOR_FORMS:
                        return new PangaeaNoteObjectDocumentComponent(compactMode, win);
                }
                return null;
            }

            @Override
            public void onInstall(PangaeaNoteApp app) {

            }
        });

    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_FORMS;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note, PangaeaNoteFrame frame) {
        return Arrays.asList(new PangaeaNoteObjectDocumentTextNavigator(service(), note, note.getContent()).iterator()
        );
    }

    @Override
    public NutsElement createDefaultContent() {
        return getContentAsElement(new PangaeaNoteObjectDocument()
                .setDescriptor(new PangaeaNoteObjectDescriptor())
                .setValues(new ArrayList<>())
        );
    }

    public NutsElement getContentAsElement(PangaeaNoteObjectDocument dynamicDocument) {
        return service().element().toElement(dynamicDocument);
    }

    public PangaeaNoteObjectDocument getContentAsObject(NutsElement s) {
        if (s != null && s.isString()) {
            return service().element().parse(s.asString(), PangaeaNoteObjectDocument.class);
        }
        return service().element().convert(s, PangaeaNoteObjectDocument.class);
    }

    @Override
    public boolean isEmptyContent(NutsElement content, PangaeaNoteFrame frame) {
        PangaeaNoteObjectDocument a = getContentAsObject(content);
        if (a == null) {
            return true;
        }
        return (a.getDescriptor() == null || a.getDescriptor().getFields() == null || a.getDescriptor().getFields().isEmpty())
                && (a.getValues() == null || a.getValues().isEmpty());
    }
}
