/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.cpp;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteCppService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteMimeType CPP = PangaeaNoteMimeType.of("text/x-c++src");

    public PangaeaNoteCppService() {
        super(CPP);
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new CppLangJSyntaxKit(true);
    }

}
