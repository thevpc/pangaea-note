/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.sh;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteShService extends AbstractPangaeaNoteSourceCodeService {

    public static final String SH = "text/sh";

    public PangaeaNoteShService() {
        super(PangaeaNoteContentType.of(SH), "file-sh");
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new ShellLangJSyntaxKit(true);
    }

    @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        for (String a : new String[]{"sh", "zsh", "tsh"}) {
            if (extension.equals(a)) {
                return 10;
            }
        }
        return -1;
    }

}
