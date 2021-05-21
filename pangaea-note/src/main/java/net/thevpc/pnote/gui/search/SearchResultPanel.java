/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.search;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.HighlightType;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class SearchResultPanel extends BorderPane {

    private PangaeaNoteFrame frame;
    private SearchResultPanelItem item;

    public SearchResultPanel(PangaeaNoteFrame frame) {
        super("SearchResults", frame.app());
        title().set(Str.i18n("Tools.SearchResults"));
        this.frame = frame;
        item = new SearchResultPanelItemImpl(frame);
        children().add((AppComponent) item);
    }

    public SearchResultPanelItem createNewPanel() {
        return item;
    }

    public void showResults() {
        active().set(true);
    }

    public interface SearchResultPanelItem {

        void appendResult(StringSearchResult<PangaeaNoteExt> x);

        boolean isSearching();

        void setSearching(boolean b);

        void resetResults();

        void setSearchProgress(double progressOrNaN, String text);

    }

    public static class SearchResultPanelItemImpl extends BorderPane implements SearchResultPanelItem {

        private boolean searching;
        private PangaeaNoteFrame frame;
        private JTable table = new JTable();
        private ProgressBar<Integer> bar;
        private DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        public SearchResultPanelItemImpl(PangaeaNoteFrame frame) {
            super(frame.app());
            this.frame = frame;
            bar = new ProgressBar<>(Integer.class, app())
                    .with(u -> u.anchor().set(Anchor.BOTTOM));
            model.setColumnIdentifiers(new Object[]{
                    frame.app().i18n().getString("Message.search.position"),
                    frame.app().i18n().getString("Message.search.note"),
                    frame.app().i18n().getString("Message.search.matchingText"),});
            frame.app().i18n().locale().onChange((x) -> {
                model.setColumnIdentifiers(new Object[]{
                        frame.app().i18n().getString("Message.search.position"),
                        frame.app().i18n().getString("Message.search.note"),
                        frame.app().i18n().getString("Message.search.matchingText"),});
            });
            table.setModel(model);
            table.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int r = table.rowAtPoint(e.getPoint());
                        if (r >= 0) {
                            StringSearchResult<PangaeaNoteExt> searchResult = (StringSearchResult<PangaeaNoteExt>) table.getModel().getValueAt(r, 2);
                            PangaeaNoteExt n = searchResult.getObject();
                            frame.treePane().setSelectedNote(n);
                            PangaeaNoteEditorTypeComponent comp = frame.noteEditor().editorComponent();
                            comp.removeHighlights(HighlightType.SEARCH_MAIN);
                            comp.removeHighlights(HighlightType.SEARCH);
                            for (StringSearchResult<PangaeaNoteExt> rr : findResults(n)) {
                                if (rr == searchResult) {
                                    comp.highlight(rr.getStart(), rr.getEnd(), HighlightType.SEARCH_MAIN);
                                    comp.moveTo(rr.getStart());
                                } else {
                                    comp.highlight(rr.getStart(), rr.getEnd(), HighlightType.SEARCH);
                                }
                            }
                        }
                    } else {
                    }

                }
            });
            table.getColumnModel().getColumn(0).setPreferredWidth(27);
            table.getColumnModel().getColumn(1).setPreferredWidth(120);
            UserControl tabControl = new UserControl("JTable", table, app());
            ScrollPane scroll = new ScrollPane(tabControl);
            scroll.prefSize().set(new Dimension(50, 50));
            children().add(scroll);
            children().add(
                    bar

            );
        }

        public List<StringSearchResult<PangaeaNoteExt>> findResults(PangaeaNoteExt n) {
            List<StringSearchResult<PangaeaNoteExt>> ok = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                StringSearchResult<PangaeaNoteExt> r = (StringSearchResult<PangaeaNoteExt>) model.getValueAt(i, 2);
                if (r.getObject() == n) {
                    ok.add(r);
                }
            }
            return ok;
        }

        public void appendResult(StringSearchResult<PangaeaNoteExt> x) {
            frame.app().runUI(() -> {
                model.addRow(new Object[]{
                        String.valueOf(x.getRow()) + ":" + String.valueOf(x.getColumn()),
                        x.getObject().getName(),
                        x
                });
            });
        }

        public boolean isSearching() {
            return searching;
        }

        public void setSearching(boolean b) {
            bar.indeterminate().set(b);
            bar.visible().set(b);
            bar.textPainted().set(b);
            this.searching = b;
        }

        public void resetResults() {
            bar.indeterminate().set(false);
            bar.visible().set(false);
            frame.app().runUI(() -> {
                while (model.getRowCount() > 0) {
                    model.removeRow(0);
                }
            });
        }

        public void setSearchProgress(double progressOrNaN, String text) {
            if (Double.isNaN(progressOrNaN) || progressOrNaN < 0 || progressOrNaN > 1) {
                bar.indeterminate().set(true);
                bar.text().set(Str.of(text));
                bar.textPainted().set(true);
            } else {
                bar.textPainted().set(true);
                bar.text().set(null);
                bar.value().set((int) (progressOrNaN * 100));
                bar.indeterminate().set(false);
            }

        }
    }
}
