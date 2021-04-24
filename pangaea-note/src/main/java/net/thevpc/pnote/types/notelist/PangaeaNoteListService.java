/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.notelist;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.types.notelist.editor.PangaeaNoteListEditorTypeComponent;
import net.thevpc.pnote.types.notelist.model.PangageaNoteListModel;
import net.thevpc.pnote.types.notelist.refactor.NoteListToAnythingContentTypeReplacer;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteListService implements PangaeaNoteTypeService {

    public static final String NOTE_LIST = "application/pangaea-note-list";
    public static final PangaeaNoteContentType C_NOTE_LIST = PangaeaNoteContentType.of("application/pangaea-note-list");

    private PangaeaNoteService service;

    public PangaeaNoteListService() {
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return C_NOTE_LIST;
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

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_NOTE_LIST;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(new StringDocumentTextNavigator("content", note, "content", ""/**
                 * nothing here*
                 */
                ).iterator()
        );
    }

    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteWindow sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_NOTE_LIST:
                return new PangaeaNoteListEditorTypeComponent(compactMode, sapp);
        }
        return null;
    }

    public NutsElement stringifyNoteListInfo(PangageaNoteListModel value) {
        return service.element().toElement(value);
    }

    public PangageaNoteListModel parseNoteListModel(NutsElement s) {
        return service.element().convert(s, PangageaNoteListModel.class);
    }

    @Override
    public boolean isEmptyContent(NutsElement content) {
        return service.isEmptyContent(content);
    }

    @Override
    public NutsElement createDefaultContent() {
        return stringifyNoteListInfo(new PangageaNoteListModel());
    }

}
