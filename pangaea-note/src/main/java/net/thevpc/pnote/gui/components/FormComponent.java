/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import java.util.List;
import net.thevpc.echo.Application;
import net.thevpc.echo.ContextMenu;

/**
 *
 * @author vpc
 */
public interface FormComponent {

    void install(Application app, ContextMenu contextMenu);

    default void setSelectValues(List<String> values) {
    }

    void setFormChangeListener(Runnable callback);

    void uninstall();

    String getContentString();

    void setContentString(String s);

    default void setContentType(String s) {

    }

    default String getContentType() {
        return null;
    }

    void setEditable(boolean b);

    boolean isEditable();

    default boolean isLargeComponent(){
        return false;
    }

}
