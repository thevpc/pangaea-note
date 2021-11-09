/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteFieldOptions;
import net.thevpc.pnote.util.PNoteUtils;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author thevpc
 */
public class PangaeaNoteTitleFormatPanel extends GridPane {

    private PangaeaNoteFrame frame;
    private ColorButton foregroundEditor;
    private ColorButton backgroundEditor;
    private CheckBox boldEditor;
    private CheckBox italicEditor;
    private CheckBox underlinedEditor;
    private CheckBox strikedEditor;
    private Label iconButtonLabel;
    private PangaeaNoteIconsButton iconButton;

    public PangaeaNoteTitleFormatPanel(PangaeaNoteFrame frame, boolean addIconButton) {
        super(1, frame.app());
        this.frame = frame;
        title().set(Str.i18n("PangaeaNoteListSettingsComponent.titleLabel"));
        foregroundEditor = new ColorButton("foregroundColor", app());
        foregroundEditor.tooltip().set(Str.of("Message.foregroundColor"));
        backgroundEditor = new ColorButton("backgroundColor", app());
        backgroundEditor.tooltip().set(Str.of("Message.backgroundEditor"));
        boldEditor = new CheckBox(Str.i18n("Message.titleBold"), app());
        italicEditor = new CheckBox(Str.i18n("Message.titleItalic"), app());
        underlinedEditor = new CheckBox(Str.i18n("Message.titleUnderlined"), app());
        strikedEditor = new CheckBox(Str.i18n("Message.titleStriked"), app());
        if (addIconButton) {
            iconButton = new PangaeaNoteIconsButton(frame);
            iconButtonLabel = new Label(Str.i18n("icon"), app());
        }

        children().addAll(
                new GridPane(4, app())
                        .with((GridPane v) -> {
                            v.parentConstraints().addAll(
                                    ContainerGrow.TOP_ROW,
                                    AllMargins.of(10, 5, 5, 5),
                                    ParentMargin.of(20, 0, 0, 0),
                                    AllAnchors.LEFT
                            );
                            v.children().addAll(
                                    boldEditor,
                                    italicEditor,
                                    underlinedEditor,
                                    strikedEditor//,
                            );
                        }),
                new GridPane(4, app())
                        .with((GridPane v) -> {
                            v.parentConstraints().addAll(
                                    ContainerGrow.TOP_ROW,
                                    AllMargins.of(10, 5, 5, 5),
                                    //                                    new ParentMargin(20, 0, 0, 0),
                                    AllAnchors.LEFT
                            );
                            v.children().addAll(
                                    Stream.of(
                                            new Label(Str.i18n("Message.titleForegroundColor"), app()),
                                            foregroundEditor.with((ColorButton c) -> {
                                                c.childConstraints().add(Fill.BOTH);
                                            }),
                                            new Label(Str.i18n("Message.titleBackgroundColor"), app()),
                                            backgroundEditor.with((ColorButton c) -> {
                                                c.childConstraints().add(Fill.BOTH);
                                            }),
                                            iconButtonLabel,
                                            iconButton
                                    ).filter(Objects::nonNull).toArray(AppComponent[]::new)
                            );
                        })
        );
    }

    public void loadFromOptions(PangaeaNoteFieldOptions options) {
        foregroundEditor.value().set(Color.of(options.getLabelForegroundColor(), app()));
        backgroundEditor.value().set(Color.of(options.getLabelBackgroundColor(), app()));
        foregroundEditor.prefSize().set(new Dimension(20, 20));
        backgroundEditor.prefSize().set(new Dimension(20, 20));
        boldEditor.selected().set(PNoteUtils.nonNullAndTrue(options.getLabelBold()));
        italicEditor.selected().set(PNoteUtils.nonNullAndTrue(options.getLabelItalic()));
        underlinedEditor.selected().set(PNoteUtils.nonNullAndTrue(options.getLabelUnderlined()));
        strikedEditor.selected().set(PNoteUtils.nonNullAndTrue(options.getLabelStriked()));
        if (iconButton != null) {
            iconButton.iconIdValue().set(options.getLabelIcon());
        }
    }

    public void loadToOptions(PangaeaNoteFieldOptions options) {
        options.setLabelBackgroundColor(Color.format(backgroundEditor.value().get()));
        options.setLabelForegroundColor(Color.format(foregroundEditor.value().get()));
        options.setLabelBold(boldEditor.selected().get());
        options.setLabelItalic(italicEditor.selected().get());
        options.setLabelUnderlined(underlinedEditor.selected().get());
        options.setLabelStriked(strikedEditor.selected().get());
        if (iconButton != null) {
            options.setLabelIcon(iconButton.iconIdValue().get());
        }
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
