/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.constraints.ParentWrapCount;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

/**
 * @author vpc
 */
public class PangaeaNoteTitleFormatPanel extends VerticalPane {

    private PangaeaNoteFrame frame;
    private ColorButton foregroundEditor;
    private ColorButton backgroundEditor;
    private CheckBox boldEditor;
    private CheckBox italicEditor;
    private CheckBox underlinedEditor;
    private CheckBox strikedEditor;

    public PangaeaNoteTitleFormatPanel(PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        title().set(Str.i18n("PangaeaNoteListSettingsComponent.titleLabel"));
        foregroundEditor = new ColorButton(app());
        backgroundEditor = new ColorButton(app());
        boldEditor = new CheckBox(Str.i18n("Message.titleBold"), app());
        italicEditor = new CheckBox(Str.i18n("Message.titleItalic"), app());
        underlinedEditor = new CheckBox(Str.i18n("Message.titleUnderlined"), app());
        strikedEditor = new CheckBox(Str.i18n("Message.titleStriked"), app());

        children().addAll(
                new VerticalPane(app())
                        .with((VerticalPane v) -> {
                            v.parentConstraints().add(new ParentWrapCount(2));
                            v.children().addAll(boldEditor,
                                    italicEditor,
                                    underlinedEditor,
                                    strikedEditor);
                        }),
                new VerticalPane(app())
                        .with((VerticalPane v) -> {
                            v.parentConstraints().add(new ParentWrapCount(2));
                            v.children().addAll(new Label(Str.i18n("Message.titleForegroundColor"), app()),
                                    foregroundEditor,
                                    new Label(Str.i18n("Message.titleBackgroundColor"), app()),
                                    backgroundEditor);
                        })

        );
    }

    public void loadFromNote(PangaeaNote note) {
        foregroundEditor.value().set(Color.of(note.getTitleForeground(), app()));
        backgroundEditor.value().set(Color.of(note.getTitleBackground(), app()));
        foregroundEditor.prefSize().set(new Dimension(20, 20));
        backgroundEditor.prefSize().set(new Dimension(20, 20));
        boldEditor.selected().set(note.isTitleBold());
        italicEditor.selected().set(note.isTitleItalic());
        underlinedEditor.selected().set(note.isTitleUnderlined());
        strikedEditor.selected().set(note.isTitleStriked());
    }

    public void loadToNote(PangaeaNote note) {
        note.setTitleBackground(Color.format(backgroundEditor.value().get()));
        note.setTitleForeground(Color.format(foregroundEditor.value().get()));
        note.setTitleBold(boldEditor.selected().get());
        note.setTitleItalic(italicEditor.selected().get());
        note.setTitleUnderlined(underlinedEditor.selected().get());
        note.setTitleStriked(strikedEditor.selected().get());
    }

}
