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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import net.thevpc.common.swing.NamedValue;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListLayout;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.dialogs.EditNoteDialog;
import net.thevpc.pnote.api.EditTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public class PangaeaNoteListSettingsComponent extends JPanel implements EditTypeComponent {

    private PangaeaNoteWindow win;
    private DefaultComboBoxModel layoutComboModel;
    private JComboBox layoutCombo;
    private JSpinner colsRows;
    JLabel colsRowsLabel = new JLabel();

    public PangaeaNoteListSettingsComponent(PangaeaNoteWindow win) {
        this.win = win;
        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EditNoteDialog.class.getResource(
                "/net/thevpc/pnote/forms/PangaeaNoteListSettingsComponent.gbl-form"
        ));
        layoutComboModel = new DefaultComboBoxModel<>();
        for (PangaeaNoteListLayout value : PangaeaNoteListLayout.values()) {
            layoutComboModel.addElement(new NamedValue(value.toString(), win.i18n().getString("PangaeaNoteListLayout." + value.toString())));
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
        gbs.bind("layoutLabel", new JLabel(win.app().i18n().getString("PangaeaNoteListSettingsComponent.layoutLabel")));
        gbs.bind("layoutComponent", layoutCombo);
        gbs.bind("colsRowsLabel", colsRowsLabel);
        gbs.bind("colsRows", colsRows);
        gbs.apply(this);
        onLayoutComboChange();
    }

    protected void onLayoutComboChange() {
        switch (getSelectedLayout()) {
            case VERTICAL: {
                colsRowsLabel.setText(win.app().i18n().getString("PangaeaNoteListSettingsComponent.colsLabel"));
                colsRowsLabel.setVisible(true);
                colsRows.setVisible(true);
                break;
            }
            case HORIZONTAL: {
                colsRowsLabel.setText(win.app().i18n().getString("PangaeaNoteListSettingsComponent.rowsLabel"));
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
    public JComponent component() {
        return this;
    }

    @Override
    public void loadFrom(PangaeaNote note) {
        PangaeaNoteListService s = (PangaeaNoteListService) win.service().getContentTypeService(PangaeaNoteListService.LIST);
        PangaeaNoteListModel c = s.elementToContent(note.getContent());
        PangaeaNoteListLayout li = c.getLayout();
        if (li == null) {
            li = PangaeaNoteListLayout.VERTICAL;
        }
        colsRows.setValue(c.getColsRows() <= 0 ? 1 : c.getColsRows());
        for (int i = 0; i < layoutComboModel.getSize(); i++) {
            NamedValue o = (NamedValue) layoutComboModel.getElementAt(i);
            if (o.getId().equals(li.toString())) {
                layoutCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void loadTo(PangaeaNote note) {
        PangaeaNoteListService s = (PangaeaNoteListService) win.service().getContentTypeService(PangaeaNoteListService.LIST);
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
        return PangaeaNoteListLayout.valueOf(((NamedValue) layoutCombo.getSelectedItem()).getId());
    }

}
