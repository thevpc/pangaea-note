/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.web;

import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.JEditorPane;

import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.echo.UserControl;
import net.thevpc.echo.WebView;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;

/**
 *
 * @author vpc
 */
public class WebViewerComponent extends WebView implements URLViewerComponent {

    PangaeaNoteFrame frame;
    private final URLViewer viewer;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    PropertyListener propertyListener = new PropertyListener() {
        @Override
        public void propertyUpdated(PropertyEvent event) {
            refresh();
        }

    };

    public WebViewerComponent(PangaeaNoteFrame frame, final URLViewer viewer, Runnable onSuccess, Consumer<Exception> onError) {
        super("WebView",null, frame.app());
        this.viewer = viewer;
        this.frame = frame;
        this.onSuccess = onSuccess;
        this.onError = onError;
//        frame.app().iconSets().id().onChange(propertyListener);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void save() {
        //
    }

    @Override
    public void setEditable(boolean editable) {
    }

    public void disposeComponent() {
//        frame.app().iconSets().id().events().remove(propertyListener);

    }

}
