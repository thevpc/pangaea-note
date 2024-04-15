package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Alert;
import net.thevpc.echo.DataPane;
import net.thevpc.echo.DataPaneRenderer;
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

class FormsDataHeaderPane extends DataPane<PangaeaNoteObjectExt> {

    private PangaeaNote currentNote;
    private PangaeaNoteFrame frame;
    PangaeaNoteObjectDocument dynamicDocument;
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

    public FormsDataHeaderPane(PangaeaNoteFrame frame) {
        super(PangaeaNoteObjectExt.class, new DataPaneRenderer<PangaeaNoteObjectExt>() {
            @Override
            public void set(int index, PangaeaNoteObjectExt value, AppComponent component, DataPane<PangaeaNoteObjectExt> dataPane) {
                FormsDataHeaderItem b = (FormsDataHeaderItem) component;
                b.setValue(value, index, index);
            }

            @Override
            public PangaeaNoteObjectExt get(int index, AppComponent component, DataPane<PangaeaNoteObjectExt> dataPane) {
                return ((FormsDataHeaderItem) component).getValue(index);
            }

            @Override
            public AppComponent create(DataPane<PangaeaNoteObjectExt> dataPane) {
                return new FormsDataHeaderItem(frame, (FormsDataHeaderPane) dataPane);
            }

            @Override
            public void dispose(AppComponent component, DataPane<PangaeaNoteObjectExt> dataPane) {
                ((FormsDataHeaderItem) component).onUninstall();
            }
        }, frame.app());
        this.frame = frame;
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
            dynamicDocument.addObject(dynamicDocument.getDescriptor().createObject());
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
                    onListValuesChangedImpl();
                }
            }
        }
    }

    private void _ensureValues() {
        if (dynamicDocument != null) {
            if (dynamicDocument.getValues() == null) {
                dynamicDocument.setValues(new ArrayList<>());
            }
        }
    }

    void onDuplicateObjectAt(int index) {
        if (dynamicDocument != null) {
            _ensureValues();
            PangaeaNoteObject o = dynamicDocument.getValues().get(index);
            dynamicDocument.getValues().add(index + 1, o.copy());
            onListValuesChangedImpl();
        }
    }

    private boolean _onSwitchValues(int index1, int index2) {
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
            }
            dynamicDocument.getValues().add(index, dynamicDocument.getDescriptor().createObject());
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
        this.values().setAll(createAllList().toArray(new PangaeaNoteObjectExt[0]));
    }

    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable().set(b);
    }
}
