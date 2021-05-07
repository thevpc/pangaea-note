/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.folder;

import java.nio.file.Files;
import java.nio.file.Paths;
import net.thevpc.pnote.core.viewers.fromeditor.*;
import java.util.function.Consumer;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.PangaeaNoteTypeService;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class FolderViewer implements PangaeaNoteFileViewerManager {

    @Override
    public int getSupport(String path, String extension, PangaeaNoteMimeType mimeType, PangaeaNoteWindow win) {
        try {
            if (Files.isDirectory(Paths.get(path))) {
                return 10;
            }
        } catch (Exception ex) {

        }
        return -1;
    }

    @Override
    public URLViewerComponent createComponent(String path, String extension, PangaeaNoteMimeType mimeType, URLViewer viewer, PangaeaNoteWindow win) {
        Runnable onSuccess=() -> viewer.fireSuccessfulLoading(path);
        Consumer<Exception> onError=viewer::fireError;
        return new FolderViewerComponent(win, viewer, onSuccess, onError);
    }

}
