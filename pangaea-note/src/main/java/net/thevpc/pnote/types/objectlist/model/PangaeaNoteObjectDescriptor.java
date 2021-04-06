/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.objectlist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author vpc
 */
public class PangaeaNoteObjectDescriptor implements Cloneable {

    private String name;
    private List<PangaeaNoteFieldDescriptor> fields;

    public String getName() {
        return name;
    }

    public PangaeaNoteObjectDescriptor setName(String name) {
        this.name = name;
        return this;
    }

    public PangaeaNoteFieldDescriptor findField(String name) {
        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                PangaeaNoteFieldDescriptor field = fields.get(i);
                if (field != null && Objects.equals(field.getName(), name)) {
                    return field;
                }
            }
        }
        return null;
    }

    public int indexOfField(String name) {
        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                PangaeaNoteFieldDescriptor field = fields.get(i);
                if (field != null && Objects.equals(field.getName(), name)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public PangaeaNoteObjectDescriptor addField(PangaeaNoteFieldDescriptor f) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        if (f != null) {
            fields.add(f);
        }
        return this;
    }

    public List<PangaeaNoteFieldDescriptor> getFields() {
        return fields;
    }

    public PangaeaNoteObjectDescriptor setFields(List<PangaeaNoteFieldDescriptor> fields) {
        this.fields = fields;
        return this;
    }

    public PangaeaNoteObject createObject() {
        PangaeaNoteObject o = new PangaeaNoteObject();
        if (this.getFields() != null) {
            for (PangaeaNoteFieldDescriptor field : this.getFields()) {
                o.addField(new PangaeaNoteField()
                        .setName(field.getName())
                        .setValue(field.getDefaultValue())
                );
            }
        }
        return o;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.fields);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PangaeaNoteObjectDescriptor other = (PangaeaNoteObjectDescriptor) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.fields, other.fields)) {
            return false;
        }
        return true;
    }

    public PangaeaNoteObjectDescriptor copy() {
        try {
            PangaeaNoteObjectDescriptor d = (PangaeaNoteObjectDescriptor) super.clone();
            if (d.fields != null) {
                d.fields = new ArrayList<>();
                for (PangaeaNoteFieldDescriptor field : fields) {
                    d.fields.add(field.copy());
                }
            }
            return d;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
