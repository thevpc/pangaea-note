/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.embedded;

import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.embedded.editor.PangaeaNoteDocumentEditorTypeComponent;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 * @author vpc
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
    public List<? extends Iterator<DocumentTextPart<PangaeaNote>>> resolveTextNavigators(PangaeaNote note) {
        return Arrays.asList(new StringDocumentTextNavigator<PangaeaNote>("content", note, "content",
                        getContentValueAsInfo(note.getContent()).getPath()
                ).iterator()
        );
    }

    @Override
    public NutsElement createDefaultContent() {
        return app.elem().toElement(app.newDocument());
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
    public boolean isEmptyContent(NutsElement content) {
        return app.isEmptyContent(content);
    }

    public NutsElement getContentValueAsElement(PangaeaNoteDocumentInfo contentString) {
        if (contentString == null) {
            contentString = new PangaeaNoteDocumentInfo();
        }
        return app.elem().toElement(contentString);
    }

    public PangaeaNoteDocumentInfo getContentValueAsInfo(NutsElement content) {
        if(content==null || content.isNull()){
            return null;
        }
        if(content.isString()){
            return new PangaeaNoteDocumentInfo().setPath(content.asString());
        }
        return app.elem().convert(content, PangaeaNoteDocumentInfo.class);
    }

}
