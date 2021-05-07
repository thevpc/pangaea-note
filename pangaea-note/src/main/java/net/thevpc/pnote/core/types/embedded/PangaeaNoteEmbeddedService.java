/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.embedded;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.core.types.embedded.editor.PangaeaNoteDocumentEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteEmbeddedService extends AbstractPangaeaNoteTypeService {

    public static final PangaeaNoteMimeType PANGAEA_NOTE_DOCUMENT
            = PangaeaNoteMimeType.of("application/x-pangaea-note-embedded");

    private PangaeaNoteService service;

    public static PangaeaNoteEmbeddedService of(PangaeaNoteService s) {
        return (PangaeaNoteEmbeddedService) s.getContentTypeService(PANGAEA_NOTE_DOCUMENT);
    }

    public PangaeaNoteEmbeddedService() {
        super(PANGAEA_NOTE_DOCUMENT);
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "composed-documents", 0);
    }

    @Override
    public PangaeaNoteMimeType getContentType() {
        return PANGAEA_NOTE_DOCUMENT;
    }

    @Override
    public void onInstall(PangaeaNoteService service, PangaeaNoteApp app) {
        this.service = service;
        app.installEditorService(new PangaeaNoteEditorService() {
            public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteWindow win) {
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

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(new StringDocumentTextNavigator<PangaeaNoteExt>("content", note, "content", getContentValueAsPath(note.getContent())).iterator()
        );
    }


    @Override
    public boolean isEmptyContent(NutsElement content) {
        return service.isEmptyContent(content);
    }

    @Override
    public NutsElement createDefaultContent() {
        return service.element().toElement(service.newDocument());
    }

    public NutsElement getContentValueAsElement(String contentString) {
        return service.stringToElement(contentString);
    }

    public String getContentValueAsPath(NutsElement content) {
        return service.elementToString(content);
    }

}
