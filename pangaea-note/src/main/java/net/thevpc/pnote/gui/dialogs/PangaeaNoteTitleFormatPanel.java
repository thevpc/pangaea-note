/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.thevpc.common.swing.color.ColorChooserButton;
import net.thevpc.common.swing.color.ColorUtils;
import net.thevpc.common.swing.layout.GridBagLayoutSupport;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 *
 * @author vpc
 */
public class PangaeaNoteTitleFormatPanel extends JPanel {

    private PangaeaNoteWindow win;
    private ColorChooserButton foregroundEditor;
    private ColorChooserButton backgroundEditor;
    private JCheckBox boldEditor;
    private JCheckBox italicEditor;
    private JCheckBox underlinedEditor;
    private JCheckBox strikedEditor;

    public PangaeaNoteTitleFormatPanel(PangaeaNoteWindow win) {
        this.win = win;
        foregroundEditor = new ColorChooserButton();
        backgroundEditor = new ColorChooserButton();
        boldEditor = new JCheckBox(win.app().i18n().getString("Message.titleBold"));
        italicEditor = new JCheckBox(win.app().i18n().getString("Message.titleItalic"));
        underlinedEditor = new JCheckBox(win.app().i18n().getString("Message.titleUnderlined"));
        strikedEditor = new JCheckBox(win.app().i18n().getString("Message.titleStriked"));

        Box modifiersEditor = Box.createHorizontalBox();
        modifiersEditor.add(boldEditor);
        modifiersEditor.add(italicEditor);
        modifiersEditor.add(underlinedEditor);
        modifiersEditor.add(strikedEditor);
        GridBagLayoutSupport gbs = GridBagLayoutSupport.load(EditNoteDialog.class.getResource(
                "/net/thevpc/pnote/forms/PangaeaNoteTitleFormatPanel.gbl-form"
        ));
        gbs.bind("foregroundLabel", new JLabel(win.app().i18n().getString("Message.titleForegroundColor")));
        gbs.bind("foregroundEditor", foregroundEditor);
        gbs.bind("backgroundLabel", new JLabel(win.app().i18n().getString("Message.titleBackgroundColor")));
        gbs.bind("backgroundEditor", backgroundEditor);
        gbs.bind("modifiersEditor", modifiersEditor);
        gbs.apply(this);
    }

    public void loadFromNote(PangaeaNote note) {
        foregroundEditor.setColorValue(ColorUtils.parseColor(note.getTitleForeground()));
        backgroundEditor.setColorValue(ColorUtils.parseColor(note.getTitleBackground()));
        foregroundEditor.setPreferredSize(new Dimension(20, 20));
        backgroundEditor.setPreferredSize(new Dimension(20, 20));
        boldEditor.setSelected(note.isTitleBold());
        italicEditor.setSelected(note.isTitleItalic());
        underlinedEditor.setSelected(note.isTitleUnderlined());
        strikedEditor.setSelected(note.isTitleStriked());
    }

    public void loadToNote(PangaeaNote note) {
        note.setTitleBackground(ColorUtils.formatColor(backgroundEditor.getColorValue()));
        note.setTitleForeground(ColorUtils.formatColor(foregroundEditor.getColorValue()));
        note.setTitleBold(boldEditor.isSelected());
        note.setTitleItalic(italicEditor.isSelected());
        note.setTitleUnderlined(underlinedEditor.isSelected());
        note.setTitleStriked(strikedEditor.isSelected());
    }

}
