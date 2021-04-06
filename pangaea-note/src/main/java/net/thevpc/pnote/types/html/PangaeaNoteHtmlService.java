/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.html;

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
import net.thevpc.pnote.types.html.editor.RichEditor;
import net.thevpc.pnote.types.html.refactor.PlainToHtmlContentTypeReplacer;

/**
 *
 * @author vpc
 */
public class PangaeaNoteHtmlService implements PangaeaNoteTypeService {

    public static final String HTML = "text/html";

    public PangaeaNoteHtmlService() {
    }

    @Override
    public ContentTypeSelector[] getContentTypeSelectors() {
        return new ContentTypeSelector[]{
            new ContentTypeSelector(getContentType(), getContentType(), PangaeaNoteTypes.EDITOR_WYSIWYG, "simple-documents", 0),
            new ContentTypeSelector(getContentType() + ":" + PangaeaNoteTypes.EDITOR_SOURCE, getContentType(), PangaeaNoteTypes.EDITOR_SOURCE, "sources", 0),};
    }

    @Override
    public String getContentType() {
        return HTML;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        service.installTypeReplacer(new PlainToHtmlContentTypeReplacer(service));
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file-html";
    }

    public String[] normalizeEditorTypes(String editorType) {
        if (editorType == null) {
            editorType = "";
        }
        editorType = editorType.trim().toLowerCase();
        if (editorType.isEmpty()) {
            return new String[]{PangaeaNoteTypes.EDITOR_WYSIWYG, PangaeaNoteTypes.EDITOR_SOURCE};
        }
        switch (editorType) {
            case PangaeaNoteTypes.EDITOR_WYSIWYG: {
                return new String[]{PangaeaNoteTypes.EDITOR_WYSIWYG};
            }
            case PangaeaNoteTypes.EDITOR_SOURCE: {
                return new String[]{PangaeaNoteTypes.EDITOR_SOURCE};
            }
            default: {
                return new String[]{PangaeaNoteTypes.EDITOR_WYSIWYG};
            }
        }
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
            case PangaeaNoteTypes.EDITOR_WYSIWYG:
                return new RichEditor(compactMode, sapp);
        }
        return null;
    }

    
    @Override
    public boolean isEmptyContent(String content) {
        return (content == null || content.trim().length() == 0);
    }
}
