/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteObject {

    private List<PangaeaNoteField> fields;

    public PangaeaNoteField findField(String name) {
        return findFields(name).stream().findFirst().orElse(null);
    }

    public List<PangaeaNoteField> findFields(String name) {
        return fields==null?null:fields.stream().filter(x->x!=null && Objects.equals(name, x.getName())).collect(Collectors.toList());
    }
    
    public List<PangaeaNoteField> getFields() {
        return fields;
    }

    public PangaeaNoteObject addField(PangaeaNoteField field) {
        if (this.fields == null) {
            this.fields = new java.util.ArrayList<>();
        }
        this.fields.add(field);
        return this;
    }

    public PangaeaNoteObject setFields(List<PangaeaNoteField> fields) {
        this.fields = fields;
        return this;
    }

    public PangaeaNoteObject copy() {
        PangaeaNoteObject d = new PangaeaNoteObject();
        d.setFields(new ArrayList<>());
        if (fields != null) {
            for (PangaeaNoteField field : fields) {
                d.getFields().add(field.copy());
            }
        }
        return d;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.fields);
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
        final PangaeaNoteObject other = (PangaeaNoteObject) obj;
        if (!Objects.equals(this.fields, other.fields)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        
        return "{" + 
                (fields ==null?"":fields.stream().map(x->String.valueOf(x)).collect(Collectors.joining(", ")))
                + '}';
    }

}
