/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import net.thevpc.echo.Application;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.GuiHelper;
import net.thevpc.pnote.model.PangaeaNoteExt;
import sun.swing.DefaultLookup;

/**
 *
 * @author vpc
 */
class SimpleDefaultTreeCellRendererImpl extends DefaultTreeCellRenderer {

    private PangaeaNoteGuiApp sapp;
    private Application app;
    Font _font;
    Color _foreground;
    Color _background;
    Boolean _opaque;
    Color _textSelectionColor;
    Color _textNonSelectionColor;
    Color _backgroundSelectionColor;
    Color _backgroundNonSelectionColor;

    public SimpleDefaultTreeCellRendererImpl(PangaeaNoteGuiApp sapp) {
        this.sapp = sapp;
        this.app = sapp.app();
        _background = getBackground();
        _foreground = getForeground();
        _opaque = isOpaque();
//        if (getFont() != null && _font == null) {
//            _font = getFont();
//        }
//        _textSelectionColor = getTextSelectionColor();
//        _textNonSelectionColor = getTextNonSelectionColor();
//        _backgroundSelectionColor = getBackgroundSelectionColor();
//        _backgroundNonSelectionColor = getBackgroundNonSelectionColor();
    }

    public void updateUI() {
        super.updateUI();
        _font = UIManager.getFont("Label.font");
        _textSelectionColor = (DefaultLookup.getColor(this, ui, "Tree.selectionForeground"));
        _textNonSelectionColor = (DefaultLookup.getColor(this, ui, "Tree.textForeground"));
        _backgroundSelectionColor = (DefaultLookup.getColor(this, ui, "Tree.selectionBackground"));
        _backgroundNonSelectionColor = (DefaultLookup.getColor(this, ui, "Tree.textBackground"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setBackgroundSelectionColor(_backgroundSelectionColor);
        setBackgroundNonSelectionColor(_backgroundNonSelectionColor);
        setTextNonSelectionColor(_textNonSelectionColor);
        setTextSelectionColor(_textSelectionColor);
        setBackground(null);

        setOpaque(false);
        if (value instanceof PangaeaNoteExt) {
            PangaeaNoteExt n = (PangaeaNoteExt) value;
            setFont(GuiHelper.deriveFont(_font, n.isTitleBold(), n.isTitleItalic(), n.isTitleUnderlined(), n.isTitleStriked()));
            if (sel) {
            } else {
                Color b = GuiHelper.parseColor(n.getTitleBackground());
                if (b != null) {
//                    setOpaque(true);
                    setBackgroundNonSelectionColor(b);
                    setBackground(b);
                    setOpaque(true);
                }
            }
            {
                Color b = GuiHelper.parseColor(n.getTitleForeground());
                if (b != null) {
                    setTextNonSelectionColor(b);
                    setTextSelectionColor(b);
                }
            }
        } else {
//            if (_opaque != null) {
//                setOpaque(_opaque);
//            }
            if (_font != null) {
                setFont(_font);
            }
        }
//                    setBackground(Color.GREEN);
//                    setOpaque(true);
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof PangaeaNoteExt) {
            PangaeaNoteExt n = (PangaeaNoteExt) value;
            String iconName = sapp.service().getNoteIcon(n.toNote(), n.getChildren().size() > 0, expanded);
            Icon icon = app.iconSets().icon(iconName).get();
            setIcon(icon);
        } else {
            setIcon(resolveIcon("file"));
        }
        return this;
    }

    protected Icon resolveIcon(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        return app.iconSets().icon(name).get();
    }

}
