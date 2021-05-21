/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.viewers.folder;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.api.components.AppComponentEvent;
import net.thevpc.echo.api.components.AppComponentEventListener;
import net.thevpc.echo.api.components.AppEventType;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.echo.constraints.ParentWrapCount;
import net.thevpc.pnote.core.viewers.image.ImageViewerComponent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.iconset.IconSetConfig;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerComponent;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class FolderViewerComponent implements URLViewerComponent {

    PangaeaNoteFrame frame;
    Panel component;
    Panel component2;
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

    public FolderViewerComponent(PangaeaNoteFrame frame, final URLViewer viewer, Runnable onSuccess, Consumer<Exception> onError) {
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.viewer = viewer;
        this.frame = frame;
        component = new VerticalPane(component.app()).with(p-> {
                    p.parentConstraints().addAll(new ParentWrapCount(8));
                    p.anchor().set(Anchor.TOP);
                }
        );
        component2 = new BorderPane( component.app());
        component2.children().add(component);
        frame.app().iconSets().id().onChange(propertyListener);
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

    public AppComponent asComponent(File file, String otherName) {
        Panel compPanel = new BorderPane( frame.app());
        AppImage icon = null;
        //JLabel.CENTER
        Label iconLabel = new Label(Str.of(""), frame.app());
        iconLabel.textStyle().align().set(Anchor.CENTER);
        if (file.isDirectory()) {
            String iconId = "..".equals(otherName) ? "folder-up" : "folder";
            icon = frame.app().iconSets()
                    .iconSet().getIcon(iconId, IconSetConfig.of(imageSize));
        } else {
            if (ImageViewerComponent.isImage(file.toString())) {
                frame.service().executorService()
                        .submit(() -> {
                            AppImage icon2 = null;
                            try {
                                icon2 = ImageViewerComponent.loadIcon(file.getPath(), imageSize, imageSize, frame.app());
                            } catch (Exception ex) {
                                //
                            }
                            if (icon2 != null) {
                                final AppImage icon3 = icon2;
                                frame.app().runUI(() -> {
                                    iconLabel.smallIcon().set(icon3);
//                                    compPanel.updateUI();
//                                    compPanel.repaint();
                                });
                            }
                        });
                String iconId = "datatype.image";
                icon = frame.app().iconSets().iconSet().getIcon(iconId, IconSetConfig.of(imageSize));
            } else {
                frame.service().executorService()
                        .submit(() -> {
                            AppImage icon2 = null;
                            try {
                                String cp = OtherUtils.probeContentType(file.getPath());
                                icon2 = frame.app().iconSets()
                                        .iconSet().getIcon("content-type." + cp, IconSetConfig.of(imageSize));
                            } catch (Exception ex) {
                                //
                            }
                            if (icon2 != null) {
                                final AppImage icon3 = icon2;
                                frame.app().runUI(() -> {
                                    iconLabel.smallIcon().set(icon3);
//                                    compPanel.updateUI();
//                                    compPanel.repaint();
                                });
                            }
                        });
                icon = frame.app().iconSets()
                        .iconSet().getIcon("file", IconSetConfig.of(imageSize));
            }
        }
        iconLabel.smallIcon().set(icon);
        iconLabel.tooltip().set(Str.of(otherName == null ? file.getName() : file.getPath()));
        iconLabel.anchor().set(Anchor.TOP);

        Label textLabel = new Label(Str.of(otherName == null ? file.getName() : file.getPath()), frame.app());
        textLabel.tooltip().set(Str.of(file.getName()));
        textLabel.prefSize().set(new Dimension(50, 20));
        textLabel.textStyle().align().set(Anchor.CENTER);
        textLabel.anchor().set(Anchor.CENTER);

        compPanel.backgroundColor().set(null);
        //compPanel.opaque().set(false);
        compPanel.children().addAll(iconLabel,textLabel);
        //textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        AppComponentEventListener li = new AppComponentEventListener() {
            @Override
            public void onEvent(AppComponentEvent event) {
                switch (event.eventType()) {
                    case MOUSE_ENTER: {
                        compPanel.backgroundColor().set(Color.LIGHT_GRAY(compPanel.app()));
                        break;
                    }
                    case MOUSE_EXIT: {
                        compPanel.backgroundColor().set(null);
                        break;
                    }
                    case MOUSE_CLICKED: {
                        if (event.isPrimaryMouseButton()) {
                            if (file.isDirectory() || (file.isFile() && event.isDoubleClick())) {
                                viewer.load(file.getPath());
                            }
                        }
                        break;
                    }
                }
            }
        };
        compPanel.events().add(li, AppEventType.MOUSE_MOVED,AppEventType.MOUSE_ENTER,AppEventType.MOUSE_EXIT);
        iconLabel.events().add(li, AppEventType.MOUSE_MOVED,AppEventType.MOUSE_ENTER,AppEventType.MOUSE_EXIT);
        return compPanel;
    }

    public void refresh() {
        setURL(url);
    }

    @Override
    public void setURL(String url) {
        this.url = url;
        component.children().clear();
        File asFile = OtherUtils.asFile(url);
        if (asFile != null) {
            if (asFile.isDirectory() && asFile.getParentFile() != null) {
                component.children().add(asComponent(asFile.getParentFile(), ".."));
            }
            for (File f : listFiles(asFile)) {
                component.children().add(asComponent(f, null));
            }
        }
        if (onSuccess != null) {
            onSuccess.run();
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
        userEditable = true;
    }

    public void disposeComponent() {
        frame.app().iconSets().id().listeners().remove(propertyListener);

    }

}
