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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.echo.Application;
import net.thevpc.pnote.gui.PangaeaNoteWindow;

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

    public FileComponent(PangaeaNoteWindow sapp) {
        openFile = new JButton("...");
        openFile.addActionListener((e)
                -> {
            onShowDialog();
        }
        );
        reloadFile = new JButton("<>");
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
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent evt) {
//                JTextField jTextField1 = (JTextField) evt.getSource();
//                if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE || evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_CONTROL) {
//
//                } else {
//                    String to_check = jTextField1.getText();
//                    int to_check_len = to_check.length();
//
//                    for (String data : possibilities()) {
//                        String check_from_data = "";
//                        for (int i = 0; i < to_check_len; i++) {
//                            if (to_check_len <= data.length()) {
//                                check_from_data = check_from_data + data.charAt(i);
//                            }
//                        }
//                        //System.out.print(check_from_data);
//                        if (check_from_data.equals(to_check)) {
//                            //System.out.print("Found");
//                            comp.setValue(data);
//                            jTextField1.setSelectionStart(to_check_len);
//                            jTextField1.setSelectionEnd(data.length());
//                            break;
//                        }
//                    }
//                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                JTextField jTextField1 = (JTextField) evt.getSource();
                if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE || evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_CONTROL) {

                } else {
                    String to_check = jTextField1.getText();
                    int to_check_len = to_check.length();

                    for (String data : possibilities()) {
                        String check_from_data = "";
                        for (int i = 0; i < to_check_len; i++) {
                            if (to_check_len <= data.length()) {
                                check_from_data = check_from_data + data.charAt(i);
                            }
                        }
                        //System.out.print(check_from_data);
                        if (check_from_data.equals(to_check)) {
                            //System.out.print("Found");
                            setContentString(data);
                            jTextField1.select(to_check_len,data.length());
                            break;
                        }
                    }
                }

            }
        });

    }

    private List<String> possibilities() {
        List<String> a = new ArrayList<>();
        String v = textField.getText();
        if (v == null || v.isEmpty()) {
            for (File file : File.listRoots()) {
                a.add(file.getPath());
            }
        } else {
            boolean isFile = false;
            for (File listRoot : File.listRoots()) {
                if (v.startsWith(listRoot.getPath())) {
                    isFile = true;
                    break;
                }
            }
            if (isFile) {
                File z = null;
                z = new File(v);
                if (v.endsWith("/") || v.endsWith("\\")) {
                } else {
                    z = z.getParentFile();
                }
                File[] lf = z.listFiles();
                if (lf != null) {
                    for (File file : lf) {
                        a.add(file.getPath());
                    }
                }
            }
        }
        return a;
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
