/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.ntf;

import javax.swing.text.EditorKit;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import net.thevpc.pnote.api.model.ContentTypeSelector;

/**
 *
 * @author vpc
 */
public class PangaeaNoteNTFService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteMimeType NUTS_TEXT_FORMAT = PangaeaNoteMimeType.of("text/x-nuts-text-format");

    public PangaeaNoteNTFService() {
        super(NUTS_TEXT_FORMAT);
    }

    @Override
    public EditorKit getSourceEditorKit() {
        return new NTFJSyntaxKit();
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }

}
