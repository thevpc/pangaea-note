/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.embedded;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElements;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.embedded.editor.PangaeaNoteDocumentEditorTypeComponent;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.frame.PangaeaNoteTypes;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;

import java.util.Collections;
import java.util.Iterator;

import net.thevpc.pnote.api.model.PangaeaNote;

/**
 * @author thevpc
 */
public class PangaeaNoteEmbeddedService extends AbstractPangaeaNoteTypeService {

    public static final PangaeaNoteMimeType PANGAEA_NOTE_DOCUMENT
            = PangaeaNoteMimeType.of("application/x-pangaea-note-embedded");

    private PangaeaNoteApp app;

    public PangaeaNoteEmbeddedService() {
        super(PANGAEA_NOTE_DOCUMENT);
    }

    public static PangaeaNoteEmbeddedService of(PangaeaNoteApp s) {
        return (PangaeaNoteEmbeddedService) s.getContentTypeService(PANGAEA_NOTE_DOCUMENT);
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "composed-documents", 0);
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT;
    }

    @Override
    public Iterator<DocumentTextPart<PangaeaNote>> resolveTextNavigators(PangaeaNote note) {
        PangaeaNoteDocumentInfo info = getContentValueAsInfo(note.getContent());
        if(info==null){
            return Collections.emptyIterator();
        }
        return new StringDocumentTextNavigator<PangaeaNote>("content", note, "content",info.getPath()).iterator();
    }

    @Override
    public NElement createDefaultContent() {
        return NElements.of().toElement(app.newDocument());
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {
        this.app = app;
        app.installEditorService(new PangaeaNoteEditorService() {
            public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
                switch (name) {
                    case PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT:
                        return new PangaeaNoteDocumentEditorTypeComponent(compactMode, win);
                }
                return null;
            }

            @Override
            public void onInstall(PangaeaNoteApp app) {

            }
        });
    }

    @Override
    public PangaeaNoteMimeType getContentType() {
        return PANGAEA_NOTE_DOCUMENT;
    }

    @Override
    public boolean isEmptyContent(NElement content) {
        return app.isEmptyContent(content);
    }

    public NElement getContentValueAsElement(PangaeaNoteDocumentInfo contentString) {
        if (contentString == null) {
            contentString = new PangaeaNoteDocumentInfo();
        }
        return NElements.of().toElement(contentString);
    }

    public PangaeaNoteDocumentInfo getContentValueAsInfo(NElement content) {
        if(content==null || content.isNull()){
            return null;
        }
        if(content.isString()){
            return new PangaeaNoteDocumentInfo().setPath(content.asStringValue().get());
        }
        return NElements.of().convert(content, PangaeaNoteDocumentInfo.class);
    }

}
