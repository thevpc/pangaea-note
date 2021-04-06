/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.file;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.source.SourceEditorPanePanel;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;
import net.thevpc.pnote.types.file.editor.FileEditorTypeComponent;
import net.thevpc.pnote.types.html.editor.RichEditor;
import net.thevpc.pnote.types.notelist.editor.PangaeaNoteListEditorTypeComponent;
import net.thevpc.pnote.types.objectlist.editor.PangaeaNoteObjectDocumentComponent;
import net.thevpc.pnote.types.pnodetembedded.editor.PangaeaNoteDocumentEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class PangaeaNoteFileService implements PangaeaNoteTypeService {

    public static final String FILE = "application/pangaea-note-file";

    public PangaeaNoteFileService() {
    }

    @Override
    public ContentTypeSelector[] getContentTypeSelectors() {
        return new ContentTypeSelector[]{
            new ContentTypeSelector(getContentType(), getContentType(), PangaeaNoteTypes.EDITOR_FILE, "simple-documents", 0)
        };
    }
    @Override
    public String getContentType() {
        return FILE;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file";
    }

    public String[] normalizeEditorTypes(String editorType) {
        return new String[]{PangaeaNoteTypes.EDITOR_FILE};
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
            case PangaeaNoteTypes.EDITOR_FILE:
                return new FileEditorTypeComponent(sapp);
        }
        return null;
    }

    @Override
    public boolean isEmptyContent(String content) {
        return (content == null || content.trim().length() == 0);
    }
}
