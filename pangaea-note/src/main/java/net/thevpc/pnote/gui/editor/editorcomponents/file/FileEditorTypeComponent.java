/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.file;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
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
    private PangaeaNoteGuiApp sapp;

    public FileEditorTypeComponent(PangaeaNoteGuiApp sapp) {
        super(new BorderLayout());
        this.sapp = sapp;
        error = new JLabel();
        comp = new FileComponent(sapp).setReloadButtonVisible(true);
        comp.addFileChangeListener(new FileComponent.FileChangeListener() {
            @Override
            public void onFilePathChanged(String path) {
                if (currentNote != null) {
                    currentNote.setContent(comp.getTextField().getText());
                }
                updateURL(path);
            }

            @Override
            public void onFilePathRelading(String path) {
                if (currentNote != null) {
                    currentNote.setContent(comp.getTextField().getText());
                }
                updateURL(path);
            }
        });
        fileViewer = new URLViewer();
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
    public void setNote(PangaeaNoteExt note, PangaeaNoteGuiApp sapp) {
        this.currentNote = note;
        String c = note.getContent();
        fileViewer.load(c);
    }

    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable = b;
        comp.setEditable(b);
    }

    public boolean isEditable() {
        return editable && comp.isEditable();
    }

}
