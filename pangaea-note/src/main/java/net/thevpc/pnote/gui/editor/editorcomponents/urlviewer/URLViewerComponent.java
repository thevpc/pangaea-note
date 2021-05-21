/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.urlviewer;

import net.thevpc.echo.api.components.AppComponent;

import javax.swing.JComponent;

/**
 *
 * @author vpc
 */
public interface URLViewerComponent {

    void setURL(String url);

    AppComponent component();

    boolean isEditable();

    void save();

    public void setEditable(boolean editable);

    void disposeComponent();
}
