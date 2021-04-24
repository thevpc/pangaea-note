/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.c;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import net.thevpc.pnote.types.cpp.CppLangJSyntaxKit;

/**
 *
 * @author vpc
 */
public class PangaeaNoteCService extends AbstractPangaeaNoteSourceCodeService {

    public static final String C = "text/c";

    public PangaeaNoteCService() {
        super(PangaeaNoteContentType.of(C), "file-c");
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new CppLangJSyntaxKit(false);
    }

    @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        return (extension.equals("c") || extension.equals("h")) ? 10 : -1;
    }

}
