/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.model;

/**
 *
 * @author vpc
 */
public enum PangaeaNoteFieldType {
    TEXT,
    DATE,
    STARS,
    TIME,
    YEAR,
    DAY_OF_WEEK,
    MONTH,
    DATETIME,
    URL,
    PASSWORD,
    COMBOBOX,
    CHECKBOX,
    RADIOBUTTON,
    TOGGLE,
    TEXTAREA;

    public boolean isSelectMulti() {
        return this == PangaeaNoteFieldType.CHECKBOX
                || this == PangaeaNoteFieldType.TOGGLE;
    }

    public boolean isSelectOne() {
        return this == PangaeaNoteFieldType.RADIOBUTTON
                || this == PangaeaNoteFieldType.COMBOBOX
                || this == PangaeaNoteFieldType.YEAR
                || this == PangaeaNoteFieldType.MONTH
                || this == PangaeaNoteFieldType.DAY_OF_WEEK;
    }

    public boolean isFreeTextTypeAcceptingNewLine() {
        return this == TEXTAREA;
    }

    public boolean isFreeTextType() {
        switch (this) {
            case TEXT:
            case TEXTAREA:
            case PASSWORD:
            case URL:
            case DATE:
            case DATETIME:
            case TIME: {
                return true;
            }
        }
        return false;
    }

    public boolean isCustomSelect() {
        switch (this) {
            case RADIOBUTTON:
            case CHECKBOX:
            case COMBOBOX:
            case TOGGLE:
                return true;
        }
        return false;
    }

    public boolean isSelect() {
        return isSelectMulti() || isSelectOne();
    }

}
