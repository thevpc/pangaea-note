/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import java.util.Iterator;

import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;

/**
 *
 * @author thevpc
 */
public interface PangaeaNoteTypeService extends PangaeaNoteTypeServiceBase {

    ContentTypeSelector getContentTypeSelector();

    void onInstall(PangaeaNoteApp app);

    default void onPostUpdateChildNoteProperties(PangaeaNote toUpdate, PangaeaNote before) {

    }

    String getContentTypeIcon(boolean folder, boolean expanded);

    public String normalizeEditorType(String editorType);

    Iterator<DocumentTextPart<PangaeaNote>> resolveTextNavigators(PangaeaNote note);

    default PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode) {
        return null;
    }

    NElement createDefaultContent();

    boolean isEmptyContent(NElement content);

    default int getFileNameSupport(String fileName, String extension, String probedContentType){
        return -1;
    }
}
