/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public abstract class AbstractPangaeaNoteSourceCodeService implements PangaeaNoteTypeService {

    private PangaeaNoteContentType contentType;
    private String icon;
    private PangaeaNoteService service;

    public AbstractPangaeaNoteSourceCodeService(PangaeaNoteContentType contentType, String icon) {
        this.contentType = contentType;
        this.icon = icon;
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "sources", 0);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return contentType;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        this.service = service;
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return icon;
    }

    @Override
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
        if (content == null) {
            return true;
        }
        switch (content.type()) {
            case ARRAY: {
                return content.asArray().isEmpty();
            }
            case OBJECT: {
                return content.asObject().isEmpty();
            }
            case STRING: {
                return content.asString().trim().isEmpty();
            }
            default: {
                return content.asString().trim().isEmpty();
            }
        }
    }

    @Override
    public NutsElement createDefaultContent() {
        return service.stringToElement("");
    }

}
