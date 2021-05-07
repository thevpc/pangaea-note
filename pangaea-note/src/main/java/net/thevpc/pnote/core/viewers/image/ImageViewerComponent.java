/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.image;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.thevpc.common.iconset.util.IconUtils;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class ImageViewerComponent implements URLViewerComponent {

    PangaeaNoteWindow win;
    JLabel label = new JLabel();
    private final URLViewer outer;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    private ImageIcon current;
    private float zoomFactor;

    public ImageViewerComponent(PangaeaNoteWindow win, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        this.outer = outer;
        this.win = win;
        this.onSuccess = onSuccess;
        this.onError = onError;
        label.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
            }
        });
    }

    public void zoomIn() {
        zoom((float) (zoomFactor * Math.pow(1.1, 1)));
    }

    public void zoomOut() {
        zoom((float) (zoomFactor / Math.pow(1.1, 1)));
    }

    public void zoom(float zoom) {
        if (current == null) {
            return;
        }
        if (zoom != 1) {
            if (zoom < 1) {
                if (zoom * current.getImage().getHeight(null) <= 1
                        || zoom * current.getImage().getWidth(null) <= 1) {
                    return;
                }
            } else {
                if (zoom * current.getImage().getHeight(null) >= 100
                        || zoom * current.getImage().getWidth(null) >= 100) {
                    return;
                }
            }
        }
        zoomFactor = zoom;
        updateImage();
    }

    public void unzoom() {
        zoom(1);
    }

//    public void bestZoom() {
//        if (current == null) {
//            return;
//        }
//        int h = current.getImage().getHeight(null);
//        int w = current.getImage().getWidth(null);
//        
//        zoomFactor = 1;
//        updateImage();
//    }
    ImageIcon resizedIcon() {
        if (current == null) {
            return null;
        }
        return new ImageIcon(IconUtils.getFactorScaledImage(current.getImage(), zoomFactor, zoomFactor));
    }

    @Override
    public void setURL(String url) {
        new Thread() {
            public void run() {
                try {
                    current = loadIcon(url, -1, -1);
                    updateImage();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } catch (Exception ex) {
                    current = null;
                    updateImage();
                    if (onError != null) {
                        onError.accept(ex);
                    }
                }
            }
        }.start();
    }

    private void updateImage() {
        SwingUtilities3.invokeLater(() -> {
            label.setIcon(resizedIcon());
            label.updateUI();
            label.repaint();
        });
    }

    @Override
    public JComponent component() {
        return label;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void save() {
    }

    public static ImageIcon loadIcon(String url, int width, int height) {
        URL u = OtherUtils.asURL(url);
        if (u == null) {
            File f = OtherUtils.asFile(url);
            if (f != null) {
                try {
                    u = f.toURI().toURL();
                } catch (MalformedURLException ex) {
                    throw new IllegalArgumentException("not an image : " + url);
                }
            }
        }
        if (u == null) {
            throw new IllegalArgumentException("not an image : " + url);
        }
        return IconUtils.loadFixedScaleImageIcon(u, width, height);

    }

    public static boolean isSvgImage(String name) {
        String suffix = OtherUtils.getFileExtension(name).toLowerCase();
        return suffix.toLowerCase().equals("svg");
    }

    public static boolean isImage(String name) {
        String suffix = OtherUtils.getFileExtension(name).toLowerCase();
        return suffix.equals("png") || suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("svg");
    }

    @Override
    public void setEditable(boolean editable) {

    }

    @Override
    public void disposeComponent() {

    }

}
