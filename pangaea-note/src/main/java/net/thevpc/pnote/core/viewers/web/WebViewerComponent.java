/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.web;

import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;

/**
 *
 * @author vpc
 */
public class WebViewerComponent implements URLViewerComponent {

    PangaeaNoteWindow win;
    JEditorPane component;
    JScrollPane component2;
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

    public WebViewerComponent(PangaeaNoteWindow win, final URLViewer viewer, Runnable onSuccess, Consumer<Exception> onError) {
        this.viewer = viewer;
        this.win = win;
        this.onSuccess = onSuccess;
        this.onError = onError;
        component = new JEditorPane("text/html","");
        component.setEditable(false);
        component2 = new JScrollPane(component);
        win.app().iconSets().id().listeners().add(propertyListener);
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
    public JComponent component() {
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
        win.app().iconSets().id().listeners().remove(propertyListener);

    }

}
