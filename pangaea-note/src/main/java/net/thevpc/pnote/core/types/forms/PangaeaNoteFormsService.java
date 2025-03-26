/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms;

import java.util.ArrayList;
import java.util.Iterator;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldType;
import net.thevpc.pnote.core.types.forms.templates.*;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.core.types.forms.refactor.FormsToAnythingContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.forms.editor.PangaeaNoteFormsEditorTypeComponent;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.core.types.forms.search.PangaeaNoteObjectDocumentTextNavigator;
import net.thevpc.pnote.core.frame.PangaeaNoteTypes;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;

/**
 *
 * @author thevpc
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
        app.register(new WebAccountTemplate());
        app.register(new EthernetConnectionTemplate());
        app.register(new WifiConnectionTemplate());
        app.register(new UrlBookmarkTemplate());
        app.register(new BankAccountTemplate());
        app.register(new CreditCardAccountTemplate());
        app.register(new EmailAccountTemplate());
        app.register(new ServerAccountTemplate());
        app.register(new AppAccountTemplate());
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
    public Iterator<DocumentTextPart<PangaeaNote>> resolveTextNavigators(PangaeaNote note) {
        return new PangaeaNoteObjectDocumentTextNavigator(app(), note, note.getContent()).iterator();
    }

    @Override
    public NElement createDefaultContent() {
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

    public NElement getContentAsElement(PangaeaNoteObjectDocument dynamicDocument) {
        return app().elem().toElement(dynamicDocument);
    }

    public PangaeaNoteObjectDocument getContentAsObject(NElement s) {
        if (s != null && s.isString()) {
            return app().elem().parse(s.asStringValue().get(), PangaeaNoteObjectDocument.class);
        }
        return app().elem().convert(s, PangaeaNoteObjectDocument.class);
    }

    @Override
    public boolean isEmptyContent(NElement content) {
        PangaeaNoteObjectDocument a = getContentAsObject(content);
        if (a == null) {
            return true;
        }
        return (a.getDescriptor() == null || a.getDescriptor().getFields() == null || a.getDescriptor().getFields().isEmpty())
                && (a.getValues() == null || a.getValues().isEmpty());
    }
}
