/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.swing.NamedValue;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.function.Consumer;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.thevpc.common.swing.file.ExtensionFileChooserFilter;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.util.OtherUtils;
import net.thevpc.pnote.gui.components.FileComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.PangaeaNoteTemplate;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class NewNoteDialogOld {

    private JPanel panel;
    private JTextField nameText;
    private JComboBox iconList;
    private PangaeaNoteTypesCombobox typeList;
    private JLabel valueLabel;
    private FileComponent typeFileValue;
    private JScrollPane typeDescriptionContent;
    private JEditorPane typeDescription;

    private boolean ok = false;
    private PangaeaNoteWindow win;

    public NewNoteDialogOld(PangaeaNoteWindow win) {
        this.win=win;
        this.valueLabel = new JLabel(win.app().i18n().getString("Message.valueLabel"));
        nameText = new JTextField("");
        typeList = new PangaeaNoteTypesCombobox(win);
        iconList = new PangaeaNoteIconsCombobox(win);
        typeDescriptionContent = new JScrollPane(typeDescription = new JEditorPane("text/html", ""));
        typeDescriptionContent.setPreferredSize(new Dimension(400, 100));
        typeDescription.setEditable(false);
        typeFileValue = new FileComponent(win).setReloadButtonVisible(false);

        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(NewNoteDialogOld.class.getResource(
                "/net/thevpc/pnote/forms/NewNoteDialog.gbl-form"
        ));
        gbs.bind("valueLabel", new JLabel("valueLabel"));
        gbs.bind("nameLabel", new JLabel(win.app().i18n().getString("Message.name")));
        gbs.bind("nameText", nameText);
        gbs.bind("iconLabel", new JLabel(win.app().i18n().getString("Message.icon")));
        gbs.bind("iconList", iconList);
        gbs.bind("typeLabel", new JLabel(win.app().i18n().getString("Message.noteType")));
        gbs.bind("typeList", typeList);
//        gbs.bind("typeSourceValue", typeSourceValue);
        gbs.bind("valueLabel", valueLabel);
        gbs.bind("typeFileValue", typeFileValue);
        gbs.bind("description", typeDescriptionContent);

        onNoteTypeChange(null);
        typeList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                NamedValue v = (NamedValue) e.getItem();
                onNoteTypeChange(v.isGroup() ? null : v.getId());
            }

        });
        typeList.setSelectedIndex(1); // 0 is a group!
        onNoteTypeChange(((NamedValue) typeList.getSelectedItem()).getId());

        panel=gbs.apply(new JPanel());
    }

    protected PangaeaNote getNote() {
        PangaeaNote n = new PangaeaNote();
        n.setName(nameText.getText());
        String selectedContentTypeId = typeList.getSelectedContentTypeId();
        if (selectedContentTypeId == null) {
            throw new IllegalArgumentException("missing content type");
        }
        if (nameText.getText() == null || nameText.getText().trim().length() == 0) {
            String betterName = typeList.getSelectedItem().toString();
            nameText.setText(betterName);
            n.setName(betterName);
            //throw new IllegalArgumentException("missing note name");
        }
        PangaeaNoteMimeType ct = PangaeaNoteMimeType.of(selectedContentTypeId);
        n.setContentType(ct.toString());
        NamedValue selectedIcon = (NamedValue) iconList.getSelectedItem();
        n.setIcon(selectedIcon != null ? selectedIcon.getId() : null);

        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(selectedContentTypeId)) {
            n.setContent(PangaeaNoteEmbeddedService.of(win.service()).getContentValueAsElement(typeFileValue.getContentString()));
        } else {
            PangaeaNoteTemplate z = win.service().getTemplate(ct);
            if (z != null) {
                n.setIcon(z.getIcon());
                z.prepare(n, win.service());
            } else {
                NutsElement dv = win.service().getContentTypeService(ct).createDefaultContent();
                n.setContent(dv);
            }
        }
        win.service().addRecentNoteType(selectedContentTypeId);
        return n;
    }

    protected void install() {
        typeFileValue.install(win.app());
    }

    protected void uninstall() {
        typeFileValue.uninstall();
    }

    protected void ok() {
        uninstall();
        this.ok = true;
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
    }

    public PangaeaNote showDialog(Consumer<Exception> exHandler) {
        while (true) {
            install();
            this.ok = false;
            win.app().newDialog()
                    .setTitleId("Message.addNewNote")
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
                return get();
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

    public String resolveNoteTypeDescription(String id) {
        if (id == null || id.isEmpty() || id.equals("id")) {
            id = "none";
        }
        if (id.startsWith("recent-")) {
            id = id.substring("recent-".length());
        }
        String s = win.app().i18n().getString("content-type." + id + ".help");
        if (s.startsWith("resource://")) {
            URL i = getClass().getClassLoader().getResource(s.substring("resource://".length()));
            if (i == null) {
                throw new IllegalArgumentException("not found resource " + s);
            }
            try (InputStream is = i.openStream()) {
                s = new String(OtherUtils.toByteArray(is));
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

            typeFileValue.setVisible(embeddedDocumentType);

            typeFileValue.setAcceptAllFileFilterUsed(embeddedDocumentType);
            typeFileValue.getFileFilters().clear();
            if (embeddedDocumentType) {
                typeFileValue.setAcceptAllFileFilterUsed(true);
                typeFileValue.getFileFilters().add(new ExtensionFileChooserFilter(
                        PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION,
                        win.app().i18n().getString("Message.pnoteDocumentFileFilter")
                ));
            }
            valueLabel.setVisible(embeddedDocumentType);
            typeDescription.setText(resolveNoteTypeDescription(id));
            valueLabel.setText(embeddedDocumentType ? win.app().i18n().getString("Message.valueForFile") : "");

        } else {
            valueLabel.setVisible(false);
            typeDescription.setText(resolveNoteTypeDescription(null));
            typeFileValue.setVisible(false);
        }
    }

}
