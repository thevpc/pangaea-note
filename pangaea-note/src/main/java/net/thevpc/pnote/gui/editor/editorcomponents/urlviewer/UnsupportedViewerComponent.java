/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.urlviewer;

import java.util.function.Consumer;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Label;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class UnsupportedViewerComponent extends Label implements URLViewerComponent {

    PangaeaNoteFrame frame;
    private final URLViewer outer;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    private String path;
    String extension;
    PangaeaNoteMimeType probedContentType;

    public UnsupportedViewerComponent(
            String path, String extension, PangaeaNoteMimeType probedContentType,
            PangaeaNoteFrame frame, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        super(Str.of("<<UNSUPPORTED>>"), frame.app());
        this.path = path;
        this.extension = extension;
        this.probedContentType = probedContentType;
        this.outer = outer;
        this.frame = frame;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    public void navigate(String url) {
        //
        if (onError != null) {
            onError.accept(new IllegalArgumentException("unsupported file format "+probedContentType+" ("+extension+")"));
        }
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