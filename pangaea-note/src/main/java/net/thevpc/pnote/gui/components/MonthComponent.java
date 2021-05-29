/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.echo.Application;
import net.thevpc.echo.ComboBox;
import net.thevpc.echo.ContextMenu;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.thevpc.echo.api.components.AppChoiceItemContext;
import net.thevpc.echo.api.components.AppChoiceItemRenderer;

/**
 *
 * @author vpc
 */
public class MonthComponent extends ComboBox<String> implements FormComponent {

    private Runnable callback;

    public MonthComponent(PangaeaNoteFrame frame) {
        super(String.class, frame.app());
        itemRenderer().set(new AppChoiceItemRenderer<String>() {
            @Override
            public void render(AppChoiceItemContext<String> context) {
                String v = context.getValue();
                if (v == null) {
                    v = "";
                }
                switch (v) {
                    case "": {
                        context.setText("<no-selection>");
                        break;
                    }
                    case "1": {
                        context.setText("january");
                        break;
                    }
                    case "2": {
                        context.setText("february");
                        break;
                    }
                    case "3": {
                        context.setText("march");
                        break;
                    }
                    case "4": {
                        context.setText("april");
                        break;
                    }
                    case "5": {
                        context.setText("may");
                        break;
                    }
                    case "6": {
                        context.setText("june");
                        break;
                    }
                    case "7": {
                        context.setText("july");
                        break;
                    }
                    case "8": {
                        context.setText("august");
                        break;
                    }
                    case "9": {
                        context.setText("september");
                        break;
                    }
                    case "10": {
                        context.setText("october");
                        break;
                    }
                    case "11": {
                        context.setText("november");
                        break;
                    }
                    case "12": {
                        context.setText("november");
                        break;
                    }
                }
                context.renderDefault();
            }
        });
        values().addCollection(IntStream.range(1, 13).mapToObj(x -> String.valueOf(x)).collect(Collectors.toList()));
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
