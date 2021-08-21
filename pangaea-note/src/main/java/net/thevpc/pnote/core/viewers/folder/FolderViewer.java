/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.folder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

import net.thevpc.echo.api.SupportSupplier;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.file.editor.URLViewer;
import net.thevpc.pnote.core.types.file.editor.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class FolderViewer implements PangaeaNoteFileViewerManager {

    @Override
    public SupportSupplier<URLViewerComponent> getSupport(String path, String extension, PangaeaNoteMimeType mimeType, URLViewer viewer, PangaeaNoteFrame win) {
        try {
            if (Files.isDirectory(Paths.get(path))) {
                return new SupportSupplier<URLViewerComponent>() {
                    @Override
                    public int getSupportLevel() {
                        return 10;
                    }

                    @Override
                    public URLViewerComponent get() {
                        Runnable onSuccess=() -> viewer.fireSuccessfulLoading(path);
                        Consumer<Exception> onError=viewer::fireError;
                        return new FolderViewerComponent(win, viewer, onSuccess, onError);
                    }
                };
            }
        } catch (Exception ex) {

        }
        return null;
    }
}
