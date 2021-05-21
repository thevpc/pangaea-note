/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.fromeditor;

import java.util.function.Consumer;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.PangaeaNoteTypeService;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class NoteEditorToViewer implements PangaeaNoteFileViewerManager {

    @Override
    public int getSupport(String path, String extension, PangaeaNoteMimeType mimeType, PangaeaNoteFrame win) {
        PangaeaNoteTypeService sp = win.service().getContentTypeServiceByFileName(path, mimeType.getContentType());
        return sp != null ? 1 : -1;
    }

    @Override
    public URLViewerComponent createComponent(String path, String extension, PangaeaNoteMimeType mimeType, URLViewer viewer,PangaeaNoteFrame win) {
        PangaeaNoteTypeService sp = win.service().getContentTypeServiceByFileName(path, mimeType.getContentType());
        String contentType = sp.getContentType().toString();
        Runnable onSuccess=() -> viewer.fireSuccessfulLoading(path);
        Consumer<Exception> onError=viewer::fireError;

        return new NoteURLViewerComponent(contentType, win, viewer, onSuccess, onError);
    }

}
