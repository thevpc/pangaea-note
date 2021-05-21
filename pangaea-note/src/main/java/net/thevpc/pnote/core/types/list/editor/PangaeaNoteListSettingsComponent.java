/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.list.editor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.thevpc.echo.GridPane;
import net.thevpc.echo.Panel;
import net.thevpc.echo.SimpleItem;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListLayout;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.api.EditTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public class PangaeaNoteListSettingsComponent extends GridPane implements EditTypeComponent {

    private PangaeaNoteFrame frame;
    private DefaultComboBoxModel layoutComboModel;
    private JComboBox layoutCombo;
    private JSpinner colsRows;
    JLabel colsRowsLabel = new JLabel();

    public PangaeaNoteListSettingsComponent(PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
//        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EditNoteDialog.class.getResource(
//                "/net/thevpc/pnote/forms/PangaeaNoteListSettingsComponent.gbl-form"
//        ));
        layoutComboModel = new DefaultComboBoxModel<>();
        for (PangaeaNoteListLayout value : PangaeaNoteListLayout.values()) {
            layoutComboModel.addElement(new SimpleItem(value.toString(), frame.i18n().getString("PangaeaNoteListLayout." + value.toString())));
        }
        layoutCombo = new JComboBox<>(layoutComboModel);
        layoutCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onLayoutComboChange();
            }
        });
        layoutCombo.setSelectedIndex(0);
        colsRows = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
//        gbs.bind("layoutLabel", new JLabel(win.app().i18n().getString("PangaeaNoteListSettingsComponent.layoutLabel")));
//        gbs.bind("layoutComponent", layoutCombo);
//        gbs.bind("colsRowsLabel", colsRowsLabel);
//        gbs.bind("colsRows", colsRows);
//        gbs.apply(this);
        onLayoutComboChange();
    }

    protected void onLayoutComboChange() {
        switch (getSelectedLayout()) {
            case VERTICAL: {
                colsRowsLabel.setText(frame.app().i18n().getString("PangaeaNoteListSettingsComponent.colsLabel"));
                colsRowsLabel.setVisible(true);
                colsRows.setVisible(true);
                break;
            }
            case HORIZONTAL: {
                colsRowsLabel.setText(frame.app().i18n().getString("PangaeaNoteListSettingsComponent.rowsLabel"));
                colsRowsLabel.setVisible(true);
                colsRows.setVisible(true);
                break;
            }
            case TAB: {
                colsRowsLabel.setVisible(false);
                colsRows.setVisible(false);
            }
        }
    }

    @Override
    public AppComponent component() {
        return this;
    }

    @Override
    public void loadFrom(PangaeaNote note) {
        PangaeaNoteListService s = (PangaeaNoteListService) frame.service().getContentTypeService(PangaeaNoteListService.LIST);
        PangaeaNoteListModel c = s.elementToContent(note.getContent());
        PangaeaNoteListLayout li = c.getLayout();
        if (li == null) {
            li = PangaeaNoteListLayout.VERTICAL;
        }
        colsRows.setValue(c.getColsRows() <= 0 ? 1 : c.getColsRows());
        for (int i = 0; i < layoutComboModel.getSize(); i++) {
            SimpleItem o = (SimpleItem) layoutComboModel.getElementAt(i);
            if (o.getId().equals(li.toString())) {
                layoutCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void loadTo(PangaeaNote note) {
        PangaeaNoteListService s = (PangaeaNoteListService) frame.service().getContentTypeService(PangaeaNoteListService.LIST);
        PangaeaNoteListModel c = s.elementToContent(note.getContent());
        PangaeaNoteListLayout oldVal = c.getLayout();
        PangaeaNoteListLayout newVal = getSelectedLayout();
        boolean change = false;
        if (oldVal == null || newVal != oldVal) {
            c.setLayout(newVal);
            change = true;
        }
        int colsRowValue = ((Number) colsRows.getValue()).intValue();
        if (newVal == PangaeaNoteListLayout.TAB) {
            colsRowValue = 1;
        }
        if (c.getColsRows() != colsRowValue) {
            c.setColsRows(colsRowValue);
            change = true;
        }
        if (change) {
            note.setContent(s.contentToElement(c));
        }
    }

    protected PangaeaNoteListLayout getSelectedLayout() {
        return PangaeaNoteListLayout.valueOf(((SimpleItem) layoutCombo.getSelectedItem()).getId());
    }

}
