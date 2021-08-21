/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.components;

import net.thevpc.echo.Application;
import net.thevpc.echo.ContextMenu;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldOptions;
import net.thevpc.pnote.core.types.forms.util.PangaeaNoteFormUtils;

import java.util.List;

/**
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

    default String getContentType() {
        return null;
    }

    default void setContentType(String s) {

    }

    boolean isEditable();

    void setEditable(boolean b);

    default boolean isLargeComponent() {
        return false;
    }

    default void setOptions(PangaeaNoteFieldOptions pangaeaNoteFieldOptions){
        PangaeaNoteFormUtils.applyOptions(pangaeaNoteFieldOptions,(AppComponent) this);
    }
}
