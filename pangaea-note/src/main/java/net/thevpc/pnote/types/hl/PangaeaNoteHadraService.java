/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.hl;

import javax.swing.text.EditorKit;
import net.hl.compiler.core.HadraLanguage;
import net.hl.ide.hl4swing.HLJSyntaxKit;
import net.thevpc.nuts.NutsSession;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteHadraService extends AbstractPangaeaNoteSourceCodeService {

    public static final String HADRA = "text/hadra-lang";

    public PangaeaNoteHadraService() {
        super(PangaeaNoteContentType.of(HADRA),"file-hadra-lang");
    }
    
    @Override
    public EditorKit getSourceEditorKit() {
        NutsSession s = getService().getContext().getSession();
        if (!HadraLanguage.isSetSingleton()) {
            //whill be marked as singleton!
            new HadraLanguage(s);
        }
        return new HLJSyntaxKit();
    }

    @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        return (extension.equals("hl")) ? 10 : -1;
    }
}
