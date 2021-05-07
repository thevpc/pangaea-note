/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.thevpc.pnote.core.types.diagram.editor.tools.JArrowChooser;
import net.thevpc.diagram4j.JDiagramCanvas;
import net.thevpc.diagram4j.model.JDiagram;
import net.thevpc.diagram4j.model.JDiagramEdge;
import net.thevpc.diagram4j.model.JDiagramElement;
import net.thevpc.diagram4j.model.JDiagramListener;
import net.thevpc.diagram4j.model.shapes.SegmentShape;

/**
 *
 * @author vpc
 */
public class ArrowEditor extends JArrowChooser {

    public ArrowEditor(JDiagramCanvas canvas) {
        JArrowChooser jc = this;
        jc.addStartArrowChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (canvas.getDiagram().getSelectionCount() > 0) {
                    canvas.getDiagram().setSelectionStartArrowType(
                            jc.getSelectedStartArrow().toString()
                    );
                } else {
                    canvas.getDiagram().setConfigStartArrowType(
                            jc.getSelectedStartArrow().toString()
                    );
                }
            }
        });
        jc.addEndArrowChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (canvas.getDiagram().getSelectionCount() > 0) {
                    canvas.getDiagram().setSelectionEndArrowType(
                            jc.getSelectedEndArrow().toString()
                    );
                } else {
                    canvas.getDiagram().setConfigEndArrowType(
                            jc.getSelectedEndArrow().toString()
                    );
                }
            }
        });
        jc.setSelectedStartArrow(canvas.getDiagram().getConfig().getStartArrowType());
        jc.setSelectedEndArrow(canvas.getDiagram().getConfig().getEndArrowType());
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
                List<JDiagramElement> se = canvas.getDiagram().getSelectedElements()
                        .stream().filter(xx
                                -> (xx instanceof SegmentShape)
                        || (xx instanceof JDiagramEdge)
                        ).collect(Collectors.toList());
                if (se.size() == 1) {
                    JDiagramElement de = se.get(0);
                    if (de instanceof SegmentShape) {
                        SegmentShape ss = (SegmentShape) de;
                        jc.setSelectedStartArrow(ss.getStartArrowType());
                        jc.setSelectedEndArrow(ss.getEndArrowType());
                    } else if (de instanceof JDiagramEdge) {
                        JDiagramEdge ss = (JDiagramEdge) de;
                        jc.setSelectedStartArrow(ss.getStartArrowType());
                        jc.setSelectedEndArrow(ss.getEndArrowType());
                    }
                } else if (se.size() == 0) {
                    jc.setSelectedStartArrow(canvas.getDiagram().getConfigStartArrowType());
                    jc.setSelectedEndArrow(canvas.getDiagram().getConfigEndArrowType());
                }
            }
        });
    }

}
