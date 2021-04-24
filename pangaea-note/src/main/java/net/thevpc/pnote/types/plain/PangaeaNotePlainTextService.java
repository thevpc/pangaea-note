/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.plain;

import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.source.SourceEditorPanePanel;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.refactor.AnythingToPlainContentTypeReplacer;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;

/**
 *
 * @author vpc
 */
public class PangaeaNotePlainTextService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteContentType PLAIN = PangaeaNoteContentType.of("text/plain");

    public PangaeaNotePlainTextService() {
        super(PLAIN, "file-text");
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        super.onInstall(service);
        service.installTypeReplacer(new AnythingToPlainContentTypeReplacer(service));
    }

    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteWindow sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_SOURCE:
                return new SourceEditorPanePanel(true, compactMode, sapp);//"Source Code"
        }
        return null;
    }

    @Override
    public int getFileNameSupport(String fileName, String extension, String probedContentType) {
        for (String a : new String[]{"txt", "conf", "log"}) {
            if (extension.equals(a)) {
                return 1;
            }
        }
        if (probedContentType.startsWith("text/") || probedContentType.equals("application/x-desktop")) {
                return 1;
        }
        return -1;
    }

}
