package net.thevpc.pnote.api;

import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.service.PangaeaNoteService;

import java.io.InputStream;

public interface PangaeaNoteFileImporter {
    String getName();
    String[] getSupportedFileExtensions();
    PangaeaNote loadNote(InputStream file, String preferredName, String fileExtension, PangaeaNoteService service);
}
