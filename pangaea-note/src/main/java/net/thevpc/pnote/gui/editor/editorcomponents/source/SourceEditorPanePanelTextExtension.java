/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor.editorcomponents.source;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.text.StyledEditorKit;
import net.thevpc.echo.Application;
import net.thevpc.jeep.editor.JEditorPaneBuilder;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;

/**
 *
 * @author vpc
 */
public class SourceEditorPanePanelTextExtension extends AbstractSourceEditorPaneExtension {

    AbstractSourceEditorPaneExtension.Context context;

    @Override
    public void uninstall(JEditorPaneBuilder editorBuilder, PangaeaNoteGuiApp sapp) {
        if (context != null) {
            for (Action action : context.getActions()) {
                uninstallAction(action, context);
            }
        }
    }

    public void prepareEditor(JEditorPaneBuilder editorBuilder, boolean compactMode, PangaeaNoteGuiApp sapp) {
        JToolBar bar = compactMode ? null : new JToolBar();
        Application app=sapp.app();
        JPopupMenu popup = editorBuilder.editor().getComponentPopupMenu();
        context = new AbstractSourceEditorPaneExtension.Context(sapp, editorBuilder.editor());
        addAction("copy", new StyledEditorKit.CopyAction(), bar, popup, context);
        addAction("paste", new StyledEditorKit.PasteAction(), bar, popup, context);
        addAction("cut", new StyledEditorKit.CutAction(), bar, popup, context);
        addSeparator(bar, popup, context);
        addContentTypeChangeListener(context, new ContentTypeChangeListener() {
            @Override
            public void onContentTypeChanged(String contentType, Context context) {
                if (bar != null) {
                    bar.setVisible(true);
                }
                context.setAllActionsVisible(true);
                context.setAllActionsEnabled(true);
            }
        });
        if (bar != null) {
            editorBuilder.header().add(bar);
            bar.setVisible(true);
        }
        context.setAllActionsVisible(true);
        context.setAllActionsEnabled(true);
    }

}
