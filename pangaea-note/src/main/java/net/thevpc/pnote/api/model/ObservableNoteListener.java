/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api.model;

/**
 *
 * @author vpc
 */
public interface ObservableNoteListener {

    void onAdded(PangaeaNoteExt child,PangaeaNoteExt parent);

    void onRemoved(PangaeaNoteExt child,PangaeaNoteExt parent);

    void onChanged(PangaeaNoteExt note,String prop, Object oval, Object nval);
}
