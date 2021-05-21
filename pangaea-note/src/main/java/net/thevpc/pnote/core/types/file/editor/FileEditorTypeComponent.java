/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.file.editor;

import java.util.Objects;

import net.thevpc.echo.BorderPane;
import net.thevpc.echo.HorizontalPane;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.Panel;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerListener;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class FileEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    private URLViewer fileViewer;
    private PangaeaNoteExt currentNote;
    private boolean editable = true;
    private PangaeaNoteFrame frame;

    public FileEditorTypeComponent(PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        fileViewer = new URLViewer(frame);
        fileViewer.addViewerListener(new URLViewerListener() {
            @Override
            public void onError(String path, Exception ex) {
            }

            @Override
            public void onStartLoading(String path) {
                if (currentNote != null) {
                    if (!Objects.equals(path, currentNote.getContent())) {
                        currentNote.setContent(frame.service().stringToElement(path));
                        frame.onDocumentChanged();
                    }
                }
            }

            @Override
            public void onSuccessfulLoading(String path) {
                if (currentNote != null) {
                    if (!Objects.equals(path, currentNote.getContent().toString())) {
                        currentNote.setContent(frame.service().stringToElement(path));
                        frame.onDocumentChanged();
                    }
                }
            }

            @Override
            public void onReset() {
                if (currentNote != null) {
                    if (!Objects.equals("", currentNote.getContent().toString())) {
                        currentNote.setContent(frame.service().stringToElement(""));
                        frame.onDocumentChanged();
                    }
                }
            }
        });
        
        children.add(fileViewer);
    }

    @Override
    public boolean isCompactMode() {
        return true;
    }

    @Override
    public AppComponent component() {
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
    public void setNote(PangaeaNoteExt note, PangaeaNoteFrame win) {
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
