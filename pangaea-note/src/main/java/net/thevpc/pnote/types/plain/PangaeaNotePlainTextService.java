/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.plain;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
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
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNotePlainTextService implements PangaeaNoteTypeService {

    public static final String PLAIN = "text/plain";
    private PangaeaNoteService service;
    public PangaeaNotePlainTextService() {
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return PangaeaNoteContentType.of(PLAIN);
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        this.service=service;
        service.installTypeReplacer(new AnythingToPlainContentTypeReplacer(service));
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file-text";
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_SOURCE;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(
                new TextStringToPatternHandler("content", note, "content", getContentAsString(note.getContent())).iterator()
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
    
    
    public NutsElement getContentAsElement(String s) {
        return service.stringToElement(s);
    }

    public String getContentAsString(NutsElement s) {
        return service.elementToString(s);
    }

    @Override
    public boolean isEmptyContent(NutsElement content) {
        return service.isEmptyContent(content);
    }

    @Override
    public NutsElement createDefaultContent() {
        return service.stringToElement("");
    }
    
}
