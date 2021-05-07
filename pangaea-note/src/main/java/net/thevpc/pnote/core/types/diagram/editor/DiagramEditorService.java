package net.thevpc.pnote.core.types.diagram.editor;

import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.core.types.diagram.PangaeaNoteDiaService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;

public class DiagramEditorService implements PangaeaNoteEditorService {
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteWindow win) {
        switch (name) {
            case PangaeaNoteDiaService.DIAGRAM_EDITOR:
                return new JDiagramEditor(compactMode, win);
        }
        return null;
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {

    }
}
