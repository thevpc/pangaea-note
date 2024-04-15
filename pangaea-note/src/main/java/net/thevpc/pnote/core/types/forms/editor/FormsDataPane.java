package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppAlertResult;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObject;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.util.PNoteUtils;

import java.util.ArrayList;
import java.util.List;

class FormsDataPane extends DataPane<PangaeaNoteObjectExt> {

    private PangaeaNote currentNote;
    private PangaeaNoteFrame frame;
    private ChoiceList<PangaeaNoteObject> headerList;
    PangaeaNoteObjectDocument dynamicDocument;
    ScrollPane scrollPane;
    PangaeaNoteObjectTracker dynamicObjectTrackerAdapter = new PangaeaNoteObjectTracker() {
        @Override
        public void onStructureChanged() {
            onStructureChangedImpl();
        }

        @Override
        public void onListValuesChanged() {
            onListValuesChangedImpl();
        }

        @Override
        public void onFieldValueChanged() {
            onFieldValueChangedImpl();
        }

    };

    public FormsDataPane(PangaeaNoteFrame frame,ChoiceList<PangaeaNoteObject> headerList) {
        super(PangaeaNoteObjectExt.class, new DataPaneRenderer<PangaeaNoteObjectExt>() {
            @Override
            public void set(int index, PangaeaNoteObjectExt value, AppComponent component, DataPane<PangaeaNoteObjectExt> dataPane) {
                FormsDataItem b = (FormsDataItem) component;
                b.setValue(value, index, index);
            }

            @Override
            public PangaeaNoteObjectExt get(int index, AppComponent component, DataPane<PangaeaNoteObjectExt> dataPane) {
                return ((FormsDataItem) component).getValue(index);
            }

            @Override
            public AppComponent create(DataPane<PangaeaNoteObjectExt> dataPane) {
                return new FormsDataItem(frame, (FormsDataPane) dataPane);
            }

            @Override
            public void dispose(AppComponent component, DataPane<PangaeaNoteObjectExt> dataPane) {
                ((FormsDataItem) component).onUninstall();
            }
        }, frame.app());
        this.frame = frame;
        this.headerList = headerList;
        container().get().parentConstraints().addAll(Layout.GRID, ColumnCount.of(1), ContainerGrow.TOP_ROW, AllMargins.of(3), AllGrow.HORIZONTAL, AllFill.HORIZONTAL);
    }

    void onEditForm() {
        if (currentNote != null) {
            PangaeaNoteObjectDescriptor newDesc = new EditFormFieldsPanel(frame)
                    .setObjectDescriptor(dynamicDocument.getDescriptor())
                    .showDialogObjectDescriptor();
            if (newDesc != null) {
                dynamicDocument.changeStructure(newDesc);
                onListValuesChangedImpl();
            }
        }
    }

    void onAddObject() {
        if (currentNote != null) {
            PangaeaNoteObject object = dynamicDocument.getDescriptor().createObject();
            dynamicDocument.addObject(object);
            headerList.values().add(object);
            onListValuesChangedImpl();
        }
    }

    public void onStructureChangedImpl() {
        onListValuesChangedImpl();
    }

    public void onFieldValueChangedImpl() {
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) frame.app().getContentTypeService(PangaeaNoteFormsService.FORMS);
        currentNote.setContent(s.getContentAsElement(this.dynamicDocument));
        frame.onDocumentChanged();
    }

    public void onListValuesChangedImpl() {
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) frame.app().getContentTypeService(PangaeaNoteFormsService.FORMS);
        currentNote.setContent(s.getContentAsElement(this.dynamicDocument));
        frame.onDocumentChanged();
        values().setAll(createAllList().toArray(new PangaeaNoteObjectExt[0]));
    }

    void onRemoveAllObjects() {
        if (dynamicDocument != null) {
            AppAlertResult s = new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.warning"));
                        a.headerText().set(Str.i18n("Message.warning"));
                        a.headerIcon().set(Str.of("warning"));
                    })
                    .setContentText(Str.i18n("Message.askDeleteAllObjects"))
                    .withYesNoButtons()
                    .showDialog();

            if (s.isYesButton()) {
                dynamicDocument.getValues().clear();
                headerList.values().clear();
                values().clear();
                onListValuesChangedImpl();
            }

        }
    }

    void onRemoveObjectAt(int index) {
        if (dynamicDocument != null) {
            if (dynamicDocument.getValues() != null) {
                AppAlertResult s = new Alert(frame)
                        .with((Alert a) -> {
                            a.title().set(Str.i18n("Message.warning"));
                            a.headerText().set(Str.i18n("Message.warning"));
                            a.headerIcon().set(Str.of("warning"));
                        })
                        .setContentText(Str.i18n("Message.askDeleteObject"))
                        .withYesNoButtons()
                        .showDialog();

                if (s.isYesButton()) {
                    dynamicDocument.getValues().remove(index);
                    headerList.values().removeAt(index);
                    onListValuesChangedImpl();
                }
            }
        }
    }

    private void _ensureValues() {
        if (dynamicDocument != null) {
            if (dynamicDocument.getValues() == null) {
                dynamicDocument.setValues(new ArrayList<>());
                headerList.values().clear();
            }
        }
    }

    void onDuplicateObjectAt(int index) {
        if (dynamicDocument != null) {
            _ensureValues();
            PangaeaNoteObject o = dynamicDocument.getValues().get(index);
            PangaeaNoteObject copy = o.copy();
            dynamicDocument.getValues().add(index + 1, copy);
            headerList.values().add(index + 1, copy);
            onListValuesChangedImpl();
        }
    }

    private boolean _onSwitchValues(int index1, int index2) {
        headerList.values().set(index1,dynamicDocument.getValues().get(index2));
        headerList.values().set(index2,dynamicDocument.getValues().get(index1));
        return PNoteUtils.switchListValues(dynamicDocument.getValues(), index1, index2);
    }

    void onMoveFirstAt(int from) {
        if (dynamicDocument != null) {
            _ensureValues();
            List<PangaeaNoteObject> _values = dynamicDocument.getValues();
            if (from > 0 && from <= _values.size() - 1) {
                PangaeaNoteObject a = _values.remove(from);
                _values.add(0, a);
            }
            onListValuesChangedImpl();
        }
    }

    void onMoveLastAt(int from) {
        if (dynamicDocument != null) {
            _ensureValues();
            List<PangaeaNoteObject> _values = dynamicDocument.getValues();
            if (from >= 0 && from < _values.size() - 1) {
                PangaeaNoteObject a = _values.remove(from);
                _values.add(a);
            }
            onListValuesChangedImpl();
        }
    }

    void onMoveUpAt(int index) {
        if (dynamicDocument != null) {
            _ensureValues();
            _onSwitchValues(index, index - 1);
            onListValuesChangedImpl();
        }
    }

    void onMoveDownAt(int index) {
        if (dynamicDocument != null) {
            _ensureValues();
            _onSwitchValues(index, index + 1);
            onListValuesChangedImpl();
        }
    }

    void onAddObjectAt(int index) {
        if (dynamicDocument != null) {
            if (dynamicDocument.getValues() == null) {
                dynamicDocument.setValues(new ArrayList<>());
                headerList.values().clear();
            }
            PangaeaNoteObject object = dynamicDocument.getDescriptor().createObject();
            dynamicDocument.getValues().add(index, object);
            headerList.values().add(index, object);
            onListValuesChangedImpl();
        }
    }

    private List<PangaeaNoteObjectExt> createAllList() {
        List<PangaeaNoteObjectExt> all = new ArrayList<>();
        if (this.dynamicDocument != null && this.dynamicDocument.getValues() != null) {
            for (PangaeaNoteObject object : this.dynamicDocument.getValues()) {
                if (object != null) {
                    all.add(new PangaeaNoteObjectExt(object, dynamicDocument.getDescriptor(), dynamicDocument));
                }
            }
        }
        return all;
    }

    public void setNote(PangaeaNote note, PangaeaNoteFrame win) {
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) win.app().getContentTypeService(PangaeaNoteFormsService.FORMS);
        this.currentNote = note;
        this.dynamicDocument = s.getContentAsObject(note.getContent());
        this.headerList.selection().set(null);
        this.headerList.values().clear();
        if (this.dynamicDocument != null && this.dynamicDocument.getValues() != null) {
            for (PangaeaNoteObject object : this.dynamicDocument.getValues()) {
                if (object != null) {
                    this.headerList.values().add(object);
                }
            }
        }
        this.values().setAll(createAllList().toArray(new PangaeaNoteObjectExt[0]));
    }

    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable().set(b);
    }
}
