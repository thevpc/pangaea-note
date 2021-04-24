/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.string;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.pnote.model.HighlightType;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.util.AnyDocumentListener;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import net.thevpc.pnote.types.rich.editor.RichEditor;

/**
 *
 * @author vpc
 */
public class StringEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent {

    private JTextField text = new JTextField();
    private PangaeaNoteExt currentNote;
    private PangaeaNoteWindow sapp;

    public StringEditorTypeComponent(PangaeaNoteWindow sapp) {
        this.sapp = sapp;
        GridBagLayoutSupport.of("[^$-==item]")
                .bind("item", text)
                .apply(this);
        text.getDocument().addDocumentListener(new AnyDocumentListener() {
            @Override
            public void anyChange(DocumentEvent e) {
                if (currentNote != null) {
                    sapp.onDocumentChanged();
                    currentNote.setContent(sapp.service().stringToElement(text.getText()));
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
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow sapp) {
        this.currentNote = note;
        if (note == null) {
            text.setText("");
        } else {
            text.setText(sapp.service().elementToString(note.getContent()));
        }
        text.setEditable(!note.isReadOnly());

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

//    @Override
//    public void highlight(int from,int to, HighlightType hightlightType) {
//        try {
//            hi
//            javax.swing.text.DefaultHighlighter.DefaultHighlightPainter highlightPainter
//                    = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
//            text.getHighlighter().addHighlight(from, to, highlightPainter);
//        } catch (BadLocationException ex) {
//            Logger.getLogger(RichEditor.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
