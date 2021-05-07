/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.hl;

import javax.swing.text.EditorKit;
import net.hl.compiler.core.HadraLanguage;
import net.hl.ide.hl4swing.HLJSyntaxKit;
import net.thevpc.nuts.NutsSession;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteHadraService extends AbstractPangaeaNoteSourceCodeService {
    public static final PangaeaNoteMimeType HADRA = PangaeaNoteMimeType.of("application/x-hadra");

    public PangaeaNoteHadraService() {
        super(HADRA);
    }
    
    @Override
    public EditorKit getSourceEditorKit() {
        NutsSession s = service().appContext().getSession();
        if (!HadraLanguage.isSetSingleton()) {
            //whill be marked as singleton!
            new HadraLanguage(s);
        }
        return new HLJSyntaxKit();
    }

}
