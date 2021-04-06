/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.ntf;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;

/**
 *
 * @author vpc
 */
public class PangaeaNoteNTFService implements PangaeaNoteTypeService {

    public static final String NUTS_TEXT_FORMAT = "text/nuts-text-format";

    public PangaeaNoteNTFService() {
    }

    @Override
    public ContentTypeSelector[] getContentTypeSelectors() {
        return new ContentTypeSelector[]{
            new ContentTypeSelector(getContentType(), getContentType(), PangaeaNoteTypes.EDITOR_SOURCE, "sources", 0)
        };
    }

    @Override
    public String getContentType() {
        return NUTS_TEXT_FORMAT;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file-nuts-text-format";
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
    public boolean isEmptyContent(String content) {
        return (content == null || content.trim().length() == 0);
    }
}
