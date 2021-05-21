/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.thevpc.pnote.core.viewers.pdf.*;
import java.util.function.Consumer;
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
    public int getSupport(String path, String extension, PangaeaNoteMimeType mimeType, PangaeaNoteFrame win) {
        if (SUPPORTED_EXTENSIONS.contains(extension) || CONTENT_TYPE_EXTENSIONS.contains(mimeType.getContentType())) {
            return 10;
        }
        return -1;
    }

    @Override
    public URLViewerComponent createComponent(String path, String extension, PangaeaNoteMimeType probedContentType, URLViewer viewer, PangaeaNoteFrame win) {
        Runnable onSuccess = () -> viewer.fireSuccessfulLoading(path);
        Consumer<Exception> onError = viewer::fireError;

        return new WebViewerComponent(win, viewer, onSuccess, onError);
    }

}
