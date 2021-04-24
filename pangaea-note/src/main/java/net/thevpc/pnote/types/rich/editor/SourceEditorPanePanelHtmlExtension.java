/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.rich.editor;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import net.thevpc.common.swing.JFontChooser;
import net.thevpc.common.swing.RainbowIcon;
import net.thevpc.jeep.editor.JEditorPaneBuilder;
import net.thevpc.common.swing.RectColorIcon;
import net.thevpc.common.swing.SwingComponentUtils;
import net.thevpc.echo.Application;
import net.thevpc.more.shef.AlignEnum;
import net.thevpc.more.shef.BlocEnum;
import net.thevpc.more.shef.InlineStyleEnum;
import net.thevpc.more.shef.ShefHelper;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.editor.editorcomponents.source.AbstractSourceEditorPaneExtension;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class SourceEditorPanePanelHtmlExtension extends AbstractSourceEditorPaneExtension {

    AbstractSourceEditorPaneExtension.Context context;

    @Override
    public void uninstall(JEditorPaneBuilder editorBuilder, PangaeaNoteWindow sapp) {
        if (context != null) {
            for (Action action : context.getActions()) {
                uninstallAction(action, context);
            }
        }
    }

    @Override
    public void prepareEditor(JEditorPaneBuilder editorBuilder, boolean compactMode, PangaeaNoteWindow sapp) {
        JEditorPane editor = editorBuilder.editor();
        JPopupMenu popup = editor.getComponentPopupMenu();
        JToolBar bar = compactMode ? null : new JToolBar();
        Application app = sapp.app();
        context = new AbstractSourceEditorPaneExtension.Context(sapp, editor);

        addActionList("insert-bloc", createBlocTypeActions(editor), bar, popup, context);

        addActionList("font-styles", createfontStyleActions(editor), bar, popup, context);

        addActionList("align-text", createAlignActions(editor), bar, popup, context);

        addSeparator(bar, popup, context);
        addActionListener("insert-ul", (e) -> ShefHelper.runInsertBloc(editor, BlocEnum.UL), bar, popup, context);
        addActionListener("insert-ol", (e) -> ShefHelper.runInsertBloc(editor, BlocEnum.OL), bar, popup, context);

        addActionList("font-type", createFontFamilyActions(editor, sapp), bar, popup, context);

        addActionList("font-size",createFontActions(),bar, popup, context);

        addActionList("colors", createColorActions(app, editor), bar, popup, context);

        addSeparator(bar, popup, context);
        addActionList("insert-tag", new ActionInfo[]{
            new ActionInfo("insert-hr", al((e) -> ShefHelper.runInsertHorizontalRule(editor))),
            new ActionInfo("insert-break", new StyledEditorKit.InsertBreakAction()),}, bar, popup, context);

        addContentTypeChangeListener(context, new ContentTypeChangeListener() {
            @Override
            public void onContentTypeChanged(PangaeaNoteContentType contentType, Context context) {
                boolean isHtml = true;//always!!
                if (bar != null) {
                    bar.setVisible(isHtml);
                }
                context.setAllActionsVisible(isHtml);
                context.setAllActionsEnabled(isHtml);
            }
        });
        if (bar != null) {
            editorBuilder.header().add(bar);
        }
        boolean isHtml = "text/html".equals(editor.getContentType());
        if (bar != null) {
            bar.setVisible(isHtml);
        }
        context.setAllActionsVisible(isHtml);
        context.setAllActionsEnabled(isHtml);
    }

    protected static ActionInfo[] createBlocTypeActions(JEditorPane editor) {
        return new ActionInfo[]{
            new ActionInfo("insert-h1", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.H1))),
            new ActionInfo("insert-h2", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.H2))),
            new ActionInfo("insert-h3", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.H3))),
            new ActionInfo("insert-h4", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.H4))),
            new ActionInfo("insert-h5", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.H5))),
            new ActionInfo("insert-h6", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.H6))),
            new ActionInfo("insert-pre", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.PRE))),
            new ActionInfo("insert-div", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.DIV))),
            new ActionInfo("insert-p", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.P))),
            new ActionInfo("insert-blockquote", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.BLOCKQUOTE))),
            new ActionInfo("insert-ol", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.OL))),
            new ActionInfo("insert-ul", ((e) -> ShefHelper.runInsertBloc(editor, BlocEnum.UL))),};
    }

    protected static ActionInfo[] createfontStyleActions(JEditorPane editor) {
        return new ActionInfo[]{
            new ActionInfo("font-bold", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.BOLD)),
            new ActionInfo("font-italic", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.ITALIC)),
            new ActionInfo("font-underline", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.UNDERLINE)),
            new ActionInfo("font-strike", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.STRIKE)),
            new ActionInfo("font-sup", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.SUP)),
            new ActionInfo("font-sub", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.SUB)),
            null,
            new ActionInfo("font-strong", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.STRONG)),
            new ActionInfo("font-em", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.EM)),
            new ActionInfo("font-cite", (e) -> ShefHelper.runToggleInlineStyle(editor, InlineStyleEnum.CITE))
        };
    }

    protected static ActionInfo[] createAlignActions(JEditorPane editor) {
        return new ActionInfo[]{
            new ActionInfo("align-left", (e) -> ShefHelper.runTextAlign(editor, AlignEnum.LEFT)),
            new ActionInfo("align-center", (e) -> ShefHelper.runTextAlign(editor, AlignEnum.CENTER)),
            new ActionInfo("align-right", (e) -> ShefHelper.runTextAlign(editor, AlignEnum.RIGHT)),
            new ActionInfo("align-justify", (e) -> ShefHelper.runTextAlign(editor, AlignEnum.JUSTIFY)),};
    }

    protected static ActionInfo[] createFontFamilyActions(JEditorPane editor, PangaeaNoteWindow sapp) {
        List<ActionInfo> fontActions = new ArrayList<>();
        fontActions.addAll(Stream.of("SansSerif", "Serif", "Monospaced", "Dialog", "DialogInput")
                .map(x -> new ActionInfo(x, new StyledEditorKit.FontFamilyAction(x, x))
                        .setConstMessage(x)
                )
                .collect(Collectors.toList()));
        fontActions.add(null);
        fontActions.add(
                new ActionInfo("customFont",
                        (e) -> {
                            JFontChooser jfc = new JFontChooser(editor.getFont());
                            int f = jfc.showDialog(sapp.frame(), "Select Font");
                            if (f == JFontChooser.ACCEPT_OPTION) {
                                Font font = jfc.getSelectedFont();
                                MutableAttributeSet attr = new SimpleAttributeSet();
                                StyleConstants.setFontFamily(attr, font.getFamily());
                                StyleConstants.setFontSize(attr, font.getSize());
                                StyleConstants.setBold(attr, 0 != (font.getStyle() & Font.BOLD));
                                StyleConstants.setItalic(attr, 0 != (font.getStyle() & Font.ITALIC));
                                SwingComponentUtils.setCharacterAttributes(editor, attr, false);
                            }
                        }
                )
        );
        return fontActions.toArray(new ActionInfo[0]);
    }

    protected static ActionInfo[] createFontActions() {
        return Stream.of(6, 8, 10, 12, 14, 16, 20, 24, 32, 36, 48, 72)
                .map(x -> new ActionInfo(String.valueOf(x), new StyledEditorKit.FontSizeAction(String.valueOf(x), x))
                        .setConstMessage(String.valueOf(x))
                )
                .toArray(ActionInfo[]::new);
    }

    public static ActionInfo[] createColorActions(Application app,JEditorPane editor) {
        Map<String, Color> colMap = new LinkedHashMap<>();
        colMap.put("Color.blue", Color.blue);
        colMap.put("Color.red", Color.red);
        colMap.put("Color.yellow", Color.yellow);
        colMap.put("Color.gray", Color.gray);
        colMap.put("Color.green", Color.green);
        colMap.put("Color.orange", Color.orange);
        colMap.put("Color.cyan", Color.cyan);
        colMap.put("Color.black", Color.black);
        colMap.put("Color.white", Color.white);
        List<ActionInfo> colorActions = new ArrayList<>();
        for (Map.Entry<String, Color> x : colMap.entrySet()) {
            Color color = x.getValue();
            StyledEditorKit.ForegroundAction foregroundAction = new StyledEditorKit.ForegroundAction(
                    x.getKey(), x.getValue());
            colorActions.add(new ActionInfo(
                    String.valueOf(x.getKey()),
                    foregroundAction)
                    .setConstIcon(new RectColorIcon(color,
                             app.iconSets().config().get().getWidth()
                    ))
                    .setMessageId(String.valueOf(x.getKey())));
        }
        colorActions.add(null);
        colorActions.add(
                new ActionInfo(
                        "customColor",
                        (e) -> {
                            Color fg = JColorChooser.showDialog(null, "Select Color", null);
                            if (fg != null) {
                                MutableAttributeSet attr = new SimpleAttributeSet();
                                StyleConstants.setForeground(attr, fg);
                                SwingComponentUtils.setCharacterAttributes(editor, attr, false);
                            }
                        }
                )
                        .setMessageId("customColor")
                        .setConstIcon(
                                new RainbowIcon(app.iconSets().config().get().getWidth())
                        )
        );
        return colorActions.toArray(new ActionInfo[0]);
    }

}
