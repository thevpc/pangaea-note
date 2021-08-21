package net.thevpc.pnote.core.types.rich.editor;

import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteTypes;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
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
