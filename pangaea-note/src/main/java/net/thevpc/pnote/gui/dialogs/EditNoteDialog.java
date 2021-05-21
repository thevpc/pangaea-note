/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.i18n.I18n;
import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.pnote.api.EditTypeComponent;
import net.thevpc.pnote.api.PangaeaNoteTypeServiceBase;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

/**
 * @author vpc
 */
public class EditNoteDialog {

    private Panel panel;
    private TextField nameEditor;
    private CheckBox readOnlyEditor;
    private PangaeaNoteIconsList iconsEditor;
    private PangaeaNoteTitleFormatPanel titleEditor;
    private PangaeaNoteTypesComboBox typeEditor;

    private boolean ok = false;
    private PangaeaNote note;
    private PangaeaNoteExt vn;
    private PangaeaNoteFrame frame;
    private EditTypeComponent editTypeComponent;
    private Panel editTypeComponentPanel;
    private TabPane jTabbedPane;

    public EditNoteDialog(PangaeaNoteFrame frame, PangaeaNoteExt vn) {
        this.frame = frame;
        this.vn = vn;
        this.note = vn.toNote();

        Application app = frame.app();
        I18n i18n = app.i18n();

        panel = new VerticalPane(app);
        panel.children().addAll(
                new Label(Str.i18n("Message.name"), app),
                nameEditor = new TextField(Str.empty(), app),
                new Label(Str.i18n("Message.noteType"), app),
                typeEditor = new PangaeaNoteTypesComboBox(frame)
                        .with(v -> {
                            v.setSelectedContentType(frame.service().normalizeContentType(note.getContentType()), note.getEditorType());
                            v.selection().onChange(e -> onNoteTypeChanged());
                        }),
                readOnlyEditor = new CheckBox(null, Str.i18n("Message.readOnly"), app),
                jTabbedPane = new TabPane(app)
                        .with(t -> {
                            t.children().add(titleEditor = new PangaeaNoteTitleFormatPanel(frame));
                            t.children().add(iconsEditor = new PangaeaNoteIconsList(frame));
                            t.children().add(editTypeComponentPanel = new BorderPane(app)
                                    .with((Panel p) -> {
                                        p.title().set(Str.i18n("PangaeaNoteListSettingsComponent.optionsLabel"));
                                    }));
                        })
        );

        onNoteTypeChanged();
        nameEditor.text().set(Str.of(note.getName()));
        iconsEditor.setSelectedIcon(note.getIcon());
        readOnlyEditor.selected().set(note.isReadOnly());
        titleEditor.loadFromNote(note);
        editTypeComponent.loadFrom(note);
    }

    private void onNoteTypeChanged() {
        PangaeaNoteMimeType ct = PangaeaNoteMimeType.of(typeEditor.getSelectedContentTypeId());
        PangaeaNoteTypeServiceBase s = frame.service().findContentTypeService(ct);
        if (s == null) {
            s = frame.service().getTemplate(ct);
        }
        EditTypeComponent c = null;
        if (s != null) {
            c = s.createEditPanel(frame);
        }
        boolean haveOptions = c != null;
        if (c == null) {
            c = new EmptyEditTypeComponent(frame.app());
        }
        editTypeComponent = c;
        editTypeComponentPanel.children().removeAll();
        editTypeComponentPanel.children().add(editTypeComponent.component());
        editTypeComponent.loadFrom(note);
        jTabbedPane.children().get(2).enabled().set(haveOptions);
//        if (panel != null) {
//            Window p = SwingUtilities.getWindowAncestor(panel);
//            if (p != null) {
//                p.pack();
//            }
//        }
    }

    protected PangaeaNote getNote() {
        String txt = nameEditor.text().getOr(x -> x == null ? null : x.value(frame.i18n()));
        note.setName(txt);
        if (txt == null || txt.trim().length() == 0) {
            String ct = note.getContentType();
            nameEditor.text().set(Str.of(ct));
            note.setName(frame.app().i18n().getString("content-type." + ct));
        }
        note.setIcon(iconsEditor.getSelectedIconId());
        note.setReadOnly(readOnlyEditor.selected().get());
        titleEditor.loadToNote(note);
        note.setContentType(typeEditor.getSelectedContentTypeId());
        if (editTypeComponent != null) {
            editTypeComponent.loadTo(note);
        }
        frame.service().addRecentNoteType(note.getContentType());
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
        while (true) {
            install();
            this.ok = false;
            new Alert(frame.app())
                    .setTitle(Str.i18n("Message.editNote"))
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
                    .showDialog(null);
            try {
                PangaeaNote n = get();
                if (n != null) {
                    frame.service().updateNoteProperties(vn, n, frame);
                    frame.onDocumentChanged();
                }
                return n;
            } catch (Exception ex) {
                frame.app().errors().add(ex);
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
