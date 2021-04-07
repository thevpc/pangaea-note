/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.objectlist.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteField;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteObject;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.types.objectlist.model.PangaeaNoteFieldType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteObjectComponent extends JPanel {

    private boolean editable = true;
    private PangaeaNoteObjectExt currentValue;
    private List<PangaeaNoteFieldDescriptorPanel> components = new ArrayList<>();
    private PangaeaNoteObjectTracker objectTracker;
    private PangaeaNoteGuiApp sapp;
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

    public PangaeaNoteObjectComponent(PangaeaNoteObjectTracker objectTracker, PangaeaNoteGuiApp sapp) {
        super(new GridBagLayout());
        this.objectTracker = objectTracker;
        this.sapp = sapp;
    }

    public PangaeaNoteObject getObject() {
        PangaeaNoteObject o = new PangaeaNoteObject();
        for (PangaeaNoteFieldDescriptorPanel c : components) {
            o.addField(c.getValue());
        }
        return o;
    }

    public void setStructure(PangaeaNoteObjectDescriptor descriptor) {
        List<PangaeaNoteFieldDescriptorPanel> newComponents = new ArrayList<>();

        List<PangaeaNoteFieldDescriptor> fields = descriptor.getFields();
        fields = fields == null ? Collections.emptyList() : fields.stream().filter(x -> x != null).collect(Collectors.toList());
        for (PangaeaNoteFieldDescriptor field : fields) {
            if (field != null) {
                List<PangaeaNoteField> f = this.currentValue.getObject().findFields(field.getName());
                if (!f.stream().anyMatch(x -> x.isHidden())) {
                    int old = indexOfDescriptor(field);
                    if (old != -1) {
                        PangaeaNoteFieldDescriptorPanel r = components.remove(old);
                        if (r.getDescr().equals(field)) {
                            newComponents.add(r);//no change!
                        } else if (r.supportsUpdateDescriptor(field)) {
                            r.updateDescriptor(field);
                            newComponents.add(r);
                        } else {
                            newComponents.add(new PangaeaNoteFieldDescriptorPanel(sapp, field, dynamicObjectTrackerAdapter));
                        }
                    } else {
                        newComponents.add(new PangaeaNoteFieldDescriptorPanel(sapp, field, dynamicObjectTrackerAdapter));
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
                field.setName(sapp.app().i18n().getString("Message.title"));
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
                    newComponents.add(new PangaeaNoteFieldDescriptorPanel(sapp, field, dynamicObjectTrackerAdapter));
                }
            } else {
                newComponents.add(new PangaeaNoteFieldDescriptorPanel(sapp, field, dynamicObjectTrackerAdapter));
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
        removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1;
//        c.weighty = 0;
        c.insets = new Insets(3, 3, 3, 3);
        int row = 0;
        for (int i = 0; i < components.size(); i++) {
            PangaeaNoteFieldDescriptorPanel cad = components.get(i);
            if (cad.getDescr().getType() == PangaeaNoteFieldType.TEXTAREA) {
                c.gridy = row;
                c.gridwidth = 3;
                c.gridheight = 1;
                c.gridx = 0;
                c.weighty = 0;
                add(cad.getLabel(), c.clone());
                row++;
                c.gridy = row;
                c.gridx = 0;
                c.gridwidth = 3;
                c.gridheight = 2;
                c.weighty = 4;
                add(cad.getComponent(), c.clone());
                row += 2;
            } else {
                c.weighty = 0;
                c.gridheight = 1;
                c.gridy = row;
                c.weightx = 0;
                c.gridwidth = 1;
                c.gridx = 0;
                add(cad.getLabel(), c.clone());
                c.weightx = 3;
                c.gridwidth = 2;
                c.gridx = 1;
                add(cad.getComponent(), c.clone());
                row++;
            }
        }
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

    public void setEditable(boolean b) {
        this.editable = b;
        for (PangaeaNoteFieldDescriptorPanel component : components) {
            component.setEditable(b);
        }
    }

    public boolean isEditable() {
        return editable;
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

}
