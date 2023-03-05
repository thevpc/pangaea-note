/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.model;

import net.thevpc.nuts.util.NStringUtils;
import net.thevpc.pnote.core.types.forms.util.PangaeaNoteFormUtils;
import net.thevpc.pnote.util.PNoteUtils;

import java.util.*;

/**
 * @author thevpc
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

    public PangaeaNoteObjectDocument setValues(List<PangaeaNoteObject> values) {
        this.values = values;
        return this;
    }

    public PangaeaNoteObjectDocument addObject(PangaeaNoteObject value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
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
                if (o.getFields() == null || !o.getFields().stream().anyMatch(x -> Objects.equals(x.getName(), descr.getName()))) {
                    o.addField(new PangaeaNoteField().setName(descr.getName()).setValue(descr.getDefaultValue()));
                }
            }
        }
        return true;
    }

    public void removeField(String fieldName) {
        if (descriptor != null && descriptor.getFields() != null) {
            for (Iterator<PangaeaNoteFieldDescriptor> it = descriptor.getFields().iterator(); it.hasNext(); ) {
                PangaeaNoteFieldDescriptor field = it.next();
                if (Objects.equals(field.getName(), fieldName)) {
                    it.remove();
                }
            }
        }
        if (values != null) {
            for (PangaeaNoteObject o : values) {
                for (Iterator<PangaeaNoteField> it = o.getFields().iterator(); it.hasNext(); ) {
                    PangaeaNoteField field = it.next();
                    if (Objects.equals(field.getName(), fieldName)) {
                        it.remove();
                    }
                }
            }
        }
    }

    public void renameField(String from, String to) {
        if (from.equals(to)) {
            return;
        }
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
            theField.getOptions().setContentType(contentType);
        }
    }

    public boolean changeStructure(PangaeaNoteObjectDescriptor newDescr) {
        List<PangaeaNoteFieldDescriptor> fieldDescs = newDescr.getFields();
        if (fieldDescs.isEmpty()) {
            throw new IllegalArgumentException("missing fields");
        }
        Map<String, PangaeaNoteFieldDescriptor> fieldDescMapByName = new LinkedHashMap<>();
        Map<String, PangaeaNoteFieldDescriptor> fieldDescMapByLabel = new LinkedHashMap<>();
        for (PangaeaNoteFieldDescriptor field : newDescr.getFields()) {
            String n1 = NStringUtils.trim(field.getName());
            if (n1.isEmpty()) {
                throw new IllegalArgumentException("empty field name");
            }
            if (fieldDescMapByName.containsKey(n1)) {
                throw new IllegalArgumentException("field name clash: " + n1);
            }
            field = field.copy();
            if (field.getType() == null) {
                field.setType(PangaeaNoteFieldType.TEXT);
            }
            //this is the new name!
            field.getOptions().setLabelName(PangaeaNoteFormUtils.getFieldName(field));
            if (fieldDescMapByLabel.containsKey(field.getOptions().getLabelName())) {
                throw new IllegalArgumentException("field name clash: " + n1);
            }
            fieldDescMapByName.put(n1, field);
            fieldDescMapByLabel.put(field.getOptions().getLabelName(), field);
        }
        List<PangaeaNoteObject> newValues = new ArrayList<>();
        for (PangaeaNoteObject value : values) {
            PangaeaNoteObject value2 = new PangaeaNoteObject();
            for (PangaeaNoteField field : value.getFields()) {
                String n1 = NStringUtils.trim(field.getName());
                PangaeaNoteFieldDescriptor fdesc = fieldDescMapByName.get(n1);
                if (fdesc != null) {
                    String fn1 = PangaeaNoteFormUtils.getFieldName(fdesc);
                    field = field.copy();
                    field.setName(fn1);
                    //reset label if it matches the new field desc name
                    if (NStringUtils.trim(field.getOptions().getLabelName()).equals(fn1)) {
                        field.getOptions().setLabelName(null);
                    }
                    if (!PangaeaNoteFormUtils.isBlank(field)) {
                        PangaeaNoteFieldDescriptor oldFieldDescriptor = descriptor.findField(n1);
                        PangaeaNoteFieldType oldType = oldFieldDescriptor.getType();
                        PangaeaNoteFieldType newType = fdesc.getType();
                        if (oldType == null) {
                            oldType = newType;
                        }
                        if (oldType != newType) {
                            PangaeaNoteFormUtils.changeFieldType(field, oldType, newType, fdesc.getValues());
                        }
                        value2.addField(field);
                    }
                } else {
                    //field is removed!
                }
            }
            newValues.add(value2);
        }

        //fix new names
        for (PangaeaNoteFieldDescriptor value : fieldDescMapByName.values()) {
            value.setName(value.getOptions().getLabelName());
            value.getOptions().setLabelName(null);
        }
        PangaeaNoteObjectDescriptor newDescriptor = new PangaeaNoteObjectDescriptor().setName(NStringUtils.trim(newDescr.getName()))
                .addFields(fieldDescMapByName.values().toArray(new PangaeaNoteFieldDescriptor[0]));

        //
        setDescriptor(newDescriptor);
        setValues(newValues);
        return true;
    }

    public boolean changeField(String fieldName, PangaeaNoteFieldDescriptor newDescr) {
        PangaeaNoteFieldDescriptor oldDescr = null;
        if (descriptor != null) {
            oldDescr = descriptor.findField(fieldName);
        }
        if (oldDescr != null) {
            if (newDescr != null) {
                if (!Objects.equals(newDescr.getName(), oldDescr.getName())) {
                    renameField(oldDescr.getName(), newDescr.getName());
                }
                PangaeaNoteFieldOptions newOptions = newDescr.getOptions();
                if (newOptions == null) {
                    throw new IllegalArgumentException("missing options");
                }
                if (!Objects.equals(newOptions.getContentType(), oldDescr.getOptions().getContentType())) {
                    changeContentType(oldDescr.getName(), oldDescr.getOptions().getContentType());
                }
                if (!Objects.equals(newDescr.getType(), oldDescr.getType())) {
                    changeType(oldDescr.getName(), newDescr.getType());
                }
                oldDescr.setOptions(newOptions);
                return true;
            }

        }
        return false;
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
        PNoteUtils.switchListValues(descriptor.getFields(), from, from + 1);
    }

    public void moveFieldUp(String name) {
        int from = descriptor.indexOfField(name);
        PNoteUtils.switchListValues(descriptor.getFields(), from, from - 1);
    }
}
