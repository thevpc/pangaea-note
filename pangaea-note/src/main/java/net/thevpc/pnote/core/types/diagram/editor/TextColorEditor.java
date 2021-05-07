/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.thevpc.common.swing.color.ColorUtils;
import net.thevpc.diagram4j.JDiagramCanvas;
import net.thevpc.diagram4j.model.JDiagram;
import net.thevpc.diagram4j.model.JDiagramElement;
import net.thevpc.diagram4j.model.JDiagramListener;

/**
 *
 * @author vpc
 */
public class TextColorEditor extends JColorChooser {

    public TextColorEditor(JDiagramCanvas canvas) {
        TextColorEditor jc = this;
        jc.getSelectionModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (canvas.getDiagram().getSelectionCount() > 0) {
                    canvas.getDiagram().setSelectionTextColor(
                            ColorUtils.formatPaint(jc.getColor())
                    );
                } else {
                    canvas.getDiagram().setConfigTextColor(
                            ColorUtils.formatPaint(jc.getColor())
                    );
                }
            }
        });
        jc.setColor(ColorUtils.parseColor(canvas.getDiagram().getConfig().getLineColor()));
        canvas.addDiagramChangeListener(new JDiagramListener() {
            @Override
            public void selectionChanged(JDiagram diagram, Set<String> before, Set<String> after) {
                String _before = before.stream().findFirst().orElse(null);
                String _after = after.stream().findFirst().orElse(null);
                if (!Objects.equals(_before, _after)) {
                    doUpdates();
                }
            }

            private void doUpdates() {
                List<JDiagramElement> se = canvas.getDiagram().getSelectedElements();
                if (se.size() == 1) {
                    JDiagramElement de = se.get(0);
                    jc.setColor(ColorUtils.parseColor(de.getTextColor()));
                } else if (se.isEmpty()) {
                    jc.setColor(ColorUtils.parseColor(canvas.getDiagram().getConfigTextColor()));
                }
            }
        });
    }

}
