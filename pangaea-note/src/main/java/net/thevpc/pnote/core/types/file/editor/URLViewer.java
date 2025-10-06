/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.file.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppEventType;
import net.thevpc.echo.constraints.*;
import net.thevpc.echo.impl.Applications;
import net.thevpc.nuts.command.NExecCmd;
import net.thevpc.nuts.command.NExecutionType;
import net.thevpc.nuts.concurrent.NConcurrent;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thevpc
 */
public class URLViewer extends BorderPane {

    private Header header;

    private ScrollPane scroll;
    private URLViewerComponent subComponent;
    //    private String path;
    private boolean editable = true;
    private boolean canEdit;
    private List<URLViewerListener> viewerListeners = new ArrayList<>();
    private PangaeaNoteFrame frame;
//    private Label error;

    public URLViewer(PangaeaNoteFrame frame) {
        super(frame.app());
        header = new Header(this, frame)
                .with(h -> h.anchor().set(Anchor.TOP));
        this.frame = frame;
        scroll = new ScrollPane(app())
                .with(h -> h.anchor().set(Anchor.CENTER));
//        error = new Label(app())
//                .with(h -> h.anchor().set(Anchor.BOTTOM));
        children().addAll(header, scroll/*, error*/);
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
            setEditable(isEditable());
            scroll.child().set(subComponent);
            JComponent jc = (JComponent) scroll.peer().toolkitComponent();
            jc.invalidate();
            jc.revalidate();
        } else {
            resetContent();
        }
    }

    protected void fireStartLoading(String url) {
//        error.text().set(Str.of("Loading..."));
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onStartLoading(url);
        }
    }

    public void fireSuccessfulLoading(String url) {
//        error.text().set(Str.empty());
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
//        error.text().set(Str.of("ERROR: " + ex.getMessage()));
//        ex.printStackTrace();
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onError(getSelectedPath(), ex);
        }
    }

    public PangaeaNoteFrame frame() {
        return frame;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        header.setEditable(editable);
        if (subComponent != null) {
            subComponent.setEditable(editable);
        }
    }

    public Header getHeader() {
        return header;
    }

    public static class Header extends GridPane {

        private TextField textField;
        //        private Application app;
        private boolean acceptAllFileFilterUsed;
        private URLViewer parent;
        private Button browseButton;
        private Button reloadButton;
        private Button sysLoadButton;
        private Button goUpButton;
        private Button goHomeButton;
        private PangaeaNoteFrame frame;
//        private Menu openFileMenu;

        public Header(URLViewer parent, PangaeaNoteFrame win) {
            super(win.app());
            parentConstraints().addAll(AllMargins.of(3), AllFill.NONE, AllAnchors.LEFT, AllGrow.NONE, ContainerGrow.TOP_ROW);
            this.frame = win;
            this.parent = parent;
            PangaeaNoteApp app = win.app();
            this.textField = new TextField(app)
                    .with(t -> t.childConstraints().addAll(Grow.HORIZONTAL, Fill.HORIZONTAL));
            ToolBar tb = new ToolBar(win.app());

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
            goUpButton = new Button(null, () -> goUp(), app)
                    .with((Button m) -> {
                        m.text().set(Str.i18n("Message.goUp"));
                        m.icon().set(Str.of("folder-up"));
                    });
            goHomeButton = new Button(null, () -> goHome(), app)
                    .with((Button m) -> {
                        m.text().set(Str.i18n("Message.goHome"));
                        m.icon().set(Str.of("home"));
                    });
//            textField.text().onChange(event -> doReload());
            browseButton = new Button(null, () -> onShowDialog(), app)
                    .with((Button m) -> {
                        m.text().set(Str.i18n("Message.browse"));
                        m.icon().set(Str.of("folder"));
                    });
            reloadButton = new Button(null, () -> doReload(), app)
                    .with((Button m) -> {
                        m.text().set(Str.i18n("Message.reload"));
                        m.icon().set(Str.of("reload"));
                    });
            sysLoadButton = new Button(null, () -> doSysLoad(), app)
                    .with((Button m) -> {
                        m.text().set(Str.i18n("Message.sysLoad"));
                        m.icon().set(Str.of("eye"));
                    });
            tb.children().addAll(reloadButton, goUpButton, goHomeButton, browseButton, sysLoadButton);

//            openFileMenu = new Menu(null, Str.i18n("Message.browse"), app)
//                    .with(m -> {
//                        m.icon().set(Str.of("folder"));
//                    });
//            openFileMenu
//            openFileMenu.children().add();
//            openFileMenu.children().add();

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
            browseButton.enabled().set(b);
//            openFileMenu.enabled().set(b);
            textField.editable().set(b);
        }

        private void onShowDialog() {
            FileChooser chooser = new FileChooser(app());
            chooser.acceptDirectories().set(true);
            chooser.acceptFiles().set(true);
            File f = Applications.asFile(textField.text().get().value());
            if (f != null && f.getParentFile() != null) {
                chooser.currentDirectory().set(f.getPath());
            }
            if (f != null) {
                chooser.selection().set(f.getPath());
            }
            chooser.filters().add(new net.thevpc.echo.FileFilter(Str.i18n("Message.AnyFileFilter"), "*.*"));
            if (chooser.showOpenDialog()) {
                setContentString(chooser.selection().get());
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
                            String r = s.substring(0, i);
                            if (r.isEmpty()) {
                                r = s.substring(0, i + 1);
                            }
                            setContentString(r);
                            return;
                        }
                    }
                }
            }
        }

        public void doReload() {
            parent.navigate(getContentString());
        }

        public void doSysLoad() {
            String ll = getContentString();
            if (!ll.trim().isEmpty()) {
                NConcurrent.of().executorService().submit(() -> {
                            try {
                                NExecCmd.of()
                                        .setExecutionType(NExecutionType.OPEN)
                                        .setCommand(ll)
                                        .run();
                            } catch (Exception ex) {
                                frame.app().errors().add(ex);
                            }
                        });
            }
        }
    }
}
