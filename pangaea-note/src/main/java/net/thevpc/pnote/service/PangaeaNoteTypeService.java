/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.service;

import java.util.Iterator;
import java.util.List;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteTypeService extends PangaeaNoteTypeServiceBase {

    ContentTypeSelector getContentTypeSelector();

    void onInstall(PangaeaNoteService service);

    default void onPostUpdateChildNoteProperties(PangaeaNoteExt toUpdate, PangaeaNote before) {

    }

    String getContentTypeIcon(boolean folder, boolean expanded);

    public String normalizeEditorType(String editorType);

    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note);

    default PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteGuiApp sapp) {
        return null;
    }

    NutsElement createDefaultContent();

    public boolean isEmptyContent(NutsElement content);
}