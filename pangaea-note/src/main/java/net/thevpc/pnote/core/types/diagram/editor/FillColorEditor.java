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
import net.thevpc.diagram4j.model.JDiagramListener;
import net.thevpc.diagram4j.model.JDiagramShape;
import net.thevpc.pnote.core.types.diagram.editor.tools.JPaintChooser;

/**
 *
 * @author vpc
 */
public class FillColorEditor extends JPaintChooser {

    public FillColorEditor(JDiagramCanvas canvas) {
        FillColorEditor jc = FillColorEditor.this;
        this.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
//                System.out.println("update chooser : " + ColorUtils.formatPaint(jc.getSelectedPaint()) + " = " + jc.getSelectedPaint());
//                if (canvas.getDiagram().getSelectionCount() > 0) {
//                    canvas.getDiagram().setSelectionFillColor(ColorUtils.formatPaint(jc.getSelectedPaint())
//                    );
//                } else {
//                    canvas.getDiagram().setConfigFillColor(ColorUtils.formatPaint(jc.getSelectedPaint())
//                    );
//                }

            }
        });
//        this.setSelectedPaint(ColorUtils.parsePaint(canvas.getDiagram().getConfig().getFillColor()));
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
                List<JDiagramShape> se = canvas.getDiagram().getSelectedShapes();
                if (se.size() == 1) {
                    JDiagramShape de = se.get(0);
//                    setSelectedPaint(ColorUtils.parsePaint(de.getFillColor()));
                } else if (se.size() == 0) {
//                    setSelectedPaint(ColorUtils.parsePaint(canvas.getDiagram().getConfigFillColor()));
                }
            }
        });
    }

}
