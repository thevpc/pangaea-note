/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.components;

import net.thevpc.echo.Application;
import net.thevpc.echo.Color;
import net.thevpc.echo.ContextMenu;
import net.thevpc.echo.TemporalField;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldOptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * @author vpc
 */
public class DateFieldComponent extends TemporalField<LocalDate> implements FormComponent {

    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH)
            .toFormatter();
    private Runnable callback;

    public DateFieldComponent(PangaeaNoteFrame win) {
        super(LocalDate.class, win.app());
//        ContextMenu p = new ContextMenu(app());
//        contextMenu().set(p);
//        p.children().add(new Button("copy", () -> app().clipboard().putString(text().get().value()), app()));
//        p.children().add(new Button("paste", () -> text().set(Str.of(app().clipboard().getString())), app()));
//        text().onChange(x -> textChanged());
        value().onChange(x -> textChanged());
    }

    private void textChanged() {
        if (callback != null) {
            callback.run();
        }
    }

    @Override
    public void install(Application app, ContextMenu contextMenu) {
    }

    @Override
    public void setFormChangeListener(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void uninstall() {
        this.callback = null;
    }

    @Override
    public String getContentString() {
        LocalDate v = value().get();
        return v == null ? null : v.format(FORMATTER);
    }

    @Override
    public void setContentString(String s) {
        try {
            LocalDate e = (s == null || s.isEmpty()) ? null : LocalDate.parse(s, FORMATTER);
            value().set(e);
        }catch (Exception ex){
            //
        }
    }

    public void setEditable(boolean b) {
        editable().set(b);
    }

    public boolean isEditable() {
        return editable().get();
    }

}
