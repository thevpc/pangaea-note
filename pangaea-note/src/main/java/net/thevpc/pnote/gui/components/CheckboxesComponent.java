/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.pnote.gui.PangaeaNoteWindow;

/**
 *
 * @author vpc
 */
public class CheckboxesComponent extends JPanel implements FormComponent {

    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private Runnable callback;
    private Box box;
    private ItemListener itemListener;
    private boolean editable = true;
    private SwingApplicationsUtils.Tracker tracker;

    public CheckboxesComponent(PangaeaNoteWindow sapp) {
        super(new BorderLayout());
        tracker = new SwingApplicationsUtils.Tracker(sapp.app());
        box = Box.createHorizontalBox();
        add(box);
        itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                callOnValueChanged();
            }
        };
    }

    private void callOnValueChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    public void setSelectValues(List<String> newValues) {
        while (checkBoxes.size() > newValues.size()) {
            JCheckBox c = checkBoxes.remove(checkBoxes.size() - 1);
            c.removeItemListener(itemListener);
            box.remove(c);
        }
        for (int i = 0; i < checkBoxes.size(); i++) {
            JCheckBox c = checkBoxes.get(i);
            String s = newValues.get(i);
            if (s == null) {
                s = "";
            }
            s = s.trim();
            c.setText(s);
        }
        for (int i = checkBoxes.size(); i < newValues.size(); i++) {
            String s = newValues.get(i);
            if (s == null) {
                s = "";
            }
            s = s.trim();
            JCheckBox cv = createCheckBox();
            cv.setText(s);
            cv.setEnabled(isEditable());
            cv.addItemListener(itemListener);
            checkBoxes.add(cv);
            box.add(cv);
        }
    }

    protected JCheckBox createCheckBox() {
        JCheckBox c = new JCheckBox("value");
        JPopupMenu p = new JPopupMenu();
        Action a = tracker.registerStandardAction(
                () -> {
                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    try {
                        clip.setContents(new StringSelection(c.getText()), null);
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                    }
                }, "copy"
        );
        p.add(new JMenuItem(a));
        c.setComponentPopupMenu(p);
        return c;
    }

    @Override
    public String getContentString() {
        StringBuilder sb = new StringBuilder();
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(cb.getText());
            }
        }
        return (sb.toString());
    }

    @Override
    public void setContentString(String s) {
        Set<String> values = new HashSet<>();
        for (String v : s.split("\n")) {
            v = v.trim();
            if (v.length() > 0) {
                values.add(v);
            }
        }
        for (JCheckBox cb : checkBoxes) {
            cb.setSelected(values.contains(cb.getText().trim()));
        }
    }

    @Override
    public void uninstall() {
        callback = null;
    }

    public void install(Application app) {
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void setEditable(boolean b) {
        for (JCheckBox checkBoxe : checkBoxes) {
            checkBoxe.setEnabled(isEditable());
        }
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

}
