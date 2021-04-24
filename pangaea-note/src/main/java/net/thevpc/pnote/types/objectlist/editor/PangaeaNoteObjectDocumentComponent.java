/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.objectlist.editor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.util.OtherUtils;
import net.thevpc.common.swing.list.JComponentList;
import net.thevpc.pnote.types.objectlist.model.PangageaNoteObjectDocument;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteObject;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.common.swing.list.JComponentListItem;
import net.thevpc.pnote.model.HighlightType;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import net.thevpc.pnote.types.objectlist.PangaeaObjectListService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteObjectDocumentComponent extends JPanel implements PangaeaNoteEditorTypeComponent {

    private JComponentList<PangaeaNoteObjectExt> componentList;
    private PangaeaNoteExt currentNote;
    private PangageaNoteObjectDocument dynamicDocument;
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
    private JToolBar bar = new JToolBar();
    private PangaeaNoteWindow sapp;
    private SwingApplicationsUtils.Tracker gtracker;
    private boolean editable = true;
    private JButton addToObjectList;
    private boolean compactMode;

    public PangaeaNoteObjectDocumentComponent(boolean compactMode, PangaeaNoteWindow sapp) {
        super(new BorderLayout());
        this.sapp = sapp;
        this.compactMode = compactMode;
        this.gtracker = new SwingApplicationsUtils.Tracker(sapp.app());
        componentList = new JComponentList<PangaeaNoteObjectExt>(new JComponentListItem<PangaeaNoteObjectExt>() {
            @Override
            public JComponent createComponent(int pos, int size) {
                return new Item(sapp, dynamicObjectTrackerAdapter);
            }

            @Override
            public void setComponentValue(JComponent comp, PangaeaNoteObjectExt value, int pos, int size) {
                Item b = (Item) comp;
                b.setValue(value, pos, size);
            }

            @Override
            public PangaeaNoteObjectExt getComponentValue(JComponent comp, int pos) {
                return ((Item) comp).getValue(pos);
            }

            @Override
            public void uninstallComponent(JComponent comp) {
                ((Item) comp).onUninstall();
            }

            @Override
            public void setEditable(JComponent component, boolean editable, int pos, int size) {
                ((Item) component).setEditable(editable);
            }

        });
        JScrollPane scrollPane = new JScrollPane(componentList);
        scrollPane.setWheelScrollingEnabled(true);
        add(scrollPane, BorderLayout.CENTER);

        Box hb = Box.createHorizontalBox();
        hb.add(Box.createHorizontalGlue());
        hb.add(bar);
        bar.setFloatable(false);
        bar.add(addToObjectList = new JButton(
                this.gtracker.registerStandardAction(() -> onAddObject(), "addToObjectList")));
        add(hb, BorderLayout.NORTH);
        refreshView();
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    public void refreshView() {
        bar.setVisible(
                dynamicDocument != null
                && (dynamicDocument.getValues() == null || dynamicDocument.getValues().isEmpty())
        );
        this.invalidate();
        this.revalidate();
    }

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow sapp) {
        PangaeaObjectListService s = (PangaeaObjectListService) sapp.service().getContentTypeService(PangaeaObjectListService.OBJECT_LIST);
        this.currentNote = note;
        this.dynamicDocument = s.getContentAsObject(note.getContent());
        componentList.setAllObjects(createAllList());
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
            dynamicDocument.addValue(dynamicDocument.getDescriptor().createObject());
            onListValuesChangedImpl();
        }
    }

    public void onStructureChangedImpl() {
        onListValuesChangedImpl();
    }

    public void onFieldValueChangedImpl() {
        PangaeaObjectListService s = (PangaeaObjectListService) sapp.service().getContentTypeService(PangaeaObjectListService.OBJECT_LIST);
        currentNote.setContent(s.getContentAsElement(this.dynamicDocument));
        sapp.onDocumentChanged();
    }

    public void onListValuesChangedImpl() {
        PangaeaObjectListService s = (PangaeaObjectListService) sapp.service().getContentTypeService(PangaeaObjectListService.OBJECT_LIST);
        currentNote.setContent(s.getContentAsElement(this.dynamicDocument));
        sapp.onDocumentChanged();
        componentList.setAllObjects(createAllList());
        refreshView();
    }

    private void onRemoveAllObjects() {
        if (dynamicDocument != null) {
            String s = sapp.newDialog()
                    .setTitleId("Message.warning")
                    .setContentTextId("Message.askDeleteAllObjects")
                    .withYesNoButtons()
                    .build().showDialog();

            if ("yes".equals(s)) {
                dynamicDocument.getValues().clear();
                componentList.setAllObjects(new ArrayList<>());
                onListValuesChangedImpl();
            }

        }
    }

    private void onRemoveObjectAt(int index) {
        if (dynamicDocument != null) {
            if (dynamicDocument.getValues() != null) {
                String s = sapp.newDialog()
                        .setTitleId("Message.warning")
                        .setContentTextId("Message.askDeleteObject")
                        .withYesNoButtons()
                        .build().showDialog();

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
        this.addToObjectList.setEnabled(b);
        this.componentList.setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    private class Item extends JPanel {

        private PangaeaNoteObjectComponent e;
        private JToolBar bar = new JToolBar();
        private int pos;
        private JButton global1;
        private SwingApplicationsUtils.Tracker stracker;

        public Item(PangaeaNoteWindow sapp, PangaeaNoteObjectTracker tracker) {
            super(new BorderLayout());
            stracker = new SwingApplicationsUtils.Tracker(sapp.app());
            e = new PangaeaNoteObjectComponent(tracker, sapp);
            Box hb = Box.createHorizontalBox();
            hb.add(Box.createHorizontalGlue());
            hb.add(bar);
            bar.setFloatable(false);
            global1 = prepareButton(new JButton(stracker.registerStandardAction(() -> onRemoveAllObjects(), "clearObjectList")));
            bar.add(prepareButton(new JButton(stracker.registerStandardAction(() -> onAddObjectAt(pos), "addToObjectList"))));
            bar.add(prepareButton(new JButton(stracker.registerStandardAction(() -> onDuplicateObjectAt(pos), "duplicateInObjectList"))));
            bar.addSeparator();
            bar.add(prepareButton(new JButton(stracker.registerStandardAction(() -> onRemoveObjectAt(pos), "removeInObjectList"))));
            bar.addSeparator();
            bar.add(prepareButton(new JButton(stracker.registerStandardAction(() -> onMoveUpAt(pos), "moveUpInObjectList"))));
            bar.add(prepareButton(new JButton(stracker.registerStandardAction(() -> onMoveDownAt(pos), "moveDownInObjectList"))));
            bar.addSeparator();
            bar.add(global1);
            add(hb, BorderLayout.NORTH);
            add(e, BorderLayout.CENTER);
        }

        public void setEditable(boolean b) {
            e.setEditable(b);
            for (Action action : stracker.getActions()) {
                action.setEnabled(b);
            }
        }

        public JButton prepareButton(JButton b) {
            b.setHideActionText(true);
            return b;
        }

        public void setValue(PangaeaNoteObjectExt value, int pos, int size) {
            this.pos = pos;
            global1.setVisible(pos == 0);
            setBorder(BorderFactory.createTitledBorder("Element " + (pos + 1)));
            e.setObject(value);
        }

        public PangaeaNoteObjectExt getValue(int pos) {
            return new PangaeaNoteObjectExt(e.getObject(), dynamicDocument.getDescriptor(), dynamicDocument);
        }

        public void onUninstall() {
            e.uninstall();
            stracker.unregisterAll();
        }
    }

}
