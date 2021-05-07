/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.search;

import net.thevpc.pnote.service.search.SearchQuery;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.thevpc.common.swing.NamedValue;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.pnote.gui.PangaeaNoteWindow;

/**
 *
 * @author vpc
 */
public class SearchDialog {

    private JPanel panel;
    private PangaeaNoteWindow win;
    private JLabel matchModeLabel;

    private JComboBox queryEditor;
    private JCheckBox matchCaseEditor;
    private JCheckBox wholeWordEditor;
    private JComboBox modeEditor;
//    private JRadioButton literalStrategyEditor;
//    private JRadioButton simpleStrategyEditor;
//    private JRadioButton regexpStrategyEditor;

    private boolean ok = false;
    private String titleId = "Message.search";
    private Object[] params = {};

    public SearchDialog(PangaeaNoteWindow win) throws HeadlessException {
        this.win = win;
        this.matchCaseEditor = new JCheckBox(win.app().i18n().getString("Message.matchCase"));
        this.wholeWordEditor = new JCheckBox(win.app().i18n().getString("Message.wholeWord"));
        this.matchModeLabel = new JLabel(win.app().i18n().getString("Message.match"));
        DefaultComboBoxModel<Object> cm = new DefaultComboBoxModel<>();
        cm.addElement(new NamedValue(SearchQuery.Strategy.LITERAL.toString(), win.app().i18n().getString("Message.searchLiteralStrategy")));
        cm.addElement(new NamedValue(SearchQuery.Strategy.SIMPLE.toString(), win.app().i18n().getString("Message.searchSimpleStrategy")));
        cm.addElement(new NamedValue(SearchQuery.Strategy.REGEXP.toString(), win.app().i18n().getString("Message.searchRegexpStrategy")));

        modeEditor = new JComboBox<Object>(cm);
        modeEditor.setSelectedIndex(0);
//        this.literalStrategyEditor = new JRadioButton(win.app().i18n().getString("Message.searchLiteralStrategy"));
//        this.simpleStrategyEditor = new JRadioButton(win.app().i18n().getString("Message.searchSimpleStrategy"));
//        this.regexpStrategyEditor = new JRadioButton(win.app().i18n().getString("Message.searchRegexpStrategy"));
//        this.literalStrategyEditor.setSelected(true);
//        ButtonGroup bg = new ButtonGroup();
//        bg.add(literalStrategyEditor);
//        bg.add(simpleStrategyEditor);
//        bg.add(regexpStrategyEditor);

        queryEditor = new JComboBox(new DefaultComboBoxModel(win.getRecentSearchQueries().toArray()));
        queryEditor.setEditable(true);
        queryEditor.setMinimumSize(new Dimension(50, 30));
        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(SearchDialog.class.getResource(
                "/net/thevpc/pnote/forms/SearchDialog.gbl-form"
        ));
        gbs.bind("label", new JLabel(win.app().i18n().getString("Message.searchLabel")));
        gbs.bind("textEditor", queryEditor);
        gbs.bind("matchCase", matchCaseEditor);
        gbs.bind("wholeWord", wholeWordEditor);
        gbs.bind("matchModeLabel", matchModeLabel);
        gbs.bind("matchMode", modeEditor);
        
        
//        gbs.bind("simpleStrategy", simpleStrategyEditor);
//        gbs.bind("regexpStrategy", regexpStrategyEditor);
//        gbs.bind("literalStrategy", literalStrategyEditor);

        panel = (gbs.apply(new JPanel()));
//        setPreferredSize(new Dimension(400, 250));
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

    public String getTitleId() {
        return titleId;
    }

    public SearchDialog setTitleId(String titleId, Object... params) {
        this.titleId = titleId;
        this.params = params;
        return this;
    }

    public SearchQuery showDialog() {
        Consumer<Exception> exHandler = win::showError;
        while (true) {
            install();
            this.ok = false;
            win.app().newDialog()
                    .setTitleId(titleId, params)
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
                    .showDialog();
            try {
                return get();
            } catch (Exception ex) {
                exHandler.accept(ex);
            }
        }
    }

    public SearchQuery get() {
        if (ok) {
            String s = (String) queryEditor.getSelectedItem();
            if (s != null && s.length() > 0) {
                SearchQuery.Strategy mm = SearchQuery.Strategy.valueOf((String) modeEditor.getSelectedItem());
                SearchQuery q = new SearchQuery(
                        s,
                        matchCaseEditor.isSelected(),
                        wholeWordEditor.isSelected(),
                        mm
                );
                return q;
            }
        }
        return null;
    }

    public void setSearchText(String selectedText) {
        if (selectedText == null) {
            selectedText = "";
        }
        queryEditor.setSelectedItem(selectedText);
    }

}
