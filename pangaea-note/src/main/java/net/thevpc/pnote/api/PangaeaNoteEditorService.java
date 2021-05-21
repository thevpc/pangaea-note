package net.thevpc.pnote.api;

import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

public interface PangaeaNoteEditorService {
    PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteFrame win);

    void onInstall(PangaeaNoteApp app);
}
