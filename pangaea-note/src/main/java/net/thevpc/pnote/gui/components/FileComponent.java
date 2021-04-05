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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.echo.Application;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;

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

    public FileComponent(PangaeaNoteGuiApp sapp) {
        openFile = new JButton("...");
        openFile.addActionListener((e)
                -> {
            onShowDialog();
        }
        );
        reloadFile = new JButton("<>");
        reloadFile.addActionListener((e)
                -> {
            for (FileChangeListener listener : listeners) {
                listener.onFilePathRelading(getContentString());
            }
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
        JFileChooser c = new JFileChooser();
        for (FileFilter filter : fileFilters) {
            c.addChoosableFileFilter(filter);
        }
        c.setAcceptAllFileFilterUsed(acceptAllFileFilterUsed);
        int v = c.showOpenDialog(
                app == null ? null : (Component) app.mainWindow().get().component()
        );
        if (v == JFileChooser.APPROVE_OPTION) {
            textField.setText(c.getSelectedFile().getPath());
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

    public FileComponent setValue(String s) {
        textField.setText(s);
        return this;
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
            for (FileChangeListener listener : listeners) {
                listener.onFilePathChanged(s);
            }
        }
    }

    public static interface FileChangeListener {

        void onFilePathChanged(String path);

        void onFilePathRelading(String path);
    }

    public void addFileChangeListener(FileChangeListener changeListener) {
        listeners.add(changeListener);
    }
}
