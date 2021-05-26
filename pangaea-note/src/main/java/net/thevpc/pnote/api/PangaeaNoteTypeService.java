/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api;

import net.thevpc.pnote.api.model.ContentTypeSelector;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.EditorKit;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteTypeService extends PangaeaNoteTypeServiceBase {

    ContentTypeSelector getContentTypeSelector();

    void onInstall(PangaeaNoteService service, PangaeaNoteApp app);

    default void onPostUpdateChildNoteProperties(PangaeaNoteExt toUpdate, PangaeaNote before) {

    }

    String getContentTypeIcon(boolean folder, boolean expanded);

    public String normalizeEditorType(String editorType);

    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note, PangaeaNoteFrame frame);

    default PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
        return null;
    }

    NutsElement createDefaultContent();

    boolean isEmptyContent(NutsElement content, PangaeaNoteFrame frame);

    default int getFileNameSupport(String fileName, String extension, String probedContentType){
        return -1;
    }
}
