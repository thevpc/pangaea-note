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

import net.thevpc.echo.api.ScoreSupplier;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.file.editor.URLViewer;
import net.thevpc.pnote.core.types.file.editor.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author thevpc
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
    public ScoreSupplier<URLViewerComponent> getSupport(String path, String extension, PangaeaNoteMimeType mimeType, URLViewer viewer, PangaeaNoteFrame win) {
        if( SUPPORTED.contains(extension)){
            return new ScoreSupplier<URLViewerComponent>() {
                @Override
                public int getScore() {
                    return 2;
                }

                @Override
                public URLViewerComponent get() {
                    Runnable onSuccess = () -> viewer.fireSuccessfulLoading(path);
                    Consumer<Exception> onError = viewer::fireError;

                    return new LibreOfficeToPdfViewerComponent(win, viewer, onSuccess, onError);
                }
            };
        }
        return null;
    }
}
