/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.model;

import net.thevpc.pnote.util.PNoteUtils;

/**
 *
 * @author vpc
 */
public class PangaeaNoteField {

    private String name;
    private String value;
    @Deprecated
    private boolean hidden;
    private PangaeaNoteFieldOptions options;

    public PangaeaNoteField() {
        options=new PangaeaNoteFieldOptions();
    }

    public PangaeaNoteField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public PangaeaNoteField setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PangaeaNoteField setValue(String value) {
        this.value = value;
        return this;
    }

    public PangaeaNoteField copy() {
        return new PangaeaNoteField().setName(name).setValue(value)
                .setOptions(options==null?null:options.copy());
    }

    public PangaeaNoteFieldOptions getOptions() {
        return options;
    }

    public PangaeaNoteField setOptions(PangaeaNoteFieldOptions options) {
        this.options = options;
        return this;
    }

    @Override
    public String toString() {
        return PNoteUtils.toEscapedName(name)
                + "=" + PNoteUtils.toEscapedValue(value);
    }
}
