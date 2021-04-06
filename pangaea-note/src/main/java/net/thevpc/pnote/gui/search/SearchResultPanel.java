/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.echo.AppToolWindow;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

/**
 *
 * @author vpc
 */
public class SearchResultPanel extends JPanel {

    private PangaeaNoteGuiApp sapp;
    private AppToolWindow resultPanelTool;
    private SearchResultPanelItem item;

    public SearchResultPanel(PangaeaNoteGuiApp sapp) {
        super(new BorderLayout());
        this.sapp = sapp;
        item = new SearchResultPanelItemImpl(sapp);
        add((JComponent) item);
    }

    public SearchResultPanelItem createNewPanel() {
        return item;
    }

    public AppToolWindow getResultPanelTool() {
        return resultPanelTool;
    }

    public void setResultPanelTool(AppToolWindow resultPanelTool) {
        this.resultPanelTool = resultPanelTool;
    }

    public void showResults() {
        resultPanelTool.active().set(true);
    }

    public static interface SearchResultPanelItem {

        void appendResult(StringSearchResult<PangaeaNoteExt> x);

        boolean isSearching();

        void resetResults();

        void setSearching(boolean b);

        void setSearchProgress(double progressOrNaN, String text);

    }

    public static class SearchResultPanelItemImpl extends JPanel implements SearchResultPanelItem {

        private boolean searching;
        private PangaeaNoteGuiApp sapp;
        private JTable table = new JTable();
        private JProgressBar bar = new JProgressBar();
        private DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        public SearchResultPanelItemImpl(PangaeaNoteGuiApp sapp) {
            super(new BorderLayout());
            this.sapp = sapp;
            model.setColumnIdentifiers(new Object[]{
                sapp.app().i18n().getString("Message.search.position"),
                sapp.app().i18n().getString("Message.search.note"),
                sapp.app().i18n().getString("Message.search.matchingText"),});
            sapp.app().i18n().locale().listeners().add((x) -> {
                model.setColumnIdentifiers(new Object[]{
                    sapp.app().i18n().getString("Message.search.position"),
                    sapp.app().i18n().getString("Message.search.note"),
                    sapp.app().i18n().getString("Message.search.matchingText"),});
            });
            table.setModel(model);
            table.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int r = table.rowAtPoint(e.getPoint());
                        if (r >= 0) {
                            StringSearchResult<PangaeaNoteExt> a = (StringSearchResult<PangaeaNoteExt>) table.getModel().getValueAt(r, 2);
                            sapp.tree().setSelectedNote(a.getObject());
                        }
                    } else {
                    }

                }
            });
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(50, 50));
            add(scroll);
            add(bar, BorderLayout.SOUTH);
            bar.setMinimum(0);
            bar.setMaximum(100);
            bar.setValue(0);
        }

        public void resetResults() {
            bar.setIndeterminate(false);
            bar.setVisible(false);
            SwingUtilities3.invokeLater(() -> {
                while (model.getRowCount() > 0) {
                    model.removeRow(0);
                }
            });
        }

        public void appendResult(StringSearchResult<PangaeaNoteExt> x) {
            SwingUtilities3.invokeLater(() -> {
                model.addRow(new Object[]{
                    String.valueOf(x.getRow()) + ":" + String.valueOf(x.getColumn()),
                    x.getObject().getName(),
                    x
                });
            });
        }

        public void setSearchProgress(double progressOrNaN, String text) {
            if (Double.isNaN(progressOrNaN) || progressOrNaN < 0 || progressOrNaN > 1) {
                bar.setIndeterminate(true);
                bar.setString(text);
                bar.setStringPainted(true);
            } else {
                bar.setStringPainted(true);
                bar.setString(null);
                bar.setValue((int) (progressOrNaN * 100));
                bar.setIndeterminate(true);
                bar.setIndeterminate(true);
            }

        }

        public void setSearching(boolean b) {
            bar.setIndeterminate(b);
            bar.setVisible(b);
            bar.setStringPainted(b);
            this.searching = b;
        }

        public boolean isSearching() {
            return searching;
        }
    }
}
