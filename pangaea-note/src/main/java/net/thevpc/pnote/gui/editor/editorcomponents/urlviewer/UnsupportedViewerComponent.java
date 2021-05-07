/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.urlviewer;

import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class UnsupportedViewerComponent implements URLViewerComponent {

    PangaeaNoteWindow win;
    JLabel a = new JLabel();
    private final URLViewer outer;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    private String path;
    String extension;
    PangaeaNoteMimeType probedContentType;

    public UnsupportedViewerComponent(
            String path, String extension, PangaeaNoteMimeType probedContentType,
            PangaeaNoteWindow win, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        this.path = path;
        this.extension = extension;
        this.probedContentType = probedContentType;
        this.outer = outer;
        this.win = win;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    public void setURL(String url) {
        //
        if (onError != null) {
            onError.accept(new IllegalArgumentException("unsupported file format "+probedContentType+" ("+extension+")"));
        }
    }

    @Override
    public JComponent component() {
        return a;
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
