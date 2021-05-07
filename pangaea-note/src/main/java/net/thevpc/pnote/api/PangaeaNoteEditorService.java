package net.thevpc.pnote.api;

import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteWindow;

public interface PangaeaNoteEditorService {
    PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteWindow win);

    void onInstall(PangaeaNoteApp app);
}
