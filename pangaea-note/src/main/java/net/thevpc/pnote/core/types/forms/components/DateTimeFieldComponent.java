/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.components;

import net.thevpc.echo.*;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * @author thevpc
 */
public class DateTimeFieldComponent extends TemporalField<LocalDateTime> implements FormComponent {

    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR)
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH)
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE)
            .toFormatter();
    private Runnable callback;

    public DateTimeFieldComponent(PangaeaNoteFrame win) {
        super(LocalDateTime.class,win.app());
//        ContextMenu p = new ContextMenu(app());
//        contextMenu().set(p);
//        p.children().add(new Button("copy", () -> app().clipboard().putString(text().get().value()), app()));
//        p.children().add(new Button("paste", () -> text().set(Str.of(app().clipboard().getString())), app()));
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
        LocalDateTime v = value().get();
        return v == null ? null : v.format(FORMATTER);
    }

    @Override
    public void setContentString(String s) {
        try {
            LocalDateTime e = (s == null || s.isEmpty()) ? null : LocalDateTime.parse(s, FORMATTER);
            value().set(e);
        }catch (Exception ex){
            //just ignore...
        }
    }

    public void setEditable(boolean b) {
        editable().set(b);
    }

    public boolean isEditable() {
        return editable().get();
    }

}
