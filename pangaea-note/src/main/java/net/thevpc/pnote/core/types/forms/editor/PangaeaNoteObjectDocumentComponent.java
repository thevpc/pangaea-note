/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.pnote.core.special.DataPane;
import net.thevpc.pnote.core.special.DataPaneRenderer;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.util.OtherUtils;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObjectDocument;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObject;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
//import net.thevpc.echo.swing.SwingApplicationUtils;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteObjectDocumentComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    private DataPane<PangaeaNoteObjectExt> componentList;
    private PangaeaNoteExt currentNote;
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
    private ToolBar bar;
    private PangaeaNoteFrame frame;
//    private SwingApplicationUtils.Tracker gtracker;
    private boolean editable = true;
    private Button addToObjectList;
    private boolean compactMode;

    public PangaeaNoteObjectDocumentComponent(boolean compactMode, PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        this.compactMode = compactMode;
//        this.gtracker = new SwingApplicationUtils.Tracker(win.app());
        componentList = new DataPane<PangaeaNoteObjectExt>(PangaeaNoteObjectExt.class,new DataPaneRenderer<PangaeaNoteObjectExt>() {
            @Override
            public AppComponent create() {
                return new Item(frame, dynamicObjectTrackerAdapter);
            }

            @Override
            public void set(int index, PangaeaNoteObjectExt value, AppComponent component) {
                Item b = (Item) component;
                b.setValue(value, index, index);
            }

            @Override
            public PangaeaNoteObjectExt get(int index, AppComponent component) {
                return ((Item) component).getValue(index);
            }

            @Override
            public void dispose(AppComponent component) {
                ((Item) component).onUninstall();
            }
        },app());
        ScrollPane scrollPane = new ScrollPane(componentList)
                .with(v->v.anchor().set(Anchor.CENTER));
//        scrollPane.setWheelScrollingEnabled(true);
        children().add(scrollPane);
        bar=new ToolBar(app()).with(v->v.anchor().set(Anchor.TOP));
        addToObjectList = new Button("addToObjectList",() -> onAddObject(), app());
        bar.children().add(addToObjectList);
        children().add(bar);
        refreshView();
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    public void refreshView() {
        bar.visible().set(
                dynamicDocument != null
                && (dynamicDocument.getValues() == null || dynamicDocument.getValues().isEmpty())
        );
//        this.invalidate();
//        this.revalidate();
    }

    @Override
    public AppComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteFrame win) {
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) win.service().getContentTypeService(PangaeaNoteFormsService.FORMS);
        this.currentNote = note;
        this.dynamicDocument = s.getContentAsObject(note.getContent());
        componentList.values().setAll(createAllList().toArray(new PangaeaNoteObjectExt[0]));
        setEditable(!note.isReadOnly());
        refreshView();
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
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) frame.service().getContentTypeService(PangaeaNoteFormsService.FORMS);
        currentNote.setContent(s.getContentAsElement(this.dynamicDocument));
        frame.onDocumentChanged();
    }

    public void onListValuesChangedImpl() {
        PangaeaNoteFormsService s = (PangaeaNoteFormsService) frame.service().getContentTypeService(PangaeaNoteFormsService.FORMS);
        currentNote.setContent(s.getContentAsElement(this.dynamicDocument));
        frame.onDocumentChanged();
        componentList.values().setAll(createAllList().toArray(new PangaeaNoteObjectExt[0]));
        refreshView();
    }

    private void onRemoveAllObjects() {
        if (dynamicDocument != null) {
            String s = new Alert(frame.app())
                    .setTitle(Str.i18n("Message.warning"))
                    .setContentText(Str.i18n("Message.askDeleteAllObjects"))
                    .withYesNoButtons()
                    .showDialog(null);

            if ("yes".equals(s)) {
                dynamicDocument.getValues().clear();
                componentList.values().clear();
                onListValuesChangedImpl();
            }

        }
    }

    private void onRemoveObjectAt(int index) {
        if (dynamicDocument != null) {
            if (dynamicDocument.getValues() != null) {
                String s = new Alert(frame.app())
                        .setTitle(Str.i18n("Message.warning"))
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

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable = b;
        this.addToObjectList.enabled().set(b);
        this.componentList.editable().set(b);
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    private class Item extends BorderPane {

        private PangaeaNoteObjectComponent e;
        private ToolBar bar;
        private int pos;
        private Button global1;

        public Item(PangaeaNoteFrame win, PangaeaNoteObjectTracker tracker) {
            super(win.app());
            e = new PangaeaNoteObjectComponent(tracker, win)
                    .with(v->v.anchor().set(Anchor.CENTER));
            bar = new ToolBar(win.app()).with(v->v.anchor().set(Anchor.TOP));
            bar.children().add(new Button("addToObjectList",() -> onAddObjectAt(pos),app()));
            bar.children().add(new Button("duplicateInObjectList",() -> onDuplicateObjectAt(pos), app()));
            bar.children().addSeparator();
            bar.children().add(new Button("removeInObjectList",() -> onRemoveObjectAt(pos), app()));
            bar.children().addSeparator();
            bar.children().add(new Button("moveUpInObjectList",() -> onMoveUpAt(pos),app()));
            bar.children().add(new Button("moveDownInObjectList",() -> onMoveDownAt(pos),app()));
            bar.children().addSeparator();
            bar.children().add(global1 = new Button("clearObjectList",() -> onRemoveAllObjects(), app()));
            children().add(bar);
            children().add(e);
        }

        public void setEditable(boolean b) {
            e.setEditable(b);
            bar.enabled().set(b);
        }

        public JButton prepareButton(JButton b) {
            b.setHideActionText(true);
            return b;
        }

        public void setValue(PangaeaNoteObjectExt value, int pos, int size) {
            this.pos = pos;
            global1.visible().set(pos == 0);
//            setBorder(BorderFactory.createTitledBorder("Element " + (pos + 1)));
            e.setObject(value);
        }

        public PangaeaNoteObjectExt getValue(int pos) {
            return new PangaeaNoteObjectExt(e.getObject(), dynamicDocument.getDescriptor(), dynamicDocument);
        }

        public void onUninstall() {
            e.uninstall();
//            stracker.unregisterAll();
        }
    }

}
