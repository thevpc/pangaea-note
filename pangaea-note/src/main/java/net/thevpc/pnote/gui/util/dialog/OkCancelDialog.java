///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.thevpc.pnote.gui.util.dialog;
//
//import java.awt.BorderLayout;
//import javax.swing.BorderFactory;
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import net.thevpc.common.swing.layout.GridBagLayoutSupport;
//import net.thevpc.common.swing.dialog.JDialog2;
//import net.thevpc.common.swing.SwingUtilities3;
//import net.thevpc.echo.Application;
//import net.thevpc.pnote.gui.PangaeaNoteFrame;
//
///**
// *
// * @author vpc
// */
//public class OkCancelDialog extends JDialog2 {
//
//    private OkCancelFooter footer;
//    protected PangaeaNoteFrame win;
//
//    public OkCancelDialog(PangaeaNoteFrame win, String titleId) {
//        super((JFrame) win.app().mainFrame().get().peer().toolkitComponent(),
//                win.app().i18n().getString(titleId), true
//        );
//        this.win = win;
//    }
//
//    protected void build(JComponent mainComponent, Runnable onOk, Runnable onCancel) {
//        getRootPane().setLayout(new BorderLayout());
//        footer = new OkCancelFooter(win.app(), onCancel, onOk);
//        getRootPane().add(mainComponent, BorderLayout.CENTER);
//        getRootPane().add(footer, BorderLayout.SOUTH);
//        SwingUtilities3.addEscapeBindings(this);
//        this.getRootPane().setDefaultButton(footer.getOkButton());
//    }
//
//    public static class OkCancelFooter extends JPanel {
//
//        JButton okButton;
//        JButton cancelButton;
//
//        public OkCancelFooter(Application app, Runnable onCancel, Runnable onOk) {
//            okButton = new JButton(app.i18n().getString("Message.ok"));
//            okButton.addActionListener((e) -> onOk.run());
//            cancelButton = new JButton(app.i18n().getString("Message.cancel"));
//            cancelButton.addActionListener((e) -> onCancel.run());
//            new GridBagLayoutSupport("[-=glue(h)][ok][cancel] ; insets(5)")
//                    .bind("ok", okButton)
//                    .bind("cancel", cancelButton)
//                    .apply(this);
//            setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
//        }
//
//        public JButton getOkButton() {
//            return okButton;
//        }
//
//        public JButton getCancelButton() {
//            return cancelButton;
//        }
//
//    }
//}
