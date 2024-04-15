/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.echo.Dimension;
import net.thevpc.echo.GridPane;
import net.thevpc.echo.ScrollPane;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.model.*;
import net.thevpc.pnote.util.PNoteUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author thevpc
 */
public class PangaeaNoteObjectComponent extends GridPane {

    private boolean editable = true;
    private AppComponent prefixComponent;
    private PangaeaNoteObjectExt currentValue;
    private List<PangaeaNoteFieldDescriptorPanel> components = new ArrayList<>();
    private PangaeaNoteObjectTracker objectTracker;
    private FormsDataItem parentDataItem;
    private PangaeaNoteFrame frame;
    private PangaeaNoteObjectTracker dynamicObjectTrackerAdapter = new PangaeaNoteObjectTracker() {
        @Override
        public void onStructureChanged() {
            if (objectTracker != null) {
                objectTracker.onStructureChanged();
            }
        }

        @Override
        public void onListValuesChanged() {
            if (objectTracker != null) {
                objectTracker.onListValuesChanged();
            }
        }

        @Override
        public void onFieldValueChanged() {
            if (objectTracker != null) {
                objectTracker.onFieldValueChanged();
            }
        }

    };

    public PangaeaNoteObjectComponent(FormsDataItem parent, PangaeaNoteObjectTracker objectTracker, PangaeaNoteFrame frame) {
        super(frame.app());
        this.objectTracker = objectTracker;
        this.parentDataItem = parent;
        this.frame = frame;
        parentConstraints().addAll(ContainerGrow.TOP_ROW, AllMargins.of(3), AllGrow.HORIZONTAL, AllFill.HORIZONTAL, AllAnchors.LEFT);
    }

    public PangaeaNoteObject getObject() {
        PangaeaNoteObject o = new PangaeaNoteObject();
        for (PangaeaNoteFieldDescriptorPanel c : components) {
            o.addField(c.getValue());
        }
        return o;
    }

    public void setObject(PangaeaNoteObjectExt value) {
        this.currentValue = value;
        setStructure(value.getDescriptor());
        Map<String, PangaeaNoteField> map = new HashMap<String, PangaeaNoteField>();
        if (value.getObject().getFields() != null) {
            for (PangaeaNoteField field : value.getObject().getFields()) {
                map.put(field.getName(), field);
            }
        }
        for (PangaeaNoteFieldDescriptorPanel component : components) {
            PangaeaNoteField v = map.get(component.getDescr().getName());
            if (v == null) {
                v = new PangaeaNoteField(component.getDescr().getName(), "");
                value.getObject().addField(v);
            }
            if (v.getValue() == null) {
                v.setValue("");
            }
            component.setValue(v, currentValue.getObject(), currentValue.getDocument());
            component.setEditable(isEditable());
        }
    }

    public void setStructure(PangaeaNoteObjectDescriptor descriptor) {
        List<PangaeaNoteFieldDescriptorPanel> newComponents = new ArrayList<>();

        List<PangaeaNoteFieldDescriptor> fields = descriptor.getFields();
        fields = fields == null ? Collections.emptyList() : fields.stream().filter(x -> x != null).collect(Collectors.toList());
        for (PangaeaNoteFieldDescriptor field : fields) {
            if (field != null) {
                List<PangaeaNoteField> f = this.currentValue.getObject().findFields(field.getName());
                if (!f.stream().anyMatch(x -> PNoteUtils.nonNullAndTrue(x.getOptions().getHidden()))) {
                    int old = indexOfDescriptor(field);
                    if (old != -1) {
                        PangaeaNoteFieldDescriptorPanel r = components.remove(old);
                        if (r.getDescr().equals(field)) {
                            newComponents.add(r);//no change!
                        } else if (r.supportsUpdateDescriptor(field)) {
                            r.updateDescriptor(field);
                            newComponents.add(r);
                        } else {
                            newComponents.add(new PangaeaNoteFieldDescriptorPanel(frame, this, field, dynamicObjectTrackerAdapter));
                        }
                    } else {
                        newComponents.add(new PangaeaNoteFieldDescriptorPanel(frame, this, field, dynamicObjectTrackerAdapter));
                    }
                } else {

                }
            }
        }

        if (newComponents.isEmpty()) {
            PangaeaNoteFieldDescriptor field = null;
            if (fields.size() > 0) {
                //if all are hidden then unhide the very first
                field = fields.get(0);
            } else {
                //if there are no fields, add a new field descriptor and create the corresponding component
                field = new PangaeaNoteFieldDescriptor();
                field.setName(frame.app().i18n().getString("Message.title"));
                field.setType(PangaeaNoteFieldType.TEXT);
                this.currentValue.getDocument().addField(field);
            }
            int old = indexOfDescriptor(field);
            if (old != -1) {
                PangaeaNoteFieldDescriptorPanel r = components.remove(old);
                if (r.getDescr().equals(field)) {
                    newComponents.add(r);//no change!
                } else if (r.supportsUpdateDescriptor(field)) {
                    r.updateDescriptor(field);
                    newComponents.add(r);
                } else {
                    newComponents.add(new PangaeaNoteFieldDescriptorPanel(frame, this, field, dynamicObjectTrackerAdapter));
                }
            } else {
                newComponents.add(new PangaeaNoteFieldDescriptorPanel(frame, this, field, dynamicObjectTrackerAdapter));
            }
        }

        while (!components.isEmpty()) {
            PangaeaNoteFieldDescriptorPanel a = components.remove(0);
            a.uninstall();
        }

        components.addAll(newComponents);
        relayoutObject();
    }

    public void relayoutObject() {
        children().clear();
        int row = 0;
        for (int i = 0; i < components.size(); i++) {
            PangaeaNoteFieldDescriptorPanel cad = components.get(i);
            if (cad.getFormComponent().isLargeComponent()) {
                int row0 = row;
                children().add(
                        cad.getLabel()
                                .with(t -> {
                                    t.childConstraints().addAll(Pos.of(0, row0), Fill.NONE, Grow.NONE, Span.col(2));
                                })
                );
                row++;
                int row1 = row;
                children().add(new ScrollPane(cad.getComponent())
                        .with(t -> {
                                    t.prefSize().set(new Dimension(400, 200));
                                    t.childConstraints().addAll(Pos.of(0, row1), Fill.BOTH, Grow.BOTH, Span.of(2, 2));
                                }
                        ));
                row += 2;
            } else {
                int row0 = row;
                children().add(
                        cad.getLabel()
                                .with(t -> {
                                    t.childConstraints().addAll(Pos.of(0, row0), Fill.NONE, Grow.NONE);
                                })
                );
                children().add(cad.getComponent()
                        .with(t -> {
                                    t.childConstraints().addAll(Pos.of(1, row0), Fill.HORIZONTAL, Grow.HORIZONTAL);
                                }
                        ));
                row++;
            }
            cad.applyFormats();
        }
    }

    public PangaeaNoteField getFieldValue(String field) {
        PangaeaNoteObjectExt cv = getCurrentValue();
        if (cv != null) {
            for (PangaeaNoteField pangaeaNoteField : cv.getObject().findFields(field)) {
                return pangaeaNoteField;
            }
        }
        return null;
    }

    public PangaeaNoteObjectExt getCurrentValue() {
        return currentValue;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean b) {
        this.editable = b;
        for (PangaeaNoteFieldDescriptorPanel component : components) {
            component.setEditable(b);
        }
    }

    public int indexOfDescriptor(PangaeaNoteFieldDescriptor d) {
        for (int i = 0; i < components.size(); i++) {
            PangaeaNoteFieldDescriptorPanel component = components.get(i);
            if (component.getDescr().equals(d)) {
                return i;
            }
        }
        return -1;
    }

    public void uninstall() {
        while (!components.isEmpty()) {
            PangaeaNoteFieldDescriptorPanel a = components.remove(0);
            a.uninstall();
        }
    }

    public FormsDataItem getParentDataItem() {
        return parentDataItem;
    }

    public AppComponent getPrefixComponent() {
        return prefixComponent;
    }

    public PangaeaNoteObjectComponent setPrefixComponent(AppComponent prefixComponent) {
        this.prefixComponent = prefixComponent;
        return this;
    }
}
