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
import net.thevpc.echo.ScrollPane;
import net.thevpc.echo.UserControl;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;

/**
 *
 * @author vpc
 */
public class WebViewerComponent implements URLViewerComponent {

    PangaeaNoteFrame frame;
    JEditorPane component;
    ScrollPane component2;
    private final URLViewer viewer;
    private String url;
//    private String contentType;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    PropertyListener propertyListener = new PropertyListener() {
        @Override
        public void propertyUpdated(PropertyEvent event) {
            refresh();
        }

    };

    public WebViewerComponent(PangaeaNoteFrame frame, final URLViewer viewer, Runnable onSuccess, Consumer<Exception> onError) {
        this.viewer = viewer;
        this.frame = frame;
        this.onSuccess = onSuccess;
        this.onError = onError;
        component = new JEditorPane("text/html","");
        component.setEditable(false);
        component2 = new ScrollPane(
                new UserControl(
                        "JEditorPane",
                        component, frame.app()
                )
        );
        frame.app().iconSets().id().onChange(propertyListener);
    }

    public void refresh() {
        setURL(url);
    }

    @Override
    public void setURL(String url) {
        try {
            this.url = url;
//            this.contentType = contentType;
            component.setPage(url);
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (IOException ex) {
            if (onError != null) {
                onError.accept(ex);
            }
        }

    }

    @Override
    public AppComponent component() {
        return component2;
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
        frame.app().iconSets().id().listeners().remove(propertyListener);

    }

}
