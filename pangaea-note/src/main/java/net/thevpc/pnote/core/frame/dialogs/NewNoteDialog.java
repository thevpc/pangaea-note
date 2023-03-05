/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppEventType;
import net.thevpc.echo.constraints.*;
import net.thevpc.echo.impl.Applications;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.pnote.api.PangaeaNoteTemplate;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * @author thevpc
 */
public class NewNoteDialog {

    private Panel panel;
    private TextField nameText;
    private PangaeaNoteTypesList typeList;

    private boolean ok = false;
    private PangaeaNoteFrame frame;

    public NewNoteDialog(PangaeaNoteFrame frame) {
        this.frame = frame;
        panel = new GridPane(1, frame.app())
                .with(p -> {
                    p.parentConstraints().addAll(AllMargins.of(5), AllFill.HORIZONTAL, AllGrow.HORIZONTAL, ContainerGrow.ALL);
                    p.children().addAll(
                            new Label(Str.i18n("Message.name"), frame.app()),
                            nameText = new TextField(Str.empty(), frame.app())
                                    .with(t -> {
                                        t.events().add((e) -> {
                                            ChoiceList<SimpleItem> list = typeList.list();
                                            if (e.code() == KeyCode.DOWN) {
                                                int i = list.selection().indices().get();
                                                if (i < list.values().size() - 1) {
                                                    list.selection().indices().set(
                                                            i + 1
                                                    );
                                                    list.ensureIndexIsVisible(i + 1);
                                                }
                                            } else if (e.code() == KeyCode.UP) {
                                                int i = list.selection().indices().get();
                                                if (i > 0) {
                                                    list.selection().indices().set(i - 1);
                                                    list.ensureIndexIsVisible(i - 1);
                                                }
                                            }
                                        }, AppEventType.KEY_PRESSED);
                                    }),
                             new Label(Str.i18n("Message.noteType"), frame.app()),
                            typeList = new PangaeaNoteTypesList(frame)
                                    .with(t -> {
                                        t.onChange(e -> onNoteTypeChange(typeList.getSelectedContentTypeId()));
                                        t.childConstraints().addAll(Fill.BOTH, Grow.BOTH);
                                    })
                    );
                });
        Applications.copyResources(frame, panel);
//        System.out.println(typeList.list().iconConfig().get());
//        onNoteTypeChange(null);
//        onNoteTypeChange(typeList.getSelectedContentTypeId());
    }

    protected PangaeaNote getNote() {
        PangaeaNote n = new PangaeaNote();
        n.setName(nameText.text().get().value());
        String selectedContentTypeId = typeList.getSelectedContentTypeId();
        if (selectedContentTypeId == null) {
            throw new IllegalArgumentException("missing content type");
        }
        if (nameText.text().get().value().trim().length() == 0) {
            String id = typeList.getSelectedContentTypeId();
            String betterName = frame.app().i18n().getString("content-type." + id);
            nameText.text().set(Str.of(betterName));
            n.setName(betterName);
            //throw new IllegalArgumentException("missing note name");
        }
        PangaeaNoteMimeType ct = PangaeaNoteMimeType.of(selectedContentTypeId);
        n.setContentType(ct.toString());
//        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(selectedContentTypeId)) {
//            n.setContent(PangaeaNoteEmbeddedService.of(win.service()).getContentValueAsElement(typeFileValue.getContentString()));
//        } else {
        PangaeaNoteTemplate z = frame.app().getTemplate(ct);
        if (z != null) {
            n.setIcon(z.getIcon());
            z.prepare(n, frame.app());
        } else {
            NElement dv = frame.app().getContentTypeService(ct).createDefaultContent();
            n.setContent(dv);
        }
//        }
        frame.app().addRecentNoteType(selectedContentTypeId);
        return n;
    }

    protected void install() {
//        typeFileValue.install(win.app());
    }

    protected void uninstall() {
//        typeFileValue.uninstall();
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
            new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.addNewNote"));
                        a.headerText().set(Str.i18n("Message.addNewNote"));
                        a.headerIcon().set(Str.of("edit-plus"));
                        a.content().set(panel);
                        a.withOkCancelButtons(
                                (b) -> {
                                    ok();
                                    b.getAlert().closeAlert();
                                },
                                (b) -> {
                                    cancel();
                                    b.getAlert().closeAlert();
                                }
                        );
                    })
                    .showDialog();
            try {
                return get();
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

    public String resolveNoteTypeDescription(String id) {
        if (id == null || id.isEmpty() || id.equals("id")) {
            id = "none";
        }
        if (id.startsWith("recent-")) {
            id = id.substring("recent-".length());
        }
        String s = frame.app().i18n().getString("content-type." + id + ".help");
        if (s.startsWith("resource://")) {
            URL i = getClass().getClassLoader().getResource(s.substring("resource://".length()));
            if (i == null) {
                throw new IllegalArgumentException("not found resource " + s);
            }
            try (InputStream is = i.openStream()) {
                s = new String(Applications.toByteArray(is));
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        String toLowerCase = s.trim().toLowerCase();
        if (!toLowerCase.startsWith("<html>")
                && !toLowerCase.startsWith("<!doctype html>")) {
            s = "<html><body>" + s + "</body></html>";
        }
        return s;
    }

    private void onNoteTypeChange(String id) {
        if (id != null) {
            boolean embeddedDocumentType = id.equals(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString());

//            typeFileValue.setVisible(embeddedDocumentType);
//            typeFileValue.setAcceptAllFileFilterUsed(embeddedDocumentType);
//            typeFileValue.getFileFilters().clear();
//            if (embeddedDocumentType) {
//                typeFileValue.setAcceptAllFileFilterUsed(true);
//                typeFileValue.getFileFilters().add(new ExtensionFileChooserFilter(
//                        PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION,
//                        win.app().i18n().getString("Message.pnoteDocumentFileFilter")
//                ));
//            }
//            valueLabel.setVisible(embeddedDocumentType);
//            valueLabel.setText(embeddedDocumentType ? win.app().i18n().getString("Message.valueForFile") : "");
        } else {
//            valueLabel.setVisible(false);
//            typeFileValue.setVisible(false);
        }
    }

}
