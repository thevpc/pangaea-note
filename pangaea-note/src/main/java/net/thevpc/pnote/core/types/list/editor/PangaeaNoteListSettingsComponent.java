/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.constraints.*;
import net.thevpc.pnote.api.EditTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListLayout;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author thevpc
 */
public class PangaeaNoteListSettingsComponent extends GridPane implements EditTypeComponent {

    Label colsRowsLabel;
    int max = 20;
    private PangaeaNoteFrame frame;
    private RadioButtonGroup<SimpleItem> layoutCombo;
    private ComboBox<Integer> colsRows;
    private CheckBox selectableItems;
    private CheckBox showNumbers;
    private CheckBox strikeSelected;

    public PangaeaNoteListSettingsComponent(PangaeaNoteFrame frame) {
        super(2, frame.app());
        parentConstraints().addAll(AllMargins.of(3), AllFill.HORIZONTAL, AllGrow.HORIZONTAL, AllAnchors.LEFT, ContainerGrow.TOP_ROW);
        this.frame = frame;
        layoutCombo = new RadioButtonGroup<SimpleItem>(SimpleItem.class, app());
        colsRowsLabel = new Label(Str.i18n("PangaeaNoteListSettingsComponent.colsLabel"), frame.app());
        selectableItems = new CheckBox(Str.i18n("PangaeaNoteListSettingsComponent.selectableItems"), frame.app());
        showNumbers = new CheckBox(Str.i18n("PangaeaNoteListSettingsComponent.showNumbers"), frame.app());
        strikeSelected = new CheckBox(Str.i18n("PangaeaNoteListSettingsComponent.strikeSelected"), frame.app());
        colsRows = new ComboBox<Integer>(Integer.class, app());
        layoutCombo.values().addCollection(
                Arrays.stream(PangaeaNoteListLayout.values())
                        .map(x -> new SimpleItem(x.toString(), Str.i18n("PangaeaNoteListLayout." + x)))
                        .collect(Collectors.toList())
        );
        colsRows.values().addCollection(IntStream.range(1, max).boxed().collect(Collectors.toList()));
        children().addAll(
                new Label(Str.i18n("PangaeaNoteListSettingsComponent.layoutLabel"), frame.app())
                    .with(i->i.childConstraints().add(Span.col(2)))
                ,
                layoutCombo
                        .with(i->i.childConstraints().add(Span.col(2)))
                ,
                colsRowsLabel
                        .with(i->i.childConstraints().add(Grow.NONE))
                ,
                colsRows
                ,
                new HorizontalPane(app())
                        .with((HorizontalPane i)-> {
                            i.childConstraints().add(Span.col(2));
                            i.children().addAll(selectableItems,showNumbers,strikeSelected);
                        })
        );
        layoutCombo.selection().indices().set(0);
        colsRows.selection().indices().set(0);
        layoutCombo.selection().onChangeAndInit(this::onLayoutComboChange);
    }

    protected void onLayoutComboChange() {
        switch (getSelectedLayout()) {
            case VERTICAL: {
                colsRowsLabel.text().set(Str.i18n("PangaeaNoteListSettingsComponent.colsLabel"));
                colsRowsLabel.visible().set(true);
                colsRows.visible().set(true);
                break;
            }
            case HORIZONTAL: {
                colsRowsLabel.text().set(Str.i18n("PangaeaNoteListSettingsComponent.rowsLabel"));
                colsRowsLabel.visible().set(true);
                colsRows.visible().set(true);
                break;
            }
            case TAB: {
                colsRowsLabel.visible().set(false);
                colsRows.visible().set(false);
            }
        }
    }

    @Override
    public void loadFrom(PangaeaNote note) {
        PangaeaNoteListService s = (PangaeaNoteListService) frame.app().getContentTypeService(PangaeaNoteListService.LIST);
        PangaeaNoteListModel c = s.elementToContent(note.getContent());
        PangaeaNoteListLayout layout = c.getLayout() == null ? PangaeaNoteListLayout.VERTICAL : c.getLayout();
        SimpleItem toBeSelectedLayout = layoutCombo.values().stream().filter(x -> x.getId().equals(layout.toString())).findFirst().orElse(null);
        int toBeSelectedColsRows = c.getColsRows() <= 0 ? 1 : c.getColsRows() > max ? max : c.getColsRows();
        colsRows.selection().set(toBeSelectedColsRows);
        layoutCombo.selection().set(
                toBeSelectedLayout
        );
        selectableItems.selected().set(c.isSelectableItems());
        showNumbers.selected().set(c.isShowNumbers());
        strikeSelected.selected().set(c.isStrikeSelected());
    }

    @Override
    public void loadTo(PangaeaNote note) {
        PangaeaNoteListService s = (PangaeaNoteListService) frame.app().getContentTypeService(PangaeaNoteListService.LIST);
        PangaeaNoteListModel c = s.elementToContent(note.getContent());
        PangaeaNoteListLayout oldVal = c.getLayout();
        PangaeaNoteListLayout newVal = getSelectedLayout();
        boolean change = false;
        if (oldVal == null || newVal != oldVal) {
            c.setLayout(newVal);
            change = true;
        }
        int colsRowValue = ((Number) colsRows.selection().get()).intValue();
        if (newVal == PangaeaNoteListLayout.TAB) {
            colsRowValue = 1;
        }
        if (c.getColsRows() != colsRowValue) {
            c.setColsRows(colsRowValue);
            change = true;
        }
        if(c.isSelectableItems()!=selectableItems.selected().get()){
            c.setSelectableItems(selectableItems.selected().get());
            change=true;
        }
        if(c.isShowNumbers()!=showNumbers.selected().get()){
            c.setShowNumbers(showNumbers.selected().get());
            change=true;
        }
        if(c.isStrikeSelected()!=strikeSelected.selected().get()){
            c.setStrikeSelected(strikeSelected.selected().get());
            change=true;
        }
        if (change) {
            note.setContent(s.contentToElement(c));
        }
    }

    protected PangaeaNoteListLayout getSelectedLayout() {
        return PangaeaNoteListLayout.valueOf(((SimpleItem) layoutCombo.selection().get()).getId());
    }

}
