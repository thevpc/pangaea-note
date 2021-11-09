/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor.tools;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.thevpc.diagram4j.render.strokes.SloppyStroke;

/**
 *
 * @author thevpc
 */
public class JLineColorChooser extends JPanel {

    JColorChooser jColorChooser;
    JSpinner thiknessSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    List<ChangeListener> colorChanged = new ArrayList<>();
    List<ChangeListener> strokeChanged = new ArrayList<>();
    JList fillModes = new JList<>();
//    int maxPatterns = 5;

    public JLineColorChooser() {
        super(new BorderLayout());
        jColorChooser = new JColorChooser();
//        jColorChooser.addChooserPanel(new EyeDropperColorChooserPanel());
        jColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                repaint();
                fireSelectedColorChanged();
            }
        });
        thiknessSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                repaint();
                fireSelectedStrokeChanged();
            }
        }
        );
        jColorChooser.setPreviewPanel(new JPanel());
        this.add(jColorChooser, BorderLayout.CENTER);
        JPanel rightPanel = new JPanel(new BorderLayout());
        Box spinnerBox = Box.createHorizontalBox();
        spinnerBox.add(new JLabel("width:"));
        spinnerBox.add(thiknessSpinner);
        rightPanel.add(spinnerBox, BorderLayout.PAGE_START);
        rightPanel.add(new JScrollPane(fillModes), BorderLayout.CENTER);
//        this.add(rightPanel, BorderLayout.EAST);
        jColorChooser.setPreviewPanel(rightPanel);

        DefaultListModel defaultListModel = new DefaultListModel();

        fillModes.setModel(defaultListModel);
//        fillModes.setPreferredSize(new Dimension(400,40));
        fillModes.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        fillModes.setVisibleRowCount(2);
        defaultListModel.addElement("No Line");
        for (int i = 1; i < 6; i++) {
            defaultListModel.addElement(i);
        }
        fillModes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fillModes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int i = fillModes.getSelectionModel().getMinSelectionIndex();
                repaint();
                fireSelectedStrokeChanged();
            }
        });
//        fillModes.setCellRenderer(new DefaultListRenderer() {
//            @Override
//            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                int w = 80;
//                int h = 20;
//                if (index == 0) {
//                    JLabel c = (JLabel) super.getListCellRendererComponent(list, "No Line", index, isSelected, cellHasFocus);
//                    c.setIcon(new EmptyIcon(2, h));
//                    return c;
//                } else {
//                    JLabel c = (JLabel) super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
//                    c.setIcon(new StrokeIcon(getSelectedColor(), w, h, getStroke(index)));
//                    return c;
//                }
//            }
//        });
        fillModes.setSelectedIndex(0);
//                disabledPanel.setEnabled(doFill.isSelected());
    }

    public Stroke getSelectedStroke() {
        int i = fillModes.getSelectedIndex();
        return getStroke(i);
    }

    public Color getSelectedColor() {
        Color sc = jColorChooser.getSelectionModel().getSelectedColor();
        if (sc == null) {
            sc = Color.BLACK;
        }
        return sc;
    }

    

    protected Stroke getStroke(int index) {
        return getStroke(index, getSelectedThikness());
    }

    protected Stroke getStroke(int index, int thickness) {
        if (index == 0) {
            return null;
        }
        switch (index) {
            case 1:
                return new BasicStroke(thickness);
            case 2:
                return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2f, 0f, 2f}, 2f);
            case 3:
                return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{1f, 1f, 1f}, 2f);
            case 4:
                return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4f, 0f, 2f}, 2f);
            case 5:
                return new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{4f, 4f, 1f}, 2f);
        }
        return new BasicStroke(thickness);
    }

    protected int getSelectedThikness() {
        return ((Number) thiknessSpinner.getValue()).intValue();
    }

    protected void fireSelectedColorChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener changeListener : colorChanged) {
            changeListener.stateChanged(e);
        }
    }

    protected void fireSelectedStrokeChanged() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener changeListener : strokeChanged) {
            changeListener.stateChanged(e);
        }
    }

    public void addColorChangeListener(ChangeListener changeListener) {
        this.colorChanged.add(changeListener);
    }

    public void addStrokeChangeListener(ChangeListener changeListener) {
        this.strokeChanged.add(changeListener);
    }

    private boolean sameStroke(Stroke p1, Stroke p2) {
        if (p1 == null && p2 == null) {
            return true;
        }
        if (p1 != null && p2 != null) {
            String s1 = StrokeUtils.formatStroke(p1);
            String s2 = StrokeUtils.formatStroke(p2);
            return s1.equals(s2);
        }
        return false;
    }

    public void setSelectedColor(Color p) {
        if (p == null) {
            fillModes.setSelectedIndex(0);
            fireSelectedColorChanged();
            repaint();
        } else {
            jColorChooser.getSelectionModel().setSelectedColor((Color) p);
            repaint();
        }
    }

    public Stroke deriveStrokeWidth(Stroke p, int width) {
        if (p instanceof BasicStroke) {
            BasicStroke s = (BasicStroke) p;
            return new BasicStroke(width, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(),
                    s.getDashArray(), s.getDashPhase());
        }
        if (p instanceof SloppyStroke) {
            SloppyStroke s = (SloppyStroke) p;
            return new SloppyStroke(width, s.getSloppiness());
        }
        return p;
    }

    public int getStrokeWidth(Stroke p) {
        if (p instanceof BasicStroke) {
            return (int) ((BasicStroke) p).getLineWidth();
        }
        if (p instanceof SloppyStroke) {
            return (int) ((SloppyStroke) p).getWidth();
        }
        return 1;
    }

    public void setSelectedStroke(Stroke p) {
        if (sameStroke(p, getSelectedStroke())) {
            return;
        }
        if (p == null) {
            thiknessSpinner.setValue(0);
            fillModes.setSelectedIndex(0);
            fireSelectedStrokeChanged();
        } else {
            int newVal = getStrokeWidth(p);
            boolean someChanges = false;
            if (newVal != getSelectedThikness()) {
                thiknessSpinner.setValue(newVal);
                someChanges = true;
            }
            for (int i = 1; i < fillModes.getModel().getSize(); i++) {
                Stroke p0 = deriveStrokeWidth(p, 1);
                Stroke pp = deriveStrokeWidth(getStroke(i), 1);
                String s1 = StrokeUtils.formatStroke(p0);
                String s2 = StrokeUtils.formatStroke(pp);
                if (s1.equals(s2)) {
                    if (fillModes.getSelectedIndex() != i) {
                        fillModes.setSelectedIndex(i);
                        someChanges = true;
                    }
                    break;
                }
            }
            if (someChanges) {
                fireSelectedStrokeChanged();
            }
        }
    }

}
