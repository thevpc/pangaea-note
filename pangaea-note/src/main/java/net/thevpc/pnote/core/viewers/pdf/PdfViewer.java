/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.pdf;

import java.util.function.Consumer;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PdfViewer implements PangaeaNoteFileViewerManager {

    @Override
    public int getSupport(String path, String extension, PangaeaNoteMimeType probedContentType, PangaeaNoteWindow win) {
        return extension.equals("pdf") ? 2 : -1;
    }

    @Override
    public URLViewerComponent createComponent(String path, String extension, PangaeaNoteMimeType probedContentType, URLViewer viewer,PangaeaNoteWindow win) {
        Runnable onSuccess=() -> viewer.fireSuccessfulLoading(path);
        Consumer<Exception> onError=viewer::fireError;

        return new PdfViewerComponent(win, viewer, onSuccess, onError);
    }

}
