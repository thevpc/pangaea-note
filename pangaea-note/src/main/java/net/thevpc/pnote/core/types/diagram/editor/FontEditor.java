/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor;

import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import net.thevpc.common.swing.font.FontUtils;
import net.thevpc.common.swing.font.JFontChooser;
import net.thevpc.diagram4j.JDiagramCanvas;

/**
 *
 * @author vpc
 */
public class FontEditor extends AbstractAction {

    private JDiagramCanvas canvas;

    public FontEditor(JDiagramCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int sc = canvas.getDiagram().getSelectionCount();
        Font initialFont = null;
        if (sc == 0) {
            initialFont = FontUtils.parseFont(canvas.getDiagram().getConfigTextFont());
        } else {
            initialFont = FontUtils.parseFont(canvas.getDiagram().getSelectedElements().get(0).getTextFont());
        }
        JFontChooser jfc = new JFontChooser(initialFont);
        int f = jfc.showDialog(SwingUtilities.getWindowAncestor(canvas), "Select Font");
        if (f == JFontChooser.ACCEPT_OPTION) {
            Font font = jfc.getSelectedFont();
            if (sc == 0) {
                canvas.getDiagram().setConfigTextFont(FontUtils.formatFont(font));
            } else {
                canvas.getDiagram().setSelectionTextFont(FontUtils.formatFont(font));
            }
        }
    }

}
