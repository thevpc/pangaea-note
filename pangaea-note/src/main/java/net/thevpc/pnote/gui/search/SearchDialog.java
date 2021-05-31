/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.search;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Label;
import net.thevpc.echo.Panel;
import net.thevpc.echo.*;
import net.thevpc.echo.constraints.AllAnchors;
import net.thevpc.echo.constraints.AllFill;
import net.thevpc.echo.constraints.AllMargins;
import net.thevpc.echo.constraints.AllPaddings;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.service.search.SearchQuery;

import java.awt.*;

/**
 * @author vpc
 */
public class SearchDialog {

    private Panel panel;
    private PangaeaNoteFrame frame;
    private Label matchModeLabel;

    private ComboBox queryEditor;
    private CheckBox matchCaseEditor;
    private CheckBox wholeWordEditor;
    private ComboBox<SimpleItem> modeEditor;

    private boolean ok = false;
    private Str titleId = Str.i18n("Message.search");

    public SearchDialog(PangaeaNoteFrame frame) throws HeadlessException {
        this.frame = frame;
        PangaeaNoteApp app = frame.app();
        this.matchCaseEditor = new CheckBox(Str.i18n("Message.matchCase"), app);
        this.wholeWordEditor = new CheckBox(Str.i18n("Message.wholeWord"), app);
        this.matchModeLabel = new Label(Str.i18n("Message.matchingText"), app);
        this.modeEditor = new ComboBox<>(SimpleItem.class, app);
        this.modeEditor.values().add(new SimpleItem(SearchQuery.Strategy.LITERAL.toString(), Str.i18n("Message.searchLiteralStrategy")));
        this.modeEditor.values().add(new SimpleItem(SearchQuery.Strategy.SIMPLE.toString(), Str.i18n("Message.searchSimpleStrategy")));
        this.modeEditor.values().add(new SimpleItem(SearchQuery.Strategy.REGEXP.toString(), Str.i18n("Message.searchRegexpStrategy")));

        this.modeEditor.selection().indices().set(0);

        this.queryEditor = new ComboBox<String>(String.class, app);
        this.queryEditor.values().setCollection(frame.getRecentSearchQueries());
        this.queryEditor.editable().set(true);
//        queryEditor.setMinimumSize(new Dimension(50, 30));
        this.panel = new GridPane(1, app)
                .with(p -> {
                    p.parentConstraints().addAll(AllMargins.of(5), AllAnchors.LEFT, AllFill.HORIZONTAL);
                });
        panel.children().addAll(new Label(Str.i18n("Message.searchLabel"), app),
                queryEditor,
                new GridPane(app)
                        .with((Panel hh) -> {
                            hh.parentConstraints().addAll(AllPaddings.of(5));
                            hh.children().addAll(
                                    matchCaseEditor,
                                    wholeWordEditor,
                                    matchModeLabel,
                                    modeEditor
                            );
                        })
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

    public SearchDialog setTitle(Str titleId) {
        this.titleId = titleId;
        return this;
    }

    public SearchQuery showDialog() {
        while (true) {
            install();
            this.ok = false;
            new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(titleId);
                        a.headerText().set(titleId);
                        a.headerIcon().set(Str.of("search"));
                    })
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
                    .showDialog(frame);
            try {
                return get();
            } catch (Exception ex) {
                frame.app().errors().add(ex);
            }
        }
    }

    public SearchQuery get() {
        if (ok) {
            String s = queryEditor.text().get().value();
            if (s != null && s.length() > 0) {
                SearchQuery.Strategy mm = SearchQuery.Strategy.valueOf(modeEditor.selection().get().getId());
                SearchQuery q = new SearchQuery(
                        s,
                        matchCaseEditor.selected().get(),
                        wholeWordEditor.selected().get(),
                        mm
                );
                return q;
            }
        }
        return null;
    }

    public void setSearchTextElseClipboard(String selectedText) {
        if(selectedText==null || selectedText.length()==0){
            String s = frame.app().clipboard().getString();
            if(s!=null){
                selectedText=s;
            }
        }
        setSearchText(selectedText);
    }
    public void setSearchText(String selectedText) {
        queryEditor.text().set(Str.of(selectedText));
        queryEditor.lastWasEdit().set(true);
    }

}
