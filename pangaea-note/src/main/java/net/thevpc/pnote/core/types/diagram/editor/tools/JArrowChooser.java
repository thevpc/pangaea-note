/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import net.thevpc.diagram4j.util.DiagramArrowType;
import net.thevpc.diagram4j.util.DiagramArrowTypeIcon;

/**
 *
 * @author vpc
 */
public class JArrowChooser extends JPanel {

    List<ChangeListener> startArrowChanged = new ArrayList<>();
    List<ChangeListener> endArrowChanged = new ArrayList<>();
    JList startArrows = new JList<>();
    JList endArrows = new JList<>();

    public JArrowChooser() {
        super(new BorderLayout());
        this.add(startArrows, BorderLayout.WEST);
        this.add(endArrows, BorderLayout.EAST);

        DefaultListModel startArrowsModel = new DefaultListModel();
        startArrows.setModel(startArrowsModel);
        for (int i = 0; i <= DiagramArrowType.INHERITS.ordinal(); i++) {
            startArrowsModel.addElement(i);
        }
        startArrows.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        startArrows.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                repaint();
                fireStartSelectedChanged();
            }
        });
        startArrows.setCellRenderer(new DefaultListRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                int w = 80;
                int h = 40;
                JLabel c = (JLabel) super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
                c.setIcon(new DiagramArrowTypeIcon(Color.BLACK, w, h,
                        DiagramArrowType.values()[index % DiagramArrowType.values().length],
                        DiagramArrowType.NONE
                ));
                return c;
            }
        });
        startArrows.setSelectedIndex(0);

        DefaultListModel endArrowsModel = new DefaultListModel();
        endArrows.setModel(endArrowsModel);
        for (int i = 0; i <= DiagramArrowType.INHERITS.ordinal(); i++) {
            endArrowsModel.addElement(i);
        }
        endArrows.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        endArrows.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                repaint();
                fireEndSelectedChanged();
            }
        });
        endArrows.setCellRenderer(new DefaultListRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                int w = 80;
                int h = 40;
                JLabel c = (JLabel) super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
                c.setIcon(new DiagramArrowTypeIcon(Color.BLACK, w, h, DiagramArrowType.NONE,
                        DiagramArrowType.values()[index % DiagramArrowType.values().length]
                ));
                return c;
            }
        });
        endArrows.setSelectedIndex(0);

//                disabledPanel.setEnabled(doFill.isSelected());
    }

    public DiagramArrowType getSelectedStartArrow() {
        int index = startArrows.getSelectionModel().getMinSelectionIndex();
        if (index <= 0) {
            return DiagramArrowType.NONE;
        }
        return DiagramArrowType.values()[index % DiagramArrowType.values().length];
    }

    public DiagramArrowType getSelectedEndArrow() {
        int index = endArrows.getSelectionModel().getMinSelectionIndex();
        if (index <= 0) {
            return DiagramArrowType.NONE;
        }
        return DiagramArrowType.values()[index % DiagramArrowType.values().length];
    }

    protected void fireStartSelectedChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener changeListener : startArrowChanged) {
            changeListener.stateChanged(e);
        }
    }
    protected void fireEndSelectedChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener changeListener : endArrowChanged) {
            changeListener.stateChanged(e);
        }
    }

    public void addStartArrowChangeListener(ChangeListener changeListener) {
        this.startArrowChanged.add(changeListener);
    }

    public void addEndArrowChangeListener(ChangeListener changeListener) {
        this.endArrowChanged.add(changeListener);
    }

    public void setSelectedStartArrow(String p) {
        DiagramArrowType sa = DiagramArrowType.NONE;
        if (p != null && p.length() > 0) {
            try {
                sa = DiagramArrowType.valueOf(p.toUpperCase());
            } catch (Exception ex) {
                //
            }
        }
        setSelectedStartArrow(sa);
    }

    public void setSelectedEndArrow(String p) {
        DiagramArrowType sa = DiagramArrowType.NONE;
        if (p != null && p.length() > 0) {
            try {
                sa = DiagramArrowType.valueOf(p.toUpperCase());
            } catch (Exception ex) {
                //
            }
        }
        setSelectedEndArrow(sa);
    }

    public void setSelectedStartArrow(DiagramArrowType p) {
        if (p == null) {
            p = DiagramArrowType.NONE;
        }
        if (getSelectedStartArrow() != p) {
            startArrows.setSelectedIndex(p.ordinal());
            fireStartSelectedChanged();
            return;
        }
    }

    public void setSelectedEndArrow(DiagramArrowType p) {
        if (p == null) {
            p = DiagramArrowType.NONE;
        }
        if (getSelectedStartArrow() != p) {
            endArrows.setSelectedIndex(p.ordinal());
            fireStartSelectedChanged();
            return;
        }
    }

}
