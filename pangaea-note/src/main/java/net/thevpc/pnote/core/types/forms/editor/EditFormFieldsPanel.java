package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.nuts.NutsUtilStrings;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.model.*;
import net.thevpc.pnote.util.PNoteUtils;

public class EditFormFieldsPanel extends BorderPane {
    private PangaeaNoteFrame frame;
    private BorderPane center;
    private AppComponent empty;
    private ChoiceList<EditFormFieldPanel> list;
    private PangaeaNoteObjectDescriptor oldObjectDescr;
    private PangaeaNoteObject oldObject;
    //    private PangaeaNoteObjectDescriptor descriptor;
    private ToolBar listToolbar;
    private boolean ok = false;
    private boolean editDescriptor = true;

    public EditFormFieldsPanel(PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        children().addAll(
                new BorderPane(app())
                        .with((BorderPane b) -> {
                            b.children().addAll(
                                    listToolbar = new ToolBar(frame.app())
                                            .with(t -> {
                                                t.anchor().set(Anchor.TOP);
                                                t.children().addAll(
                                                        new Button("add", this::onAddField, app()),
                                                        new Button("remove", this::onRemoveField, app()),
                                                        new Button("moveUp", this::onMoveUp, app()),
                                                        new Button("moveDown", this::onMoveDown, app()),
                                                        new Button("moveFirst", this::onMoveFirst, app()),
                                                        new Button("moveLast", this::onMoveLast, app())
                                                );
                                            }),
                                    new ScrollPane(
                                            list = new ChoiceList<>(EditFormFieldPanel.class, app())
                                                    .with(
                                                            (ChoiceList<EditFormFieldPanel> c) -> {
                                                                c.itemRenderer().set(context -> {
                                                                    EditFormFieldPanel v = context.getValue();
                                                                    if (v == null) {
                                                                        context.setText("<no-selection>");
                                                                    } else {
                                                                        String s = v.getLabelName().text().get().value().trim();
                                                                        context.setText(s.trim().isEmpty() ? "<empty>" : s.trim());
                                                                    }
                                                                    context.renderDefault();
                                                                });
                                                            }
                                                    )
                                    ).with((ScrollPane s) -> s.anchor().set(Anchor.CENTER))
                            );
                            b.anchor().set(Anchor.LEFT);
                        }),
                center = new BorderPane(app())
        );
        empty = createEmpty();
        list.selection().onChangeAndInit(() -> {
            updateCenter();
        });
    }

    public boolean isEditDescriptor() {
        return editDescriptor;
    }

    public EditFormFieldsPanel setEditDescriptor(boolean editDescriptor) {
        this.editDescriptor = editDescriptor;
        for (AppComponent child : listToolbar.children()) {
            child.enabled().set(editDescriptor);
        }
        for (EditFormFieldPanel value : list.values()) {
            value.setEditDescriptor(editDescriptor);
        }
        return this;
    }

    public void updateCenter() {
        EditFormFieldPanel q = list.selection().get();
        center.children().clear();
        center.children().add(
                q == null ? empty : q
        );
    }

    public void onMoveUpAt(int index) {
        PNoteUtils.moveWritableListItemUp(list.values(), list.selection(), index);
        updateCenter();
    }

    public void onMoveDownAt(int index) {
        PNoteUtils.moveWritableListItemDown(list.values(), list.selection(), index);
        updateCenter();
    }

    public void onMoveFirstAt(int index) {
        PNoteUtils.moveWritableListItemFirst(list.values(), list.selection(), index);
        updateCenter();
    }

    public void onMoveLastAt(int index) {
        PNoteUtils.moveWritableListItemLast(list.values(), list.selection(), index);
        updateCenter();
    }

    public void onMoveUp() {
        int i = currentSelectionIndex();
        if (i >= 0) {
            onMoveUpAt(i);
        }
    }

    public void onMoveDown() {
        int i = currentSelectionIndex();
        if (i >= 0) {
            onMoveDownAt(i);
        }
    }

    public void onMoveFirst() {
        int i = currentSelectionIndex();
        if (i >= 0) {
            onMoveFirstAt(i);
        }
    }

    public void onMoveLast() {
        int i = currentSelectionIndex();
        if (i >= 0) {
            onMoveLastAt(i);
        }
    }

    public int currentSelectionIndex() {
        Integer i = list.selection().indices().get();
        if (i != null && i >= 0) {
            return i;
        }
        return -1;
    }

    public void onRemoveField() {
        int i = currentSelectionIndex();
        if (i >= 0) {
            onRemoveFieldAt(i);
        }
    }

    public void onRemoveFieldAt(int index) {
        list.values().removeAt(index);
//        descriptor.getFields().remove(index);
        updateCenter();
    }

    public void onSelectField(int index) {
        if (index <= 0) {
            list.selection().clear();
        } else {
            list.selection().set(list.values().get(index));
        }
        updateCenter();
    }

    public EditFormFieldPanel findFieldPanel(String name) {
        for (EditFormFieldPanel value : list.values()) {
//            String fn = value.getLabelName().text().get().value();
            String fl = value.getFieldName().text().get().value();
            if (NutsUtilStrings.trim(fl).equals(NutsUtilStrings.trim(name))) {
                return value;
            }
        }
        return null;
    }

    public String generateNewFieldName(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            prefix = "Field ";
        }
        int x = 1;
        while (true) {
            String n = NutsUtilStrings.trim(prefix + ((x <= 0) ? "" : String.valueOf(x)));
            EditFormFieldPanel a = findFieldPanel(n);
            if (a == null) {
                return n;
            }
            x++;
        }
    }

    public void onAddField() {
        PangaeaNoteFieldDescriptor d = new PangaeaNoteFieldDescriptor();
        d.setName("");
        d.setType(PangaeaNoteFieldType.TEXT);
        d.getOptions().setLabelName(generateNewFieldName("field"));
        EditFormFieldPanel value = new EditFormFieldPanel(frame).setTypeDescriptor(d);
        list.values().add(value);
        list.selection().set(value);
        updateCenter();
    }

    private AppComponent createEmpty() {
        BorderPane b = new BorderPane(app());
        b.children().add(new Label(Str.of("EMPTY"), app()));
        return b;
    }

    protected void install() {

    }

    protected void uninstall() {

    }

    public boolean isAllRecordsMode() {
        return true;
    }

    public PangaeaNoteObject getObjectValue() {
        PangaeaNoteObject d=new PangaeaNoteObject();
        for (EditFormFieldPanel value : list.values()) {
            PangaeaNoteField dd = value.getField();
            d.addField(dd);
            dd.setOptions(
                    PangaeaNoteFieldOptions.nullifyDefaults(
                            dd.getOptions(),
                            oldObjectDescr.findField(dd.getName()).getOptions()
                    )
            );
            PangaeaNoteField oldField = oldObject.findField(dd.getName());
            if(oldField!=null){
                dd.setValue(oldField.getValue());
            }
        }
        return d;
    }
    public PangaeaNoteObjectDescriptor getObjectDescriptor() {
        PangaeaNoteObjectDescriptor d = new PangaeaNoteObjectDescriptor();
        d.setName(""); //???
        for (EditFormFieldPanel value : list.values()) {
            PangaeaNoteFieldDescriptor dd = value.getFieldDescriptor();
            d.addField(dd);
        }
        return d;
    }

    public EditFormFieldsPanel setObjectDescriptor(PangaeaNoteObjectDescriptor objectDescriptor) {
        setEditDescriptor(true);
        list.selection().clear();
        list.values().setAll(
                objectDescriptor.getFields()
                        .stream().map(
                                x -> {
                                    EditFormFieldPanel p = new EditFormFieldPanel(frame);
                                    p.setTypeDescriptor(x);
                                    return p;
                                }
                        ).toArray(EditFormFieldPanel[]::new)
        );
        if (list.values().size() > 0) {
            list.selection().set(list.values().get(0));
        }
        return this;
    }

    public EditFormFieldsPanel setObjectValue(PangaeaNoteObject object, PangaeaNoteObjectDescriptor objectDescriptor) {
        setEditDescriptor(false);
        this.oldObject=object;
        this.oldObjectDescr=objectDescriptor;
        list.selection().clear();
        list.values().setAll(
                objectDescriptor.getFields()
                        .stream().map(
                                x -> {
                                    EditFormFieldPanel p = new EditFormFieldPanel(frame);
                                    PangaeaNoteField f = object.findField(x.getName());
                                    p.setValueDescriptor(f.getOptions(), x);
                                    return p;
                                }
                        ).toArray(EditFormFieldPanel[]::new)
        );
        if (list.values().size() > 0) {
            list.selection().set(list.values().get(0));
        }
        return this;
    }

    public PangaeaNoteObjectDescriptor showDialogObjectDescriptor() {
        while (true) {
            install();
            this.ok = false;

            Alert alert = new Alert(frame).with((Alert a) -> {
                a.title().set(Str.i18n(
                        isAllRecordsMode() ? "Message.editFormFieldsPanel.title" : "Message.editRecordForm.title"
                ));
                a.headerText().set(Str.i18n(
                        isAllRecordsMode() ? "Message.editFormFieldsPanel.header" : "Message.editRecordForm.header"
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
                    return getObjectDescriptor();
                }
                return null;
            } catch (Exception ex) {
                frame.app().errors().add(ex);
            }
        }
    }
    public PangaeaNoteObject showDialogObject() {
        while (true) {
            install();
            this.ok = false;

            Alert alert = new Alert(frame).with((Alert a) -> {
                a.title().set(Str.i18n(
                        isAllRecordsMode() ? "Message.editFormFieldsPanel.title" : "Message.editRecordForm.title"
                ));
                a.headerText().set(Str.i18n(
                        isAllRecordsMode() ? "Message.editFormFieldsPanel.header" : "Message.editRecordForm.header"
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
                    return getObjectValue();
                }
                return null;
            } catch (Exception ex) {
                frame.app().errors().add(ex);
            }
        }
    }

    protected void ok() {
        uninstall();
        this.ok = true;
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
    }
}
