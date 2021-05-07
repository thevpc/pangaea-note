/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.pnote.api.EditTypeComponent;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Consumer;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.thevpc.common.i18n.I18n;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.api.PangaeaNoteTypeServiceBase;

/**
 *
 * @author vpc
 */
public class EditNoteDialog {

    private JPanel panel;
    private JTextField nameEditor;
    private JCheckBox readOnlyEditor;
    private PangaeaNoteIconsList iconsEditor;
    private PangaeaNoteTitleFormatPanel titleEditor;
    private PangaeaNoteTypesCombobox typeEditor;

    private boolean ok = false;
    private PangaeaNote note;
    private PangaeaNoteExt vn;
    private PangaeaNoteWindow win;
    private EditTypeComponent editTypeComponent;
    private JPanel editTypeComponentPanel;
    private JTabbedPane jTabbedPane;

    public EditNoteDialog(PangaeaNoteWindow win, PangaeaNoteExt vn) {
        this.win = win;
        this.vn = vn;
        this.note = vn.toNote();
        nameEditor = new JTextField("");
        typeEditor = new PangaeaNoteTypesCombobox(win);
        titleEditor = new PangaeaNoteTitleFormatPanel(win);
        typeEditor.setSelectedContentType(win.service().normalizeContentType(note.getContentType()), note.getEditorType());
        typeEditor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    onNoteTypeChanged();
                }
            }
        });
        iconsEditor = new PangaeaNoteIconsList(win);
        I18n i18n = win.app().i18n();
        readOnlyEditor = new JCheckBox(i18n.getString("Message.readOnly"));

        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EditNoteDialog.class.getResource(
                "/net/thevpc/pnote/forms/EditNoteDialog.gbl-form"
        ));
        gbs.bind("nameLabel", new JLabel(i18n.getString("Message.name")));
        gbs.bind("nameEditor", nameEditor);
        gbs.bind("typeLabel", new JLabel(i18n.getString("Message.noteType")));
        gbs.bind("typeEditor", typeEditor);
        gbs.bind("readOnlyEditor", readOnlyEditor);

        editTypeComponentPanel = new JPanel(new BorderLayout());
        jTabbedPane = new JTabbedPane();
        gbs.bind("tabs", jTabbedPane);
        jTabbedPane.addTab(i18n.getString("PangaeaNoteListSettingsComponent.titleLabel"), titleEditor);
        jTabbedPane.addTab(i18n.getString("PangaeaNoteListSettingsComponent.iconsLabel"), iconsEditor);
        jTabbedPane.addTab(i18n.getString("PangaeaNoteListSettingsComponent.optionsLabel"), editTypeComponentPanel);
        gbs.bind("tabs", jTabbedPane);

        onNoteTypeChanged();
        iconsEditor.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                jTabbedPane.setIconAt(1, iconsEditor.getSelectedIcon());
            }
        });
        nameEditor.setText(note.getName());
        iconsEditor.setSelectedIcon(note.getIcon());
        readOnlyEditor.setSelected(note.isReadOnly());
        titleEditor.loadFromNote(note);
        editTypeComponent.loadFrom(note);

        panel = (gbs.apply(new JPanel()));
    }

    private void onNoteTypeChanged() {
        PangaeaNoteMimeType ct = PangaeaNoteMimeType.of(typeEditor.getSelectedContentTypeId());
        PangaeaNoteTypeServiceBase s = win.service().findContentTypeService(ct);
        if (s == null) {
            s = win.service().getTemplate(ct);
        }
        EditTypeComponent c = null;
        if (s != null) {
            c = s.createEditPanel(win);
        }
        boolean haveOptions=c!=null;
        if (c == null) {
            c = new EmptyEditTypeComponent();
        }
        editTypeComponent = c;
        editTypeComponentPanel.removeAll();
        editTypeComponentPanel.add(editTypeComponent.component());
        editTypeComponent.loadFrom(note);
        jTabbedPane.setEnabledAt(2, haveOptions);
        if (panel != null) {
            Window p = SwingUtilities.getWindowAncestor(panel);
            if (p != null) {
                p.pack();
            }
        }
    }

    protected PangaeaNote getNote() {
        note.setName(nameEditor.getText());
        if (nameEditor.getText() == null || nameEditor.getText().trim().length() == 0) {
            String ct = note.getContentType();
            nameEditor.setText(ct);
            note.setName(win.app().i18n().getString("content-type." + ct));
        }
        note.setIcon(iconsEditor.getSelectedIconId());
        note.setReadOnly(readOnlyEditor.isSelected());
        titleEditor.loadToNote(note);
        note.setContentType(typeEditor.getSelectedContentTypeId());
        if (editTypeComponent != null) {
            editTypeComponent.loadTo(note);
        }
        win.service().addRecentNoteType(note.getContentType());
        return note;
    }

    protected void install() {
    }

    protected void uninstall() {
    }

    protected void ok() {
        uninstall();
        this.ok = true;
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
    }

    public PangaeaNote showDialog() {
        Consumer<Exception> exHandler = win::showError;
        while (true) {
            install();
            this.ok = false;
            win.app().newDialog()
                    .setTitleId("Message.editNote")
                    .setContent(panel)
                    .withOkCancelButtons(
                            (a) -> {
                                ok();
                                a.getDialog().closeDialog();
                            },
                            (a) -> {
                                cancel();
                                a.getDialog().closeDialog();
                            }
                    )
                    .showDialog();
            try {
                PangaeaNote n = get();
                if (n != null) {
                    win.service().updateNoteProperties(vn, n);
                    win.onDocumentChanged();
                }
                return n;
            } catch (Exception ex) {
                exHandler.accept(ex);
            }
        }
    }

    public PangaeaNote get() {
        if (ok) {
            return getNote();
        }
        return null;
    }

}
