/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.image;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Application;
import net.thevpc.echo.Image;
import net.thevpc.echo.Label;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.api.components.AppComponentEvent;
import net.thevpc.echo.api.components.AppComponentEventListener;
import net.thevpc.echo.api.components.AppEventType;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.util.OtherUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * @author vpc
 */
public class ImageViewerComponent implements URLViewerComponent {

    private final URLViewer outer;
    PangaeaNoteFrame frame;
    Label label;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    private AppImage current;
    private float zoomFactor;

    public ImageViewerComponent(PangaeaNoteFrame frame, final URLViewer outer, Runnable onSuccess, Consumer<Exception> onError) {
        this.outer = outer;
        this.frame = frame;
        this.onSuccess = onSuccess;
        this.onError = onError;
        label = new Label(Str.empty(), frame.app());
        label.events().add(new AppComponentEventListener() {
            @Override
            public void onEvent(AppComponentEvent event) {
                if (event.wheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
            }
        }, AppEventType.MOUSE_WHEEL_MOVED);
    }

    public static AppImage loadIcon(String url, int width, int height, Application app) {
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
        return new Image(u, app).scaleTo(width, height);
    }

    public static boolean isSvgImage(String name) {
        String suffix = OtherUtils.getFileExtension(name).toLowerCase();
        return suffix.toLowerCase().equals("svg");
    }

    public static boolean isImage(String name) {
        String suffix = OtherUtils.getFileExtension(name).toLowerCase();
        return suffix.equals("png") || suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("svg");
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
                if (zoom * current.getHeight() <= 1
                        || zoom * current.getWidth() <= 1) {
                    return;
                }
            } else {
                if (zoom * current.getHeight() >= 100
                        || zoom * current.getWidth() >= 100) {
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
    AppImage resizedIcon() {
        if (current == null) {
            return null;
        }
        return current.scaleBase(zoomFactor, zoomFactor);
    }

    @Override
    public void setURL(String url) {
        new Thread() {
            public void run() {
                try {
                    current = loadIcon(url, -1, -1, frame.app());
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

    @Override
    public AppComponent component() {
        return label;
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

    private void updateImage() {
        frame.app().runUI(() -> {
            label.smallIcon().set(resizedIcon());
//            label.updateUI();
//            label.repaint();
        });
    }

}
