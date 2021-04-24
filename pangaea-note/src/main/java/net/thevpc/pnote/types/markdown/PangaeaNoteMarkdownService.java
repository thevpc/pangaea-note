/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.markdown;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteMarkdownService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteContentType MARKDOWN = PangaeaNoteContentType.of("text/markdown");

    public PangaeaNoteMarkdownService() {
        super(MARKDOWN, "file-markdown");
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new MarkdownJSyntaxKit();
    }

    @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        return (extension.equals("md")) ? 10 : -1;
    }

}
