/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.pnodetembedded;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;
import net.thevpc.pnote.types.pnodetembedded.editor.PangaeaNoteDocumentEditorTypeComponent;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteEmbeddedService implements PangaeaNoteTypeService {

    public static final PangaeaNoteContentType PANGAEA_NOTE_DOCUMENT = PangaeaNoteContentType.of("application/pangaea-note-document");

    private PangaeaNoteService service;

    public static PangaeaNoteEmbeddedService of(PangaeaNoteService s) {
        return (PangaeaNoteEmbeddedService) s.getContentTypeService(PANGAEA_NOTE_DOCUMENT);
    }

    public PangaeaNoteEmbeddedService() {
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return PANGAEA_NOTE_DOCUMENT;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        this.service = service;
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file-pnote";
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(
                new TextStringToPatternHandler("content", note, "content", getContentValueAsPath(note.getContent())).iterator()
        );
    }

    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteGuiApp sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT:
                return new PangaeaNoteDocumentEditorTypeComponent(compactMode, sapp);
        }
        return null;
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
