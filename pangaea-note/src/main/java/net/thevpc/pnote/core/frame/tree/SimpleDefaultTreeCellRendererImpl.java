/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame.tree;

import net.thevpc.echo.Application;
import net.thevpc.echo.Color;
import net.thevpc.echo.FontPosture;
import net.thevpc.echo.FontWeight;
import net.thevpc.echo.api.AppColor;
import net.thevpc.echo.api.AppFont;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.api.components.AppTree;
import net.thevpc.echo.api.components.AppTreeItemContext;
import net.thevpc.echo.api.components.AppTreeItemRenderer;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

/**
 *
 * @author thevpc
 */
class SimpleDefaultTreeCellRendererImpl implements AppTreeItemRenderer<PangaeaNote> {

    private PangaeaNoteFrame frame;
    private Application app;
    AppFont _font;
    AppColor _foreground;
    AppColor _background;
    Boolean _opaque;
    AppColor _textSelectionColor;
    AppColor _textNonSelectionColor;
    AppColor _backgroundSelectionColor;
    AppColor _backgroundNonSelectionColor;

    public SimpleDefaultTreeCellRendererImpl(PangaeaNoteFrame frame) {
        this.frame = frame;
        this.app = frame.app();
//        _background = getBackground();
//        _foreground = getForeground();
//        _opaque = isOpaque();
//        if (getFont() != null && _font == null) {
//            _font = getFont();
//        }
//        _textSelectionColor = getTextSelectionColor();
//        _textNonSelectionColor = getTextNonSelectionColor();
//        _backgroundSelectionColor = getBackgroundSelectionColor();
//        _backgroundNonSelectionColor = getBackgroundNonSelectionColor();
    }

//    public void updateUI() {
//        super.updateUI();
//        _font = UIManager.getFont("Label.font");
//        _textSelectionColor = (UIManager.getColor( "Tree.selectionForeground"));
//        _textNonSelectionColor = (UIManager.getColor( "Tree.textForeground"));
//        _backgroundSelectionColor = (UIManager.getColor( "Tree.selectionBackground"));
//        _backgroundNonSelectionColor = (UIManager.getColor( "Tree.textBackground"));
//    }

    @Override
    public void render(AppTreeItemContext<PangaeaNote> context) {
//        context.setBackgroundSelectionColor(_backgroundSelectionColor);
        context.setBackgroundNonSelectionColor(_backgroundNonSelectionColor);
        context.setTextNonSelectionColor(_textNonSelectionColor);
        context.setTextSelectionColor(_textSelectionColor);
        context.setBackgroundColor(null);
        if(_background==null) {
            _background = context.getBackgroundColor();
        }
        if(_background==null) {
            _foreground = context.getColor();
        }
        if(_opaque==null) {
            _opaque = context.isOpaque();
        }
        context.setOpaque(false);
        PangaeaNote value = context.getValue();
        if (value instanceof PangaeaNote) {
            PangaeaNote n = value;
            AppFont font = context.getFont();
            context.setText(n.getName());
            context.setTextFont(font==null?null:font.derive(null,null,n.isTitleBold()? FontWeight.BOLD : null,
                    n.isTitleItalic()? FontPosture.ITALIC : null));
            context.setTextUnderline(n.isTitleUnderlined());
            context.setTextStrikeThrough(n.isTitleStriked());
            if (context.isSelected()) {

            } else {
                AppColor b = Color.of(n.getTitleBackground(),app);
                if (b != null) {
                    context.setBackgroundNonSelectionColor(b);
                    context.setBackgroundColor(b);
                    context.setOpaque(true);
                }
            }
            {
                AppColor b = Color.of(n.getTitleForeground(),app);
                if (b != null) {
                    context.setTextNonSelectionColor(b);
                    context.setTextSelectionColor(b);
                }
            }
        } else {
            if (_font != null) {
                context.setTextFont(_font);
            }
        }

        context.renderDefaults();

        if (value instanceof PangaeaNote) {
            PangaeaNote n = (PangaeaNote) value;
            String iconName = frame.app().getNoteIcon(n, context.isExpanded());
            AppTree<PangaeaNote> tree = context.getTree();
            AppImage icon = app.iconSets().icon(iconName,tree);
            context.setIcon(icon);
        } else {
            context.setIcon(resolveIcon("file",context));
        }
    }

    protected AppImage resolveIcon(String name,AppTreeItemContext<PangaeaNote> context) {
        if (name == null || name.length() == 0) {
            return null;
        }
        return app.iconSets().icon(name,context.getTree());
    }

}
