/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppDialogResult;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.core.types.forms.model.*;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.components.*;

import java.util.*;

/**
 * @author vpc
 */
class PangaeaNoteFieldDescriptorPanel {

    private PangaeaNoteObjectDocument document;
    private PangaeaNoteObject object;
    private PangaeaNoteField field;

    private PangaeaNoteFieldDescriptor descr;
    private Label label;
    private AppComponent component;

    private PangaeaNoteObjectTracker objectTracker;
    private PangaeaNoteFrame frame;

    private Menu changeTypeMenu;
    private boolean editable = true;

    //        ButtonGroup bg;
    public PangaeaNoteFieldDescriptorPanel(PangaeaNoteFrame frame, PangaeaNoteFieldDescriptor descr, PangaeaNoteObjectTracker objectTracker) {
        this.objectTracker = objectTracker;
        this.descr = descr.copy();
        this.frame = frame;
        Application app = frame.app();
        if (descr.getType() == null) {
            descr.setType(PangaeaNoteFieldType.TEXT);
        }
        label = new Label(Str.of(descr.getName()), app);
        ContextMenu jPopupMenu = new ContextMenu(app);
        label.contextMenu().set(jPopupMenu);
        jPopupMenu.children().add(new Button("changeFieldName", this::onDescriptorRename, app));

        changeTypeMenu = new Menu(Str.i18n("Action.changeFieldType"), app);
        jPopupMenu.children().add(changeTypeMenu);
        changeTypeMenu.children().add(new Button("changeFieldTypeText", () -> onDescriptorChangeType(PangaeaNoteFieldType.TEXT), app));
        changeTypeMenu.children().add(new Button("changeFieldTypeCombobox", () -> onDescriptorChangeType(PangaeaNoteFieldType.COMBOBOX), app));
        changeTypeMenu.children().add(new Button("changeFieldTypeCheckbox", () -> onDescriptorChangeType(PangaeaNoteFieldType.CHECKBOX), app));
        changeTypeMenu.children().add(new Button("changeFieldTypeLongText", () -> onDescriptorChangeType(PangaeaNoteFieldType.TEXTAREA), app));
        changeTypeMenu.children().add(new Button("changeFieldTypeURL", () -> onDescriptorChangeType(PangaeaNoteFieldType.URL), app));
        changeTypeMenu.children().add(new Button("changeFieldTypePassword", () -> onDescriptorChangeType(PangaeaNoteFieldType.PASSWORD), app));
        jPopupMenu.children().add(new Button("changeFieldValues", () -> onDescriptorEditValues(), app));

        jPopupMenu.children().addSeparator();
        jPopupMenu.children().add(new Button("addField", () -> onAddField(), app));

        jPopupMenu.children().addSeparator();
        jPopupMenu.children().add(new Button("removeField", () -> onRemoveField(), app));

        jPopupMenu.children().addSeparator();
        jPopupMenu.children().add(new Button("moveUpField", () -> onMoveUpField(), app));
        jPopupMenu.children().add(new Button("moveDownField", () -> onMoveDownField(), app));
        jPopupMenu.children().addSeparator();
        jPopupMenu.children().add(new Button("hideField", () -> onHideField(), app));
        jPopupMenu.children().add(new Button("unhideFields", () -> onUnhideFields(), app));
        jPopupMenu.children().addSeparator();
        jPopupMenu.children().add(new Button("copy", () -> onCopyLabel(), app));

        FormComponent comp = createFormComponent(descr.getType());
        comp.install(app);
        comp.setSelectValues(resolveValues(descr));
        comp.setFormChangeListener(() -> callOnValueChanged());
        component = (AppComponent) comp;
    } //=new ButtonGroup()

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
        a.add(dv);
        if (a.isEmpty()) {
            a.add("");
        }
        return new ArrayList<>(a);
    }

    private FormComponent createFormComponent(PangaeaNoteFieldType t) {
        switch (t) {
            case TEXT: {
                return new TextFieldComponent(frame);
            }
            case PASSWORD: {
                return new PasswordComponent(frame);
            }
            case URL: {
                return new URLComponent(frame);
            }

            case COMBOBOX: {
                return new ComboboxComponent(frame);
            }
            case CHECKBOX: {
                return new CheckboxesComponent(frame);
            }
            case TEXTAREA: {
                return new TextAreaComponent(frame);
            }
        }
        return new TextFieldComponent(frame);
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

    public void uninstall() {
        this.frame = null;
        this.objectTracker = null;
        formComponent().uninstall();
    }

    public PangaeaNoteFieldDescriptor getDescr() {
        return descr;
    }

    public Label getLabel() {
        return label;
    }

    public AppComponent getComponent() {
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
        label.text().set(Str.i18n(this.descr.getName()));
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
                frame.app().clipboard().putString(field.getName());
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
            AppDialogResult s = new Alert(frame.app())
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.changeFieldValues"));
                        a.headerText().set(Str.i18n("Message.changeFieldValues"));
                        a.headerIcon().set(Str.of("edit"));
                        a.prefSize().set(new Dimension(400, 300));
                    })
                    .setInputTextAreaContent(Str.i18n("Message.changeFieldValues.label"), Str.of(oldValue))
                    .withOkCancelButtons()
                    .showInputDialog(null);
            if (s.isButton("ok") && !s.isBlankValue()) {
                document.updateFieldValues(descr.getName(), s.<String>value().split("\n"));
                callOnStructureChanged();
            }
        }
    }

    public void onDescriptorRename() {
        if (document != null) {
            AppDialogResult r = new Alert(frame.app())
                    .withOkCancelButtons()
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.renameField"));
                        a.headerText().set(Str.i18n("Message.renameField"));
                        a.headerIcon().set(Str.of("edit"));
                    })
                    .setInputTextFieldContent(
                            Str.i18n("Message.renameField.label"), Str.of(descr.getName())
                    )
                    //                    .setPreferredSize(400, 200)
                    .showInputDialog(null);
            if (r.isButton("ok") && !r.isBlankValue()) {
                String n = r.<String>value().trim();
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
            String s = new Alert(frame.app())
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.warning"));
                        a.headerText().set(Str.i18n("Message.warning"));
                        a.headerIcon().set(Str.of("delete"));
                    })
                    .setContentText(Str.i18n("Message.askDeleteField"))
                    .withYesNoButtons()
                    .showDialog(null);

            if ("yes".equals(s)) {
                document.removeField(descr.getName());
                callOnStructureChanged();
            }
        }
    }

    public void onAddField() {
        if (document != null) {
            AppDialogResult r = new Alert(frame.app())
                    .withOkCancelButtons()
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.addField"));
                        a.headerText().set(Str.i18n("Message.addField"));
                        a.headerIcon().set(Str.of("add"));
                    })
                    .setInputTextFieldContent(
                            Str.i18n("Message.addField.label"), Str.of("")
                    )
                    //                    .setPreferredSize(400, 200)
                    .showInputDialog(null);
            if ("ok".equals(r.buttonId())) {
                String n = r.value();
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

    //    public void onDuplicateField() {
//        if (document != null) {
//            String n = JOptionPane.showInputDialog(null, descr.getName());
//            if (n != null) {
//                document.addField(new PangaeaNoteFieldDescriptor().setName(n).setType(PangaeaNoteFieldType.TEXT));
//                callOnStructureChanged();
//            }
//        }
//    }
//    private Action createAction(String id, Runnable r) {
//        AbstractAction a = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                r.run();
//            }
//        };
//        tracker.registerStandardAction(a, id);
//        return a;
//    }
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        formComponent().setEditable(editable);
//        for (AbstractButton button : buttons) {
//            button.setEnabled(editable);
//        }
//        for (Action action : tracker.getActions()) {
//            action.setEnabled(editable);
//        }
    }

    private FormComponent formComponent() {
        return (FormComponent) component;
    }

}
