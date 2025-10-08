/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.pdf;

import net.thevpc.echo.api.ScoreSupplier;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.file.editor.URLViewer;
import net.thevpc.pnote.core.types.file.editor.URLViewerComponent;

import java.util.function.Consumer;

/**
 * @author thevpc
 */
public class PdfViewer implements PangaeaNoteFileViewerManager {

    @Override
    public ScoreSupplier<URLViewerComponent> getSupport(String path, String extension, PangaeaNoteMimeType probedContentType, URLViewer viewer, PangaeaNoteFrame win) {
        if (extension.equals("pdf")) {
            return new ScoreSupplier<URLViewerComponent>() {
                @Override
                public int getScore() {
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
