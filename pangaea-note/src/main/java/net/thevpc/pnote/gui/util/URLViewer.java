/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.util;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.thevpc.common.iconset.util.SVGSalamander;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class URLViewer extends JPanel {

    private JScrollPane scroll;
    private URLViewerComponent subComponent;
    private String path;
    private URL url;
    private File file;
    private boolean canEdit;
    private List<URLViewerListener> viewerListeners = new ArrayList<>();
    private PangaeaNoteWindow win;

    public URLViewer(PangaeaNoteWindow win) {
        super(new BorderLayout());
        this.win = win;
        scroll = new JScrollPane();
        add(scroll);
    }

    public void addViewerListener(URLViewerListener listener) {
        viewerListeners.add(listener);
    }

    public void resetContent() {
        this.url = null;
        this.file = null;
        subComponent = new UnsupportedViewerComponent(win);
        scroll.getViewport().setView(subComponent.component());
    }

    public boolean isSupportedEdit() {
        if (file != null && file.canWrite()) {
            String n = file.getName();
            return n.matches(".*[.](txt|java|xml|html)");
        }
        return false;
    }

    public URL asURL(String url) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            URL url0 = null;
            try {
                return new URL(url);
            } catch (Exception ex) {
            }
        } else {
            //this is a file?
            File file1 = null;
            try {
                file1 = new File(url);
                return file1.toURI().toURL();
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public void load(String url) {
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onStartLoading(url);
        }
        this.path = url;
        if (path != null && path.length() > 0) {
            URL uurl = asURL(url);
            if (uurl == null) {
                try {
                    new URL(url);
                } catch (Exception ex) {
                    fireError(ex);
                }
                return;
            }
            String contentType = null;
            contentType = resolveSubComponent(url, contentType);
            scroll.getViewport().setView(subComponent.component());
            subComponent.setURL(url, contentType);
        } else {
            resetContent();
        }
    }

    protected String resolveSubComponent(String url1, String contentType) {
        String probedContentType = null;
        File asFile = asFile(path);
        if (asFile != null) {
            try {
                probedContentType = Files.probeContentType(asFile.toPath());
            } catch (IOException ex) {
                //
            }
        }
        PangaeaNoteTypeService sp = win.service().getContentTypeServiceByFileName(url1, probedContentType);
        if (sp == null) {
            if (isImage(path)) {
                subComponent = new ImageViewerComponent(win);
                return contentType;
            } else if (isFolder(url1)) {
                subComponent = new FolderViewerComponent(win);
                return contentType;
            } else {
                subComponent = new UnsupportedViewerComponent(win);
            }
        } else {
            contentType = sp.getContentType().toString();
            subComponent = new NoteURLViewerComponent(win);
        }
        return contentType;
    }

    private File asFile(String url) {
        File file1 = null;
        try {
            if (url.startsWith("file:")) {
                URL u = new URL(url);

            }
            file1 = new File(url);
            if (file1.exists()) {
                return file1;
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public boolean isFolder(String name) {
        File f = asFile(name);
        return f != null && f.isDirectory();
    }

    public boolean isSvgImage(String name) {
        name = name.replace('\\', '/');
        int x = name.lastIndexOf('/');
        if (x >= 0) {
            name = name.substring(x + 1);
        }
        x = name.lastIndexOf('.');
        String suffix = "";
        if (x >= 0) {
            suffix = name.substring(x + 1);
        }
        return suffix.toLowerCase().equals("svg");
    }

    public boolean isImage(String name) {
        name = name.replace('\\', '/');
        int x = name.lastIndexOf('/');
        if (x >= 0) {
            name = name.substring(x + 1);
        }
        x = name.lastIndexOf('.');
        String suffix = "";
        if (x >= 0) {
            suffix = name.substring(x + 1);
        }
        return suffix.equals("png") || suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("svg");
    }

    protected void firePathLoaded() {
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onSuccessfulLoading(path);
        }

    }

    protected void fireError(Exception ex) {
        ex.printStackTrace();
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onError(path, ex);
        }
    }

    public void setEditable(boolean editable) {
        //pane.setEditable(editable);
    }

    public class ImageViewerComponent implements URLViewerComponent {

        PangaeaNoteWindow win;
        JLabel label = new JLabel();

        public ImageViewerComponent(PangaeaNoteWindow win) {
            this.win = win;
        }

        @Override
        public void setURL(String url, String contentType) {
            new Thread() {
                public void run() {
                    try {
                        if (isSvgImage(url)) {
                            Image img = SVGSalamander.getImageFromSvg(asURL(url), -1);
                            SwingUtilities3.invokeLater(() -> label.setIcon(new ImageIcon(img)));
                        } else if (isImage(url)) {
                            BufferedImage img = ImageIO.read(asURL(url));
                            SwingUtilities3.invokeLater(() -> label.setIcon(new ImageIcon(img)));
                        }
                    } catch (IOException ex) {
                        SwingUtilities3.invokeLater(() -> label.setIcon(null));
                        fireError(ex);
                    }
                }
            }.start();

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

    }

    public class UnsupportedViewerComponent implements URLViewerComponent {

        PangaeaNoteWindow win;
        JLabel a = new JLabel();

        public UnsupportedViewerComponent(PangaeaNoteWindow win) {
            this.win = win;
        }

        @Override
        public void setURL(String url, String contentType) {
            //
            fireError(new IllegalArgumentException("unsupported file format"));
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

    }

    public class FolderViewerComponent implements URLViewerComponent {

        PangaeaNoteWindow win;
        JTextArea a;

        public FolderViewerComponent(PangaeaNoteWindow win) {
            this.win = win;
            a = new JTextArea();
        }

        @Override
        public void setURL(String url, String contentType) {
            File f = asFile(url.toString());
            if (f != null && f.isDirectory()) {
                File[] listFiles = f.listFiles();
                if (listFiles != null) {
                    a.setText(Arrays.stream(listFiles).map(x -> x.toString()).collect(Collectors.joining("\n")));
                    firePathLoaded();
                    return;
                }
            }
            a.setText("");
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
            //
        }

    }

    public class NoteURLViewerComponent implements URLViewerComponent {

        PangaeaNoteWindow win;
        PangaeaNoteEditor ed;

        public NoteURLViewerComponent(PangaeaNoteWindow win) {
            this.win = win;
            this.ed = new PangaeaNoteEditor(win, true);
        }

        public void setURL(String url, String contentType) {
            new Thread() {
                public void run() {
                    InputStream is = null;
                    try {
                        try {
                            is = asURL(url).openStream();
                            if (is == null) {
                                SwingUtilities3.invokeLater(() -> setContent("", contentType));
                                fireError(new IllegalArgumentException("null stream"));
                            } else {
                                String s = new String(OtherUtils.toByteArray(is));
                                SwingUtilities3.invokeLater(() -> setContent(s, contentType));
                                firePathLoaded();
                            }
                        } finally {
                            if (is != null) {
                                is.close();
                            }
                        }
                    } catch (IOException ex) {
                        resetContent();
                        fireError(ex);
                    }
                }
            }.start();
        }

        public void setContent(String content, String contentType) {

            PangaeaNoteExt d = PangaeaNoteExt.of(win.service().newDocument());
            PangaeaNoteExt n = PangaeaNoteExt.of(
                    new PangaeaNote()
                            .setContentType(contentType)
                            .setContent(win.service().stringToElement(content))
            );
            d.addChild(n);
            ed.setNote(n);
        }

        @Override
        public JComponent component() {
            return ed;
        }

        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public void save() {
            //
        }

    }

    public static interface URLViewerComponent {

        void setURL(String url, String contentType);

        JComponent component();

        boolean isEditable();

        void save();
    }

    public static interface URLViewerListener {

        void onError(String path, Exception ex);

        void onStartLoading(String path);

        void onSuccessfulLoading(String path);

        void onReset();
    }
}
