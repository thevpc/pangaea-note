/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

/**
 *
 * @author vpc
 */
public interface PangaeaNoteObjectTracker {

    void onStructureChanged();

    void onListValuesChanged();
    void onFieldValueChanged();
}
