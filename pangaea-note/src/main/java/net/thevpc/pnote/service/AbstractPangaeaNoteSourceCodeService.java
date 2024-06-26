/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import java.util.Arrays;
import java.util.Iterator;

import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteTypes;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author thevpc
 */
public abstract class AbstractPangaeaNoteSourceCodeService extends AbstractPangaeaNoteTypeService {

    private String[] extensions;
    private String contentSelector;

    public AbstractPangaeaNoteSourceCodeService(PangaeaNoteMimeType contentType, String contentSelector,String[] mimetypes, String[] extensions) {
        super(contentType, mimetypes);
        this.extensions = extensions;
        this.contentSelector = contentSelector==null?"sources":contentSelector;
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), contentSelector, 0);
    }
    
    @Override
    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_SOURCE;
    }

    @Override
    public Iterator<DocumentTextPart<PangaeaNote>> resolveTextNavigators(PangaeaNote note) {
        return new StringDocumentTextNavigator<PangaeaNote>("content", note, "content", getContentAsString(note.getContent())).iterator();
    }

    @Override
    public NElement createDefaultContent() {
        return app().stringToElement("");
    }

     @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        int a = super.getFileNameSupport(fileName, extension, probedContentType);
        if (a > 0) {
            return a;
        }
        for (String e : extensions) {
            if (extension.equals(e)) {
                return 10;
            }
        }
        return -1;
    }
}
