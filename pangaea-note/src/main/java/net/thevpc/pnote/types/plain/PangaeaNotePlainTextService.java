/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.plain;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.source.SourceEditorPanePanel;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.refactor.AnythingToPlainContentTypeReplacer;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;

/**
 *
 * @author vpc
 */
public class PangaeaNotePlainTextService implements PangaeaNoteTypeService {

    public static final String PLAIN = "text/plain";

    public PangaeaNotePlainTextService() {
    }

    @Override
    public ContentTypeSelector[] getContentTypeSelectors() {
        return new ContentTypeSelector[]{
            new ContentTypeSelector(getContentType(), getContentType(), PangaeaNoteTypes.EDITOR_SOURCE, "sources", 0)
        };
    }

    @Override
    public String getContentType() {
        return PLAIN;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        service.installTypeReplacer(new AnythingToPlainContentTypeReplacer(service));
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file-text";
    }

    public String[] normalizeEditorTypes(String editorType) {
        return new String[]{PangaeaNoteTypes.EDITOR_SOURCE};
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(
                new TextStringToPatternHandler("content", note, "content", note.getContent()).iterator()
        );
    }

    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteGuiApp sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_SOURCE:
                return new SourceEditorPanePanel(true, compactMode, sapp);//"Source Code"
        }
        return null;
    }

    @Override
    public boolean isEmptyContent(String content) {
        return (content == null || content.trim().length() == 0);
    }
}
