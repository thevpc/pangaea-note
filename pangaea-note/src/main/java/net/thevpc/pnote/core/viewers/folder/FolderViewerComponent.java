/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.folder;

import net.thevpc.pnote.core.viewers.image.ImageViewerComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.thevpc.common.iconset.IconSetConfig;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class FolderViewerComponent implements URLViewerComponent {

    PangaeaNoteWindow win;
    JPanel component;
    JPanel component2;
    private final URLViewer viewer;
    private boolean userEditable = true;
    private String url;
    private Runnable onSuccess;
    private Consumer<Exception> onError;
    private int imageSize = 64;
    PropertyListener propertyListener = new PropertyListener() {
        @Override
        public void propertyUpdated(PropertyEvent event) {
            refresh();
        }

    };

    public FolderViewerComponent(PangaeaNoteWindow win, final URLViewer viewer, Runnable onSuccess, Consumer<Exception> onError) {
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.viewer = viewer;
        this.win = win;
        component = new JPanel(new GridLayout(0, 8));
        component2 = new JPanel(new BorderLayout());
        component2.add(component, BorderLayout.PAGE_START);
        win.app().iconSets().id().listeners().add(propertyListener);
    }

    public List<File> listFiles(File f) {
        List<File> all = new ArrayList<>();
        if (f != null && f.isDirectory()) {
            File[] listFiles = f.listFiles();
            if (listFiles != null) {
                all.addAll(Arrays.asList(listFiles));
            }
        }
        all.sort((File a, File b) -> {
            int d1 = a.isDirectory() ? 0 : 1;
            int d2 = b.isDirectory() ? 0 : 1;
            int x = d1 - d2;
            if (x != 0) {
                return x;
            }
            return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
        });

        return all;
    }

    public JComponent asComponent(File file, String otherName) {
        JPanel compPanel = new JPanel(new BorderLayout());
        ImageIcon icon = null;
        JLabel iconLabel = new JLabel("", null, JLabel.CENTER);
        if (file.isDirectory()) {
            String iconId = "..".equals(otherName) ? "folder-up" : "folder";
            icon = win.app().iconSets()
                    .iconSet().getIcon(iconId, IconSetConfig.of(imageSize));

        } else {
            if (ImageViewerComponent.isImage(file.toString())) {
                win.service().executorService()
                        .submit(() -> {
                            ImageIcon icon2 = null;
                            try {
                                icon2 = ImageViewerComponent.loadIcon(file.getPath(), imageSize, imageSize);
                            } catch (Exception ex) {
                                //
                            }
                            if (icon2 != null) {
                                final ImageIcon icon3 = icon2;
                                SwingUtilities.invokeLater(() -> {
                                    iconLabel.setIcon(icon3);
                                    compPanel.updateUI();
                                    compPanel.repaint();
                                });
                            }
                        });
                String iconId = "datatype.image";
                icon = win.app().iconSets().iconSet().getIcon(iconId, IconSetConfig.of(imageSize));
            } else {
                win.service().executorService()
                        .submit(() -> {
                            ImageIcon icon2 = null;
                            try {
                                String cp = OtherUtils.probeContentType(file.getPath());
                                icon2 = win.app().iconSets()
                                        .iconSet().getIcon("content-type." + cp, IconSetConfig.of(imageSize));
                            } catch (Exception ex) {
                                //
                            }
                            if (icon2 != null) {
                                final ImageIcon icon3 = icon2;
                                SwingUtilities.invokeLater(() -> {
                                    iconLabel.setIcon(icon3);
                                    compPanel.updateUI();
                                    compPanel.repaint();
                                });
                            }
                        });
                icon = win.app().iconSets()
                        .iconSet().getIcon("file", IconSetConfig.of(imageSize));
            }
        }
        iconLabel.setIcon(icon);
        iconLabel.setToolTipText(otherName == null ? file.getName() : file.getPath());
        JLabel textLabel = new JLabel(otherName == null ? file.getName() : otherName);
        textLabel.setToolTipText(file.getName());
        textLabel.setPreferredSize(new Dimension(50, 20));
        compPanel.setBackground(null);
        compPanel.add(iconLabel, BorderLayout.NORTH);
        compPanel.add(textLabel, BorderLayout.CENTER);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        MouseAdapter li = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                compPanel.setBackground(Color.lightGray);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                compPanel.setBackground(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (file.isDirectory() || (file.isFile() && e.getClickCount() == 2)) {
                        viewer.load(file.getPath());
                    }
                }
            }

        };

        compPanel.addMouseListener(li);
        iconLabel.addMouseListener(li);

        return compPanel;
    }

    public void refresh() {
        setURL(url);
    }

    @Override
    public void setURL(String url) {
        this.url = url;
        component.removeAll();
        File asFile = OtherUtils.asFile(url);
        if (asFile != null) {
            if (asFile.isDirectory() && asFile.getParentFile() != null) {
                component.add(asComponent(asFile.getParentFile(), ".."));
            }
            for (File f : listFiles(asFile)) {
                component.add(asComponent(f, null));
            }
        }
        if (onSuccess != null) {
            onSuccess.run();
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
        userEditable = true;
    }

    public void disposeComponent() {
        win.app().iconSets().id().listeners().remove(propertyListener);

    }

}
