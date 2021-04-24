/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.file.editor;

import java.awt.BorderLayout;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.util.URLViewer;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.components.FileComponent;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class FileEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent {

    private JLabel error;
    private FileComponent comp;
    private URLViewer fileViewer;
    private PangaeaNoteExt currentNote;
    private boolean editable = true;
    private PangaeaNoteWindow win;

    public FileEditorTypeComponent(PangaeaNoteWindow win) {
        super(new BorderLayout());
        this.win = win;
        error = new JLabel();
        comp = new FileComponent(win).setReloadButtonVisible(true);
        comp.addFileChangeListener(new FileComponent.FileChangeListener() {
            @Override
            public void onFilePathChanged(String path) {
                if (currentNote != null) {
                    String t = comp.getTextField().getText();
                    if (!Objects.equals(t, currentNote.getContent())) {
                        currentNote.setContent(win.service().stringToElement(t));
                        win.onDocumentChanged();
                    }
                }
                updateURL(path);
            }

            @Override
            public void onFilePathReloading(String path) {
                if (currentNote != null) {
                    String t = comp.getTextField().getText();
                    if (!Objects.equals(t, currentNote.getContent())) {
                        currentNote.setContent(win.service().stringToElement(t));
                        win.onDocumentChanged();
                    }

                }
                updateURL(path);
            }
        });
        fileViewer = new URLViewer(win);
        add(comp, BorderLayout.NORTH);
        add(fileViewer, BorderLayout.CENTER);
        add(error, BorderLayout.SOUTH);
        fileViewer.addViewerListener(new URLViewer.URLViewerListener() {
            @Override
            public void onError(String path, Exception ex) {
                error.setText("ERROR: " + ex.getMessage());
            }

            @Override
            public void onStartLoading(String path) {
                error.setText("Loading...");
            }

            @Override
            public void onSuccessfulLoading(String path) {
                error.setText("");
            }

            @Override
            public void onReset() {
                error.setText("");
            }
        });
    }


    @Override
    public boolean isCompactMode() {
        return true;
    }

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
    }

    protected void updateURL(String s) {
        fileViewer.load(s);
        fileViewer.setEditable(fileViewer.isSupportedEdit());
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow sapp) {
        this.currentNote = note;
        String c = sapp.service().elementToString(note.getContent());
        comp.setContentString(c);
        fileViewer.load(c);
    }

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable = b;
        comp.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return editable && comp.isEditable();
    }
}
