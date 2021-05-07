/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
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
public class FileComponent extends JPanel {

    private JTextField textField = new JTextField();
    private JButton openFile;
    private JButton reloadFile;
    private Application app;
    private boolean acceptAllFileFilterUsed;
    private List<FileFilter> fileFilters = new ArrayList<>();
    private List<FileChangeListener> listeners = new ArrayList<>();
    private String contentString = "";
    private SelectMode selectMode = SelectMode.FILES_AND_DIRECTORIES;

    public FileComponent(PangaeaNoteWindow win) {
        openFile = new JButton();
        SwingApplicationsUtils.registerButton(
                openFile,
                null, "folder",
                win.app()
        );
        openFile.addActionListener((e)
                -> {
            onShowDialog();
        }
        );

        reloadFile = new JButton();
        SwingApplicationsUtils.registerButton(
                reloadFile,
                null, "reload",
                win.app()
        );
        reloadFile.addActionListener((e)
                -> {
            doReload();
        }
        );
        reloadFile.setVisible(false);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setText(contentString);
            }

            @Override
            public void focusLost(FocusEvent e) {
                String s = textField.getText();
                setContentString(s);

            }
        });
        new GridBagLayoutSupport("[pwd-===][check][reload] ; insets(2)")
                .bind("pwd", textField)
                .bind("check", openFile)
                .bind("reload", reloadFile)
                .apply(this);

        TextAutoCompleteSupport.setup(textField, new FilePathTextAutoComplete());
    }

    public boolean isReloadButtonVisible() {
        return reloadFile.isVisible();
    }

    public FileComponent setReloadButtonVisible(boolean visible) {
        this.reloadFile.setVisible(visible);
        return this;
    }

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
        chooser.setFileSelectionMode(
                getSelectMode() == SelectMode.FILES_ONLY ? JFileChooser.FILES_ONLY
                        : getSelectMode() == SelectMode.DIRECTORIES_ONLY ? JFileChooser.DIRECTORIES_ONLY
                                : getSelectMode() == SelectMode.FILES_AND_DIRECTORIES ? JFileChooser.FILES_AND_DIRECTORIES
                                        : JFileChooser.FILES_AND_DIRECTORIES);
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

    public FileComponent setAcceptAllFileFilterUsed(boolean acceptAllFileFilterUsed) {
        this.acceptAllFileFilterUsed = acceptAllFileFilterUsed;
        return this;
    }

    public String getContentString() {
        return textField.getText();
    }

    public List<FileFilter> getFileFilters() {
        return fileFilters;
    }

    public FileComponent setFileFilters(List<FileFilter> fileFilters) {
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
        if (!s.equals(contentString)) {
            contentString = s;
            textField.setText(s);
            for (FileChangeListener listener : listeners) {
                listener.onFilePathChanged(s);
            }
            doReload();
        }
    }

    public SelectMode getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(SelectMode selectMode) {
        this.selectMode = selectMode == null ? SelectMode.FILES_AND_DIRECTORIES : selectMode;
    }

    public static enum SelectMode {
        FILES_ONLY,
        DIRECTORIES_ONLY,
        FILES_AND_DIRECTORIES,
    }

    public static interface FileChangeListener {

        void onFilePathChanged(String path);

        void onFilePathReloading(String path);
    }

    public void addFileChangeListener(FileChangeListener changeListener) {
        listeners.add(changeListener);
    }

    public void doReload() {
        for (FileChangeListener listener : listeners) {
            listener.onFilePathReloading(getContentString());
        }
    }
}
