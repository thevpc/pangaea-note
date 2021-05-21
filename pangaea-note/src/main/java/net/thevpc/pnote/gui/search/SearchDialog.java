/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.search;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.pnote.service.search.SearchQuery;

import java.awt.HeadlessException;
//import net.thevpc.common.swing.NamedValue;
//import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

/**
 *
 * @author vpc
 */
public class SearchDialog {

    private Panel panel;
    private PangaeaNoteFrame frame;
    private Label matchModeLabel;

    private ComboBox queryEditor;
    private CheckBox matchCaseEditor;
    private CheckBox wholeWordEditor;
    private ComboBox modeEditor;

    private boolean ok = false;
    private Str titleId = Str.i18n("Message.search");
    private Object[] params = {};

    public SearchDialog(PangaeaNoteFrame frame) throws HeadlessException {
        this.frame = frame;
        this.matchCaseEditor = new CheckBox(Str.i18n("Message.matchCase"),frame.app());
        this.wholeWordEditor = new CheckBox(Str.i18n("Message.wholeWord"),frame.app());
        this.matchModeLabel = new Label(Str.i18n("Message.match"),frame.app());
        ComboBox<SimpleItem> modeEditor=new ComboBox<>(SimpleItem.class, frame.app());
        modeEditor.values().add(new SimpleItem(SearchQuery.Strategy.LITERAL.toString(), frame.app().i18n().getString("Message.searchLiteralStrategy")));
        modeEditor.values().add(new SimpleItem(SearchQuery.Strategy.SIMPLE.toString(), frame.app().i18n().getString("Message.searchSimpleStrategy")));
        modeEditor.values().add(new SimpleItem(SearchQuery.Strategy.REGEXP.toString(), frame.app().i18n().getString("Message.searchRegexpStrategy")));

        modeEditor.selection().indices().set(0);

        queryEditor = new ComboBox<String>(String.class, frame.app());
        queryEditor.values().setAll(
                frame.getRecentSearchQueries().toArray(new String[0])
        );
        queryEditor.editable().set(true);
//        queryEditor.setMinimumSize(new Dimension(50, 30));
        panel=new VerticalPane(frame.app());
        panel.children().addAll(
                new Label(Str.i18n("Message.searchLabel"),frame.app()),
                queryEditor,
                matchCaseEditor,
                wholeWordEditor,
                matchModeLabel,
                modeEditor
        );
    }

    protected void install() {
    }

    protected void uninstall() {
    }

    protected void ok() {
        uninstall();
        this.ok = true;
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
    }

    public Str getTitleId() {
        return titleId;
    }

    public SearchDialog setTitle(Str titleId, Object... params) {
        this.titleId = titleId;
        this.params = params;
        return this;
    }

    public SearchQuery showDialog() {
        while (true) {
            install();
            this.ok = false;
            new Alert(frame.app())
                    .setTitle(titleId, params)
                    .setContent(panel)
                    .withOkCancelButtons(
                            (a) -> {
                                ok();
                                a.getDialog().closeDialog();
                            },
                            (a) -> {
                                cancel();
                                a.getDialog().closeDialog();
                            }
                    )
                    .showDialog(null);
            try {
                return get();
            } catch (Exception ex) {
                frame.app().errors().add(ex);
            }
        }
    }

    public SearchQuery get() {
        if (ok) {
//            String s = (String) queryEditor.getSelectedItem();
//            if (s != null && s.length() > 0) {
//                SearchQuery.Strategy mm = SearchQuery.Strategy.valueOf((String) modeEditor.getSelectedItem());
//                SearchQuery q = new SearchQuery(
//                        s,
//                        matchCaseEditor.isSelected(),
//                        wholeWordEditor.isSelected(),
//                        mm
//                );
//                return q;
//            }
        }
        return null;
    }

    public void setSearchText(String selectedText) {
        if (selectedText == null) {
            selectedText = "";
        }
//        queryEditor.setSelectedItem(selectedText);
    }

}
