//package net.thevpc.pnote.gui.util;
//
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.Map;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import net.thevpc.swing.plaf.UIPlaf;
//import net.thevpc.swing.plaf.UIPlafListener;
//import net.thevpc.swing.plaf.UIPlafManager;
//
//public class UIPropsTool extends JPanel{
//
//    private JTextField filter=new JTextField();
//    private JTable table;
//
//    public UIPropsTool() {
//        super(new BorderLayout());
//        table = new JTable();
//        this.add(filter,BorderLayout.NORTH);
//        this.add(new JScrollPane(table),BorderLayout.CENTER);
//        UIPlafManager.INSTANCE.addListener(new UIPlafListener() {
//            @Override
//            public void plafChanged(UIPlaf plaf) {
//                onLookChanged();
//            }
//        });
//        filter.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                onLookChanged();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                onLookChanged();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                onLookChanged();
//            }
//        });
//
//    }
//
//    public void onLookChanged() {
//        DefaultTableModel tm = new DefaultTableModel();
//        tm.addColumn("Key");
//        tm.addColumn("Value");
//        java.util.List<Object[]> list = new ArrayList<>();
//        for (Map.Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
//            Object key = entry.getKey();
//            if (key instanceof Color || entry.getValue() instanceof Color) {
//                if(filter.getText().length()>0){
//                    if(key instanceof String){
//                        if(!key.toString().toLowerCase().contains(filter.getText().toLowerCase())){
//                            continue;
//                        }
//                    }else{
//                        continue;
//                    }
//                }
//                list.add(new Object[]{key, entry.getValue()});
//            }
//        }
//        list.sort(new Comparator<Object>() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                String s1 = String.valueOf(((Object[]) o1)[0]);
//                String s2 = String.valueOf(((Object[]) o2)[0]);
//                return s1.compareTo(s2);
//            }
//        });
//        for (Object[] objects : list) {
//            tm.addRow(objects);
//        }
//        table.setModel(tm);
//        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                if (value instanceof Color) {
//                    setBackground((Color) value);
//                } else {
//                    setForeground(Color.black);
//                    setBackground(Color.white);
//                }
//                return c;
//            }
//
//        });
//
//    }
//
//}
