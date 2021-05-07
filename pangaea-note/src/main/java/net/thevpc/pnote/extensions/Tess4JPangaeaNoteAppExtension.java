/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.extensions;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import net.sourceforge.tess4j.Tesseract;
import net.thevpc.common.swing.SwingUtilities3;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.jeep.editor.JEditorPaneBuilder;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.source.SourceEditorPanePanel;
import net.thevpc.pnote.api.PangaeaNoteAppExtension;

/**
 *
 * @author vpc
 */
public class Tess4JPangaeaNoteAppExtension implements PangaeaNoteAppExtension {

    @Override
    public void uninstallNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteWindow win) {
        if (component instanceof SourceEditorPanePanel) {
            SourceEditorPanePanel s = (SourceEditorPanePanel) component;
            JEditorPaneBuilder editorBuilder = s.getEditorBuilder();
            SwingApplicationsUtils.Tracker tracker = (SwingApplicationsUtils.Tracker) editorBuilder.editor().getClientProperty("Tess4JPangaeaNoteAppExtension.Tracker");
            if(tracker!=null){
                tracker.unregisterAll();
            }
        }
    }

    @Override
    public void installNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteWindow win) {
        if (component instanceof SourceEditorPanePanel) {
            SourceEditorPanePanel s = (SourceEditorPanePanel) component;
            if (!component.isCompactMode()) {
                SwingApplicationsUtils.Tracker tracker = new SwingApplicationsUtils.Tracker(win.app());
                JEditorPaneBuilder editorBuilder = s.getEditorBuilder();
                editorBuilder.editor().putClientProperty("Tess4JPangaeaNoteAppExtension.Tracker", tracker);
                JToolBar bar = new JToolBar();
                ///usr/share/tessdata/
                for (String tessdata : new String[]{"/usr/share/tessdata/"}) {
                    if (new File(tessdata).isDirectory()) {
                        String tessdataFinal = tessdata;
                        AbstractAction a = new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JFileChooser jfc = new JFileChooser();
                                int a = jfc.showOpenDialog(null);
                                if (a == JFileChooser.APPROVE_OPTION) {
                                    File f = jfc.getSelectedFile();
                                    new Thread(
                                            () -> {
                                                try {
                                                    editorBuilder.editor().setEditable(false);
                                                    Tesseract instance = new Tesseract();
                                                    instance.setDatapath(tessdataFinal);
//                        instance.setDatapath("/usr/local/Cellar/tesseract/4.0.0/share/tessdata");
                                                    instance.setLanguage("eng");
                                                    instance.setHocr(false);
                                                    String result = instance.doOCR(f);
                                                    SwingUtilities3.invokeLater(
                                                            () -> {
                                                                try {
                                                                    editorBuilder.editor().getDocument().insertString(editorBuilder.editor().getCaretPosition(), result, null);
                                                                } catch (Exception ex) {
                                                                    win.showError(ex);
                                                                } finally {
                                                                    editorBuilder.editor().setEditable(true);
                                                                }
                                                            }
                                                    );
                                                } catch (Exception ex) {
                                                    win.showError(ex);
                                                    editorBuilder.editor().setEditable(true);

                                                }
                                            }
                                    ).start();

                                }
                            }

                        };
                        SwingApplicationsUtils.registerStandardAction(a, "ocr", win.app());
                        bar.add(a);
                        tracker.add(a);
                        editorBuilder.editor().getComponentPopupMenu().add(a);
                        editorBuilder.header().add(bar);
                        break;
                    }
                }
            }
        }
    }

}
