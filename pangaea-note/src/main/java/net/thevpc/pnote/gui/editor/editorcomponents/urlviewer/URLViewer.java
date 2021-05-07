/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.urlviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.common.swing.button.JDropDownButton;
import net.thevpc.common.swing.text.FilePathTextAutoComplete;
import net.thevpc.common.swing.text.TextAutoCompleteSupport;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class URLViewer extends JPanel {

    private Header header;

    private JScrollPane scroll;
    private URLViewerComponent subComponent;
//    private String path;
    private boolean userEditable = true;
    private boolean canEdit;
    private List<URLViewerListener> viewerListeners = new ArrayList<>();
    private PangaeaNoteWindow win;
    private JLabel error = new JLabel();

    public URLViewer(PangaeaNoteWindow win) {
        super(new BorderLayout());
        header = new Header(this, win);
        this.win = win;
        scroll = new JScrollPane();
        add(header, BorderLayout.NORTH);
        add(scroll);
        add(error, BorderLayout.SOUTH);
    }

    public String getSelectedPath() {
        return header.getContentString();
    }

    public void addViewerListener(URLViewerListener listener) {
        viewerListeners.add(listener);
    }

    public void resetContent() {
        subComponent = new UnsupportedViewerComponent("", "", null, win, this, () -> {
        }, x -> {
        });
        scroll.getViewport().setView(subComponent.component());
    }

    public boolean isSupportedEdit() {
        return false;
    }

    public void load(String url) {
        if (url == null) {
            url = "";
        }
        header.getTextField().setText(url);
        fireStartLoading(url);
        if (url.length() > 0) {
            String contentType = null;
            if (subComponent != null) {
                subComponent.disposeComponent();
            }
            subComponent = resolveSubComponent(url, contentType);
            scroll.getViewport().setView(subComponent.component());
            subComponent.setURL(url);
        } else {
            resetContent();
        }
    }

    protected void fireStartLoading(String url) {
        error.setText("Loading...");
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onStartLoading(url);
        }
    }

    public void fireSuccessfulLoading(String url) {
        error.setText("");
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onSuccessfulLoading(url);
        }
    }

    protected URLViewerComponent resolveSubComponent(String path, String contentType) {
        if (path == null) {
            return new UnsupportedViewerComponent("", "", null, win, this, () -> {
            }, x -> {
            });
        }
        try {
            return win.getViewer(path, this);
        } catch (Exception ex) {
            return new UnsupportedViewerComponent("", "", null, win, this, () -> fireSuccessfulLoading(path), this::fireError);
        }
    }

    public boolean isFolder(String name) {
        File f = OtherUtils.asFile(name);
        return f != null && f.isDirectory();
    }

    public void fireError(Exception ex) {
        error.setText("ERROR: " + ex.getMessage());
        ex.printStackTrace();
        for (URLViewerListener viewerListener : viewerListeners) {
            viewerListener.onError(getSelectedPath(), ex);
        }
    }

    public void setEditable(boolean editable) {
        userEditable = editable;
        if (subComponent != null) {
            subComponent.setEditable(editable);
        }
    }

    public boolean isEditable() {
        return subComponent != null && subComponent.isEditable();
    }

    public static class Header extends JPanel {

        private JTextField textField = new JTextField();
        private JDropDownButton openFile;
        private JMenuItem openFileMenu;
        private JMenuItem goHomeMenu;

//        private JMenuItem reloadFileMenu;
//        private JMenuItem goUpMenu;
        private JButton reloadFileButton;
        private JButton goUpButton;
        
        private Application app;
        private boolean acceptAllFileFilterUsed;
        private List<FileFilter> fileFilters = new ArrayList<>();
//        private String contentString = "";
        private URLViewer parent;

        public Header(URLViewer parent, PangaeaNoteWindow win) {
            this.parent = parent;
            openFile = new JDropDownButton();
            openFile.setQuickActionDelay(0);
            SwingApplicationsUtils.registerButton(
                    openFile,
                    null, "folder",
                    win.app()
            );
            openFileMenu = new JMenuItem();
            SwingApplicationsUtils.registerButton(
                    openFileMenu,
                    "Message.browse", "open",
                    win.app()
            );
            openFileMenu.addActionListener((e)
                    -> {
                onShowDialog();
            }
            );
            
            
//            reloadFileMenu = new JMenuItem();
//            SwingApplicationsUtils.registerButton(
//                    reloadFileMenu,
//                    "Message.reload", "reload",
//                    win.app()
//            );
//            reloadFileMenu.addActionListener((e)
//                    -> {
//                doReload();
//            }
//            );
            reloadFileButton = new JButton();
            SwingApplicationsUtils.registerButton(
                    reloadFileButton,
                    null, "reload",
                    win.app()
            );
            reloadFileButton.addActionListener((e)
                    -> {
                doReload();
            }
            );
            goHomeMenu = new JMenuItem();
            SwingApplicationsUtils.registerButton(
                    goHomeMenu,
                    "Message.goHome", "home",
                    win.app()
            );
            goHomeMenu.addActionListener((e)
                    -> {
                goHome();
            }
            );
            goUpButton = new JButton();
            SwingApplicationsUtils.registerButton(
                    goUpButton,
                    null, "folder-up",
                    win.app()
            );
            goUpButton.addActionListener((e)
                    -> {
                goUp();
            }
            );
//            goUpMenu = new JMenuItem();
//            SwingApplicationsUtils.registerButton(
//                    goUpMenu,
//                    "Message.goUp", "folder-up",
//                    win.app()
//            );
//            goUpMenu.addActionListener((e)
//                    -> {
//                goUp();
//            }
//            );
            openFile.add(openFileMenu);
//            openFile.add(reloadFileMenu);
            openFile.addSeparator();
            openFile.add(goHomeMenu);
//            openFile.add(goUpMenu);

//            reloadFileMenu.setVisible(false);
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                }

                @Override
                public void focusLost(FocusEvent e) {
                    doReload();
                }
            });
            new GridBagLayoutSupport("[txt-===][refresh][up][open] ; insets(2)")
                    .bind("txt", textField)
                    .bind("open", openFile)
                    .bind("refresh", reloadFileButton)
                    .bind("up", goUpButton)
                    .apply(this);

            TextAutoCompleteSupport.setup(textField, new FilePathTextAutoComplete());
        }

//        public boolean isReloadButtonVisible() {
//            return reloadFileMenu.isVisible();
//        }

        public JTextField getTextField() {
            return textField;
        }

        public boolean isEditable() {
            return openFile.isEnabled() && textField.isEditable();
        }

        public void setEditable(boolean b) {
            openFile.setEnabled(b);
            textField.setEditable(b);
        }

        private void onShowDialog() throws HeadlessException {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            File f = OtherUtils.asFile(textField.getText());
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
                    app == null ? null : (Component) app.mainWindow().get().component()
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
            return textField.getText();
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

        public void setContentString(String s) {
            if (s == null) {
                s = "";
            }
            if (!s.equals(textField.getText())) {
                textField.setText(s);
            }
            doReload();
        }

        public static interface FileChangeListener {

            void onFilePathChanged(String path);

            void onFilePathReloading(String path);
        }

//        public void addFileChangeListener(FileChangeListener changeListener) {
//            listeners.add(changeListener);
//        }
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
            parent.load(getContentString());
        }

    }

}
