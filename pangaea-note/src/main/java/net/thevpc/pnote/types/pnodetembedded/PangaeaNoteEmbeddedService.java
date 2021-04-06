/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.pnodetembedded;

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
import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;
import net.thevpc.pnote.types.pnodetembedded.editor.PangaeaNoteDocumentEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class PangaeaNoteEmbeddedService implements PangaeaNoteTypeService {

    public static final String PANGAEA_NOTE_DOCUMENT = "application/pangaea-note-document";

    public PangaeaNoteEmbeddedService() {
    }

    @Override
    public ContentTypeSelector[] getContentTypeSelectors() {
        return new ContentTypeSelector[]{
            new ContentTypeSelector(getContentType(), getContentType(), PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT, "simple-documents", 0)
        };
    }

    @Override
    public String getContentType() {
        return PANGAEA_NOTE_DOCUMENT;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file-pnote";
    }

    public String[] normalizeEditorTypes(String editorType) {
        return new String[]{PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT};
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(
                new TextStringToPatternHandler("content", note, "content", note.getContent()).iterator()
        );
    }

        @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name,boolean compactMode, PangaeaNoteGuiApp sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT:
                return new PangaeaNoteDocumentEditorTypeComponent(compactMode, sapp);
        }
        return null;
    }

    @Override
    public boolean isEmptyContent(String content) {
        return (content == null || content.trim().length() == 0);
    }
}
