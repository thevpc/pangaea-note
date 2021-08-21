/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.components;

import net.thevpc.echo.Application;
import net.thevpc.echo.Color;
import net.thevpc.echo.ComboBox;
import net.thevpc.echo.ContextMenu;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.thevpc.echo.api.components.AppChoiceItemContext;
import net.thevpc.echo.api.components.AppChoiceItemRenderer;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldOptions;

/**
 *
 * @author vpc
 */
public class DayOfWeekComponent extends ComboBox<String> implements FormComponent {

    private Runnable callback;

    public DayOfWeekComponent(PangaeaNoteFrame frame) {
        super(String.class, frame.app());
        itemRenderer().set(new AppChoiceItemRenderer<String>() {
            @Override
            public void render(AppChoiceItemContext<String> context) {
                String v = context.getValue();
                if (v == null) {
                    v = "";
                }
                switch (v) {
                    case "1": {
                        context.setText(app().i18n().getString("DayOfWeek.sunday"));
                        break;
                    }
                    case "2": {
                        context.setText(app().i18n().getString("DayOfWeek.monday"));
                        break;
                    }
                    case "3": {
                        context.setText(app().i18n().getString("DayOfWeek.tuesday"));
                        break;
                    }
                    case "4": {
                        context.setText(app().i18n().getString("DayOfWeek.wednesday"));
                        break;
                    }
                    case "5": {
                        context.setText(app().i18n().getString("DayOfWeek.thursday"));
                        break;
                    }
                    case "6": {
                        context.setText(app().i18n().getString("DayOfWeek.friday"));
                        break;
                    }
                    case "7": {
                        context.setText(app().i18n().getString("DayOfWeek.saturday"));
                        break;
                    }
                    default:{
                        context.setText(app().i18n().getString("DayOfWeek.undefined"));
                    }
                }
                context.renderDefault();
            }
        });
        values().addCollection(IntStream.range(1, 7).mapToObj(x -> String.valueOf(x)).collect(Collectors.toList()));
        selection().onChange((e) -> callOnValueChanged());
    }

    private void callOnValueChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    public void setSelectValues(List<String> values) {
        //
    }

    @Override
    public String getContentString() {
        return selection().get();
    }

    @Override
    public void setContentString(String s) {
        selection().set(s);
    }

    public void uninstall() {
        callback = null;
    }

    public void install(Application app, ContextMenu contextMenu) {
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void setEditable(boolean b) {
        enabled().set(b);
    }

    @Override
    public boolean isEditable() {
        return enabled().get();
    }

}
