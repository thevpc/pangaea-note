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
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.core.special.DataPane;
import net.thevpc.pnote.core.special.DataPaneRenderer;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObject;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.util.OtherUtils;

import java.util.ArrayList;
import java.util.List;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 * @author vpc
 */
public class PangaeaNoteFormsEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    private FormsDataPane componentList;

    private PangaeaNoteFrame frame;
    private boolean editable = true;
    private boolean compactMode;

    public PangaeaNoteFormsEditorTypeComponent(boolean compactMode, PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        this.compactMode = compactMode;
        componentList = new FormsDataPane(frame);
        ScrollPane scrollPane = new ScrollPane(componentList)
                .with(v -> v.anchor().set(Anchor.CENTER));
        children().add(scrollPane);
        prepareToolBar();
        propagateEvents();
        refreshView();
    }

    protected void installEnabler(Runnable r) {
        r.run();
        frame.app().toolkit().focusOwner().onChange(r);
        frame.treePane().tree().selection().onChange(r);
    }

    protected FormsDataPane resolveFormsDataPane() {
        AppComponent fo = frame.app().toolkit().focusOwner().get();
        if (fo != null) {
            FormsDataPane r = resolveFormsDataPane(fo);
            if (r != null) {
                return r;
            }
        }
        PangaeaNoteEditorTypeComponent ed = frame.noteEditor().getCurrentEditor();
        if (ed instanceof PangaeaNoteFormsEditorTypeComponent) {
            return ((PangaeaNoteFormsEditorTypeComponent) ed).componentList;
        }
        return null;
    }

    protected FormsDataPane resolveFormsDataPane(AppComponent component) {
        if (component == null) {
            return null;
        }
        if (component instanceof FormsDataPane) {
            return (FormsDataPane) component;
        }
        return resolveFormsDataPane(component.parent());
    }

    protected void prepareToolBar() {
        frame.findOrCreateToolBar("Forms", editToolBar -> {
            PangaeaNoteApp app = frame.app();
            editToolBar.children().add(new Button("addToObjectList", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                FormsDataPane q = resolveFormsDataPane();
                if (q != null) {
                    if (q.editable().get()) {
                        q.onAddObject();
                    }
                }
            }, app).with(
                    x -> installEnabler(() -> {
                        FormsDataPane q = resolveFormsDataPane();
                        boolean copyEnabled = false;
                        if (q != null) {
                            if (q.editable().get()) {
                                copyEnabled = true;
                            }
                        }
                        x.enabled().set(copyEnabled);
                        x.visible().set(
                                !app.hideDisabled().get() || copyEnabled
                        );
                    })
            ));

            editToolBar.children().add(new Button("clearObjectList", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                FormsDataPane q = resolveFormsDataPane();
                if (q != null) {
                    if (q.editable().get()) {
                        q.onRemoveAllObjects();
                    }
                }
            }, app).with(
                    x -> installEnabler(() -> {
                        FormsDataPane q = resolveFormsDataPane();
                        boolean copyEnabled = false;
                        if (q != null) {
                            if (q.editable().get()) {
                                copyEnabled = true;
                            }
                        }
                        x.enabled().set(copyEnabled);
                        x.visible().set(
                                !app.hideDisabled().get() || copyEnabled
                        );
                    })
            ));
        });
    }

    public void refreshView() {
//        bar.visible().set(
//                dynamicDocument != null
//                        && (dynamicDocument.getValues() == null || dynamicDocument.getValues().isEmpty())
//        );
//        this.invalidate();
//        this.revalidate();
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNote note) {
        componentList.setNote(note, frame);
        setEditable(!note.isReadOnly());
        refreshView();
    }

    public static class FormsDataItem extends BorderPane {

        private PangaeaNoteObjectComponent e;
        private ToolBar bar;
        private int pos;
        private FormsDataPane formsDataPane;

        public FormsDataItem(PangaeaNoteFrame win, FormsDataPane formsDataPane0) {
            super(win.app());
            this.formsDataPane = formsDataPane0;
            e = new PangaeaNoteObjectComponent(this, formsDataPane0.dynamicObjectTrackerAdapter, win)
                    .with(v -> v.anchor().set(Anchor.CENTER));
            bar = new ToolBar(win.app()).with(v -> v.anchor().set(Anchor.TOP));

//            bar.children().add(new Button("addToObjectList", () -> formsDataPane.onAddObjectAt(pos), app()));
            bar.children().add(new Spacer(app()).expandX());
//            bar.children().add(new Button("duplicateInObjectList", () -> formsDataPane.onDuplicateObjectAt(pos), app()));
//            bar.children().addSeparator();
//            bar.children().add(new Button("moveUpInObjectList", () -> formsDataPane.onMoveUpAt(pos), app()));
//            bar.children().add(new Button("moveDownInObjectList", () -> formsDataPane.onMoveDownAt(pos), app()));
//            bar.children().add(new Button("moveFirstInObjectList", () -> formsDataPane.onMoveFirstAt(pos), app()));
//            bar.children().add(new Button("moveLastInObjectList", () -> formsDataPane.onMoveLastAt(pos), app()));
//            bar.children().addSeparator();
//            bar.children().add(new Button("removeInObjectList", () -> formsDataPane.onRemoveObjectAt(pos), app()));
            children().add(bar);
            children().add(e);
        }

        public void onMoveUp() {
            formsDataPane.onMoveUpAt(pos);
        }

        public void onMoveDown() {
            formsDataPane.onMoveDownAt(pos);
        }

        public void onMoveFirst() {
            formsDataPane.onMoveFirstAt(pos);
        }

        public void onMoveLast() {
            formsDataPane.onMoveLastAt(pos);
        }

        public void onDuplicateObject() {
            formsDataPane.onDuplicateObjectAt(pos);
        }

        public void onRemoveObject() {
            formsDataPane.onRemoveObjectAt(pos);
        }

        public void setEditable(boolean b) {
            e.setEditable(b);
            bar.enabled().set(b);
        }

        public void setValue(PangaeaNoteObjectExt value, int pos, int size) {
            this.pos = pos;
            e.setObject(value);
        }

        public PangaeaNoteObjectExt getValue(int pos) {
            return new PangaeaNoteObjectExt(e.getObject(), formsDataPane.dynamicDocument.getDescriptor(), formsDataPane.dynamicDocument);
        }

        public void onUninstall() {
            e.uninstall();
//            stracker.unregisterAll();
        }
    }

    private static class FormsDataPane extends DataPane<PangaeaNoteObjectExt> {

        private PangaeaNote currentNote;
        private PangaeaNoteFrame frame;
        private PangaeaNoteObjectDocument dynamicDocument;
        private PangaeaNoteObjectTracker dynamicObjectTrackerAdapter = new PangaeaNoteObjectTracker() {
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

        public FormsDataPane(PangaeaNoteFrame frame) {
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
            container().get().parentConstraints().addAll(Layout.GRID, ColumnCount.of(1), ContainerGrow.TOP_ROW, AllMargins.of(3), AllGrow.HORIZONTAL, AllFill.HORIZONTAL);
        }

        private void onAddObject() {
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

        private void onRemoveAllObjects() {
            if (dynamicDocument != null) {
                String s = new Alert(frame)
                        .with((Alert a) -> {
                            a.title().set(Str.i18n("Message.warning"));
                            a.headerText().set(Str.i18n("Message.warning"));
                            a.headerIcon().set(Str.of("warning"));
                        })
                        .setContentText(Str.i18n("Message.askDeleteAllObjects"))
                        .withYesNoButtons()
                        .showDialog(null);

                if ("yes".equals(s)) {
                    dynamicDocument.getValues().clear();
                    values().clear();
                    onListValuesChangedImpl();
                }

            }
        }

        private void onRemoveObjectAt(int index) {
            if (dynamicDocument != null) {
                if (dynamicDocument.getValues() != null) {
                    String s = new Alert(frame)
                            .with((Alert a) -> {
                                a.title().set(Str.i18n("Message.warning"));
                                a.headerText().set(Str.i18n("Message.warning"));
                                a.headerIcon().set(Str.of("warning"));
                            })
                            .setContentText(Str.i18n("Message.askDeleteObject"))
                            .withYesNoButtons()
                            .showDialog(null);

                    if ("yes".equals(s)) {
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

        private void onDuplicateObjectAt(int index) {
            if (dynamicDocument != null) {
                _ensureValues();
                PangaeaNoteObject o = dynamicDocument.getValues().get(index);
                dynamicDocument.getValues().add(index + 1, o.copy());
                onListValuesChangedImpl();
            }
        }

        private boolean _onSwitchValues(int index1, int index2) {
            return OtherUtils.switchListValues(dynamicDocument.getValues(), index1, index2);
        }

        private void onMoveFirstAt(int from) {
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

        private void onMoveLastAt(int from) {
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

        private void onMoveUpAt(int index) {
            if (dynamicDocument != null) {
                _ensureValues();
                _onSwitchValues(index, index - 1);
                onListValuesChangedImpl();
            }
        }

        private void onMoveDownAt(int index) {
            if (dynamicDocument != null) {
                _ensureValues();
                _onSwitchValues(index, index + 1);
                onListValuesChangedImpl();
            }
        }

        private void onAddObjectAt(int index) {
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

    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public void setEditable(boolean b) {
        this.componentList.editable().set(b);
        this.editable = b;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

}
