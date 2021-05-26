/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.thevpc.echo.api.SupportSupplier;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class WebViewer implements PangaeaNoteFileViewerManager {

    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<String>(
            Arrays.asList(
                    "html", "htm"
            )
    );
    private static final Set<String> CONTENT_TYPE_EXTENSIONS = new HashSet<String>(
            Arrays.asList(
                    "text/html", "application/html"
            )
    );

    @Override
    public SupportSupplier<URLViewerComponent> getSupport(String path, String extension, PangaeaNoteMimeType mimeType, URLViewer viewer, PangaeaNoteFrame win) {
        if (SUPPORTED_EXTENSIONS.contains(extension) || CONTENT_TYPE_EXTENSIONS.contains(mimeType.getContentType())) {
            return new SupportSupplier<URLViewerComponent>() {
                @Override
                public int getSupportLevel() {
                    return 10;
                }

                @Override
                public URLViewerComponent get() {
                    Runnable onSuccess = () -> viewer.fireSuccessfulLoading(path);
                    Consumer<Exception> onError = viewer::fireError;

                    return new WebViewerComponent(win, viewer, onSuccess, onError);
                }
            };
        }
        return null;
    }

}
