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

    public static final String[] SOURCE_CONTENT_TYPES = {
            "text/java;text/x-java",
            "text/x-csrc;text/x-c++hdr",
            "text/x-c++src",
            "text/javascript",
            "application/x-shellscript;ext=sh,zsh,tsh",
            "text/markdown;group=simple-documents",
            "text/x-nuts-text-format;group=simple-documents;ext=ntf",
            "application/x-hadra"
    };
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
        app.service().installNoteTypeService(new PangaeaNotePlainTextService());
        app.service().installNoteTypeService(new PangaeaNoteRichService());

        app.service().installNoteTypeService(new PangaeaNoteListService());
        app.service().installNoteTypeService(new PangaeaNoteFormsService());
        app.service().installNoteTypeService(new PangaeaNoteFileService());
        app.service().installNoteTypeService(new PangaeaNoteEmbeddedService());

        for (String sourceContentType : SOURCE_CONTENT_TYPES) {
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
            app.service().installNoteTypeService(new DefaultPangaeaNoteSourceCodeService(
                            contentType,
                            group,
                            extraList.toArray(new String[0]),
                            extList.toArray(new String[0])
                    )
            );
        }

        app.service().installNoteTypeService(new PangaeaNoteHtmlService());
        if (false) {
            app.service().installNoteTypeService(new PangaeaNoteDiaService());
        }

        app.service().installTypeReplacer(new EmptySourceContentTypeReplacer());
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
