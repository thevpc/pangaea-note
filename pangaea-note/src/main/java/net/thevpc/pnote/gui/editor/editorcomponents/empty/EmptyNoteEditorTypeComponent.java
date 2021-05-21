/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.empty;

import net.thevpc.common.props.Path;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.echo.BorderPane;
import net.thevpc.echo.Button;
import net.thevpc.echo.ContextMenu;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.Panel;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class EmptyNoteEditorTypeComponent extends BorderPane/*GradientPanel*/ implements PangaeaNoteEditorTypeComponent {

    private PangaeaNoteFrame frame;
    UpdateLocale updateLocale = new UpdateLocale();

    public EmptyNoteEditorTypeComponent(PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        PangaeaNoteApp app = frame.app();
        app.i18n().locale().onChange(updateLocale);


        ContextMenu treePopupMenu = new ContextMenu(app);
        contextMenu().set(treePopupMenu);
        treePopupMenu.children().add(new Button("AddChildNote", () -> frame.addNote(), app));
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("Import.Any", () -> frame.importFileInto(), app), Path.of("/Import/*"));
        treePopupMenu.children().add(new Button("Import.PangaeaNote", () ->
                frame.importFileInto(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString())
                , app), Path.of("/Import/*"));

        for (PangaeaNoteFileImporter fileImporter : frame.service().getFileImporters()) {
            treePopupMenu.children().add(new Button("Import." + fileImporter.getName(), () ->
                    frame.importFileInto(fileImporter.getSupportedFileExtensions())
                    , app), Path.of("/Import/*"));
        }
        treePopupMenu.children().addSeparator();
        treePopupMenu.children().add(new Button("SearchNote", () -> frame.searchNote(), app));

    }

//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        Graphics2D g2d = (Graphics2D) g;
//        Dimension s = getSize();
//
//        g2d.setRenderingHint(
//                RenderingHints.KEY_TEXT_ANTIALIASING,
//                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        g2d.setRenderingHint(
//                RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//
//        int x = s.width / 2;
//        int y = s.height / 2;
//
//        Color color = g2d.getColor();
//        Color shortcutTextColor = ColorResource.of("Label.disabledForeground,gray").get();
//        Color shortcutColor = ColorResource.of("Component.linkColor,darkcyan").get();
//        int smallFont = 12;
//        int startDescX = 70;
//        int startShortcutX = 120;
//        drawString("Pangaea Note", 0, -70, 32, color, g2d);
//        FontMetrics fm = g2d.getFontMetrics();
//        int x0 = s.width / 2;
//        int y0 = s.height / 2;
//        int tx = x0 - fm.stringWidth("Pangaea Note") / 2;
//        int ty = y0 - fm.getHeight() / 2 + fm.getAscent() + -70;
//        startDescX = tx + 20;
//        startShortcutX = tx + 150;
//        I18n i18n = win.app().i18n();
//
//        drawStringX(i18n.getString("Action.NewFile"), startDescX, -40, smallFont, shortcutTextColor, g2d);
//        drawStringX("CTRL+N", startShortcutX, -40, smallFont, shortcutColor, g2d);
//
//        drawStringX(i18n.getString("Action.OpenFile"), startDescX, -20, smallFont, shortcutTextColor, g2d);
//        drawStringX("CTRL+O", startShortcutX, -20, smallFont, shortcutColor, g2d);
//
//        drawStringX(i18n.getString("Action.AddNote"), startDescX, 0, smallFont, shortcutTextColor, g2d);
//        drawStringX("CTRL+B", startShortcutX, 0, smallFont, shortcutColor, g2d);
//
//        drawStringX(i18n.getString("Action.SearchNote"), startDescX, 20, smallFont, shortcutTextColor, g2d);
//        drawStringX("CTRL+SHIFT+F", startShortcutX, 20, smallFont, shortcutColor, g2d);
//    }
//
//    public void drawStringX(String text, int x, int dy, int fontSize, Color color, Graphics2D g2d) {
//        g2d.setFont(getFont().deriveFont(Font.BOLD, fontSize));
//        FontMetrics fm = g2d.getFontMetrics();
//        Dimension s = getSize();
//        int y = s.height / 2;
//        int ty = y - fm.getHeight() / 2 + fm.getAscent() + dy;
//        g2d.setColor(color);
//        g2d.drawString(text, x, ty);
//    }
//
//    public void drawString(String text, int dx, int dy, int fontSize, Color color, Graphics2D g2d) {
//        g2d.setFont(getFont().deriveFont(Font.BOLD, fontSize));
//        FontMetrics fm = g2d.getFontMetrics();
//        Dimension s = getSize();
//        int x = s.width / 2;
//        int y = s.height / 2;
//        int tx = x - fm.stringWidth(text) / 2 + dx;
//        int ty = y - fm.getHeight() / 2 + fm.getAscent() + dy;
//        g2d.setColor(color);
//        g2d.drawString(text, tx, ty);
//    }

    @Override
    public AppComponent component() {
        return this;
    }

    @Override
    public boolean isCompactMode() {
        return true;
    }

    @Override
    public void uninstall() {
        frame.app().i18n().locale().listeners().remove(updateLocale);
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteFrame win) {
    }

    @Override
    public void setEditable(boolean b) {

    }

    @Override
    public boolean isEditable() {
        return false;
    }

    private class UpdateLocale implements PropertyListener {

        public UpdateLocale() {
        }

        @Override
        public void propertyUpdated(PropertyEvent event) {
            //repaint();
        }
    }

}
