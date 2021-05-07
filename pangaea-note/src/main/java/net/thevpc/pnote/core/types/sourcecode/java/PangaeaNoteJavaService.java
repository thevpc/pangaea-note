/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.java;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteJavaService extends AbstractPangaeaNoteSourceCodeService {
    public static final PangaeaNoteMimeType JAVA = PangaeaNoteMimeType.of("text/java");

    public PangaeaNoteJavaService() {
        super(JAVA,"text/x-java");
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new JavaJSyntaxKit();
    }


}
