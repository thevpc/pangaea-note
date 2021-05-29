/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.plain;

import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.service.refactor.AnythingToPlainContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;

/**
 *
 * @author vpc
 */
public class PangaeaNotePlainTextService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteMimeType PLAIN = PangaeaNoteMimeType.of("text/plain");

    public PangaeaNotePlainTextService() {
        super(PLAIN, "simple-documents",
                new String[]{"application/x-desktop"},
                new String[]{"txt", "text", "log"}
        );
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {
        super.onInstall(app);
        app.installTypeReplacer(new AnythingToPlainContentTypeReplacer());
    }

    @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        int a = super.getFileNameSupport(fileName, extension, probedContentType);
        if (a > 0) {
            return a;
        }
        if (probedContentType.startsWith("text/")) {
            return 1;
        }
        return -1;
    }

}
