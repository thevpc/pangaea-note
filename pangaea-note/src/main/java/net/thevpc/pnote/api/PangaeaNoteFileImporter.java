package net.thevpc.pnote.api;

import net.thevpc.pnote.api.model.PangaeaNote;

import java.io.InputStream;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

public interface PangaeaNoteFileImporter {
    String getName();
    String[] getSupportedFileExtensions();
    PangaeaNote loadNote(InputStream file, String preferredName, String fileExtension, PangaeaNoteApp app);
}
