/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.tree.dialogs;

import net.thevpc.common.swing.NamedValue;
import net.thevpc.pnote.gui.util.ComboboxHelper;
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
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.dialog.OkCancelDialog;
import net.thevpc.pnote.util.OtherUtils;
import net.thevpc.pnote.gui.util.FileComponent;
import net.thevpc.pnote.gui.util.URLComponent;
import net.thevpc.pnote.model.PangageaNoteObjectDocument;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.service.PangaeaNoteTemplate;

/**
 *
 * @author vpc
 */
public class NewNoteDialog extends OkCancelDialog {

    private JTextField nameText;
    private JComboBox iconList;
    private JComboBox typeList;
    private JLabel valueLabel;
//    private JTextField typeSourceValue;

    private FileComponent typeFileValue;
    private JScrollPane typeDescriptionContent;
    private JEditorPane typeDescription;

    private boolean ok = false;

    public NewNoteDialog(PangaeaNoteGuiApp sapp) {
        super(sapp, "Message.addNewNote");

        this.valueLabel = new JLabel(sapp.app().i18n().getString("Message.valueLabel"));
        nameText = new JTextField("");
        iconList = createIconListComponent(sapp);
        typeDescriptionContent = new JScrollPane(typeDescription = new JEditorPane("text/html", ""));
        typeDescriptionContent.setPreferredSize(new Dimension(400, 100));
        typeDescription.setEditable(false);
        typeFileValue = new FileComponent();

        typeList = createTypeListComponent(sapp);

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

    private JComboBox createIconListComponent(PangaeaNoteGuiApp sapp1) {
        List<NamedValue> list = new ArrayList<>();
        list.add(new NamedValue(false, "", sapp1.app().i18n().getString("Icon.none"), null));
        for (String icon : PangaeaNoteTypes.ALL_USER_ICONS) {
            list.add(createIconValue(icon));
        }
        return ComboboxHelper.createCombobox(sapp1.app(), list.toArray(new NamedValue[0]));
    }

    private List<NamedValue> createTypeListNamedValue() {
        List<NamedValue> availableTypes = new ArrayList<>();
//        availableTypes.add(createNoteTypeFamilyNameGroup("quick-strings"));
//        for (String s : new String[]{PangaeaNoteTypes.STRING, PangaeaNoteTypes.PASSWORD}) {
//            availableTypes.add(createNoteTypeFamilyNameValue(s));
//        }
        availableTypes.add(createNoteTypeFamilyNameGroup("simple-documents"));
        for (String s : new String[]{PangaeaNoteTypes.PLAIN, PangaeaNoteTypes.RICH_HTML}) {
            availableTypes.add(createNoteTypeFamilyNameValue(s));
        }
//        availableTypes.add(createNoteTypeFamilyNameGroup("lists"));
        availableTypes.add(createNoteTypeFamilyNameValue(PangaeaNoteTypes.NOTE_LIST));
        availableTypes.add(createNoteTypeFamilyNameValue(PangaeaNoteTypes.OBJECT_LIST));
        availableTypes.add(createNoteTypeFamilyNameValue(PangaeaNoteTypes.FILE));

        for (PangaeaNoteTemplate value : sapp.service().getTemplates()) {
            String s = value.getLabel(sapp.service());
            if (s == null) {
                s = sapp.app().i18n().getString("PangaeaNoteTypeFamily." + value.getId());
            }
            NamedValue n = new NamedValue(false, value.getId(), s, sapp.service().getContentTypeIcon(value.getId()));
            availableTypes.add(n);
        }

//        if (extra.size() > 0) {
//            availableTypes.add(createNoteTypeFamilyNameGroup("custom"));
//            for (PangaeaNoteTemplate value : extra.values()) {
//                String s = value.getLabel(sapp);
//                if (s == null) {
//                    s = sapp.app().i18n().getString("PangaeaNoteTypeFamily." + value.getId());
//                }
//                NamedValue n = new NamedValue(false, value.getId(), s, null);
//                availableTypes.add(n);
//            }
//        }
        availableTypes.add(createNoteTypeFamilyNameGroup("sources"));
        for (String s : new String[]{
            PangaeaNoteTypes.SOURCE_HTML,
            PangaeaNoteTypes.SOURCE_MARKDOWN,
            PangaeaNoteTypes.SOURCE_NUTS_TEXT_FORMAT,
            PangaeaNoteTypes.JAVA,
            PangaeaNoteTypes.JAVASCRIPT,
            PangaeaNoteTypes.C,
            PangaeaNoteTypes.CPP,}) {
            availableTypes.add(createNoteTypeFamilyNameValue(s));
        }
        return availableTypes;
    }

    private JComboBox createTypeListComponent(PangaeaNoteGuiApp sapp1) {
        List<NamedValue> availableTypes = new ArrayList<>();
        List<String> rct = sapp.config().getRecentContentTypes();
        if (rct != null) {
            List<NamedValue> recent = new ArrayList<>();
            for (String id : rct) {
                recent.add(
                        new NamedValue(false, "recent:" + id, sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id),
                                sapp.service().getContentTypeIcon(id)
                        )
                );
            }
            if (recent.size() > 0) {
                availableTypes.add(createNoteTypeFamilyNameGroup("recent-documents"));
                availableTypes.addAll(recent);
            }
        }
        availableTypes.addAll(createTypeListNamedValue());
        return ComboboxHelper.createCombobox(sapp1.app(), availableTypes.toArray(new NamedValue[0]));
    }

    protected NamedValue createNoteTypeFamilyNameValue(String id) {
        return new NamedValue(false, id,
                sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id),
                sapp.service().getContentTypeIcon(id)
        );
    }

    protected NamedValue createNoteTypeFamilyNameGroup(String id) {
        return new NamedValue(true, id, sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id), null);
    }

    protected NamedValue createIconValue(String id) {
        return new NamedValue(false, id,
                sapp.app().i18n().getString("Icon." + id),
                id);
    }

    protected PangaeaNote getNote() {
        PangaeaNote n = new PangaeaNote();
        n.setName(nameText.getText());
        if (nameText.getText() == null || nameText.getText().trim().length() == 0) {
            String betterName = typeList.getSelectedItem().toString();
            nameText.setText(betterName);
            n.setName(betterName);
            //throw new IllegalArgumentException("missing note name");
        }
        NamedValue selectedIcon = (NamedValue) iconList.getSelectedItem();
        n.setIcon(selectedIcon != null ? selectedIcon.getId() : null);
        NamedValue selectedContentType = (NamedValue) typeList.getSelectedItem();
        if (selectedContentType == null) {
            throw new IllegalArgumentException("missing content type");
        }
        String selectedContentTypeId = selectedContentType.getId();
        if (selectedContentTypeId.startsWith("recent:")) {
            selectedContentTypeId = selectedContentTypeId.substring("recent:".length());
        }

        String[] contentTypeId = selectedContentTypeId.split(":");
        n.setContentType(contentTypeId[0]);
        if (contentTypeId.length > 1) {
            n.setEditorType(contentTypeId[1]);
        }
        switch (selectedContentTypeId) {
            case PangaeaNoteTypes.FILE:
            case PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT: {
                n.setContent(typeFileValue.getContentString());
                break;
            }
            case PangaeaNoteTypes.NOTE_LIST: {
                n.setContent("");
                break;
            }
            case PangaeaNoteTypes.OBJECT_LIST: {
                n.setContent(sapp.service().stringifyDescriptor(new PangageaNoteObjectDocument()
                        .setDescriptor(new PangaeaNoteObjectDescriptor())
                        .setValues(new ArrayList<>())
                )
                );
                break;
            }
            default: {
                PangaeaNoteTemplate z = sapp.service().getTemplate(selectedContentTypeId);
                if (z != null) {
                    z.prepare(n, sapp.service());
                } else {
//                    n.setContentType(selectedContentTypeId = PangaeaNoteTypes.PLAIN);
                }
            }

        }
        List<String> recentContentTypes = sapp.config().getRecentContentTypes();
        if (recentContentTypes == null) {
            recentContentTypes = new ArrayList<>();
        }
        recentContentTypes.add(0, selectedContentTypeId);
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
        if (id.startsWith("recent:")) {
            id = id.substring("recent:".length());
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
            boolean objList = id.equals(PangaeaNoteTypes.OBJECT_LIST);
            boolean fileType = id.equals(PangaeaNoteTypes.FILE) || id.equals(PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT);

            typeFileValue.setVisible(fileType);

            typeFileValue.setAcceptAllFileFilterUsed(id.equals(PangaeaNoteTypes.FILE));
            typeFileValue.getFileFilters().clear();
            if (id.equals(PangaeaNoteTypes.FILE)) {
                typeFileValue.setAcceptAllFileFilterUsed(true);
            } else if (id.equals(PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT)) {
                typeFileValue.setAcceptAllFileFilterUsed(true);
                typeFileValue.getFileFilters().add(new ExtensionFileChooserFilter(
                        PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION,
                        sapp.app().i18n().getString("Message.pnoteDocumentFileFilter")
                ));
            }
            valueLabel.setVisible(objList || fileType);
            typeDescription.setText(resolveNoteTypeDescription(id));
            valueLabel.setText(
                    objList ? sapp.app().i18n().getString("Message.valueForObjList")
                                    : fileType ? sapp.app().i18n().getString("Message.valueForFile")
                                                            : ""
            );

        } else {
            valueLabel.setVisible(false);
            typeDescription.setText(resolveNoteTypeDescription(null));
            typeFileValue.setVisible(false);
        }
        pack();
    }

}
