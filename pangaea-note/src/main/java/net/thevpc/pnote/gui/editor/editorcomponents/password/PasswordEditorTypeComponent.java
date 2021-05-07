/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.password;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import net.thevpc.pnote.api.model.HighlightType;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.util.AnyDocumentListener;
import net.thevpc.pnote.gui.components.PasswordComponent;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

/**
 *
 * @author vpc
 */
public class PasswordEditorTypeComponent extends JPanel implements PangaeaNoteEditorTypeComponent {

    private PasswordComponent text;
    private PangaeaNoteExt currentNote;

    public PasswordEditorTypeComponent(PangaeaNoteWindow win) {
        setLayout(new BorderLayout());
        text = new PasswordComponent(win);
        add(text, BorderLayout.NORTH);
        add(new JLabel(), BorderLayout.CENTER);
        text.getPasswordField().getDocument().addDocumentListener(new AnyDocumentListener() {
            @Override
            public void anyChange(DocumentEvent e) {
                if (currentNote != null) {
                    win.onDocumentChanged();
                    currentNote.setContent(win.service().stringToElement(text.getContentString()));
                }
            }
        });
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
        text.uninstall();
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow win) {
        this.currentNote = note;
        if (note == null) {
            text.setContentString("");
        } else {
            text.setContentString(win.service().elementToString(note.getContent()));
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

    
}
