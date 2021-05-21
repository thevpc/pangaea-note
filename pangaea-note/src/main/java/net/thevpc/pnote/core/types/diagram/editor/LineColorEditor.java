/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.thevpc.diagram4j.JDiagramCanvas;
import net.thevpc.diagram4j.model.JDiagram;
import net.thevpc.diagram4j.model.JDiagramElement;
import net.thevpc.diagram4j.model.JDiagramListener;
import net.thevpc.pnote.core.types.diagram.editor.tools.JLineColorChooser;
import net.thevpc.pnote.core.types.diagram.editor.tools.StrokeUtils;

/**
 *
 * @author vpc
 */
public class LineColorEditor extends JLineColorChooser {

    public LineColorEditor(JDiagramCanvas canvas) {
        LineColorEditor jc = this;
        jc.addColorChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (canvas.getDiagram().getSelectionCount() > 0) {
//                    canvas.getDiagram().setSelectionLineColor(
//                            ColorUtils.formatPaint(jc.getSelectedColor())
//                    );
                } else {
//                    canvas.getDiagram().setConfigLineColor(
//                            ColorUtils.formatPaint(jc.getSelectedColor())
//                    );
                }
            }
        });
        jc.addStrokeChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                canvas.getDiagram().updateLineStroke(
                        StrokeUtils.formatStroke(jc.getSelectedStroke())
                );
            }
        });
//        jc.setSelectedColor(ColorUtils.parseColor(canvas.getDiagram().getConfig().getLineColor()));
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
//                    jc.setSelectedColor(ColorUtils.parseColor(de.getLineColor()));
                    jc.setSelectedStroke(StrokeUtils.parseStroke(de.getLineStroke()));
                } else if (se.isEmpty()) {
//                    jc.setSelectedColor(ColorUtils.parseColor(canvas.getDiagram().getConfigLineColor()));
                    jc.setSelectedStroke(StrokeUtils.parseStroke(canvas.getDiagram().getConfigLineStroke()));
                }
            }
        });
    }

}
