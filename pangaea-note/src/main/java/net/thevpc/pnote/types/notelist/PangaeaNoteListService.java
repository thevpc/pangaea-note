/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.notelist;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.TextStringToPatternHandler;
import net.thevpc.pnote.types.notelist.editor.PangaeaNoteListEditorTypeComponent;
import net.thevpc.pnote.types.notelist.model.PangageaNoteListModel;
import net.thevpc.pnote.types.notelist.refactor.NoteListToAnythingContentTypeReplacer;

/**
 *
 * @author vpc
 */
public class PangaeaNoteListService implements PangaeaNoteTypeService {

    public static final String NOTE_LIST = "application/pangaea-note-list";

    private PangaeaNoteService service;

    public PangaeaNoteListService() {
    }

    @Override
    public ContentTypeSelector[] getContentTypeSelectors() {
        return new ContentTypeSelector[]{
            new ContentTypeSelector(getContentType(), getContentType(), PangaeaNoteTypes.EDITOR_NOTE_LIST, "simple-documents", 0)
        };
    }

    @Override
    public String getContentType() {
        return NOTE_LIST;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        this.service = service;
        service.installTypeReplacer(new NoteListToAnythingContentTypeReplacer(service));
    }

    @Override
    public void onPostUpdateChildNoteProperties(PangaeaNoteExt toUpdate, PangaeaNote before) {
        String oldName = before.getName();
        String newName = toUpdate.getName();
        PangageaNoteListModel oldModel = parseNoteListModel(toUpdate.getParent().getContent());
        if (oldModel == null) {
            oldModel = new PangageaNoteListModel();
        }
        if (oldModel.getSelectedNames().contains(oldName)) {
            oldModel.getSelectedNames().remove(oldName);
            oldModel.getSelectedNames().add(newName);
            toUpdate.getParent().setContent(stringifyNoteListInfo(oldModel));
        }
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "pangaea-note-list";
    }

    public String[] normalizeEditorTypes(String editorType) {
        return new String[]{PangaeaNoteTypes.EDITOR_NOTE_LIST};
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(
                new TextStringToPatternHandler("content", note, "content", note.getContent()).iterator()
        );
    }

    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteGuiApp sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_NOTE_LIST:
                return new PangaeaNoteListEditorTypeComponent(compactMode, sapp);
        }
        return null;
    }

    public String stringifyNoteListInfo(PangageaNoteListModel value) {
        return service.stringifyAny(value);
    }

    public PangageaNoteListModel parseNoteListModel(String s) {
        return service.parseAny(s, PangageaNoteListModel.class);
    }
    @Override
    public boolean isEmptyContent(String content) {
        return (content == null || content.trim().length() == 0);
    }
}
