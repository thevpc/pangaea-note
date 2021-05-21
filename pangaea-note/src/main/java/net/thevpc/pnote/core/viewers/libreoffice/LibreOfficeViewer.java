/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.libreoffice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
public class LibreOfficeViewer implements PangaeaNoteFileViewerManager {

    private static final Set<String> SUPPORTED = new HashSet<String>(
            Arrays.asList(
                    "ods","odt","odp",
                    "doc","docx",
                    "xls","xlsx",
                    "ppt","pptx"
            )
    );

    @Override
    public int getSupport(String path, String extension, PangaeaNoteMimeType mimeType, PangaeaNoteFrame win) {
        return SUPPORTED.contains(extension) ? 2 : -1;
    }

    @Override
    public URLViewerComponent createComponent(String path, String extension, PangaeaNoteMimeType mimeType, URLViewer viewer, PangaeaNoteFrame win) {
        Runnable onSuccess = () -> viewer.fireSuccessfulLoading(path);
        Consumer<Exception> onError = viewer::fireError;

        return new LibreOfficeToPdfViewerComponent(win, viewer, onSuccess, onError);
    }

}
