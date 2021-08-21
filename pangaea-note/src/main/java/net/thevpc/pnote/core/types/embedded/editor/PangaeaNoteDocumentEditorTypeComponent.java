/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.embedded.editor;

import net.thevpc.echo.BorderPane;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public class PangaeaNoteDocumentEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    private PangaeaNote currentNote;
    private boolean editable = true;
    private PangaeaNoteFrame frame;
    private boolean compactMode;

    public PangaeaNoteDocumentEditorTypeComponent(boolean compactMode,PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        this.compactMode=compactMode;
//        add(new JLabel("pangaea-note-document"), BorderLayout.NORTH);
//        add(file = new JLabel(""), BorderLayout.CENTER);
//        add(error = new JLabel(""), BorderLayout.SOUTH);
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNote note) {
        try {
            this.currentNote = note;
//            String path = PangaeaNoteEmbeddedService.of(win.service()).getContentValueAsInfo(note.getContent()).getPath();
//            if (path == null || path.length() == 0) {
//                error.setText("missing file");
//            } else {
//                if (!note.isLoaded()) {
//                    PangaeaNote n = note.toNote();
//                    win.service().loadNode(n, win.wallet(), false, win.getCurrentFilePath());
//                    note.copyFrom(n);
//                }
//                error.setText(note.error == null ? "" : note.error.getEx().toString());
//            }
            setEditable(!note.isReadOnly());
        } catch (Exception ex) {
            //error.setText(ex.toString());
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable = b;
    }

}
