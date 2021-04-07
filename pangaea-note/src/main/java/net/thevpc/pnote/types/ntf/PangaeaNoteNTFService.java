/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.ntf;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteNTFService implements PangaeaNoteTypeService {

    public static final PangaeaNoteContentType NUTS_TEXT_FORMAT = PangaeaNoteContentType.of("text/nuts-text-format");
    private PangaeaNoteService service;

    public PangaeaNoteNTFService() {
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "sources", 0);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return NUTS_TEXT_FORMAT;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        this.service=service;
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file-nuts-text-format";
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
