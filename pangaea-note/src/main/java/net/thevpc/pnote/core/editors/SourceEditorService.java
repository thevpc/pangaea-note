package net.thevpc.pnote.core.editors;

import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.source.SourceEditorPanePanel;

public class SourceEditorService implements PangaeaNoteEditorService {
    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteWindow win) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_SOURCE:
                return new SourceEditorPanePanel(true, compactMode, win);//"Source Code"
        }
        return null;
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {

    }
}
