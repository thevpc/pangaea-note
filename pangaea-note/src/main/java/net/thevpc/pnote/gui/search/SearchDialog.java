/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.search;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.thevpc.common.swing.GridBagLayoutSupport;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.dialog.OkCancelDialog;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.search.PangaeaNoteExtSearchResult;

/**
 *
 * @author vpc
 */
public class SearchDialog extends OkCancelDialog {

    private PangaeaNoteGuiApp sapp;
    private JLabel valueLabel;

    private JComboBox queryEditor;
    private JCheckBox caseEditor;

    private boolean ok = false;

    public SearchDialog(PangaeaNoteGuiApp sapp) throws HeadlessException {
        super(sapp,"Message.search");

        this.sapp = sapp;
        this.valueLabel = new JLabel(sapp.app().i18n().getString("Message.search"));
        this.caseEditor = new JCheckBox(sapp.app().i18n().getString("Message.caseSensitive"));
        queryEditor = new JComboBox(new DefaultComboBoxModel(sapp.getRecentSearchQueries().toArray()));
        queryEditor.setEditable(true);
        queryEditor.setMinimumSize(new Dimension(50, 30));
        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(SearchDialog.class.getResource(
                "/net/thevpc/pnote/forms/SearchDialog.gbl-form"
        ));
        gbs.bind("label", new JLabel(sapp.app().i18n().getString("Message.searchLabel")));
        gbs.bind("textEditor", queryEditor);
        gbs.bind("case", caseEditor);
        
        build(gbs.apply(new JPanel()), this::ok, this::cancel);
        setPreferredSize(new Dimension(400,200));
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
        String s = showDialog();
        if (s != null && s.length() > 0) {
            new Thread(() -> {
                SearchResultPanel.SearchResultPanelItem resultPanel = sapp.searchResultsTool().createNewPanel();
                sapp.searchResultsTool().showResults();
                resultPanel.resetResults();
                resultPanel.setSearching(true);
                sapp.addRecentSearchQuery(s);
                PangaeaNoteExtSearchResult e = sapp.service().search(note, s);
                e.stream().forEach(x -> {
                    resultPanel.appendResult(x);
                });
                resultPanel.setSearching(false);
                sapp.searchResultsTool().showResults();
            }).start();
        }
    }

    public String showDialog() {
        Consumer<Exception> exHandler=sapp::showError;
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

    public String get() {
        if (ok) {
            return (String)queryEditor.getSelectedItem();
        }
        return null;
    }

}
