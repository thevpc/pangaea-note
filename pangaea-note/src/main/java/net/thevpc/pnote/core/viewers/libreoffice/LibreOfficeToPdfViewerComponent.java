/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.libreoffice;

import java.awt.*;
import java.io.File;
import java.util.function.Consumer;
//import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.thevpc.echo.BorderPane;
import net.thevpc.echo.Panel;
import net.thevpc.echo.UserControl;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.util.OtherUtils;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.jodconverter.LocalConverter;
import org.jodconverter.office.InstalledOfficeManagerHolder;
import org.jodconverter.office.LocalOfficeManager;

/**
 *
 * @author vpc
 */
public class LibreOfficeToPdfViewerComponent implements URLViewerComponent {

    PangaeaNoteFrame frame;
//    JLabel label = new JLabel();
    private final URLViewer outer;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
//    private ImageIcon current;
    private float zoomFactor;
    private Panel panel;
    private static boolean alreadyInstalled = false;

    public LibreOfficeToPdfViewerComponent(PangaeaNoteFrame frame, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        this.outer = outer;
        this.frame = frame;
        this.onSuccess = onSuccess;
        this.onError = onError;
        panel = new BorderPane(frame.app());
        if (!alreadyInstalled) {
            try {
                LocalOfficeManager.install();
                InstalledOfficeManagerHolder.getInstance().start();
                alreadyInstalled=true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void setURL(String url) {
        File u = OtherUtils.asFile(url);
        if (u != null) {
            File targetFile = null;
            try {
                targetFile = new File(frame.service().appContext().getWorkspace().io().tmp().createTempFile("a.pdf"));
                File sourceFile = u;
                LocalConverter.builder()
                        .build()
                        .convert(sourceFile)
                        .to(targetFile)
                        .execute();

               SwingController controller = new SwingController();
                SwingViewBuilder factory = new SwingViewBuilder(controller);
                JPanel viewer = factory.buildViewerPanel();
                viewer.setPreferredSize(new Dimension(400, 243));
                viewer.setMaximumSize(new Dimension(400, 243));
                ComponentKeyBinding.install(controller, viewer);
                controller.getDocumentViewController().setAnnotationCallback(
                        new org.icepdf.ri.common.MyAnnotationCallback(
                                controller.getDocumentViewController()));
                controller.openDocument(targetFile.getPath());
                panel.children().removeAll();
                panel.children().add(new UserControl(null,viewer, frame.app()));
                onSuccess.run();
                return;
            } catch (Exception ex) {
                onError.accept(ex);
                return;
            }
        }
        onError.accept(new IllegalArgumentException("unsupported " + url));
    }

    @Override
    public AppComponent component() {
        return panel;
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
