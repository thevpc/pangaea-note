/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.pdf;

import net.thevpc.echo.api.SupportSupplier;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;

import java.util.function.Consumer;

/**
 * @author vpc
 */
public class PdfViewer implements PangaeaNoteFileViewerManager {

    @Override
    public SupportSupplier<URLViewerComponent> getSupport(String path, String extension, PangaeaNoteMimeType probedContentType, URLViewer viewer, PangaeaNoteFrame win) {
        if (extension.equals("pdf")) {
            return new SupportSupplier<URLViewerComponent>() {
                @Override
                public int getSupportLevel() {
                    return 2;
                }

                @Override
                public URLViewerComponent get() {
                    Runnable onSuccess = () -> viewer.fireSuccessfulLoading(path);
                    Consumer<Exception> onError = viewer::fireError;

                    return new PdfViewerComponent(win, viewer, onSuccess, onError);
                }
            };
        }
        return null;
    }

}
