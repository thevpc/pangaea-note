/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.list;

import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.core.types.list.editor.PangaeaNoteListEditorTypeComponent;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.core.types.list.refactor.NoteListToAnythingContentTypeReplacer;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.core.types.list.editor.PangaeaNoteListSettingsComponent;
import net.thevpc.pnote.api.EditTypeComponent;

/**
 * @author vpc
 */
public class PangaeaNoteListService extends AbstractPangaeaNoteTypeService {

    public static final PangaeaNoteMimeType LIST = PangaeaNoteMimeType.of("application/x-pangaea-note-list");

    public PangaeaNoteListService() {
        super(LIST);
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "composed-documents", 0);
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_NOTE_LIST;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNote>>> resolveTextNavigators(PangaeaNote note) {
        return Arrays.asList(new StringDocumentTextNavigator<PangaeaNote>("content", note, "content", ""/**
         * nothing here*
         */
        ).iterator()
        );
    }

    @Override
    public NutsElement createDefaultContent() {
        return contentToElement(new PangaeaNoteListModel());
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {
        super.onInstall(app);
        app().installTypeReplacer(new NoteListToAnythingContentTypeReplacer());

        app.installEditorService(new PangaeaNoteEditorService() {
            @Override
            public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
                switch (name) {
                    case PangaeaNoteTypes.EDITOR_NOTE_LIST:
                        return new PangaeaNoteListEditorTypeComponent(compactMode, win);
                }
                return null;
            }

            @Override
            public void onInstall(PangaeaNoteApp app) {

            }
        });
    }

    @Override
    public EditTypeComponent createEditPanel(PangaeaNoteFrame win) {
        return new PangaeaNoteListSettingsComponent(win);
    }

    @Override
    public PangaeaNoteMimeType getContentType() {
        return LIST;
    }

    @Override
    public boolean isEmptyContent(NutsElement content) {
        return app().isEmptyContent(content);
    }

    public NutsElement contentToElement(PangaeaNoteListModel value) {
        return app().element().toElement(value);
    }

    public PangaeaNoteListModel elementToContent(NutsElement s) {
        if (s == null || s.isNull()) {
            return new PangaeaNoteListModel();
        }
        if (!s.isObject()) {
            return new PangaeaNoteListModel();
        } else {
            return app().element().convert(s, PangaeaNoteListModel.class);
        }
    }

}
