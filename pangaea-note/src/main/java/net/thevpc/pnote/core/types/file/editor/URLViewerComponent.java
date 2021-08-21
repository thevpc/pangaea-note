/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.file.editor;

import net.thevpc.echo.api.components.AppComponent;

import javax.swing.JComponent;

/**
 *
 * @author vpc
 */
public interface URLViewerComponent extends AppComponent{

    void navigate(String url);

    boolean isEditable();

    void save();

    void setEditable(boolean editable);

    void disposeComponent();
}
