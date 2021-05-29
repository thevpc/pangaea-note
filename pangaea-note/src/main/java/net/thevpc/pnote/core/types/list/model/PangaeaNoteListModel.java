/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.list.model;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vpc
 */
public class PangaeaNoteListModel {

//    private Set<String> selectedNames = new HashSet<>();
    private PangaeaNoteListLayout layout;
    private int colsRows;
    private boolean selectableItems;
    private boolean showNumbers;
    private boolean strikeSelected;

    public boolean isSelectableItems() {
        return selectableItems;
    }

    public PangaeaNoteListModel setSelectableItems(boolean selectableItems) {
        this.selectableItems = selectableItems;
        return this;
    }

    public int getColsRows() {
        return colsRows;
    }

    public void setColsRows(int colsRows) {
        this.colsRows = colsRows;
    }

//    public Set<String> getSelectedNames() {
//        return selectedNames;
//    }
//
//    public void setSelectedNames(Set<String> selectedNames) {
//        this.selectedNames = selectedNames;
//    }

    public PangaeaNoteListLayout getLayout() {
        return layout;
    }

    public void setLayout(PangaeaNoteListLayout layout) {
        this.layout = layout;
    }

    public boolean isShowNumbers() {
        return showNumbers;
    }

    public PangaeaNoteListModel setShowNumbers(boolean showNumbers) {
        this.showNumbers = showNumbers;
        return this;
    }

    public boolean isStrikeSelected() {
        return strikeSelected;
    }

    public PangaeaNoteListModel setStrikeSelected(boolean strikeSelected) {
        this.strikeSelected = strikeSelected;
        return this;
    }
}
