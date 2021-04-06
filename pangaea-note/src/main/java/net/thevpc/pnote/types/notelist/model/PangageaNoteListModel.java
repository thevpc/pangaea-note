/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.notelist.model;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vpc
 */
public class PangageaNoteListModel {
    private Set<String> selectedNames=new HashSet<>();

    public Set<String> getSelectedNames() {
        return selectedNames;
    }

    public void setSelectedNames(Set<String> selectedNames) {
        this.selectedNames = selectedNames;
    }
    
}
