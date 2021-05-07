/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.empty;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JPanel;
import net.thevpc.common.i18n.I18n;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.jeep.editor.ColorResource;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;

/**
 *
 * @author vpc
 */
public class EmpyNNodtEditorTypeComponent extends JPanel/*GradientPanel*/ implements PangaeaNoteEditorTypeComponent {

    private PangaeaNoteWindow win;
    UpdateLocale updateLocale = new UpdateLocale();

    public EmpyNNodtEditorTypeComponent(PangaeaNoteWindow win) {
        this.win = win;
//        setHue(180f / 256);
//        setContrastFactor(16);
//        setSaturation(0.3f);
//        setBackgroundImageURL(
//                EmpyNNodtEditorTypeComponent.class.getResource("/net/thevpc/pnote/icon-big.png")
//        );
        win.app().i18n().locale().listeners().add(updateLocale);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
//        FontMetrics fm = g.getFontMetrics();
        Dimension s = getSize();

//        g.setColor(Color.BLACK);
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        int x = s.width / 2;
        int y = s.height / 2;

//        textAt.translate(tx, ty);
//        TextLayout textTl = new TextLayout(text,
//                new Font("Helvetica", 1, 50),
//                new FontRenderContext(null, false, false));
//
//        AffineTransform textAt = new AffineTransform();
//        Shape textShape = textTl.getOutline(textAt);
//
//        int tx = (int) (s.getWidth() / 2 - textTl.getBounds().getWidth() / 2);
//        int ty = (int) (s.getHeight() / 2
//                - (int) textTl.getBounds().getHeight() / 2);
//        g2d.setTransform(textAt);
//        g2d.setStroke(new BasicStroke(2));
//        g2d.draw(textShape);
        Color color = g2d.getColor();
        Color shortcutTextColor = ColorResource.of("Label.disabledForeground,gray").get();
        Color shortcutColor = ColorResource.of("Component.linkColor,darkcyan").get();
        int smallFont = 12;
        int startDescX = 70;
        int startShortcutX = 120;
        drawString("Pangaea Note", 0, -70, 32, color, g2d);
        FontMetrics fm = g2d.getFontMetrics();
        int x0 = s.width / 2;
        int y0 = s.height / 2;
        int tx = x0 - fm.stringWidth("Pangaea Note") / 2;
        int ty = y0 - fm.getHeight() / 2 + fm.getAscent() + -70;
        startDescX = tx + 20;
        startShortcutX = tx + 150;
        I18n i18n = win.app().i18n();

        drawStringX(i18n.getString("Action.NewFile"), startDescX, -40, smallFont, shortcutTextColor, g2d);
        drawStringX("CTRL+N", startShortcutX, -40, smallFont, shortcutColor, g2d);

        drawStringX(i18n.getString("Action.OpenFile"), startDescX, -20, smallFont, shortcutTextColor, g2d);
        drawStringX("CTRL+O", startShortcutX, -20, smallFont, shortcutColor, g2d);

        drawStringX(i18n.getString("Action.AddNote"), startDescX, 0, smallFont, shortcutTextColor, g2d);
        drawStringX("CTRL+B", startShortcutX, 0, smallFont, shortcutColor, g2d);

        drawStringX(i18n.getString("Action.SearchNote"), startDescX, 20, smallFont, shortcutTextColor, g2d);
        drawStringX("CTRL+SHIFT+F", startShortcutX, 20, smallFont, shortcutColor, g2d);
    }

    public void drawStringX(String text, int x, int dy, int fontSize, Color color, Graphics2D g2d) {
        g2d.setFont(getFont().deriveFont(Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        Dimension s = getSize();
        int y = s.height / 2;
        int ty = y - fm.getHeight() / 2 + fm.getAscent() + dy;
        g2d.setColor(color);
        g2d.drawString(text, x, ty);
    }

    public void drawString(String text, int dx, int dy, int fontSize, Color color, Graphics2D g2d) {
        g2d.setFont(getFont().deriveFont(Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        Dimension s = getSize();
        int x = s.width / 2;
        int y = s.height / 2;
        int tx = x - fm.stringWidth(text) / 2 + dx;
        int ty = y - fm.getHeight() / 2 + fm.getAscent() + dy;
        g2d.setColor(color);
        g2d.drawString(text, tx, ty);
    }

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public boolean isCompactMode() {
        return true;
    }

    @Override
    public void uninstall() {
        win.app().i18n().locale().listeners().remove(updateLocale);
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow win) {
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
            repaint();
        }
    }

}
