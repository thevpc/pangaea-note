/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.urlviewer;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Button;
import net.thevpc.echo.Label;
import net.thevpc.echo.Menu;
import net.thevpc.echo.ScrollPane;
import net.thevpc.echo.TextField;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppEventType;
import net.thevpc.echo.constraints.*;
import net.thevpc.echo.impl.Applications;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class URLViewer extends BorderPane {

    private Header header;

    private ScrollPane scroll;
    private URLViewerComponent subComponent;
    //    private String path;
    private boolean userEditable = true;
    private boolean canEdit;
    private List<URLViewerListener> viewerListeners = new ArrayList<>();
    private PangaeaNoteFrame frame;
    private Label error;

    public URLViewer(PangaeaNoteFrame frame) {
        super(frame.app());
        header = new Header(this, frame)
                .with(h -> h.anchor().set(Anchor.TOP));
        this.frame = frame;
        scroll = new ScrollPane(app())
                .with(h -> h.anchor().set(Anchor.CENTER));
        error = new Label(app())
                .with(h -> h.anchor().set(Anchor.BOTTOM));
        children().addAll(header, scroll, error);
    }

    @Override
    public void requestFocus() {
        header.textField.requestFocus();
    }

    public String getSelectedPath() {
        return header.getContentString();
    }

    public void addViewerListener(URLViewerListener listener) {
        viewerListeners.add(listener);
    }

    public void resetContent() {
        subComponent = new UnsupportedViewerComponent("", "", null, frame, this, () -> {
        }, x -> {
        });
        scroll.child().set(subComponent);
        //scroll.getViewport().setView(subComponent.component());
    }

    public boolean isSupportedEdit() {
        return false;
    }

    public void navigate(String url) {
        if (url == null) {
            url = "";
        }
        header.getTextField().text().set(Str.of(url));
        fireStartLoading(url);
        if (url.length() > 0) {
            String contentType = null;
            if (subComponent != null) {
                subComponent.disposeComponent();
            }
            url = url.trim();
            subComponent = resolveSubComponent(url, contentType);
            subComponent.navigate(url);
            scroll.child().set(subComponent);
        } else {
            resetContent();
        }
    }

    protected void fireStartLoading(String url) {
        error.text().set(Str.of("Loading..."));
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onStartLoading(url);
        }
    }

    public void fireSuccessfulLoading(String url) {
        error.text().set(Str.empty());
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onSuccessfulLoading(url);
        }
    }

    protected URLViewerComponent resolveSubComponent(String path, String contentType) {
        if (path == null) {
            return new UnsupportedViewerComponent("", "", null, frame, this, () -> {
            }, x -> {
            });
        }
        try {
            return frame.getViewer(path, this);
        } catch (Exception ex) {
            return new UnsupportedViewerComponent("", "", null, frame, this, () -> fireSuccessfulLoading(path), this::fireError);
        }
    }

    public boolean isFolder(String name) {
        File f = Applications.asFile(name);
        return f != null && f.isDirectory();
    }

    public void fireError(Exception ex) {
        error.text().set(Str.of("ERROR: " + ex.getMessage()));
//        ex.printStackTrace();
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onError(getSelectedPath(), ex);
        }
    }

    public PangaeaNoteFrame frame() {
        return frame;
    }

    public boolean isEditable() {
        return subComponent != null && subComponent.isEditable();
    }

    public void setEditable(boolean editable) {
        userEditable = editable;
        if (subComponent != null) {
            subComponent.setEditable(editable);
        }
    }

    public static class Header extends GridPane {

        private TextField textField;
        //        private Application app;
        private boolean acceptAllFileFilterUsed;
        private List<FileFilter> fileFilters = new ArrayList<>();
        private URLViewer parent;
        private Button reloadButton;
        private Button goUpButton;
        private Button goHomeButton;
        private Menu openFileMenu;

        public Header(URLViewer parent, PangaeaNoteFrame win) {
            super(win.app());
            parentConstraints().addAll(AllMargins.of(3), AllFill.NONE, AllAnchors.LEFT, AllGrow.NONE, GrowContainer.HORIZONTAL);
            this.parent = parent;
            PangaeaNoteApp app = win.app();
            this.textField = new TextField(app)
                    .with(t -> t.childConstraints().addAll(Grow.HORIZONTAL, Fill.HORIZONTAL));
            ToolBar tb = new ToolBar(win.app());
            openFileMenu = new Menu(null, Str.i18n("Message.browse"), app)
                    .with(m -> {
                        m.icon().set(Str.of("folder"));
                    });

            //reload if press ENTER or lost focus
            textField.events().add(event -> {
                if (event.code() == KeyCode.ENTER) {
                    doReload();
                }
            }, AppEventType.KEY_PRESSED);
            textField.focused().onChange(event -> {
                if (!textField.focused().get()) {
                    //doReload();
                }
            });

//            textField.text().onChange(event -> doReload());
            tb.children().addAll(goUpButton = new Button(null, () -> goUp(), app)
                    .with((Button m) -> {
                        m.text().set(Str.i18n("Message.goUp"));
                        m.icon().set(Str.of("folder-up"));
                    }),
                    goHomeButton = new Button(null, () -> goHome(), app)
                            .with((Button m) -> {
                                m.text().set(Str.i18n("Message.goHome"));
                                m.icon().set(Str.of("home"));
                            }),
                    openFileMenu);
            openFileMenu.children().add(
                    new Button(null, () -> onShowDialog(), app)
                            .with((Button m) -> {
                                m.text().set(Str.i18n("Message.browse"));
                                m.icon().set(Str.of("folder"));
                            })
            );
            openFileMenu.children().add(
                    reloadButton = new Button(null, () -> doReload(), app)
                            .with((Button m) -> {
                                m.text().set(Str.i18n("Message.reload"));
                                m.icon().set(Str.of("reload"));
                            })
            );
            children().addAll(
                    textField,
                    tb
            );
            //TextAutoCompleteSupport.setup(textField, new FilePathTextAutoComplete());
        }

        public TextField getTextField() {
            return textField;
        }

        public boolean isEditable() {
            return textField.editable().get();
        }

        public void setEditable(boolean b) {
            goUpButton.enabled().set(b);
            goHomeButton.enabled().set(b);
            openFileMenu.enabled().set(b);
            textField.editable().set(b);
        }

        private void onShowDialog() throws HeadlessException {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            File f = Applications.asFile(textField.text().get().value());
            if (f != null && f.getParentFile() != null) {
                chooser.setCurrentDirectory(f);
            }
            if (f != null) {
                chooser.setSelectedFile(f);
            }
            for (FileFilter filter : fileFilters) {
                chooser.addChoosableFileFilter(filter);
            }
            chooser.setAcceptAllFileFilterUsed(acceptAllFileFilterUsed);
            int v = chooser.showOpenDialog(
                    (Component) app().mainFrame().get().peer().toolkitComponent()
            );
            if (v == JFileChooser.APPROVE_OPTION) {
                setContentString(chooser.getSelectedFile().getPath());
            }
        }

        public boolean isAcceptAllFileFilterUsed() {
            return acceptAllFileFilterUsed;
        }

        public Header setAcceptAllFileFilterUsed(boolean acceptAllFileFilterUsed) {
            this.acceptAllFileFilterUsed = acceptAllFileFilterUsed;
            return this;
        }

        public String getContentString() {
            return textField.text().get().value();
        }

        public void setContentString(String s) {
            if (s == null) {
                s = "";
            }
            if (!s.equals(getContentString())) {
                textField.text().set(Str.of(s));
            }
            doReload();
        }

        public List<FileFilter> getFileFilters() {
            return fileFilters;
        }

        public Header setFileFilters(List<FileFilter> fileFilters) {
            this.fileFilters = fileFilters;
            return this;
        }

        public void uninstall() {
        }

        public void install(Application app) {
        }

        public void goHome() {
            setContentString(System.getProperty("user.home"));
        }

        public void goUp() {
            String s = getContentString();
            if (s != null) {
                int x = s.length() - 1;
                while (x >= 0 && (s.charAt(x) == '/' || s.charAt(x) == '\\')) {
                    x--;
                }
                if (x > 0) {
                    for (int i = x; i >= 0; i--) {
                        char c = s.charAt(i);
                        if (c == '/' || c == '\\') {
                            setContentString(s.substring(0, i));
                            return;
                        }
                    }
                }
            }
        }

        public void doReload() {
            parent.navigate(getContentString());
        }

    }

    public Header getHeader() {
        return header;
    }
}
