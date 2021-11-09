/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppAlertResult;
import net.thevpc.echo.api.AppColor;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.frame.util.PangaeaNoteLabelHelper;
import net.thevpc.pnote.core.types.forms.components.*;
import net.thevpc.pnote.core.types.forms.model.*;
import net.thevpc.pnote.core.types.forms.util.PangaeaNoteFormUtils;
import net.thevpc.pnote.util.PNoteUtils;

import java.util.*;

/**
 * @author thevpc
 */
class PangaeaNoteFieldDescriptorPanel {

    private PangaeaNoteObjectComponent parentObject;
    private PangaeaNoteObjectDocument document;
    private PangaeaNoteObject object;
    private PangaeaNoteField field;

    private PangaeaNoteFieldDescriptor descr;
    private Label label;
    private AppComponent component;
    private FormComponent formComponent;

    private PangaeaNoteObjectTracker objectTracker;
    private PangaeaNoteFrame frame;

//    private Menu changeFieldTypeMenu;
//    private Menu changeContentTypeMenu;
    private boolean editable = true;

    //        ButtonGroup bg;
    public PangaeaNoteFieldDescriptorPanel(PangaeaNoteFrame frame, PangaeaNoteObjectComponent objectComponent, PangaeaNoteFieldDescriptor descr, PangaeaNoteObjectTracker objectTracker) {
        this.parentObject = objectComponent;
        this.objectTracker = objectTracker;
        this.descr = descr.copy();
        this.frame = frame;
        Application app = frame.app();
        if (descr.getType() == null) {
            descr.setType(PangaeaNoteFieldType.TEXT);
        }
        label = new Label(Str.of(descr.getName()), app);
//        label.peer();
        ContextMenu jPopupMenu = new ContextMenu(app);
        label.contextMenu().set(jPopupMenu);

//        jPopupMenu.children().add(new Button("changeFieldName", this::onDescriptorRename, app));
        jPopupMenu.children().add(new Button("copyFieldName", () -> onCopyLabel(), app));
        jPopupMenu.children().addSeparator();
//        Menu fieldMenu = new Menu("forms.field", Str.i18n("forms.field"), app);
//        jPopupMenu.children().add(fieldMenu);
//
//        fieldMenu.children().add(new Button("addField", () -> onAddField(), app));
//        fieldMenu.children().addSeparator();
//
//        fieldMenu.children().add(new Button("changeFieldName", this::onDescriptorRename, app));
//        fieldMenu.children().add(new Button("editFormStructure", this::onEditFormFieldDefinition, app));

//        changeFieldTypeMenu = new Menu(Str.i18n("Action.changeFieldType"), app);
//        fieldMenu.children().add(changeFieldTypeMenu);
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeText", () -> onDescriptorChangeType(PangaeaNoteFieldType.TEXT), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeLongText", () -> onDescriptorChangeType(PangaeaNoteFieldType.TEXTAREA), app));
//
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypePassword", () -> onDescriptorChangeType(PangaeaNoteFieldType.PASSWORD), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeURL", () -> onDescriptorChangeType(PangaeaNoteFieldType.URL), app));
//
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeCombobox", () -> onDescriptorChangeType(PangaeaNoteFieldType.COMBOBOX), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeRadioButton", () -> onDescriptorChangeType(PangaeaNoteFieldType.RADIOBUTTON), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeToggleButton", () -> onDescriptorChangeType(PangaeaNoteFieldType.TOGGLE), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeCheckbox", () -> onDescriptorChangeType(PangaeaNoteFieldType.CHECKBOX), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeStars", () -> onDescriptorChangeType(PangaeaNoteFieldType.STARS), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeDate", () -> onDescriptorChangeType(PangaeaNoteFieldType.DATE), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeTime", () -> onDescriptorChangeType(PangaeaNoteFieldType.TIME), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeDateTime", () -> onDescriptorChangeType(PangaeaNoteFieldType.DATETIME), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeYear", () -> onDescriptorChangeType(PangaeaNoteFieldType.YEAR), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeMonth", () -> onDescriptorChangeType(PangaeaNoteFieldType.MONTH), app));
//        changeFieldTypeMenu.children().add(new Button("changeFieldTypeDayOfWeek", () -> onDescriptorChangeType(PangaeaNoteFieldType.DAY_OF_WEEK), app));
//        Button changeValuesButton = new Button("changeFieldValues", () -> onDescriptorEditValues(), app);
//        fieldMenu.children().add(changeValuesButton);

//        changeContentTypeMenu = new Menu(Str.i18n("Action.changeContentType"), app);
//        fieldMenu.children().add(changeContentTypeMenu);
//        {
//            String sourceType = "text/plain";
//            Button b = new Button(null, () -> onDescriptorChangeContentType(sourceType), app);
//            b.text().set(Str.i18n("content-type." + sourceType));
//            b.icon().set(Str.of("content-type." + sourceType));
//            changeContentTypeMenu.children().add(b);
//        }
//        for (String sourceType : frame.app().getSourceTypes()) {
//            Button b = new Button(null, () -> onDescriptorChangeContentType(sourceType), app);
//            b.text().set(Str.i18n("content-type." + sourceType));
//            b.icon().set(Str.of("content-type." + sourceType));
//            changeContentTypeMenu.children().add(b);
//        }

//        fieldMenu.children().addSeparator();
//        fieldMenu.children().add(new Button("moveUpField", () -> onMoveUpField(), app));
//        fieldMenu.children().add(new Button("moveDownField", () -> onMoveDownField(), app));
//        fieldMenu.children().addSeparator();
//        fieldMenu.children().add(new Button("removeField", () -> onRemoveField(), app));

//        jPopupMenu.children().addSeparator();

//        Menu recordMenu = new Menu("forms.record", Str.i18n("forms.record"), app);
//        jPopupMenu.children().add(recordMenu);

        jPopupMenu.children().add(new Button("duplicateInObjectList", () -> parentObject.getParentDataItem().onDuplicateObject(), app));
        jPopupMenu.children().addSeparator();
        jPopupMenu.children().add(new Button("moveUpInObjectList", () -> parentObject.getParentDataItem().onMoveUp(), app));
        jPopupMenu.children().add(new Button("moveDownInObjectList", () -> parentObject.getParentDataItem().onMoveDown(), app));
        jPopupMenu.children().add(new Button("moveFirstInObjectList", () -> parentObject.getParentDataItem().onMoveFirst(), app));
        jPopupMenu.children().add(new Button("moveLastInObjectList", () -> parentObject.getParentDataItem().onMoveLast(), app));
//        recordMenu.children().addSeparator();
//        recordMenu.children().add(new Button("changeFieldBackgroundColor", () -> onChangeFieldBackgroundColor(), app));
//        recordMenu.children().addSeparator();
//        recordMenu.children().add(new Button("hideField", () -> onHideField(), app));
//        recordMenu.children().add(new Button("unhideFields", () -> onUnhideFields(), app));
        jPopupMenu.children().addSeparator();
        jPopupMenu.children().add(new Button("removeInObjectList", () -> parentObject.getParentDataItem().onRemoveObject(), app));
        jPopupMenu.children().addSeparator();
        jPopupMenu.children().add(new Button("editRecordFormat", () -> onEditCurrentRecord(), app));

//        jPopupMenu.children().addSeparator();


        formComponent = createFormComponent(descr.getType());

        formComponent.install(app, jPopupMenu);
        formComponent.setSelectValues(resolveValues(descr));
        formComponent.setContentType(descr.getOptions().getContentType());
        formComponent.setFormChangeListener(() -> callOnValueChanged());
        component = (AppComponent) formComponent;
//        changeContentTypeMenu.enabled().set(
//                descr.getType() == PangaeaNoteFieldType.TEXTAREA
//        );
//        changeValuesButton.enabled().set(descr.getType().isCustomSelect());
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
        a.add(dv);
        if (a.isEmpty()) {
            a.add("");
        }
        return new ArrayList<>(a);
    }

    public FormComponent getFormComponent() {
        return (FormComponent) component;
    }


    private void onEditCurrentRecord() {
        if (document != null) {
            PangaeaNoteObject o = new EditFormFieldsPanel(frame)
                    .setObjectValue(object,document.getDescriptor())
                    .showDialogObject();
            if (o != null) {
                PangaeaNoteFormUtils.copyObject(o,object);
                callOnStructureChanged();
            }
        }
    }

    private void onEditFormFieldDefinition() {
        if (document != null) {
            if (field != null) {
                PangaeaNoteFieldDescriptor o = new EditFormFieldPanel(frame)
                        .setTypeDescriptor(descr)
                        .showDialogDescriptor();
                if (o != null) {
                    if (document.changeField(descr.getName(), o)) {
                        callOnStructureChanged();
                    }
                }
            }
        }
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
            case DATE: {
                return new DateFieldComponent(frame);
            }
            case TIME: {
                return new TimeFieldComponent(frame);
            }
            case DATETIME: {
                return new DateTimeFieldComponent(frame);
            }
            case MONTH: {
                return new MonthComponent(frame);
            }
            case YEAR: {
                return new YearComponent(frame);
            }
            case DAY_OF_WEEK: {
                return new DayOfWeekComponent(frame);
            }
            case RADIOBUTTON: {
                return new RadioButtonComponent(frame);
            }
            case STARS: {
                return new StarsComponent(frame);
            }
            case TOGGLE: {
                return new ToggleButtonComponent(frame);
            }
            default: {
                throw new IllegalArgumentException("unexpected");
            }
        }
        //return new TextFieldComponent(frame);
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
        applyFormats();
    }

    public PangaeaNoteField getValue() {
        PangaeaNoteField df = new PangaeaNoteField();
        df.setName(this.descr.getName());
        df.setValue(getStringValue());
        df.setOptions(
                (field==null || field.getOptions() == null) ?
                        new PangaeaNoteFieldOptions() :
                        field.getOptions().copy());
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

    public void applyFormats() {
        PangaeaNoteFieldOptions options = PangaeaNoteFormUtils.fieldOptions(
                getValue(),
                getDescr()
        );
        PangaeaNoteLabelHelper.LabelFormat f = PangaeaNoteFormUtils.formatOf(
                options
        );
        PangaeaNoteLabelHelper.formalLabel(f, getLabel());
        formComponent.setOptions(options);
        boolean hidden = PNoteUtils.nonNullAndTrue(options.getHidden());
        getLabel().visible().set(!hidden);
        component.visible().set(!hidden);
        boolean readOnly = PNoteUtils.nonNullAndTrue(options.getContentReadOnly());
        formComponent.setEditable(!readOnly && isEditable());
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

    public void onChangeFieldBackgroundColor() {
        if (document != null) {
            if (field != null) {
                ColorChooser cc = new ColorChooser(frame.app());
                switch (cc.showDialog(frame).button()) {
                    case "selectColor": {
                        AppColor c = cc.value().get();
                        field.getOptions().setEditorBackgroundColor(c == null ? null : c.format());
                        callOnStructureChanged();
                        break;
                    }
                    case "selectNoColor": {
                        field.getOptions().setEditorBackgroundColor(null);
                        callOnStructureChanged();
                        break;
                    }
                }
            }
        }
    }

    public void onHideField() {
        if (document != null) {
            if (field != null) {
                field.getOptions().setHidden(true);
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
                    f.getOptions().setHidden(null);
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
            AppAlertResult s = new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.changeFieldValues"));
                        a.headerText().set(Str.i18n("Message.changeFieldValues"));
                        a.headerIcon().set(Str.of("edit"));
                        a.prefSize().set(new Dimension(400, 300));
                    })
                    .setInputTextAreaContent(Str.i18n("Message.changeFieldValues.label"), Str.of(oldValue))
                    .withOkCancelButtons()
                    .showDialog();
            if (s.isButton("ok") && !s.isBlankValue()) {
                document.updateFieldValues(descr.getName(), s.<String>value().split("\n"));
                callOnStructureChanged();
            }
        }
    }

    public void onDescriptorRename() {
        if (document != null) {
            AppAlertResult r = new Alert(frame)
                    .withOkCancelButtons()
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.renameField.title"));
                        a.headerText().set(Str.i18n("Message.renameField.header"));
                        a.headerIcon().set(Str.of("edit"));
                    })
                    .setInputTextFieldContent(
                            Str.i18n("Message.renameField.label"), Str.of(descr.getName())
                    )
                    //                    .setPreferredSize(400, 200)
                    .showDialog();
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

    public void onDescriptorChangeContentType(String type) {
        if (document != null) {
            document.changeContentType(descr.getName(), type);
            callOnStructureChanged();
        }
    }

    public void onRemoveField() {
        if (document != null) {
            if (new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.warning"));
                        a.headerText().set(Str.i18n("Message.warning"));
                        a.headerIcon().set(Str.of("delete"));
                    })
                    .setContentText(Str.i18n("Message.askDeleteField"))
                    .withYesNoButtons()
                    .showDialog().button().equals("yes")) {
                document.removeField(descr.getName());
                callOnStructureChanged();
            }
        }
    }

    public void onAddField() {
        if (document != null) {
            AppAlertResult r = new Alert(frame)
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
                    .showDialog();
            if ("ok".equals(r.button())) {
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
