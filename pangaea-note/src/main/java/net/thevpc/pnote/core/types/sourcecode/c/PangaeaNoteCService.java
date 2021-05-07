/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.c;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.sourcecode.cpp.CppLangJSyntaxKit;

/**
 *
 * @author vpc
 */
public class PangaeaNoteCService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteMimeType C = PangaeaNoteMimeType.of("text/x-csrc");

    public PangaeaNoteCService() {
        super(C, "text/x-c++hdr");
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new CppLangJSyntaxKit(false);
    }

}
