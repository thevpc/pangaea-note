/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.pdf;

import net.thevpc.echo.UserControl;
import net.thevpc.echo.impl.Applications;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.function.Consumer;

/**
 *
 * @author vpc
 */
public class PdfViewerComponent extends UserControl implements URLViewerComponent {

    PangaeaNoteFrame frame;
//    JLabel label = new JLabel();
    private final URLViewer outer;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    private ImageIcon current;
    private float zoomFactor;
    private SwingController controller;
    private JPanel viewerComponentPanel;

    
    public PdfViewerComponent(PangaeaNoteFrame frame, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        super("Pdfcomponent",null, frame.app());

        this.outer = outer;
        this.frame = frame;
        this.onSuccess = onSuccess;
        this.onError = onError;
        controller = new SwingController();
        SwingViewBuilder factory = new SwingViewBuilder(controller);
        viewerComponentPanel = factory.buildViewerPanel();
        viewerComponentPanel.setPreferredSize(new Dimension(400, 243));
        viewerComponentPanel.setMaximumSize(new Dimension(400, 243));
        ComponentKeyBinding.install(controller, viewerComponentPanel);
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));
        this.renderer().set(viewerComponentPanel);
    }


    @Override
    public void navigate(String url) {
        URL u = Applications.asURL(url);
        if (u != null) {
            try {
                controller.openDocument(u);
                onSuccess.run();
            } catch (Exception ex) {
                onError.accept(ex);
                return;
            }
        }
        onError.accept(new IllegalArgumentException("unsupported " + url));
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
