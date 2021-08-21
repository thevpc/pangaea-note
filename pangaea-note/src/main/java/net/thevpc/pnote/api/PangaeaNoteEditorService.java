package net.thevpc.pnote.api;

import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

public interface PangaeaNoteEditorService {
    PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win);

    void onInstall(PangaeaNoteApp app);
}
