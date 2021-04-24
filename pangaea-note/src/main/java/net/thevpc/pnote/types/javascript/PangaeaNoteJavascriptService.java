/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.javascript;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import net.thevpc.pnote.types.java.JavaJSyntaxKit;

/**
 *
 * @author vpc
 */
public class PangaeaNoteJavascriptService extends AbstractPangaeaNoteSourceCodeService {

    public static final String JAVASCRIPT = "text/javascript";

    public PangaeaNoteJavascriptService() {
        super(PangaeaNoteContentType.of(JAVASCRIPT), "file-javascript");
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new JavaJSyntaxKit();
    }

        @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        return (extension.equals("js")) ? 10 : -1;
    }

}
