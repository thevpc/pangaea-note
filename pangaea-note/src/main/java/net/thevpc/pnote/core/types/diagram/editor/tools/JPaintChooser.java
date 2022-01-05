///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.thevpc.pnote.core.types.diagram.editor.tools;
//
//import javax.swing.*;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.event.ListSelectionEvent;
//import javax.swing.event.ListSelectionListener;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
////import net.thevpc.common.swing.color.ColorUtils;
////import net.thevpc.common.swing.icon.EmptyIcon;
////import net.thevpc.common.swing.icon.PaintIcon;
////import org.jdesktop.swingx.color.EyeDropperColorChooserPanel;
////import org.jdesktop.swingx.renderer.DefaultListRenderer;
//
///**
// *
// * @author thevpc
// */
//public class JPaintChooser extends JPanel {
//
////    DisabledPanel disabledPanel;
//    JColorChooser jColorChooser;
//    List<ChangeListener> colorChanged = new ArrayList<>();
//    JList fillModes = new JList<>();
//
//    public JPaintChooser() {
//        super(new BorderLayout());
//        jColorChooser = new JColorChooser();
////        jColorChooser.addChooserPanel(new EyeDropperColorChooserPanel());
//        jColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                repaint();
//                fireSelectedColorChanged();
//            }
//        });
////        disabledPanel = new DisabledPanel(jColorChooser, new Color(255, 0, 0, 30));
////        this.add(disabledPanel, BorderLayout.CENTER);
//        this.add(fillModes, BorderLayout.EAST);
//        DefaultListModel defaultListModel = new DefaultListModel();
//
//        fillModes.setModel(defaultListModel);
//        defaultListModel.addElement("No Fill");
//        for (int i = 1; i < 7; i++) {
//            defaultListModel.addElement(i);
//        }
//        fillModes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        fillModes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                int i = fillModes.getSelectionModel().getMinSelectionIndex();
////                disabledPanel.setEnabled(i > 0);
//                repaint();
//                fireSelectedColorChanged();
//            }
//        });
////        fillModes.setCellRenderer(new DefaultListRenderer() {
////            @Override
////            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
////                int w = 80;
////                int h = 40;
////                if (index == 0) {
////                    JLabel c = (JLabel) super.getListCellRendererComponent(list, "No Fill", index, isSelected, cellHasFocus);
////                    c.setIcon(new EmptyIcon(2, h));
////                    return c;
////                } else {
////                    JLabel c = (JLabel) super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
////                    c.setIcon(new PaintIcon(getPaint(index), w, h));
////                    return c;
////                }
////            }
////        });
//        fillModes.setSelectedIndex(0);
////                disabledPanel.setEnabled(doFill.isSelected());
//    }
//
//    private Color getSelectedColor() {
//        Color sc = jColorChooser.getSelectionModel().getSelectedColor();
//        if (sc == null) {
//            sc = Color.BLACK;
//        }
//        return sc;
//    }
//
//    protected Paint getPaint(int index) {
//        Color c = getSelectedColor();
//        return getPaint(index, c);
//    }
//
//    protected Paint getPaint(int index, Color c) {
////        String cs = ColorUtils.formatColor(c);
////        switch (index) {
////            case 0:
////                return null;
////            case 1:
////                return c;
////            case 2:
////                return ColorUtils.parsePaint("texture:" + ColorUtils.TEXTUTE_ID_SAMPLE1 + ";color=" + cs + ";width=8;height=8");
////            case 3:
////                return ColorUtils.parsePaint("texture:" + ColorUtils.TEXTUTE_ID_SAMPLE2 + ";color=" + cs + ";width=8;height=8");
////            case 4:
////                return ColorUtils.parsePaint("texture:" + ColorUtils.TEXTUTE_ID_SAMPLE3 + ";color=" + cs + ";width=8;height=8");
////            case 5:
////                return ColorUtils.parsePaint("texture:" + ColorUtils.TEXTUTE_ID_SAMPLE4 + ";color=" + cs + ";width=8;height=8");
////            case 6:
////                return ColorUtils.parsePaint("texture:" + ColorUtils.TEXTUTE_ID_SAMPLE5 + ";color=" + cs + ";color2=" + ColorUtils.formatColor(Color.WHITE) + ";size=8");
////        }
//        throw new IllegalArgumentException("unsupported");
//    }
//
//    protected void fireSelectedColorChanged() {
//        ChangeEvent e = new ChangeEvent(this);
//        for (ChangeListener changeListener : colorChanged) {
//            changeListener.stateChanged(e);
//        }
//    }
//
//    public void addChangeListener(ChangeListener changeListener) {
//        this.colorChanged.add(changeListener);
//    }
//
//    private boolean samePaint(Paint p1, Paint p2) {
//        if (p1 == null && p2 == null) {
//            return true;
//        }
//        if (p1 != null && p2 != null) {
////            String s1 = net.thevpc.echo.Paint.format(p1);
////            String s2 = net.thevpc.echo.Paint.format(p2);
////            return s1.equals(s2);
//        }
//        return false;
//    }
//
//    public void setSelectedPaint(Paint p) {
//        if (samePaint(p, getSelectedPaint())) {
////            System.out.println("setselected paint same " + p
////                    +" as "+jColorChooser.getSelectionModel().getSelectedColor()
////                    +" and "+getSelectedPaint()
////            );
//            return;
//        }
////        System.out.println("setselected paint new " + p);
//        if (p == null) {
//            fillModes.setSelectedIndex(0);
//            fireSelectedColorChanged();
//            return;
//        } else {
//            if (p instanceof Color) {
//                fillModes.setSelectedIndex(1);
//                jColorChooser.getSelectionModel().setSelectedColor((Color) p);
//            } else {
////                Color cc = ColorUtils.paintToColor(p);
////                if (cc == null) {
////                    cc = Color.BLACK;
////                }
////                for (int i = 2; i < 7; i++) {
////                    Paint pp = getPaint(i, cc);
////                    String s1 = ColorUtils.formatPaint(pp);
////                    String s2 = ColorUtils.formatPaint(pp);
////                    if (s1.equals(s2)) {
////                        jColorChooser.getSelectionModel().setSelectedColor(cc);
////                        fillModes.setSelectedIndex(i);
////                        fireSelectedColorChanged();
////                        return;
////                    }
////                }
//            }
//        }
//    }
//
//    public Paint getSelectedPaint() {
//        return getPaint(fillModes.getSelectedIndex());
//    }
//
//}
