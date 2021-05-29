/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class PangaeaNoteObjectDocument {

    private PangaeaNoteObjectDescriptor descriptor;
    private List<PangaeaNoteObject> values = new ArrayList<>();

    public PangaeaNoteObjectDescriptor getDescriptor() {
        return descriptor;
    }

    public PangaeaNoteObjectDocument setDescriptor(PangaeaNoteObjectDescriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public List<PangaeaNoteObject> getValues() {
        return values;
    }

    public PangaeaNoteObjectDocument addObject(PangaeaNoteObject value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
        return this;
    }

    public PangaeaNoteObjectDocument setValues(List<PangaeaNoteObject> values) {
        this.values = values;
        return this;
    }

    public boolean addField(PangaeaNoteFieldDescriptor descr) {
        if (descriptor == null) {
            descriptor = new PangaeaNoteObjectDescriptor();
        }
        if (descriptor.getFields() == null) {
            descriptor.setFields(new ArrayList<>());
        }
        if (descriptor.findField(descr.getName()) != null) {
            return false;
        }
        descriptor.addField(descr);
        if (values != null) {
            for (PangaeaNoteObject o : values) {
                if (o.getFields()==null || !o.getFields().stream().anyMatch(x -> Objects.equals(x.getName(), descr.getName()))) {
                    o.addField(new PangaeaNoteField().setName(descr.getName()).setValue(descr.getDefaultValue()));
                }
            }
        }
        return true;
    }

    public void removeField(String fieldName) {
        if (descriptor != null && descriptor.getFields() != null) {
            for (Iterator<PangaeaNoteFieldDescriptor> it = descriptor.getFields().iterator(); it.hasNext();) {
                PangaeaNoteFieldDescriptor field = it.next();
                if (Objects.equals(field.getName(), fieldName)) {
                    it.remove();
                }
            }
        }
        if (values != null) {
            for (PangaeaNoteObject o : values) {
                for (Iterator<PangaeaNoteField> it = o.getFields().iterator(); it.hasNext();) {
                    PangaeaNoteField field = it.next();
                    if (Objects.equals(field.getName(), fieldName)) {
                        it.remove();
                    }
                }
            }
        }
    }

    public void renameField(String from, String to) {
        if (descriptor != null && descriptor.getFields() != null) {
            for (PangaeaNoteFieldDescriptor field : descriptor.getFields()) {
                if (Objects.equals(field.getName(), from)) {
                    field.setName(to);
                }
            }
        }
        if (values != null) {
            for (PangaeaNoteObject o : values) {
                for (PangaeaNoteField field : o.getFields()) {
                    if (Objects.equals(field.getName(), from)) {
                        field.setName(to);
                    }
                }
            }
        }
    }

    public void changeContentType(String fieldName, String contentType) {
        PangaeaNoteFieldDescriptor theField = null;
        if (descriptor != null) {
            theField = descriptor.findField(fieldName);
        }
        if (theField != null) {
            theField.setContentType(contentType);
        }
    }
    
    public void changeType(String fieldName, PangaeaNoteFieldType newFieldType) {
        PangaeaNoteFieldDescriptor theField = null;
        if (descriptor != null) {
            theField = descriptor.findField(fieldName);
        }
        if (theField != null) {
            PangaeaNoteFieldType fromType = theField.getType();
            theField.setType(newFieldType);
            if (fromType == null) {
                return;
            }
            if (fromType != newFieldType) {
                if (fromType.isFreeTextType()) {
                    if (newFieldType.isSelectOne()) {
                        LinkedHashSet<String> allValues = new LinkedHashSet<>();
                        if (theField.getValues() != null) {
                            allValues.addAll(theField.getValues());
                        }
                        if (values != null) {
                            for (PangaeaNoteObject o : values) {
                                for (PangaeaNoteField field : o.getFields()) {
                                    if (Objects.equals(field.getName(), fieldName)) {
                                        String s = field.getValue();
                                        if (s == null) {
                                            s = "";
                                            field.setValue(s);
                                        }
                                        allValues.add(s);
                                    }
                                }
                            }
                        }
                        theField.setValues(new ArrayList<>(allValues));
                    } else if (newFieldType.isSelectMulti()) {
                        LinkedHashSet<String> allValues = new LinkedHashSet<>();
                        if (theField.getValues() != null) {
                            allValues.addAll(theField.getValues());
                        }
                        if (values != null) {
                            for (PangaeaNoteObject o : values) {
                                for (PangaeaNoteField field : o.getFields()) {
                                    if (Objects.equals(field.getName(), fieldName)) {
                                        String s = field.getValue();
                                        if (s == null) {
                                            s = "";
                                            field.setValue(s);
                                        }
                                        Set<String> selected = new LinkedHashSet<>();
                                        for (String nv : s.split("[\n,;]")) {
                                            nv = nv.trim();
                                            selected.add(nv);
                                        }
                                        field.setValue(String.join("\n", selected));
                                        allValues.addAll(selected);
                                    }
                                }
                            }
                        }
                        theField.setValues(new ArrayList<>(allValues));
                    }
                } else if (fromType.isSelectOne()) {
                    //do nothing, can be transofrmed to any other...
                } else if (fromType.isSelectMulti()) {
                    if (newFieldType.isFreeTextType()) {
                        if (newFieldType.isFreeTextTypeAcceptingNewLine()) {
                            //do nothing
                        } else {
                            //replace newline with ';'
                            if (values != null) {
                                for (PangaeaNoteObject o : values) {
                                    for (PangaeaNoteField field : o.getFields()) {
                                        if (Objects.equals(field.getName(), fieldName)) {
                                            String s = field.getValue();
                                            if (s == null) {
                                                s = "";
                                            }
                                            Set<String> selected = new LinkedHashSet<>();
                                            for (String nv : s.split("\n")) {
                                                nv = nv.trim();
                                                selected.add(nv);
                                            }
                                            field.setValue(String.join(";", selected));
                                        }
                                    }
                                }
                            }
                        }
                    } else if (newFieldType.isSelectOne()) {
                        //expand multiple selections and select only the first one... (non bijective)!!
                        LinkedHashSet<String> allValues = new LinkedHashSet<>();
                        if (theField.getValues() != null) {
                            allValues.addAll(theField.getValues());
                        }
                        if (values != null) {
                            for (PangaeaNoteObject o : values) {
                                for (PangaeaNoteField field : o.getFields()) {
                                    if (Objects.equals(field.getName(), fieldName)) {
                                        String s = field.getValue();
                                        if (s == null) {
                                            s = "";
                                        }
                                        Set<String> selected = new LinkedHashSet<>();
                                        for (String nv : s.split("\n")) {
                                            nv = nv.trim();
                                            selected.add(nv);
                                        }
                                        //only first is selected!
                                        if (selected.size() > 0) {
                                            field.setValue((String) selected.toArray()[0]);
                                        } else {
                                            field.setValue("");
                                        }
                                        allValues.addAll(selected);
                                    }
                                }
                            }
                        }
                        theField.setValues(new ArrayList<>(allValues));
                    }
                }
            }
        }
    }

    public void updateFieldValues(String name, String[] split) {
        Set<String> all = new TreeSet<String>();
        if (split != null) {
            for (String s : split) {
                if (s == null) {
                    s = "";
                }
                s = s.trim();
                all.add(s);
            }
        }
        if (descriptor != null) {
            PangaeaNoteFieldDescriptor d = descriptor.findField(name);
            if (d != null) {
                if (d.getType() == PangaeaNoteFieldType.COMBOBOX) {
                    if (values != null) {
                        for (PangaeaNoteObject value : values) {
                            for (PangaeaNoteField field : value.getFields()) {
                                if (Objects.equals(field.getName(), name)) {
                                    String v = field.getValue();
                                    if (v == null) {
                                        v = "";
                                    }
                                    v = v.trim();
                                    field.setValue(v);
                                    all.add(v);
                                }
                            }
                        }
                    }
                }
                d.setValues(new ArrayList<>(all));
            }

        }
    }

    public void moveFieldDown(String name) {
        int from = descriptor.indexOfField(name);
        OtherUtils.switchListValues(descriptor.getFields(), from, from + 1);
    }

    public void moveFieldUp(String name) {
        int from = descriptor.indexOfField(name);
        OtherUtils.switchListValues(descriptor.getFields(), from, from - 1);
    }
}
