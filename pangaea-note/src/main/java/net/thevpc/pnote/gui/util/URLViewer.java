/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.util;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class URLViewer extends JPanel {

    private JScrollPane scroll;
    private JComponent base;
    private JEditorPane pane;
    private String path;
    private URL url;
    private File file;
    private List<URLViewerListener> viewerListeners = new ArrayList<>();

    public URLViewer() {
        super(new BorderLayout());
        pane = new JEditorPane();
        pane.setEditable(false);
        base = pane;
        scroll = new JScrollPane(base);
        add(scroll);
    }

    public void addViewerListener(URLViewerListener listener) {
        viewerListeners.add(listener);
    }

    public void resetContent() {
        this.url = null;
        this.file = null;
        SwingUtilities3.invokeLater(() -> pane.setText(""));
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onReset();
        }
    }

    public boolean isSupportedEdit() {
        if (file != null && file.canWrite()) {
            String n = file.getName();
            return n.matches(".*[.](txt|java|xml|html)");
        }
        return false;
    }

    public void load(String url) {
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onStartLoading(url);
        }
        this.path = url;
        if (path != null && path.length() > 0) {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                URL url0 = null;
                try {
                    url0 = new URL(url);
                } catch (Exception ex) {
                    fireError(ex);
                }
                this.url = url0;
                load0(this.url);
            } else {
                //this is a file?
                URL url0 = null;
                File file1 = null;
                try {
                    file1 = new File(url);
                    url0 = file1.toURI().toURL();
                } catch (Exception ex) {
                    fireError(ex);
                }
                this.url = url0;
                if (url0 != null) {
                    this.file = file1;
                }
                load0(this.url);
            }
        } else {
            resetContent();
        }
    }

    public void load0(URL url) {
        if (url == null) {
            SwingUtilities3.invokeLater(() -> pane.setText(""));
            fireError(new IllegalArgumentException("empty url"));
        } else {
            new Thread() {
                public void run() {
                    InputStream is = null;
                    try {
                        try {
                            is = url.openStream();
                            if (is == null) {
                                SwingUtilities3.invokeLater(() -> pane.setText(""));
                                fireError(new IllegalArgumentException("null stream"));
                            } else {
                                String s = new String(OtherUtils.toByteArray(is));
                                SwingUtilities3.invokeLater(() -> pane.setText(s));
                                firePathLoaded();
                            }
                        } finally {
                            if (is != null) {
                                is.close();
                            }
                        }
                    } catch (IOException ex) {
                        SwingUtilities3.invokeLater(() -> pane.setText(""));
                        fireError(ex);
                    }
                }
            }.start();
        }

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
        pane.setEditable(editable);
    }

    public static interface URLViewerListener {

        void onError(String path, Exception ex);

        void onStartLoading(String path);

        void onSuccessfulLoading(String path);

        void onReset();
    }
}
