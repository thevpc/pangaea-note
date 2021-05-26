/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.file.editor;

import java.util.Objects;

import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewer;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.editorcomponents.urlviewer.URLViewerListener;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class PangaeaNoteFileEditorTypeComponent extends URLViewer implements PangaeaNoteEditorTypeComponent {

    private PangaeaNoteExt currentNote;
    private boolean editable = true;

    public PangaeaNoteFileEditorTypeComponent(PangaeaNoteFrame frame) {
        super(frame);
        this.addViewerListener(new URLViewerListener() {
            @Override
            public void onError(String path, Exception ex) {
            }

            @Override
            public void onStartLoading(String path) {
                if (currentNote != null) {
                    if (!Objects.equals(path, currentNote.getContent().toString())) {
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
    }


    @Override
    public boolean isCompactMode() {
        return true;
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
        navigate(c);
    }

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable = b;
        super.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return editable && super.isEditable();
    }
}
