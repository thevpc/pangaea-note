package net.thevpc.pnote.core;

import net.thevpc.pnote.api.PangaeaNoteAppExtension;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.editors.SourceEditorService;
import net.thevpc.pnote.core.types.diagram.PangaeaNoteDiaService;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.core.types.file.PangaeaNoteFileService;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.core.types.rich.PangaeaNoteRichService;
import net.thevpc.pnote.core.types.sourcecode.html.PangaeaNoteHtmlService;
import net.thevpc.pnote.core.viewers.folder.FolderViewer;
import net.thevpc.pnote.core.viewers.fromeditor.NoteEditorToViewer;
import net.thevpc.pnote.core.viewers.image.ImageViewer;
import net.thevpc.pnote.core.viewers.libreoffice.LibreOfficeViewer;
import net.thevpc.pnote.core.viewers.pdf.PdfViewer;
import net.thevpc.pnote.core.viewers.web.WebViewer;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.service.DefaultPangaeaNoteSourceCodeService;
import net.thevpc.pnote.service.refactor.EmptySourceContentTypeReplacer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CorePangaeaNoteApp {

    private PangaeaNoteAppExtension e = new PangaeaNoteAppExtension() {
        @Override
        public void onLoad(PangaeaNoteApp app) {
            CorePangaeaNoteApp.this.onLoad(app);
        }

        @Override
        public void onDisable(PangaeaNoteApp app) {
            //
        }

        @Override
        public void onEnable(PangaeaNoteApp app) {
            //
        }

        @Override
        public void uninstallNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteFrame win) {
            CorePangaeaNoteApp.this.uninstallNoteEditorTypeComponent(editorContentType, component, win);
        }

        @Override
        public void installNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteFrame win) {
            CorePangaeaNoteApp.this.installNoteEditorTypeComponent(editorContentType, component, win);
        }
    };

    public PangaeaNoteAppExtension asExtension() {
        return e;
    }

    public void onLoad(PangaeaNoteApp app) {
        app.installNoteTypeService(new PangaeaNotePlainTextService());
        app.installNoteTypeService(new PangaeaNoteRichService());

        app.installNoteTypeService(new PangaeaNoteListService());
        app.installNoteTypeService(new PangaeaNoteFormsService());
        app.installNoteTypeService(new PangaeaNoteFileService());
        app.installNoteTypeService(new PangaeaNoteEmbeddedService());

        for (String sourceContentType : PangaeaNoteApp.SOURCE_CONTENT_TYPES) {
            List<String> extraList = new ArrayList<>();
            List<String> extList = new ArrayList<>();
            PangaeaNoteMimeType contentType = null;
            String group = "sources";
            PangaeaNoteMimeType contentType0 = PangaeaNoteMimeType.of(sourceContentType);
            contentType = PangaeaNoteMimeType.of(contentType0.getMajor() + "/" + contentType0.getMinor());
            for (Map.Entry<String, String> entry : contentType.getProperties().entrySet()) {
                switch (entry.getKey()) {
                    case "ext": {
                        extList.addAll(Arrays.asList(entry.getValue().split(",")));
                        break;
                    }
                    case "group": {
                        group = entry.getValue();
                        break;
                    }
                    default: {
                        if (entry.getValue().isEmpty() && entry.getKey().contains("/")) {
                            extraList.add(entry.getValue());
                        }
                    }
                }
            }
            app.installNoteTypeService(new DefaultPangaeaNoteSourceCodeService(
                            contentType,
                            group,
                            extraList.toArray(new String[0]),
                            extList.toArray(new String[0])
                    )
            );
        }

        app.installNoteTypeService(new PangaeaNoteHtmlService());
        if (false) {
            app.installNoteTypeService(new PangaeaNoteDiaService());
        }

        app.installTypeReplacer(new EmptySourceContentTypeReplacer());
        app.installEditorService(new SourceEditorService());

        app.installViewer(new NoteEditorToViewer());
        app.installViewer(new ImageViewer());
        app.installViewer(new PdfViewer());
        app.installViewer(new LibreOfficeViewer());
        app.installViewer(new WebViewer());
        app.installViewer(new FolderViewer());
    }

    public void uninstallNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteFrame win) {

    }

    public void installNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteFrame win) {
    }
}
