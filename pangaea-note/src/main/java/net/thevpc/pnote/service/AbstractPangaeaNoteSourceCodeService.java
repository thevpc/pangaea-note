/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import net.thevpc.pnote.api.model.ContentTypeSelector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
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
    public List<? extends Iterator<DocumentTextPart<PangaeaNote>>> resolveTextNavigators(PangaeaNote note) {
        return Arrays.asList(new StringDocumentTextNavigator<PangaeaNote>("content", note, "content", getContentAsString(note.getContent())).iterator()
        );
    }

    @Override
    public NutsElement createDefaultContent() {
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
