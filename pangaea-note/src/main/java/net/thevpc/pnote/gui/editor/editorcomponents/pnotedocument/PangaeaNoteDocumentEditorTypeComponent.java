/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.pnotedocument;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.PNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class PangaeaNoteDocumentEditorTypeComponent extends JPanel implements PNoteEditorTypeComponent {

    private JLabel file;
    private JLabel error;
    private PangaeaNoteExt currentNote;
    private boolean editable = true;
    private PangaeaNoteGuiApp sapp;

    public PangaeaNoteDocumentEditorTypeComponent(PangaeaNoteGuiApp sapp) {
        super(new BorderLayout());
        this.sapp=sapp;
        add(new JLabel("pangaea-note-document"), BorderLayout.NORTH);
        add(file = new JLabel(""), BorderLayout.CENTER);
        add(error = new JLabel(""), BorderLayout.SOUTH);
    }

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteGuiApp sapp) {
        try {
            this.currentNote = note;
            if (note.getContent() == null || note.getContent().length() == 0) {
                error.setText("missing file");
            } else {
                if (!note.isLoaded()) {
                    PangaeaNote n = note.toNote();
                    sapp.service().loadNode(n, sapp.wallet(), false, sapp.getCurrentFilePath());
                    note.copyFrom(n);
                }
                error.setText(note.error == null ? "" : note.error.getEx().toString());
            }
            setEditable(!note.isReadOnly());
        } catch (Exception ex) {
            error.setText(ex.toString());
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
