/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor;

import net.thevpc.pnote.core.types.diagram.ser.JDiagramBoundsMapper;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Set;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import net.thevpc.common.swing.color.ColorUtils;
import net.thevpc.common.swing.font.FontUtils;
import net.thevpc.echo.AppToolBar;
import net.thevpc.echo.swing.SwingApplications;
import net.thevpc.echo.swing.core.swing.JComponentSupplier;
import net.thevpc.echo.swing.core.swing.JToolbarGroup;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.core.types.diagram.PangaeaNoteDiaService;
import net.thevpc.echo.AppToolButtonType;
import net.thevpc.pnote.core.types.diagram.editor.tools.StrokeUtils;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.diagram4j.JDiagramCanvas;
import net.thevpc.diagram4j.JFontFormatter;
import net.thevpc.diagram4j.actions.*;
import net.thevpc.diagram4j.model.JDiagram;
import net.thevpc.diagram4j.model.JDiagramModel;
import net.thevpc.diagram4j.JPaintFormatter;
import net.thevpc.diagram4j.JStrokeFormatter;
import net.thevpc.diagram4j.model.JDiagramBounds;
import net.thevpc.diagram4j.model.GridAlignMode;
import net.thevpc.diagram4j.model.GridDistributeMode;
import net.thevpc.diagram4j.model.GridLayerMode;
import net.thevpc.diagram4j.model.GridMagnetMode;
import net.thevpc.diagram4j.model.JDiagramGeometry;
import net.thevpc.diagram4j.model.JDiagramListener;
import net.thevpc.echo.AppToolToggleModel;
import net.thevpc.pnote.core.types.diagram.ser.JDiagramGeometryMapper;

/**
 *
 * @author vpc
 */
public class JDiagramEditor extends JPanel implements PangaeaNoteEditorTypeComponent {

    private boolean compactMode;
    private PangaeaNoteWindow win;
    private JDiagramCanvas canvas;
    private AppToolBar toolbar;
    private PangaeaNoteExt note;

    public JDiagramEditor(boolean compactMode, PangaeaNoteWindow win) {
        super(new BorderLayout());
        this.compactMode = compactMode;
        this.win = win;
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
            public Paint parsePaint(String paint) {
                return ColorUtils.parsePaint(paint);
            }

            @Override
            public String formatPaint(Paint paint) {
                return ColorUtils.formatPaint(paint);
            }
        }
        );
        this.canvas.getRenderManager().setStrokeFormatter(new JStrokeFormatter() {
            @Override
            public Stroke parseStroke(String stroke) {
                return StrokeUtils.parseStroke(stroke);
            }

            @Override
            public String formatStroke(Stroke stroke) {
                return StrokeUtils.formatStroke(stroke);
            }
        }
        );
        this.canvas.getRenderManager().setFontFormatter(new JFontFormatter() {
            @Override
            public Font parseFont(String font) {
                return FontUtils.parseFont(font);
            }

            @Override
            public String formatFont(Font font) {
                return FontUtils.formatFont(font);
            }
        }
        );
        add(canvas, BorderLayout.CENTER);
        toolbar = SwingApplications.Components.createToolBar(win.app());
        Box header = Box.createHorizontalBox();
        JToolbarGroup jtoolbarGroup = (JToolbarGroup) ((JComponentSupplier) toolbar).component();
        header.add(jtoolbarGroup);
        header.add(Box.createHorizontalGlue());
        add(header, BorderLayout.PAGE_START);

        toolbar.tools().<Boolean>addToggle()
                .bind(new DiagramActionModel(JDiagramSelectNodeAction.class, canvas))
                .path("/Default/graphSelectNode")
                .group("Graph.Tool")
                .buttonType(AppToolButtonType.BUTTON)
                .tool();

        toolbar.tools()
                .addToggle()
                .bind(new DiagramActionModel(JDiagramAddCircleAction.class, canvas))
                .path("/Default/graphAddCircle")
                .group("Graph.Tool")
                .buttonType(AppToolButtonType.BUTTON)
                .tool();

        toolbar.tools()
                .addToggle()
                .bind(new DiagramActionModel(JDiagramAddEllipseAction.class, canvas))
                .path("/Default/graphAddEllipse")
                .group("Graph.Tool")
                .buttonType(AppToolButtonType.BUTTON)
                .tool();

        toolbar.tools()
                .addToggle()
                .bind(new DiagramActionModel(JDiagramAddSquareAction.class, canvas))
                .path("/Default/graphAddSquare")
                .group("Graph.Tool")
                .buttonType(AppToolButtonType.BUTTON)
                .tool();

        toolbar.tools()
                .addToggle()
                .bind(new DiagramActionModel(JDiagramAddRectangleAction.class, canvas))
                .path("/Default/graphAddRectangle")
                .group("Graph.Tool")
                .buttonType(AppToolButtonType.BUTTON)
                .tool();

        toolbar.tools()
                .addToggle()
                .bind(new DiagramActionModel(JDiagramAddSegmentAction.class, canvas))
                .path("/Default/graphAddSegment")
                .group("Graph.Tool")
                .buttonType(AppToolButtonType.BUTTON)
                .tool();

        toolbar.tools()
                .addToggle()
                .bind(new DiagramActionModel(JDiagramAddEdgeAction.class, canvas))
                .path("/Default/graphAddEdge")
                .group("Graph.Tool")
                .buttonType(AppToolButtonType.BUTTON)
                .tool();

        toolbar.tools()
                .addToggle()
                .bind(new DiagramActionModel(JDiagramAddTextAction.class, canvas))
                .path("/Default/graphAddText")
                .group("Graph.Tool")
                .buttonType(AppToolButtonType.BUTTON)
                .tool();

        toolbar.tools()
                .addSeparator("/Default/sep1");

        toolbar.tools()
                .addAction()
                .bind(() -> {
                    getDiagram().clear();
                }
                ).path(
                        "/Default/graphClear").tool();

        toolbar.tools()
                .addCustomTool("/Default/Fill/graphFillColor", c -> new FillColorEditor(getCanvas()));

        toolbar.tools()
                .addCustomTool("/Default/Line/graphLineColor", c -> new LineColorEditor(getCanvas()));

        toolbar.tools()
                .addCustomTool("/Default/Arrows/graphArrow", c -> new ArrowEditor(getCanvas()));

        toolbar.tools()
                .addCustomTool("/Default/Texts/graphTextColor", c -> new TextColorEditor(getCanvas()));
        toolbar.tools()
                .addAction()
                .bind(new FontEditor(getCanvas()))
                .path("/Default/graphTextFont")
                .tool();

        //        AbstractSourceEditorPaneExtension.addActionList(
        //                "font-size", createSizeActions(
        //                        (c) -> {
        //                            getCanvas().getConfig().setLineThikness(c);
        //                        }
        //                ), jtoolbar, null, win.app());
        toolbar.tools().addSeparator("/Default/before-z");
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().moveToLayer(GridLayerMode.UP, true))
                .path("/Default/move-up")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().moveToLayer(GridLayerMode.TOP, true))
                .path("/Default/move-top")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().moveToLayer(GridLayerMode.DOWN, true))
                .path("/Default/move-down")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().moveToLayer(GridLayerMode.BOTTOM, true))
                .path("/Default/move-bottom")
                .tool();
        toolbar.tools().addSeparator("/Default/before-align");
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().align(GridAlignMode.TOP, true))
                .path("/Default/align-top")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().align(GridAlignMode.BOTTOM, true))
                .path("/Default/align-bottom")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().align(GridAlignMode.LEFT, true))
                .path("/Default/align-left")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().align(GridAlignMode.RIGHT, true))
                .path("/Default/align-right")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().align(GridAlignMode.CENTER_HORINZONTAL, true))
                .path("/Default/align-center-horizontally")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().align(GridAlignMode.VCENTER, true))
                .path("/Default/align-center-vertically")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().distribute(GridDistributeMode.HORIZONTAL, true))
                .path("/Default/distribute-horizontally")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().distribute(GridDistributeMode.VERTICAL, true))
                .path("/Default/distribute-vertically")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().snapeToGrid(GridMagnetMode.POSITION, true))
                .path("/Default/align-position")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().snapeToGrid(GridMagnetMode.BOUNDS, true))
                .path("/Default/align-bounds")
                .tool();
        toolbar.tools().addSeparator("/Default/before-zoom-separator");
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().zoomIn())
                .path("/Default/zoom-in")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().zoomOut())
                .path("/Default/zoom-out")
                .tool();
        toolbar.tools()
                .addAction()
                .bind((e) -> getDiagram().unzoom())
                .path("/Default/un-zoom")
                .tool();
        toolbar.tools().addSeparator("/Default/before-grid");
        toolbar.tools()
                .addToggle().buttonType(AppToolButtonType.CHECK)
                .bind(new AppToolToggleModel() {
                    @Override
                    public boolean isSelected() {
                        return getDiagram().isGridVisible();
                    }

                    @Override
                    public void setSelected(boolean b) {
                        getDiagram().setGridVisible(b);
                    }
                })
                .path("/Default/Grid/gridVisible")
                .tool();
        toolbar.tools()
                .addCustomTool("/Default/Grid/gridSize",
                        (c) -> {
                            JSlider sl = new JSlider(5, 400);
                            sl.setSnapToTicks(true);
                            sl.setExtent(5);
                            sl.setPaintTicks(true);
                            sl.setValue(getDiagram().getGridX());
                            getCanvas().addDiagramChangeListener(new JDiagramListener() {
                                @Override
                                public void propertyChanged(JDiagram diagram, JDiagramGeometry geometry, String property, Object oldValue, Object newValue) {
                                    if (property.equals("gridX") || property.equals("gridY")) {
                                        sl.setValue(getDiagram().getGridX());
                                    }
                                }
                            });
                            sl.addChangeListener((a) -> {
                                getDiagram().setGridX(sl.getValue());;
                                getDiagram().setGridY(sl.getValue());;
                            });
                            return sl;
                        }
                );
        win.getNutsWorkspace().formats().element().setMapper(JDiagramBounds.class, new JDiagramBoundsMapper());
        win.getNutsWorkspace().formats().element().setMapper(JDiagramGeometry.class, new JDiagramGeometryMapper());
    }

    public JDiagramCanvas getCanvas() {
        return canvas;
    }

    public JDiagram getDiagram() {
        return canvas.getDiagram();
    }

    public JComponent component() {
        return this;
    }

    public void uninstall() {
        //
    }

    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow win) {
        this.note = note;
        NutsElement cnt = note.getContent();
        PangaeaNoteDiaService service = (PangaeaNoteDiaService) win.service().getContentTypeService(PangaeaNoteDiaService.DIAGRAM);
        canvas.setDiagram(service.elementToDiagram(cnt));
        repaint();
    }

    public void setEditable(boolean b) {
        getDiagram().setEditable(b);
        repaint();
    }

    public boolean isEditable() {
        return getDiagram().isEditable();
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    protected void updateNoteFromDiagram() {
        if (note != null) {
            JDiagramModel d = canvas.getDiagram().getModel();
            PangaeaNoteDiaService service = (PangaeaNoteDiaService) win.service().getContentTypeService(PangaeaNoteDiaService.DIAGRAM);
            note.setContent(service.diagramToElement(d));
        }
    }
}
