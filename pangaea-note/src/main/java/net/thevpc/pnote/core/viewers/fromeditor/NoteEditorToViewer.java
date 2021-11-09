/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.fromeditor;

import java.util.function.Consumer;

import net.thevpc.echo.api.SupportSupplier;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.PangaeaNoteTypeService;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.file.editor.URLViewer;
import net.thevpc.pnote.core.types.file.editor.URLViewerComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author thevpc
 */
public class NoteEditorToViewer implements PangaeaNoteFileViewerManager {

    @Override
    public SupportSupplier<URLViewerComponent> getSupport(String path, String extension, PangaeaNoteMimeType mimeType, URLViewer viewer, PangaeaNoteFrame win) {
        PangaeaNoteTypeService sp = win.app().getContentTypeServiceByFileName(path, mimeType.getContentType());
        if(sp!=null){
            return new SupportSupplier<URLViewerComponent>() {
                @Override
                public int getSupportLevel() {
                    return 1;
                }

                @Override
                public URLViewerComponent get() {
                    String contentType = sp.getContentType().toString();
                    Runnable onSuccess=() -> viewer.fireSuccessfulLoading(path);
                    Consumer<Exception> onError=viewer::fireError;

                    return new NoteURLViewerComponent(contentType, win, viewer, onSuccess, onError);
                }
            };
        }
        return null;
    }


}
