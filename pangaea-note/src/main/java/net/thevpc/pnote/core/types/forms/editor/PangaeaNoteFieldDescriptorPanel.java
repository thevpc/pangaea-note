/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.components.CheckboxesComponent;
import net.thevpc.pnote.gui.components.ComboboxComponent;
import net.thevpc.pnote.gui.components.FormComponent;
import net.thevpc.pnote.gui.components.PasswordComponent;
import net.thevpc.pnote.gui.components.TextAreaComponent;
import net.thevpc.pnote.gui.components.TextFieldComponent;
import net.thevpc.pnote.gui.components.URLComponent;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteField;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObject;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldType;
import net.thevpc.echo.AppDialogResult;

/**
 *
 * @author vpc
 */
class PangaeaNoteFieldDescriptorPanel {

    private PangaeaNoteObjectDocument document;
    private PangaeaNoteObject object;
    private PangaeaNoteField field;

    private PangaeaNoteFieldDescriptor descr;
    private JLabel label;
    private JComponent component;

    private PangaeaNoteObjectTracker objectTracker;
    private PangaeaNoteWindow win;
    private SwingApplicationsUtils.Tracker tracker;
    private List<AbstractButton> buttons = new ArrayList<>();

    private JMenu changeTypeMenu;
    private boolean editable = true;

    //        ButtonGroup bg;
    public PangaeaNoteFieldDescriptorPanel(PangaeaNoteWindow win, PangaeaNoteFieldDescriptor descr, PangaeaNoteObjectTracker objectTracker) {
        this.objectTracker = objectTracker;
        this.descr = descr.copy();
        this.win = win;
        tracker = new SwingApplicationsUtils.Tracker(win.app());
        if (descr.getType() == null) {
            descr.setType(PangaeaNoteFieldType.TEXT);
        }
        label = new JLabel(descr.getName());
        JPopupMenu jPopupMenu = new JPopupMenu();
        label.setComponentPopupMenu(jPopupMenu);
        jPopupMenu.add(createAction("changeFieldName", this::onDescriptorRename));
        changeTypeMenu = new JMenu();
        tracker.registerStandardButton(changeTypeMenu, "Action.changeFieldType");
        jPopupMenu.add(changeTypeMenu);
        changeTypeMenu.add(createAction("changeFieldTypeText", () -> onDescriptorChangeType(PangaeaNoteFieldType.TEXT)));
        changeTypeMenu.add(createAction("changeFieldTypeCombobox", () -> onDescriptorChangeType(PangaeaNoteFieldType.COMBOBOX)));
        changeTypeMenu.add(createAction("changeFieldTypeCheckbox", () -> onDescriptorChangeType(PangaeaNoteFieldType.CHECKBOX)));
        changeTypeMenu.add(createAction("changeFieldTypeLongText", () -> onDescriptorChangeType(PangaeaNoteFieldType.TEXTAREA)));
        changeTypeMenu.add(createAction("changeFieldTypeURL", () -> onDescriptorChangeType(PangaeaNoteFieldType.URL)));
        changeTypeMenu.add(createAction("changeFieldTypePassword", () -> onDescriptorChangeType(PangaeaNoteFieldType.PASSWORD)));
        jPopupMenu.add(createAction("changeFieldValues", () -> onDescriptorEditValues()));

        jPopupMenu.addSeparator();
        jPopupMenu.add(createAction("addField", () -> onAddField()));

        jPopupMenu.addSeparator();
        jPopupMenu.add(createAction("removeField", () -> onRemoveField()));

        jPopupMenu.addSeparator();
        jPopupMenu.add(createAction("moveUpField", () -> onMoveUpField()));
        jPopupMenu.add(createAction("moveDownField", () -> onMoveDownField()));
        jPopupMenu.addSeparator();
        jPopupMenu.add(createAction("hideField", () -> onHideField()));
        jPopupMenu.add(createAction("unhideFields", () -> onUnhideFields()));
        jPopupMenu.addSeparator();
        jPopupMenu.add(createAction("copy", () -> onCopyLabel()));

        FormComponent comp = createFormComponent(descr.getType());
        comp.install(win.app());
        comp.setSelectValues(resolveValues(descr));
        comp.setFormChangeListener(() -> callOnValueChanged());
        component = (JComponent) comp;
    } //=new ButtonGroup()

    private FormComponent createFormComponent(PangaeaNoteFieldType t) {
        switch (t) {
            case TEXT: {
                return new TextFieldComponent(win);
            }
            case PASSWORD: {
                return new PasswordComponent(win);
            }
            case URL: {
                return new URLComponent();
            }

            case COMBOBOX: {
                return new ComboboxComponent();
            }
            case CHECKBOX: {
                return new CheckboxesComponent(win);
            }
            case TEXTAREA: {
                return new TextAreaComponent(win);
            }
        }
        return new TextFieldComponent(win);
    }

    private void callOnStructureChanged() {
        if (objectTracker != null) {
            objectTracker.onStructureChanged();
        }
    }

    private void callOnValueChanged() {
        if (this.field != null) {
            this.field.setValue(getStringValue());
        }
        if (objectTracker != null) {
            objectTracker.onFieldValueChanged();
        }
    }

    public String getStringValue() {
        return formComponent().getContentString();
    }

    public void setStringValue(String s) {
        formComponent().setContentString(s);
    }

    public void setValue(PangaeaNoteField field, PangaeaNoteObject object, PangaeaNoteObjectDocument document) {
        this.field = field;
        this.object = object;
        this.document = document;
        String s = field.getValue();
        if (s == null) {
            s = "";
        }
        setStringValue(s);
        formComponent().setEditable(isEditable());
    }

    public PangaeaNoteField getValue() {
        PangaeaNoteField df = new PangaeaNoteField();
        df.setName(this.descr.getName());
        df.setValue(getStringValue());
        return df;
    }

    private static List<String> resolveValues(PangaeaNoteFieldDescriptor descr) {
        Set<String> a = new LinkedHashSet<>();
        if (descr.getValues() != null) {
            for (String value : descr.getValues()) {
                if (value == null) {
                    value = "";
                }
                a.add(value);
            }
        }
        String dv = descr.getDefaultValue();
        if (dv == null) {
            dv = "";
        }
        if (!a.contains(dv)) {
            a.add(dv);
        }
        if (a.isEmpty()) {
            a.add("");
        }
        return new ArrayList<>(a);
    }

    public void uninstall() {
        this.win = null;
        this.objectTracker = null;
        formComponent().uninstall();
        tracker.unregisterAll();
    }

    public PangaeaNoteFieldDescriptor getDescr() {
        return descr;
    }

    public JLabel getLabel() {
        return label;
    }

    public JComponent getComponent() {
        return component;
    }

    public boolean supportsUpdateDescriptor(PangaeaNoteFieldDescriptor field) {
        if (field.getType() != descr.getType()) {
            return false;
        }
        return true;
    }

    public void updateDescriptor(PangaeaNoteFieldDescriptor field) {
        if (!supportsUpdateDescriptor(field)) {
            throw new IllegalArgumentException("Cannot morph this field panel");
        }
        this.descr = field;
        label.setText(this.descr.getName());
        formComponent().setSelectValues(resolveValues(descr));
    }

    public void onDescriptorMoveDownField() {
        if (document != null) {
            document.moveFieldDown(descr.getName());
            callOnStructureChanged();
        }
    }

    public void onDescriptorMoveUpField() {
        if (document != null) {
            document.moveFieldUp(descr.getName());
            callOnStructureChanged();
        }
    }

    public void onHideField() {
        if (document != null) {
            if (field != null) {
                field.setHidden(true);
            }
            callOnStructureChanged();
        }
    }

    public void onCopyLabel() {
        if (document != null) {
            if (object != null) {
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    clip.setContents(new StringSelection(field.getName()), null);
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }
        }
    }

    public void onUnhideFields() {
        if (document != null) {
            if (object != null) {
                for (PangaeaNoteField f : object.getFields()) {
                    f.setHidden(false);
                }
            }
            callOnStructureChanged();
        }
    }

    public void onDescriptorEditValues() {
        if (document != null) {
            String oldValue = "";
            if (descr.getValues() != null) {
                TreeSet<String> all = new TreeSet<>();
                for (String s : descr.getValues()) {
                    if (s == null) {
                        s = "";
                    }
                    s = s.trim();
                    all.add(s);
                }
                oldValue = (String.join("\n", all));
            }
            AppDialogResult s = win.newDialog()
                    .setTitleId("Message.changeFieldValues")
                    .setInputTextAreadContent("Message.changeFieldValues.label", oldValue)
                    .withOkCancelButtons()
                    .setPreferredSize(400, 300)
                    .showInputDialog();
            if (s.isButton("ok") && !s.isBlankValue()) {
                document.updateFieldValues(descr.getName(), s.<String>getValue().split("\n"));
                callOnStructureChanged();
            }
        }
    }

    public Container resolveAncestor() {
        return SwingUtilities3.getAncestorOfClass(new Class[]{Window.class}, component);
    }

    public void onDescriptorRename() {
        if (document != null) {
            AppDialogResult r = win.newDialog()
                    .withOkCancelButtons()
                    .setTitleId("Message.renameField")
                    .setInputTextFieldContent(
                            "Message.renameField.label", descr.getName()
                    )
                    //                    .setPreferredSize(400, 200)
                    .showInputDialog();
            if (r.isButton("ok") && !r.isBlankValue()) {
                String n = r.<String>getValue().trim();
                if (document != null) {
                    document.renameField(descr.getName(), n);
                }
                callOnStructureChanged();
            }
        }
    }

    public void onDescriptorChangeType(PangaeaNoteFieldType type) {
        if (document != null) {
            document.changeType(descr.getName(), type);
            callOnStructureChanged();
        }
    }

    public void onRemoveField() {
        if (document != null) {
            String s = win.newDialog()
                    .setTitleId("Message.warning")
                    .setContentTextId("Message.askDeleteField")
                    .withYesNoButtons()
                    .build().showDialog();

            if ("yes".equals(s)) {
                document.removeField(descr.getName());
                callOnStructureChanged();
            }
        }
    }

    public void onAddField() {
        if (document != null) {
            AppDialogResult r = win.newDialog()
                    .withOkCancelButtons()
                    .setTitleId("Message.addField")
                    .setInputTextFieldContent(
                            "Message.addField.label", ""
                    )
                    //                    .setPreferredSize(400, 200)
                    .showInputDialog();
            if ("ok".equals(r.getButtonId())) {
                String n = r.getValue();
                if (n != null) {
                    n = n.trim();
                    if (n.length() > 0) {
                        document.addField(new PangaeaNoteFieldDescriptor().setName(n).setType(PangaeaNoteFieldType.TEXT));
                        callOnStructureChanged();
                    }
                }
            }
        }
    }

    public void onMoveDownField() {
        if (document != null) {
            document.moveFieldDown(descr.getName());
            callOnStructureChanged();
        }
    }

    public void onMoveUpField() {
        if (document != null) {
            document.moveFieldUp(descr.getName());
            callOnStructureChanged();
        }
    }

    public void onDuplicateField() {
        if (document != null) {
            String n = JOptionPane.showInputDialog(null, descr.getName());
            if (n != null) {
                document.addField(new PangaeaNoteFieldDescriptor().setName(n).setType(PangaeaNoteFieldType.TEXT));
                callOnStructureChanged();
            }
        }
    }

    private Action createAction(String id, Runnable r) {
        AbstractAction a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                r.run();
            }
        };
        tracker.registerStandardAction(a, id);
        return a;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        formComponent().setEditable(editable);
        for (AbstractButton button : buttons) {
            button.setEnabled(editable);
        }
        for (Action action : tracker.getActions()) {
            action.setEnabled(editable);
        }
    }

    private FormComponent formComponent() {
        return (FormComponent) component;
    }

}
