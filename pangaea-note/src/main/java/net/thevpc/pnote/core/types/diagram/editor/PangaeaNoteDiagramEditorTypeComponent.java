/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor;

import net.thevpc.common.props.Path;
import net.thevpc.diagram4j.*;
import net.thevpc.diagram4j.actions.*;
import net.thevpc.diagram4j.model.*;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppFont;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.core.types.diagram.PangaeaNoteDiaService;
import net.thevpc.pnote.core.types.diagram.editor.tools.StrokeUtils;
import net.thevpc.pnote.core.types.diagram.ser.JDiagramBoundsMapper;
import net.thevpc.pnote.core.types.diagram.ser.JDiagramGeometryMapper;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.Set;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 * @author vpc
 */
public class PangaeaNoteDiagramEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    private boolean compactMode;
    private PangaeaNoteFrame frame;
    private JDiagramCanvas canvas;
    private ToolBar toolbar;
    private PangaeaNote note;

    public PangaeaNoteDiagramEditorTypeComponent(boolean compactMode, PangaeaNoteFrame frame) {
        super(frame.app());
        this.compactMode = compactMode;
        this.frame = frame;
        this.canvas = new JDiagramCanvas();
        this.canvas.addDiagramChangeListener(new JDiagramListener() {
            @Override
            public void geometryAdded(JDiagram diagram, JDiagramGeometry element) {
                updateNoteFromDiagram();
            }

            @Override
            public void geometryRemoved(JDiagram diagram, JDiagramGeometry element) {
                updateNoteFromDiagram();
            }

            @Override
            public void selectionChanged(JDiagram diagram, Set<String> before, Set<String> after) {
                updateNoteFromDiagram();
            }

            @Override
            public void propertyChanged(JDiagram diagram, JDiagramGeometry geometry, String property, Object oldValue, Object newValue) {
                if (property.toLowerCase().contains("hover")) {
                    return;
                }
                updateNoteFromDiagram();
            }
        });
        this.canvas.getRenderManager().setPaintFormatter(new JPaintFormatter() {
                                                             @Override
                                                             public java.awt.Paint parsePaint(String paint) {
                                                                 throw new IllegalArgumentException("unsupported");
//                                                                 return ColorUtils.parsePaint(paint);
                                                             }

                                                             @Override
                                                             public String formatPaint(java.awt.Paint paint) {
                                                                 throw new IllegalArgumentException("unsupported");
//                                                                 return ColorUtils.formatPaint(paint);
                                                             }
                                                         }
        );
        this.canvas.getRenderManager().setStrokeFormatter(new JStrokeFormatter() {
                                                              @Override
                                                              public java.awt.Stroke parseStroke(String stroke) {
                                                                  return StrokeUtils.parseStroke(stroke);
                                                              }

                                                              @Override
                                                              public String formatStroke(java.awt.Stroke stroke) {
                                                                  return StrokeUtils.formatStroke(stroke);
                                                              }
                                                          }
        );
        this.canvas.getRenderManager().setFontFormatter(new JFontFormatter() {
                                                            @Override
                                                            public java.awt.Font parseFont(String font) {
                                                                throw new IllegalArgumentException("unsupported");
//                                                                return FontUtils.parseFont(font);
                                                            }

                                                            @Override
                                                            public String formatFont(java.awt.Font font) {
                                                                throw new IllegalArgumentException("unsupported");
//                                                                return FontUtils.formatFont(font);
                                                            }
                                                        }
        );
        parentConstraints().addAll(Layout.BORDER);
        children().add(
                new UserControl("canvas", canvas, frame.app())
                        .with((UserControl c) -> c.anchor().set(Anchor.CENTER))
        );
        toolbar = new ToolBar(frame.app()).with(t -> t.anchor().set(Anchor.TOP));
        addJDiagramActionToggle("graphSelectNode", JDiagramSelectNodeAction.class);
        addJDiagramActionToggle("graphAddCircle", JDiagramAddCircleAction.class);
        addJDiagramActionToggle("graphAddEllipse", JDiagramAddEllipseAction.class);
        addJDiagramActionToggle("graphAddSquare", JDiagramAddSquareAction.class);
        addJDiagramActionToggle("graphAddRectangle", JDiagramAddRectangleAction.class);
        addJDiagramActionToggle("graphAddSegment", JDiagramAddSegmentAction.class);
        addJDiagramActionToggle("graphAddEdge", JDiagramAddEdgeAction.class);
        addJDiagramActionToggle("graphAddText", JDiagramAddTextAction.class);


        toolbar.children()
                .addSeparator(Path.of("/Default/*"));

        toolbar.children()
                .add(new Button("graphClear", () -> getDiagram().clear()
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new UserControl("graphFillColor", new FillColorEditor(getCanvas()), app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new UserControl("graphLineColor", new LineColorEditor(getCanvas()), app()), Path.of("/Default/Line/*"));
        toolbar.children()
                .add(new UserControl("graphArrow", new ArrowEditor(getCanvas()), app()), Path.of("/Default/Arrows/*"));
        toolbar.children()
                .add(new UserControl("graphTextColor", new TextColorEditor(getCanvas()), app()), Path.of("/Default/Texts/*"));
        toolbar.children()
                .add(new Button("graphTextFont", ()->showFont(),app()), Path.of("/Default/Texts/*"));

        toolbar.children().addSeparator(Path.of("/Default/before-z"));

        toolbar.children()
                .add(new Button("move-up", () -> getDiagram().moveToLayer(GridLayerMode.UP, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("move-top", () -> getDiagram().moveToLayer(GridLayerMode.TOP, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("move-down", () -> getDiagram().moveToLayer(GridLayerMode.DOWN, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("move-bottom", () -> getDiagram().moveToLayer(GridLayerMode.BOTTOM, true)
                        , app()), Path.of("/Default/*"));

        toolbar.children().addSeparator(Path.of("/Default/before-align"));
        toolbar.children()
                .add(new Button("align-top", () -> getDiagram().align(GridAlignMode.TOP, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("align-bottom", () -> getDiagram().align(GridAlignMode.BOTTOM, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("align-left", () -> getDiagram().align(GridAlignMode.LEFT, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("align-right", () -> getDiagram().align(GridAlignMode.RIGHT, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("align-center-horizontally", () -> getDiagram().align(GridAlignMode.CENTER_HORIZONTAL, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("align-center-vertically", () -> getDiagram().align(GridAlignMode.CENTER_VERTICAL, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("distribute-horizontally", () -> getDiagram().distribute(GridDistributeMode.HORIZONTAL, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("distribute-vertically", () -> getDiagram().distribute(GridDistributeMode.VERTICAL, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("align-position", () -> getDiagram().snapToGrid(GridMagnetMode.POSITION, true)
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("align-bounds", () -> getDiagram().snapToGrid(GridMagnetMode.BOUNDS, true)
                        , app()), Path.of("/Default/*"));

        toolbar.children().addSeparator(Path.of("/Default/before-zoom-separator"));
        toolbar.children()
                .add(new Button("zoom-in", () -> getDiagram().zoomIn()
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("zoom-out", () -> getDiagram().zoomOut()
                        , app()), Path.of("/Default/*"));
        toolbar.children()
                .add(new Button("un-zoom", () -> getDiagram().unzoom()
                        , app()), Path.of("/Default/*"));

        toolbar.children().addSeparator(Path.of("/Default/before-grid"));

        CheckBox t = new CheckBox("gridVisible", frame.app());
        t.selected().bindTarget(b -> getDiagram().setGridVisible(b));
        getDiagram().addDiagramChangeListener(new JDiagramListener() {
            @Override
            public void propertyChanged(JDiagram diagram, JDiagramGeometry geometry, String property, Object oldValue, Object newValue) {
                if ("gridVisible".equals(property)) {
                    t.selected().set((Boolean) newValue);
                }
            }
        });
        toolbar.children().add(
                new NumberSlider(
                        "gridSize",int.class,app()
                ).with((NumberSlider s)->{
                    s.min().set(5);
                    s.max().set(400);
                    s.snapToTicks().set(true);
                    s.step().set(5);
                    s.value().set(getDiagram().getGridX());
                    getCanvas().addDiagramChangeListener(new JDiagramListener() {
                        @Override
                        public void propertyChanged(JDiagram diagram, JDiagramGeometry geometry, String property, Object oldValue, Object newValue) {
                            if (property.equals("gridX") || property.equals("gridY")) {
                                s.value().set(getDiagram().getGridX());
                            }
                        }
                    });
                    s.onChange((a) -> getDiagram().setGridX(a.newValue()));
                }),Path.of("/Default/Grid/*")
        );
        frame.getNutsWorkspace().elem().setMapper(JDiagramBounds.class, new JDiagramBoundsMapper());
        frame.getNutsWorkspace().elem().setMapper(JDiagramGeometry.class, new JDiagramGeometryMapper());
    }

    @Override
    public void requestFocus() {
        canvas.requestFocus();
    }
    
    private ToggleButton addJDiagramActionToggle(String id, Class clazz) {
        ToggleButton t = new ToggleButton(id, "Graph.Tool", frame.app());
        t.selected().bindTarget(b -> {
            if (b) {
                canvas.setAction(clazz);
            }
        });
        canvas.addDiagramChangeListener(new JDiagramListener() {
            @Override
            public void propertyChanged(JDiagram diagram, JDiagramGeometry geometry, String property, Object oldValue, Object newValue) {
                if ("action".equals(property)) {
                    JDiagramEditorAction a = canvas.getAction();
                    t.selected().set(
                            clazz.isInstance(a)
                    );
                }
            }
        });
        JDiagramEditorAction a = canvas.getAction();
        t.selected().set(
                clazz.isInstance(a)
        );
        toolbar.children().add(
                t, Path.of("/Default/" + id)
        );
        return t;
    }

    public JDiagramCanvas getCanvas() {
        return canvas;
    }

    public JDiagram getDiagram() {
        return canvas.getDiagram();
    }

    public void uninstall() {
        //
    }

    public void setNote(PangaeaNote note) {
        this.note = note;
        NutsElement cnt = note.getContent();
        PangaeaNoteDiaService service = (PangaeaNoteDiaService) frame.app().getContentTypeService(PangaeaNoteDiaService.DIAGRAM);
        canvas.setDiagram(service.elementToDiagram(cnt));
    }

    protected void updateNoteFromDiagram() {
        if (note != null) {
            JDiagramModel d = canvas.getDiagram().getModel();
            PangaeaNoteDiaService service = (PangaeaNoteDiaService) frame.app().getContentTypeService(PangaeaNoteDiaService.DIAGRAM);
            note.setContent(service.diagramToElement(d));
        }
    }

    public void setEditable(boolean b) {
        getDiagram().setEditable(b);
    }

    public boolean isEditable() {
        return getDiagram().isEditable();
    }

    public boolean isCompactMode() {
        return compactMode;
    }


    public void showFont() {
        int sc = canvas.getDiagram().getSelectionCount();
        Font initialFont = null;
        if (sc == 0) {
            initialFont = Font.of(canvas.getDiagram().getConfigTextFont(),app());
        } else {
            initialFont = Font.of(canvas.getDiagram().getSelectedElements().get(0).getTextFont(),app());
        }
        FontChooser jfc = new FontChooser(app());
        jfc.selection().set(initialFont);
        if (jfc.showDialog(null)) {
            AppFont font = jfc.selection().get();
            if (sc == 0) {
                canvas.getDiagram().setConfigTextFont(Font.format(font));
            } else {
                canvas.getDiagram().setSelectionTextFont(Font.format(font));
            }
        }
    }

}
