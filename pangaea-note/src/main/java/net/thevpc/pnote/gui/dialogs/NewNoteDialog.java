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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.thevpc.common.swing.ExtensionFileChooserFilter;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.dialog.OkCancelDialog;
import net.thevpc.pnote.util.OtherUtils;
import net.thevpc.pnote.gui.components.FileComponent;
import net.thevpc.pnote.gui.util.PangaeaNoteIconsCombobox;
import net.thevpc.pnote.gui.util.PangaeaNoteTypesCombobox;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.service.PangaeaNoteTemplate;
import net.thevpc.pnote.types.pnodetembedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class NewNoteDialog extends OkCancelDialog {

    private JTextField nameText;
    private JComboBox iconList;
    private PangaeaNoteTypesCombobox typeList;
    private JLabel valueLabel;
    private FileComponent typeFileValue;
    private JScrollPane typeDescriptionContent;
    private JEditorPane typeDescription;

    private boolean ok = false;

    public NewNoteDialog(PangaeaNoteGuiApp sapp) {
        super(sapp, "Message.addNewNote");

        this.valueLabel = new JLabel(sapp.app().i18n().getString("Message.valueLabel"));
        nameText = new JTextField("");
        typeList = new PangaeaNoteTypesCombobox(sapp);
        iconList = new PangaeaNoteIconsCombobox(sapp);
        typeDescriptionContent = new JScrollPane(typeDescription = new JEditorPane("text/html", ""));
        typeDescriptionContent.setPreferredSize(new Dimension(400, 100));
        typeDescription.setEditable(false);
        typeFileValue = new FileComponent(sapp).setReloadButtonVisible(false);

        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(NewNoteDialog.class.getResource(
                "/net/thevpc/pnote/forms/NewNoteDialog.gbl-form"
        ));
        gbs.bind("valueLabel", new JLabel("valueLabel"));
        gbs.bind("nameLabel", new JLabel(sapp.app().i18n().getString("Message.name")));
        gbs.bind("nameText", nameText);
        gbs.bind("iconLabel", new JLabel(sapp.app().i18n().getString("Message.icon")));
        gbs.bind("iconList", iconList);
        gbs.bind("typeLabel", new JLabel(sapp.app().i18n().getString("Message.noteType")));
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

        build(gbs.apply(new JPanel()), this::ok, this::cancel);
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
        PangaeaNoteContentType ct = PangaeaNoteContentType.of(selectedContentTypeId);
        n.setContentType(ct.toString());
        NamedValue selectedIcon = (NamedValue) iconList.getSelectedItem();
        n.setIcon(selectedIcon != null ? selectedIcon.getId() : null);

        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(selectedContentTypeId)) {
            n.setContent(PangaeaNoteEmbeddedService.of(sapp.service()).getContentValueAsElement(typeFileValue.getContentString()));
        } else {
            PangaeaNoteTemplate z = sapp.service().getTemplate(ct);
            if (z != null) {
                z.prepare(n, sapp.service());
            } else {
                NutsElement dv = sapp.service().getContentTypeService(ct).createDefaultContent();
                n.setContent(dv);
            }
        }
        List<String> recentContentTypes = new ArrayList<>();
        recentContentTypes.add(0, selectedContentTypeId);
        List<String> recentContentTypes1 = sapp.config().getRecentContentTypes();
        if (recentContentTypes1 != null) {
            for (String r : recentContentTypes1) {
                if (sapp.service().isValidContentTypeExt(r)) {
                    recentContentTypes.add(r);
                }
            }
        }
        recentContentTypes = new ArrayList<>(new LinkedHashSet<String>(recentContentTypes));
        int maxRecentContentTypes = 12;
        while (recentContentTypes.size() > maxRecentContentTypes) {
            recentContentTypes.remove(recentContentTypes.size() - 1);
        }
        sapp.config().setRecentContentTypes(recentContentTypes);
        sapp.saveConfig();
        return n;
    }

    protected void install() {
        typeFileValue.install(sapp.app());
    }

    protected void uninstall() {
        typeFileValue.uninstall();
    }

    protected void ok() {
        uninstall();
        this.ok = true;
        setVisible(false);
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
        setVisible(false);
    }

    public PangaeaNote showDialog(Consumer<Exception> exHandler) {
        while (true) {
            install();
            this.ok = false;
            pack();
            setLocationRelativeTo((JFrame) sapp.app().mainWindow().get().component());
            setVisible(true);
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
        String s = sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id + ".help");
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
                        sapp.app().i18n().getString("Message.pnoteDocumentFileFilter")
                ));
            }
            valueLabel.setVisible(embeddedDocumentType);
            typeDescription.setText(resolveNoteTypeDescription(id));
            valueLabel.setText(embeddedDocumentType ? sapp.app().i18n().getString("Message.valueForFile") : "");

        } else {
            valueLabel.setVisible(false);
            typeDescription.setText(resolveNoteTypeDescription(null));
            typeFileValue.setVisible(false);
        }
        pack();
    }

}
