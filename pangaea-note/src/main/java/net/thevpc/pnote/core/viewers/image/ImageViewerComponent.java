/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.image;

import net.thevpc.echo.ImageView;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.file.editor.URLViewer;
import net.thevpc.pnote.core.types.file.editor.URLViewerComponent;

import java.util.function.Consumer;

/**
 * @author thevpc
 */
public class ImageViewerComponent extends ImageView implements URLViewerComponent {

    PangaeaNoteFrame frame;
    private Runnable onSuccess;
    private Consumer<Exception> onError;

    public ImageViewerComponent(PangaeaNoteFrame frame, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        super(frame.app());
        this.frame = frame;
        this.onSuccess = onSuccess;
        this.onError = onError;
        enableZoom().set(true);
    }

    @Override
    public void navigate(String url) {
        image().load(url);
    }


    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void save() {
    }

    @Override
    public void setEditable(boolean editable) {

    }

    @Override
    public void disposeComponent() {

    }


}
