/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.echo.impl.Applications;
import net.thevpc.nuts.NutsUtilStrings;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.frame.dialogs.PangaeaNoteIconsButton;
import net.thevpc.pnote.core.frame.dialogs.PangaeaNoteTitleFormatPanel;
import net.thevpc.pnote.core.frame.dialogs.PangaeaNoteTypesComboBox;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteField;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldOptions;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldType;
import net.thevpc.pnote.core.types.forms.util.PangaeaNoteFormUtils;
import net.thevpc.pnote.util.PNoteUtils;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author vpc
 */
public class EditFormFieldPanel extends GridPane {

    //    private Panel panel;
    private TextField fieldNameEditor;
    private Label fieldNameLabel;
    private Label labelNameLabel;
    private TextField labelName;
    private TextField fieldName;
    private CheckBox hideEditor;
    private ContentEditorFormatPanel contentEditor;
    private LabelEditorFormatPanel labelEditor;
    private PossibleValuesList possibleValues;
    private Label fieldTypeLabel;
    private ComboBox<PangaeaNoteFieldType> fieldType;

    private boolean ok = false;
    private PangaeaNoteFieldOptions options;
    private PangaeaNoteFieldDescriptor descr;
    private PangaeaNoteFrame frame;
    private TabPane jTabbedPane;
    private Label defaultValueLabel;
    private TextField defaultValueEditor;
    private boolean editDescriptor = true;

    public EditFormFieldPanel(PangaeaNoteFrame frame) {
        super(1, frame.app());
        this.frame = frame;
        Application app = frame.app();

        this.parentConstraints().addAll(AllAnchors.LEFT, AllFill.HORIZONTAL, AllMargins.of(3));
        this.children().addAll(
                fieldNameLabel = new Label(Str.i18n("Message.fieldName"), app),
                fieldName = new TextField(Str.empty(), app),
                labelNameLabel = new Label(Str.i18n("Message.fieldLabel"), app),
                labelName = new TextField(Str.empty(), app),

                fieldTypeLabel = new Label(Str.i18n("Message.fieldType"), app),
                fieldType = new ComboBox<>(PangaeaNoteFieldType.class, app),
                defaultValueLabel = new Label(Str.i18n("Message.defaultValue"), app),
                defaultValueEditor = new TextField(Str.empty(), app),
                hideEditor = new CheckBox(null, Str.i18n("Message.hideField"), app),
                jTabbedPane = new TabPane(app)
                        .with(t -> {
                            t.childConstraints().addAll(Fill.BOTH, Grow.BOTH);
                            t.children().add(labelEditor = new LabelEditorFormatPanel(EditFormFieldPanel.this));
                            t.children().add(contentEditor = new ContentEditorFormatPanel(EditFormFieldPanel.this));
                            t.children().add(possibleValues = new PossibleValuesList(app));
                        })
        );
        fieldType.selection().onChangeAndInit(() -> {
            PangaeaNoteFieldType t = fieldType.selection().get();
            possibleValues.enabled().set(
                    t != null && t.isSelect()
            );
            contentEditor.onTypeChanged();
        });
        fieldName.editable().set(false);
//        prefSize().set(new Dimension(700,450));
    }

    public PangaeaNoteField getField() {
        PangaeaNoteField d = new PangaeaNoteField();
        d.setName(fieldName.text().get().value());
        contentEditor.loadToOptions(d.getOptions());
        labelEditor.loadToOptions(d.getOptions());
        d.getOptions().setLabelName(labelName.text().get().value());
        if (d.getName().isEmpty()) {
            d.setName(d.getOptions().getLabelName());
        }
        return d;
    }

    public PangaeaNoteFieldDescriptor getFieldDescriptor() {
        PangaeaNoteFieldDescriptor d = new PangaeaNoteFieldDescriptor();
        d.setName(fieldName.text().get().value());
        d.setType(fieldType.selection().get() == null ? PangaeaNoteFieldType.TEXT : fieldType.selection().get());
        d.setValues(possibleValues.getValues());
        d.setDefaultValue(defaultValueEditor.text().get().value());
        contentEditor.loadToOptions(d.getOptions());
        labelEditor.loadToOptions(d.getOptions());
        d.getOptions().setLabelName(labelName.text().get().value());
        if (d.getName().isEmpty()) {
            d.setName(d.getOptions().getLabelName());
        }
        return d;
    }

    public TextField getLabelName() {
        return labelName;
    }

    public TextField getFieldName() {
        return fieldName;
    }

    public EditFormFieldPanel setTypeDescriptor(PangaeaNoteFieldDescriptor descr) {
        return this.setField(descr.getOptions(), descr, true);
    }

    public EditFormFieldPanel setValueDescriptor(PangaeaNoteFieldOptions options, PangaeaNoteFieldDescriptor descr) {
        options = PangaeaNoteFormUtils.fieldOptions(new PangaeaNoteField().setOptions(options), descr);
        return this.setField(options, descr, false);
    }

    private EditFormFieldPanel setField(PangaeaNoteFieldOptions options, PangaeaNoteFieldDescriptor descr, boolean allRecordsMode) {
        setEditDescriptor(allRecordsMode);
        options = options.copy();
        descr = descr.copy();
        if (NutsUtilStrings.isBlank(options.getLabelName())) {
            options.setLabelName(descr.getName());
        }
        this.options = options;
        this.descr = descr;
        this.fieldType.selection().set(descr.getType() == null ? PangaeaNoteFieldType.TEXT : descr.getType());
        this.fieldName.text().set(Str.of(descr.getName()));
        this.labelName.text().set(Str.of(options.getLabelName()));
        this.hideEditor.selected().set(PNoteUtils.nonNullAndTrue(options.getHidden()));

        this.labelEditor.loadFromOptions(options);
        this.contentEditor.loadFromOptions(options);

        if (isEditDescriptor()) {
            this.labelEditor.loadFromOptions(options);
            this.possibleValues.setValues(descr.getValues());
            this.possibleValues.editable().set(true);
        } else {
            this.possibleValues.editable().set(false);
        }
//        this.possibleValues.enabled().set(descr.getType().isSelect());
        return this;
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

    public PangaeaNoteFieldOptions showDialogOptions() {
        if (!isEditDescriptor()) {
            throw new IllegalArgumentException("unexpected");
        }
        while (true) {
            install();
            this.ok = false;

            Alert alert = new Alert(frame).with((Alert a) -> {
                a.title().set(Str.i18n(
                        isEditDescriptor() ? "Message.editAllRecordsForm.title" : "Message.editRecordForm.title"
                ));
                a.headerText().set(Str.i18n(
                        isEditDescriptor() ? "Message.editAllRecordsForm.header" : "Message.editRecordForm.header"
                ));
                a.headerIcon().set(Str.of("edit"));
                a.content().set(this);
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
            });
            alert.showDialog();
            try {
                if (ok) {
                    PangaeaNoteFieldOptions o = new PangaeaNoteFieldOptions();
                    String txt = Applications.rawString(labelName.text(), labelName);
                    o.setLabelName(txt);
                    contentEditor.loadToOptions(o);
                    labelEditor.loadToOptions(o);
                    return o;
                }
                return null;
            } catch (Exception ex) {
                frame.app().errors().add(ex);
            }
        }
    }

    public PangaeaNoteFieldDescriptor showDialogDescriptor() {
        if (!isEditDescriptor()) {
            throw new IllegalArgumentException("unexpected");
        }
        while (true) {
            install();
            this.ok = false;

            Alert alert = new Alert(frame).with((Alert a) -> {
                a.title().set(Str.i18n(
                        isEditDescriptor() ? "Message.editAllRecordsForm.title" : "Message.editRecordForm.title"
                ));
                a.headerText().set(Str.i18n(
                        isEditDescriptor() ? "Message.editAllRecordsForm.header" : "Message.editRecordForm.header"
                ));
                a.headerIcon().set(Str.of("edit"));
                a.content().set(this);
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
            });
            alert.showDialog();
            try {
                if (ok) {
                    PangaeaNoteFieldOptions o = new PangaeaNoteFieldOptions();
                    String txt = Applications.rawString(labelName.text(), labelName);
                    o.setLabelName(txt);
                    contentEditor.loadToOptions(o);
                    labelEditor.loadToOptions(o);
                    PangaeaNoteFieldDescriptor d = new PangaeaNoteFieldDescriptor();
                    contentEditor.loadToDescriptor(d);
                    d.setDefaultValue(Applications.rawString(defaultValueEditor.text(), defaultValueEditor));
                    d.setName(o.getLabelName());
                    d.setOptions(o);
                    return d;
                }
                return null;
            } catch (Exception ex) {
                frame.app().errors().add(ex);
            }
        }
    }

    private boolean isEditDescriptor() {
        return descr != null;
    }

    public EditFormFieldPanel setEditDescriptor(boolean editDescriptor) {
        this.editDescriptor = editDescriptor;
        if (this.editDescriptor) {
//            fieldNameLabel.visible().set(false);
//            fieldName.visible().set(false);
        } else {
            fieldNameLabel.visible().set(true);
            fieldName.visible().set(true);
        }
        defaultValueEditor.editable().set(editDescriptor);
        possibleValues.editable().set(editDescriptor);
        fieldType.enabled().set(editDescriptor);
        return this;
    }

    private static class LabelEditorFormatPanel extends PangaeaNoteTitleFormatPanel {
        EditFormFieldPanel editFormDialog;

        public LabelEditorFormatPanel(EditFormFieldPanel editFormDialog) {
            super(editFormDialog.frame, true);
            parentConstraints().addAll(AllAnchors.LEFT, AllFill.HORIZONTAL, AllMargins.of(3));
            title().set(Str.i18n("EditFormDialog.LabelEditorFormatPanel.title"));
            this.editFormDialog = editFormDialog;
        }

    }

    private static class ContentEditorFormatPanel extends GridPane {
        private Label contentTypeLabel;
        private PangaeaNoteTypesComboBox contentTypeEditor;
        private CheckBox readOnlyEditor;
        private EditFormFieldPanel editFormDialog;
        private ColorButton foregroundEditor;
        private ColorButton backgroundEditor;
        private PangaeaNoteIconsButton iconButton;

        public ContentEditorFormatPanel(EditFormFieldPanel editFormDialog) {
            super(1, editFormDialog.frame.app());
            parentConstraints().addAll(AllAnchors.LEFT, AllFill.HORIZONTAL, AllMargins.of(3), ContainerGrow.ALL);
            title().set(Str.i18n("EditFormDialog.ContentEditorFormatPanel.title"));
            this.editFormDialog = editFormDialog;
            foregroundEditor = new ColorButton("foregroundColor", app());
            foregroundEditor.tooltip().set(Str.of("Message.foregroundColor"));
            backgroundEditor = new ColorButton("backgroundColor", app());
            backgroundEditor.tooltip().set(Str.of("Message.backgroundEditor"));
            Application app = app();
            this.children().addAll(
                    new GridPane(2, app())
                            .with((Panel h) -> {
                                h.parentConstraints().addAll(AllAnchors.LEFT, AllFill.HORIZONTAL, AllMargins.of(3), ContainerGrow.ALL);
                                h.children().addAll(
                                        contentTypeLabel = new Label(Str.i18n("Message.fieldSourceType"), app),
                                        contentTypeEditor = new PangaeaNoteTypesComboBox(editFormDialog.frame,
                                                new PangaeaNoteTypesComboBox.Options()
                                                        .setShowGroups(false)
                                                        .setShowRecent(false)
                                                        .setShowTemplates(false)
                                                        .setFilter(new PangaeaNoteTypesComboBox.Filter() {
                                                            @Override
                                                            public boolean accept(PangaeaNoteMimeType mimeType, String group) {
                                                                if ("sources".equalsIgnoreCase(group)) {
                                                                    return true;
                                                                }
                                                                return false;
                                                            }
                                                        })
                                        )
                                );
                            }),
                    new GridPane(4, app())
                            .with((GridPane v) -> {
                                v.parentConstraints().addAll(
                                        ContainerGrow.TOP_ROW,
                                        AllMargins.of(10, 5, 5, 5),
                                        //                                    new ParentMargin(20, 0, 0, 0),
                                        AllAnchors.LEFT
                                );
                                v.children().addAll(
                                        Stream.of(
                                                new Label(Str.i18n("Message.titleForegroundColor"), app()),
                                                foregroundEditor.with((ColorButton c) -> {
                                                    c.childConstraints().add(Fill.BOTH);
                                                }),
                                                new Label(Str.i18n("Message.titleBackgroundColor"), app()),
                                                backgroundEditor.with((ColorButton c) -> {
                                                    c.childConstraints().add(Fill.BOTH);
                                                })
                                        ).filter(Objects::nonNull).toArray(AppComponent[]::new)
                                );
                            }),
                    readOnlyEditor = new CheckBox(null, Str.i18n("Message.readOnly"), app)
            );
//            defaultValueLabel.visible().set(editFormDialog.descr != null);
//            defaultValueEditor.visible().set(editFormDialog.descr != null);
        }

        public void loadToDescriptor(PangaeaNoteFieldDescriptor d) {
//            d.setName(o.getLabelName());
//            d.setValues(descr.getValues());//TODO
//            d.setType(descr.getType());//TODO
        }

        void onTypeChanged() {
            PangaeaNoteFieldType t = editFormDialog.fieldType.values().get();
            contentTypeEditor.visible().set(t != null && t.isFreeTextType());
            contentTypeLabel.visible().set(t != null && t.isFreeTextType());
        }

        public void loadFromOptions(PangaeaNoteFieldOptions options) {
            readOnlyEditor.selected().set(PNoteUtils.nonNullAndTrue(options.getContentReadOnly()));
            foregroundEditor.value().set(Color.of(options.getEditorForegroundColor(), app()));
            backgroundEditor.value().set(Color.of(options.getEditorBackgroundColor(), app()));
            contentTypeEditor.setSelectedContentType(editFormDialog.frame.app().normalizeContentType(options.getContentType()), null);
        }

        public void loadToOptions(PangaeaNoteFieldOptions options) {
            options.setContentReadOnly(readOnlyEditor.selected().get());
            options.setContentType(contentTypeEditor.getSelectedContentTypeId());
            options.setEditorBackgroundColor(Color.format(backgroundEditor.value().get()));
            options.setEditorForegroundColor(Color.format(foregroundEditor.value().get()));
        }
    }

}
