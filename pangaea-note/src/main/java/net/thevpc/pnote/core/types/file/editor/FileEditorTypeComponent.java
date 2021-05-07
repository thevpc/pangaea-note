/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.file.editor;

import java.awt.BorderLayout;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerListener;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class FileEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent {

    private URLViewer fileViewer;
    private PangaeaNoteExt currentNote;
    private boolean editable = true;
    private PangaeaNoteWindow win;

    public FileEditorTypeComponent(PangaeaNoteWindow win) {
        super(new BorderLayout());
        this.win = win;
        fileViewer = new URLViewer(win);
        fileViewer.addViewerListener(new URLViewerListener() {
            @Override
            public void onError(String path, Exception ex) {
            }

            @Override
            public void onStartLoading(String path) {
                if (currentNote != null) {
                    if (!Objects.equals(path, currentNote.getContent())) {
                        currentNote.setContent(win.service().stringToElement(path));
                        win.onDocumentChanged();
                    }
                }
            }

            @Override
            public void onSuccessfulLoading(String path) {
                if (currentNote != null) {
                    if (!Objects.equals(path, currentNote.getContent())) {
                        currentNote.setContent(win.service().stringToElement(path));
                        win.onDocumentChanged();
                    }
                }
            }

            @Override
            public void onReset() {
                if (currentNote != null) {
                    if (!Objects.equals("", currentNote.getContent())) {
                        currentNote.setContent(win.service().stringToElement(""));
                        win.onDocumentChanged();
                    }
                }
            }
        });
        
        add(fileViewer, BorderLayout.CENTER);
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

//    protected void updateURL(String s) {
//        fileViewer.load(s);
//        fileViewer.setEditable(fileViewer.isSupportedEdit());
//    }
    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow win) {
        this.currentNote = note;
        String c = win.service().elementToString(note.getContent());
        fileViewer.load(c);
    }

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable = b;
        fileViewer.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return editable && fileViewer.isEditable();
    }
}
