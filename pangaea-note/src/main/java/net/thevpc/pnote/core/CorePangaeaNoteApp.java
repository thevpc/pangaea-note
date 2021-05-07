package net.thevpc.pnote.core;

import net.thevpc.pnote.api.PangaeaNoteAppExtension;
import net.thevpc.pnote.core.editors.SourceEditorService;
import net.thevpc.pnote.core.types.diagram.PangaeaNoteDiaService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.service.refactor.EmptySourceContentTypeReplacer;
import net.thevpc.pnote.core.types.sourcecode.c.PangaeaNoteCService;
import net.thevpc.pnote.core.types.sourcecode.cpp.PangaeaNoteCppService;
import net.thevpc.pnote.core.types.file.PangaeaNoteFileService;
import net.thevpc.pnote.core.types.sourcecode.html.PangaeaNoteHtmlService;
import net.thevpc.pnote.core.types.sourcecode.java.PangaeaNoteJavaService;
import net.thevpc.pnote.core.types.sourcecode.javascript.PangaeaNoteJavascriptService;
import net.thevpc.pnote.core.types.markdown.PangaeaNoteMarkdownService;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.ntf.PangaeaNoteNTFService;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.core.types.rich.PangaeaNoteRichService;
import net.thevpc.pnote.core.types.sh.PangaeaNoteShService;
import net.thevpc.pnote.core.viewers.folder.FolderViewer;
import net.thevpc.pnote.core.viewers.fromeditor.NoteEditorToViewer;
import net.thevpc.pnote.core.viewers.image.ImageViewer;
import net.thevpc.pnote.core.viewers.libreoffice.LibreOfficeViewer;
import net.thevpc.pnote.core.viewers.pdf.PdfViewer;
import net.thevpc.pnote.core.viewers.web.WebViewer;

public class CorePangaeaNoteApp {
   private PangaeaNoteAppExtension e=new PangaeaNoteAppExtension() {
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
       public void uninstallNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteWindow win) {
           CorePangaeaNoteApp.this.uninstallNoteEditorTypeComponent(editorContentType, component, win);
       }

       @Override
       public void installNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteWindow win) {
           CorePangaeaNoteApp.this.installNoteEditorTypeComponent(editorContentType, component, win);
       }
   };
    public PangaeaNoteAppExtension asExtension(){
        return e;
    }

    public void onLoad(PangaeaNoteApp app) {
        app.service().installNoteTypeService(new PangaeaNotePlainTextService());
        app.service().installNoteTypeService(new PangaeaNoteRichService());
        app.service().installNoteTypeService(new PangaeaNoteMarkdownService());
        app.service().installNoteTypeService(new PangaeaNoteNTFService());

        app.service().installNoteTypeService(new PangaeaNoteListService());
        app.service().installNoteTypeService(new PangaeaNoteFormsService());
        app.service().installNoteTypeService(new PangaeaNoteFileService());
        app.service().installNoteTypeService(new PangaeaNoteEmbeddedService());

        app.service().installNoteTypeService(new PangaeaNoteHtmlService());
        app.service().installNoteTypeService(new PangaeaNoteJavaService());
        app.service().installNoteTypeService(new PangaeaNoteCppService());
        app.service().installNoteTypeService(new PangaeaNoteCService());
        app.service().installNoteTypeService(new PangaeaNoteJavascriptService());
        app.service().installNoteTypeService(new PangaeaNoteShService());
        app.service().installNoteTypeService(new PangaeaNoteDiaService());

        app.service().installTypeReplacer(new EmptySourceContentTypeReplacer());
        app.installEditorService(new SourceEditorService());
        
        app.installViewer(new NoteEditorToViewer());
        app.installViewer(new ImageViewer());
        app.installViewer(new PdfViewer());
        app.installViewer(new LibreOfficeViewer());
        app.installViewer(new WebViewer());
        app.installViewer(new FolderViewer());
    }

    public void uninstallNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteWindow win) {

    }

    public void installNoteEditorTypeComponent(String editorContentType, PangaeaNoteEditorTypeComponent component, PangaeaNoteWindow win) {
    }
}
