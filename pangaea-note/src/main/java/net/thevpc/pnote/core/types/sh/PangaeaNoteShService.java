/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sh;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteShService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteMimeType SH = PangaeaNoteMimeType.of("application/x-shellscript");

    public PangaeaNoteShService() {
        super(SH);
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new ShellLangJSyntaxKit(true);
    }

    @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        int a = super.getFileNameSupport(fileName, extension, probedContentType);
        if (a > 0) {
            return a;
        }
        for (String e : new String[]{"sh", "zsh", "tsh"}) {
            if (extension.equals(e)) {
                return 10;
            }
        }
        return -1;
    }

}
