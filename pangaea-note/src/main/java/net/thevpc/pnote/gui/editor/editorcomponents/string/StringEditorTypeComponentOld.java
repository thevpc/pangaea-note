/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.string;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.AnyDocumentListener;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class StringEditorTypeComponentOld extends JPanel implements PangaeaNoteEditorTypeComponent {

    private JTextField text = new JTextField();
    private PangaeaNoteExt currentNote;

    public StringEditorTypeComponentOld() {
        GridBagLayoutSupport.of("[^$-==item]")
                .bind("item", text)
                .apply(this);
        text.getDocument().addDocumentListener(new AnyDocumentListener() {
            @Override
            public void anyChange(DocumentEvent e) {
                if (currentNote != null) {
                    currentNote.setContent(text.getText());
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
            text.setText("");
        } else {
            text.setText(note.getContent());
        }

    }

      @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        text.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return text.isEditable();
    }
}
