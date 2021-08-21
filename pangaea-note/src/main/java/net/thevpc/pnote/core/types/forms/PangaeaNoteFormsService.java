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
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldType;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.core.types.forms.templates.UrlCardTemplate;
import net.thevpc.pnote.core.types.forms.refactor.FormsToAnythingContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.forms.editor.PangaeaNoteFormsEditorTypeComponent;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.core.types.forms.search.PangaeaNoteObjectDocumentTextNavigator;
import net.thevpc.pnote.core.types.forms.templates.BankAccountTemplate;
import net.thevpc.pnote.core.types.forms.templates.CreditCardAccountTemplate;
import net.thevpc.pnote.core.types.forms.templates.EthernetConnectionTemplate;
import net.thevpc.pnote.core.types.forms.templates.UrlBookmarkTemplate;
import net.thevpc.pnote.core.types.forms.templates.WifiConnectionTemplate;
import net.thevpc.pnote.core.frame.PangaeaNoteTypes;
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
    public void onInstall(PangaeaNoteApp app) {
        super.onInstall(app);
        app.installTypeReplacer(new FormsToAnythingContentTypeReplacer());
        app.register(new UrlCardTemplate());
        app.register(new EthernetConnectionTemplate());
        app.register(new WifiConnectionTemplate());
        app.register(new UrlBookmarkTemplate());
        app.register(new BankAccountTemplate());
        app.register(new CreditCardAccountTemplate());
        app.installEditorService(new PangaeaNoteEditorService() {
            @Override
            public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
                switch (name) {
                    case PangaeaNoteTypes.EDITOR_FORMS:
                        return new PangaeaNoteFormsEditorTypeComponent(compactMode, win);
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
    public List<? extends Iterator<DocumentTextPart<PangaeaNote>>> resolveTextNavigators(PangaeaNote note) {
        return Arrays.asList(new PangaeaNoteObjectDocumentTextNavigator(app(), note, note.getContent()).iterator()
        );
    }

    @Override
    public NutsElement createDefaultContent() {
        return getContentAsElement(new PangaeaNoteObjectDocument()
                .setDescriptor(
                        new PangaeaNoteObjectDescriptor()
                                .setName("Item")
                                .addField(
                                        new PangaeaNoteFieldDescriptor()
                                                .setName("Title")
                                                .setType(PangaeaNoteFieldType.TEXT)
                                )
                                .addField(
                                        new PangaeaNoteFieldDescriptor()
                                                .setName("Observation")
                                                .setType(PangaeaNoteFieldType.TEXTAREA)
                                )
                )
                .setValues(new ArrayList<>())
        );
    }

    public NutsElement getContentAsElement(PangaeaNoteObjectDocument dynamicDocument) {
        return app().elem().toElement(dynamicDocument);
    }

    public PangaeaNoteObjectDocument getContentAsObject(NutsElement s) {
        if (s != null && s.isString()) {
            return app().elem().parse(s.asString(), PangaeaNoteObjectDocument.class);
        }
        return app().elem().convert(s, PangaeaNoteObjectDocument.class);
    }

    @Override
    public boolean isEmptyContent(NutsElement content) {
        PangaeaNoteObjectDocument a = getContentAsObject(content);
        if (a == null) {
            return true;
        }
        return (a.getDescriptor() == null || a.getDescriptor().getFields() == null || a.getDescriptor().getFields().isEmpty())
                && (a.getValues() == null || a.getValues().isEmpty());
    }
}
