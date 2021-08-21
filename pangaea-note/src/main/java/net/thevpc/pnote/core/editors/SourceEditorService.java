package net.thevpc.pnote.core.editors;

import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteTypes;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.core.types.sourcecode.PangaeaNoteSourceEditorTypeComponent;

public class SourceEditorService implements PangaeaNoteEditorService {
    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_SOURCE:
                return new PangaeaNoteSourceEditorTypeComponent(compactMode, win);//"Source Code"
        }
        return null;
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {

    }
}
