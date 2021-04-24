/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.breadcrumb;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import net.thevpc.common.swing.JBreadCrumb;
import net.thevpc.common.swing.ObjectListModel;
import net.thevpc.common.swing.ObjectListModelListener;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.util.DefaultObjectListModel;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.model.ObservableNoteSelectionListener;

/**
 *
 * @author vpc
 */
public class PangaeaNodeBreadcrumb extends JScrollPane {

    public PangaeaNodeBreadcrumb(PangaeaNoteWindow sapp) {
        JBreadCrumb b = new JBreadCrumb();
        b.setOpaque(false);
        b.setBorder(null);
        //sapp.
        sapp.tree().addNoteSelectionListener(new ObservableNoteSelectionListener() {
            @Override
            public void onSelectionChanged(PangaeaNoteExt note) {
                SwingUtilities3.invokeLater(() -> {
                    b.setModel(createBreadCrumModel(note));
                    PangaeaNodeBreadcrumb.this.invalidate();
                    PangaeaNodeBreadcrumb.this.revalidate();
                    getHorizontalScrollBar().invalidate();
                    getHorizontalScrollBar().revalidate();
//                    PangaeaNodeBreadcrumb.this.getViewport().fireStateChanged();
                    getHorizontalScrollBar().setValue(getHorizontalScrollBar().getMaximum());
                });
            }

            private ObjectListModel createBreadCrumModel(PangaeaNoteExt note) {
                List<PangaeaNoteExt> bm = new ArrayList<>();
                PangaeaNoteExt n = note;
                while (n != null) {
                    bm.add(0, n);
                    n = n.getParent();
                }
                if (bm.size() > 0) {
                    bm.remove(0);//remove root!
                }
                DefaultObjectListModel model = new DefaultObjectListModel(bm);
                return model;
            }
        });
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setMinimumSize(new Dimension(60, 36));
        this.setPreferredSize(new Dimension(60, 36));
        this.setViewportView(b);
        b.addListener(new ObjectListModelListener() {
            @Override
            public void onSelected(Object component, int index) {
                PangaeaNoteExt v = (PangaeaNoteExt) component;
                sapp.tree().setSelectedNote(v);
            }
        });
    }

}
