/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.ntf;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteNTFService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteContentType NUTS_TEXT_FORMAT = PangaeaNoteContentType.of("text/nuts-text-format");

    public PangaeaNoteNTFService() {
        super(NUTS_TEXT_FORMAT, "file-nuts-text-format");
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new NTFJSyntaxKit();
    }

        @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        return (extension.equals("ntf")) ? 10 : -1;
    }

}
