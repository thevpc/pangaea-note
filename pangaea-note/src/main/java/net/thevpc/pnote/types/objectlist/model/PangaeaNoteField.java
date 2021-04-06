/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.objectlist.model;

import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class PangaeaNoteField {

    private String name;
    private String value;
    private boolean hidden;

    public PangaeaNoteField() {
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
        return new PangaeaNoteField().setName(name).setValue(value).setHidden(hidden);
    }

    @Override
    public String toString() {
        return OtherUtils.toEscapedName(name)
                + "=" + OtherUtils.toEscapedValue(value);
    }

    public boolean isHidden() {
        return hidden;
    }

    public PangaeaNoteField setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

}
