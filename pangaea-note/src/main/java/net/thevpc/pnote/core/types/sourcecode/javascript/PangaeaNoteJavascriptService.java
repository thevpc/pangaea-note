/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.javascript;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.sourcecode.java.JavaJSyntaxKit;

/**
 *
 * @author vpc
 */
public class PangaeaNoteJavascriptService extends AbstractPangaeaNoteSourceCodeService {
    public static final PangaeaNoteMimeType JAVASCRIPT = PangaeaNoteMimeType.of("text/javascript");

    public PangaeaNoteJavascriptService() {
        super(JAVASCRIPT);
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new JavaJSyntaxKit();
    }


}
