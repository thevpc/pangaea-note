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
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.util.dialog.OkCancelDialog;
import net.thevpc.pnote.model.PangaeaNoteExt;

/**
 *
 * @author vpc
 */
public class SearchDialog extends OkCancelDialog {

    private PangaeaNoteWindow sapp;
    private JLabel valueLabel;

    private JComboBox queryEditor;
    private JCheckBox matchCaseEditor;
    private JCheckBox wholeWordEditor;
    private JRadioButton literalStrategyEditor;
    private JRadioButton simpleStrategyEditor;
    private JRadioButton regexpStrategyEditor;

    private boolean ok = false;

    public SearchDialog(PangaeaNoteWindow sapp) throws HeadlessException {
        super(sapp, "Message.search");

        this.sapp = sapp;
        this.valueLabel = new JLabel(sapp.app().i18n().getString("Message.search"));
        this.matchCaseEditor = new JCheckBox(sapp.app().i18n().getString("Message.matchCase"));
        this.wholeWordEditor = new JCheckBox(sapp.app().i18n().getString("Message.wholeWord"));
        this.literalStrategyEditor = new JRadioButton(sapp.app().i18n().getString("Message.searchLiteralStrategy"));
        this.simpleStrategyEditor = new JRadioButton(sapp.app().i18n().getString("Message.searchSimpleStrategy"));
        this.regexpStrategyEditor = new JRadioButton(sapp.app().i18n().getString("Message.searchRegexpStrategy"));
        this.literalStrategyEditor.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(literalStrategyEditor);
        bg.add(simpleStrategyEditor);
        bg.add(regexpStrategyEditor);

        queryEditor = new JComboBox(new DefaultComboBoxModel(sapp.getRecentSearchQueries().toArray()));
        queryEditor.setEditable(true);
        queryEditor.setMinimumSize(new Dimension(50, 30));
        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(SearchDialog.class.getResource(
                "/net/thevpc/pnote/forms/SearchDialog.gbl-form"
        ));
        gbs.bind("label", new JLabel(sapp.app().i18n().getString("Message.searchLabel")));
        gbs.bind("textEditor", queryEditor);
        gbs.bind("matchCase", matchCaseEditor);
        gbs.bind("wholeWord", wholeWordEditor);
        gbs.bind("simpleStrategy", simpleStrategyEditor);
        gbs.bind("regexpStrategy", regexpStrategyEditor);
        gbs.bind("literalStrategy", literalStrategyEditor);

        build(gbs.apply(new JPanel()), this::ok, this::cancel);
        setPreferredSize(new Dimension(400, 250));
    }

    public void setPreembuleText(String text) {
        setTitle(text);
    }

    protected void install() {
    }

    protected void uninstall() {
    }

    protected void ok() {
        uninstall();
        this.ok = true;
        setVisible(false);
    }

    protected void cancel() {
        uninstall();
        this.ok = false;
        setVisible(false);
    }

    public void showDialogAndSearch(PangaeaNoteExt note) {
        sapp.searchInNodes(showDialog(), note);
    }

    public SearchQuery showDialog() {
        Consumer<Exception> exHandler = sapp::showError;
        while (true) {
            install();
            this.ok = false;
            pack();
            setLocationRelativeTo((JFrame) sapp.app().mainWindow().get().component());
            setVisible(true);
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
                SearchQuery q = new SearchQuery(
                        s,
                        matchCaseEditor.isSelected(),
                        wholeWordEditor.isSelected(),
                        literalStrategyEditor.isSelected() ? SearchQuery.Strategy.LITERAL
                        : simpleStrategyEditor.isSelected() ? SearchQuery.Strategy.SIMPLE
                        : regexpStrategyEditor.isSelected() ? SearchQuery.Strategy.REGEXP
                        : SearchQuery.Strategy.LITERAL
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
