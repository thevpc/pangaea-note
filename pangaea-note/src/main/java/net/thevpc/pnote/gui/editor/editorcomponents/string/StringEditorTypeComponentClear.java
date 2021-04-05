/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.string;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.AnyDocumentListener;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class StringEditorTypeComponentClear extends JTextField implements PangaeaNoteEditorTypeComponent {

    private PangaeaNoteExt currentNote;

    public StringEditorTypeComponentClear() {
        this.getDocument().addDocumentListener(new AnyDocumentListener() {
            @Override
            public void anyChange(DocumentEvent e) {
                if (currentNote != null) {
                    currentNote.setContent(StringEditorTypeComponentClear.this.getText());
                }
            }
        });
//        setBorder(BorderFactory.createLineBorder(Color.red));
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

    @Override
    public void setNote(PangaeaNoteExt note,PangaeaNoteGuiApp sapp) {
        this.currentNote = note;
        if (note == null) {
            this.setText("");
        } else {
            this.setText(note.getContent());
        }
    }

}
