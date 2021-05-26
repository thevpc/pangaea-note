package net.thevpc.pnote.core.types.rich.editor;

import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.core.types.rich.editor.PangaeaNoteRichEditorTypeComponent;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;

public class RichEditorService implements PangaeaNoteEditorService {
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_WYSIWYG:
                return new PangaeaNoteRichEditorTypeComponent(compactMode, win);
        }
        return null;
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {

    }
}
