/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.file.editor;

import java.util.Objects;

import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public class PangaeaNoteFileEditorTypeComponent extends URLViewer implements PangaeaNoteEditorTypeComponent {

    private PangaeaNote currentNote;

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
                        currentNote.setContent(frame.app().stringToElement(path));
                        frame.onDocumentChanged();
                    }
                }
            }

            @Override
            public void onSuccessfulLoading(String path) {
                if (currentNote != null) {
                    if (!Objects.equals(path, currentNote.getContent().toString())) {
                        currentNote.setContent(frame.app().stringToElement(path));
                        frame.onDocumentChanged();
                    }
                }
            }

            @Override
            public void onReset() {
                if (currentNote != null) {
                    if (!Objects.equals("", currentNote.getContent().toString())) {
                        currentNote.setContent(frame.app().stringToElement(""));
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
    public void setNote(PangaeaNote note) {
        this.currentNote = note;
        String c = frame().app().elementToString(note.getContent());
        navigate(c);
        setEditable(currentNote==null?false:!currentNote.isReadOnly());
    }

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        super.setEditable(b);
    }

}
