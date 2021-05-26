/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.rich;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.thevpc.echo.RichHtmlEditor;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.core.types.rich.editor.RichEditorService;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.core.types.sourcecode.html.refactor.PlainToHtmlContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteRichService extends AbstractPangaeaNoteTypeService {

    public static final PangaeaNoteMimeType RICH_HTML = PangaeaNoteMimeType.of("application/html");

    public PangaeaNoteRichService() {
        super(RICH_HTML);
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }

    @Override
    public PangaeaNoteMimeType getContentType() {
        return RICH_HTML;
    }

    @Override
    public void onInstall(PangaeaNoteService service, PangaeaNoteApp app) {
        super.onInstall(service, app);
        service.installTypeReplacer(new PlainToHtmlContentTypeReplacer());
        app.installEditorService(new RichEditorService());
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_WYSIWYG;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note, PangaeaNoteFrame frame) {
        String content = getContentAsString(note.getContent());
        content = extractTextFromHtml(content, frame);
        return Arrays.asList(
                new StringDocumentTextNavigator<PangaeaNoteExt>("content", note, "content", content).iterator()
        );
    }

    @Override
    public boolean isEmptyContent(NutsElement element, PangaeaNoteFrame frame) {
        String content = service().elementToString(element);
        if (content == null || content.trim().length() == 0) {
            return true;
        }
        return extractTextFromHtml(content, frame).trim().isEmpty();
    }

    private String extractTextFromHtml(String content, PangaeaNoteFrame frame) {
        RichHtmlEditor ed = new RichHtmlEditor(content,frame.app());
        return ed.getText(0, ed.getTextLength());
    }

    @Override
    public NutsElement createDefaultContent() {
        return service().stringToElement("");
    }
}
