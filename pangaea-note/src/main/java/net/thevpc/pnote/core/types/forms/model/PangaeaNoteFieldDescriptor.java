/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.thevpc.pnote.util.PNoteUtils;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteFieldDescriptor implements Cloneable {

    private String name;
    private String defaultValue;
    private PangaeaNoteFieldType type;
    private List<String> values;
    private PangaeaNoteFieldOptions options;

    @Deprecated
    private String contentType;
    @Deprecated
    private String pattern;

    public PangaeaNoteFieldDescriptor() {
        options=new PangaeaNoteFieldOptions();
    }

    public PangaeaNoteFieldOptions getOptions() {
        return options;
    }

    public PangaeaNoteFieldDescriptor setOptions(PangaeaNoteFieldOptions options) {
        this.options = options;
        return this;
    }

    public String getName() {
        return name;
    }

    public PangaeaNoteFieldDescriptor setName(String name) {
        this.name = name;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public PangaeaNoteFieldDescriptor setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public PangaeaNoteFieldType getType() {
        return type;
    }

    public PangaeaNoteFieldDescriptor setType(PangaeaNoteFieldType type) {
        this.type = type;
        return this;
    }

    public List<String> getValues() {
        return values;
    }

    public PangaeaNoteFieldDescriptor addValue(String value) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        if (value == null) {
            value = "";
        }
        values.add(value);
        return this;
    }

    public PangaeaNoteFieldDescriptor setValues(List<String> values) {
        this.values = values;
        return this;
    }

    public PangaeaNoteFieldDescriptor copy() {
        try {
            PangaeaNoteFieldDescriptor d = (PangaeaNoteFieldDescriptor) super.clone();
            if (d.values != null) {
                d.values = new ArrayList<>(d.values);
            }
            if(d.options!=null){
                d.options=d.options.copy();
            }
            return d;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.defaultValue);
        hash = 29 * hash + Objects.hashCode(this.type);
        hash = 29 * hash + Objects.hashCode(this.values);
        hash = 29 * hash + Objects.hashCode(this.options);
//        hash = 29 * hash + Objects.hashCode(this.pattern);
//        hash = 29 * hash + Objects.hashCode(this.contentType);
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
        final PangaeaNoteFieldDescriptor other = (PangaeaNoteFieldDescriptor) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.defaultValue, other.defaultValue)) {
            return false;
        }
//        if (!Objects.equals(this.pattern, other.pattern)) {
//            return false;
//        }
//        if (!Objects.equals(this.contentType, other.contentType)) {
//            return false;
//        }
        if (!Objects.equals(this.options, other.options)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.values, other.values)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return PNoteUtils.toEscapedName(name) + ":" + (type == null ? "<null>" : type.toString().toLowerCase());
    }


}
