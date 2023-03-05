/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.file;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.frame.PangaeaNoteTypes;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.core.types.file.editor.PangaeaNoteFileEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteFileService extends AbstractPangaeaNoteTypeService {
    public static final PangaeaNoteMimeType FILE
            = PangaeaNoteMimeType.of("application/x-pangaea-note-file");

    public PangaeaNoteFileService() {
        super(FILE);
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {
        super.onInstall(app);
        app.installEditorService(new PangaeaNoteEditorService() {
            @Override
            public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
                switch (name) {
                    case PangaeaNoteTypes.EDITOR_FILE:
                        return new PangaeaNoteFileEditorTypeComponent(win);
                }
                return null;
            }

            @Override
            public void onInstall(PangaeaNoteApp app) {

            }
        });
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "composed-documents", 0);
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_FILE;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNote>>> resolveTextNavigators(PangaeaNote note) {
        return Arrays.asList(new StringDocumentTextNavigator<PangaeaNote>("content", note, "content", getContentAsString(note.getContent())).iterator()
        );
    }



        
    @Override
    public boolean isEmptyContent(NElement content) {
        return app().isEmptyContent(content);
    }

    @Override
    public NElement createDefaultContent() {
        return app().stringToElement("");
    }
    
}
