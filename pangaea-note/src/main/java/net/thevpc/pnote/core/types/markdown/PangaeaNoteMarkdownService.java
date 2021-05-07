/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.markdown;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.api.model.ContentTypeSelector;

/**
 *
 * @author vpc
 */
public class PangaeaNoteMarkdownService extends AbstractPangaeaNoteSourceCodeService {
    public static final PangaeaNoteMimeType MARKDOWN = PangaeaNoteMimeType.of("text/markdown");

    public PangaeaNoteMarkdownService() {
        super(MARKDOWN);
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new MarkdownJSyntaxKit();
    }
    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }
}
