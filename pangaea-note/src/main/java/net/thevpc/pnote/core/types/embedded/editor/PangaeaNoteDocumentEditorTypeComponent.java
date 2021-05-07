/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.embedded.editor;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteDocumentEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent {

    private JLabel file;
    private JLabel error;
    private PangaeaNoteExt currentNote;
    private boolean editable = true;
    private PangaeaNoteWindow win;
    private boolean compactMode;

    public PangaeaNoteDocumentEditorTypeComponent(boolean compactMode,PangaeaNoteWindow win) {
        super(new BorderLayout());
        this.win = win;
        this.compactMode=compactMode;
        add(new JLabel("pangaea-note-document"), BorderLayout.NORTH);
        add(file = new JLabel(""), BorderLayout.CENTER);
        add(error = new JLabel(""), BorderLayout.SOUTH);
    }

    public boolean isCompactMode() {
        return compactMode;
    }
    

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow win) {
        try {
            this.currentNote = note;
            String path = PangaeaNoteEmbeddedService.of(win.service()).getContentValueAsPath(note.getContent());
            if (path == null || path.length() == 0) {
                error.setText("missing file");
            } else {
                if (!note.isLoaded()) {
                    PangaeaNote n = note.toNote();
                    win.service().loadNode(n, win.wallet(), false, win.getCurrentFilePath());
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
