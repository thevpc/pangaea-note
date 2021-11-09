/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor;

import net.thevpc.diagram4j.JDiagramCanvas;
import net.thevpc.echo.api.AppToolToggleModel;
import net.thevpc.diagram4j.JDiagramEditorAction;

/**
 *
 * @author thevpc
 */
public class DiagramActionModel implements AppToolToggleModel {

    private Class<? extends JDiagramEditorAction> actionType;
    private JDiagramCanvas canvas;

    public DiagramActionModel(Class<? extends JDiagramEditorAction> actionType, JDiagramCanvas canvas) {
        this.actionType = actionType;
        this.canvas = canvas;
    }

    @Override
    public boolean isSelected() {
        JDiagramEditorAction a = canvas.getAction();
        return actionType.isInstance(a);
    }

    @Override
    public void setSelected(boolean b) {
        try {
            canvas.setAction(actionType.newInstance());
        } catch (Exception ex) {
            throw new IllegalArgumentException("cannot instantiate " + actionType, ex);
        }
    }

}
