/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.tree.dialogs;

import net.thevpc.common.swing.NamedValue;
import net.thevpc.pnote.gui.util.ComboboxHelper;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.thevpc.common.swing.ColorChooserButton;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.GuiHelper;
import net.thevpc.pnote.gui.util.PangaeaNoteIconsCombobox;
import net.thevpc.pnote.gui.util.PangaeaNoteTypesCombobox;
import net.thevpc.pnote.gui.util.dialog.OkCancelDialog;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class EditNoteDialog extends OkCancelDialog {

    private JTextField nameEditor;
    private JComboBox iconEditor;
    private ColorChooserButton foregroundEditor;
    private ColorChooserButton backgroundEditor;
    private JCheckBox readOnlyEditor;
    private JCheckBox boldEditor;
    private JCheckBox italicEditor;
    private JCheckBox underlinedEditor;
    private JCheckBox strikedEditor;
    private PangaeaNoteTypesCombobox typeEditor;

    private boolean ok = false;
    private PangaeaNote note;
    private PangaeaNoteExt vn;

    public EditNoteDialog(PangaeaNoteGuiApp sapp, PangaeaNoteExt vn) {
        super(sapp, "Message.editNote");
        this.vn = vn;
        this.note = vn.toNote();
        nameEditor = new JTextField("");
        typeEditor = new PangaeaNoteTypesCombobox(sapp);
        typeEditor.setEnabled(false);
        typeEditor.setSelectedContentType(note.getContentType(), note.getEditorType());
        iconEditor = new PangaeaNoteIconsCombobox(sapp);
        foregroundEditor = new ColorChooserButton();
        backgroundEditor = new ColorChooserButton();
        readOnlyEditor = new JCheckBox(sapp.app().i18n().getString("Message.readOnly"));

        boldEditor = new JCheckBox(sapp.app().i18n().getString("Message.titleBold"));
        italicEditor = new JCheckBox(sapp.app().i18n().getString("Message.titleItalic"));
        underlinedEditor = new JCheckBox(sapp.app().i18n().getString("Message.titleUnderlined"));
        strikedEditor = new JCheckBox(sapp.app().i18n().getString("Message.titleStriked"));

        Box modifiersEditor = Box.createHorizontalBox();
        modifiersEditor.add(boldEditor);
        modifiersEditor.add(italicEditor);
        modifiersEditor.add(underlinedEditor);
        modifiersEditor.add(strikedEditor);

        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EditNoteDialog.class.getResource(
                "/net/thevpc/pnote/forms/EditNoteDialog.gbl-form"
        ));
        gbs.bind("nameLabel", new JLabel(sapp.app().i18n().getString("Message.name")));
        gbs.bind("nameEditor", nameEditor);
        gbs.bind("typeLabel", new JLabel(sapp.app().i18n().getString("Message.noteType")));
        gbs.bind("typeEditor", typeEditor);
        gbs.bind("iconLabel", new JLabel(sapp.app().i18n().getString("Message.icon")));
        gbs.bind("iconEditor", iconEditor);
        gbs.bind("forgroundLabel", new JLabel(sapp.app().i18n().getString("Message.titleForegroundColor")));
        gbs.bind("forgroundEditor", foregroundEditor);
        gbs.bind("backgroundLabel", new JLabel(sapp.app().i18n().getString("Message.titleBackgroundColor")));
        gbs.bind("backgroundEditor", backgroundEditor);
        gbs.bind("readOnlyEditor", readOnlyEditor);
        gbs.bind("modifiersEditor", modifiersEditor);

        nameEditor.setText(note.getName());
        for (int i = 0; i < iconEditor.getModel().getSize(); i++) {
            NamedValue v = (NamedValue) iconEditor.getModel().getElementAt(i);
            if (v != null && Objects.equals(OtherUtils.trim(v.getId()), OtherUtils.trim(note.getIcon()))) {
                iconEditor.setSelectedItem(v);
                break;
            }
        }
        iconEditor.setSelectedItem(note.getIcon());
        foregroundEditor.setColorValue(GuiHelper.parseColor(note.getTitleForeground()));
        backgroundEditor.setColorValue(GuiHelper.parseColor(note.getTitleBackground()));
        foregroundEditor.setPreferredSize(new Dimension(20, 20));
        backgroundEditor.setPreferredSize(new Dimension(20, 20));
        boldEditor.setSelected(note.isTitleBold());
        italicEditor.setSelected(note.isTitleItalic());
        underlinedEditor.setSelected(note.isTitleUnderlined());
        strikedEditor.setSelected(note.isTitleStriked());
        readOnlyEditor.setSelected(note.isReadOnly());

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

    protected NamedValue createNoteTypeFamilyNameValue(String id) {
        return new NamedValue(false, id, sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id), null);
    }

    protected NamedValue createIconValue(String id) {
        return new NamedValue(false, id,
                sapp.app().i18n().getString("Icon." + id),
                id);
    }

    protected PangaeaNote getNote() {
        note.setName(nameEditor.getText());
        if (nameEditor.getText() == null || nameEditor.getText().trim().length() == 0) {
            String ct = note.getContentType();
            nameEditor.setText(ct);
            note.setName(sapp.app().i18n().getString("PangaeaNoteTypeFamily." + ct));
            //throw new IllegalArgumentException("missing note name");
        }
        NamedValue selectedIcon = (NamedValue) iconEditor.getSelectedItem();
        note.setIcon(selectedIcon != null ? selectedIcon.getId() : null);
        note.setReadOnly(readOnlyEditor.isSelected());
        note.setTitleBackground(GuiHelper.formatColor(backgroundEditor.getColorValue()));
        note.setTitleForeground(GuiHelper.formatColor(foregroundEditor.getColorValue()));
        note.setTitleBold(boldEditor.isSelected());
        note.setTitleItalic(italicEditor.isSelected());
        note.setTitleUnderlined(underlinedEditor.isSelected());
        note.setTitleStriked(strikedEditor.isSelected());
        return note;
    }

    protected void install() {
    }

    protected void uninstall() {
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

    public PangaeaNote showDialog() {
        Consumer<Exception> exHandler = sapp::showError;
        while (true) {
            install();
            this.ok = false;
            pack();
            setLocationRelativeTo((JFrame) sapp.app().mainWindow().get().component());
            setVisible(true);
            try {
                PangaeaNote n = get();
                if (n != null) {
                    sapp.service().updateNoteProperties(vn, n);
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
