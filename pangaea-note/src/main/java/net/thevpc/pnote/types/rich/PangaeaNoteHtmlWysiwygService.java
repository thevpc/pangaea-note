/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.rich;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import net.thevpc.more.shef.ShefHelper;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.types.rich.editor.RichEditor;
import net.thevpc.pnote.types.html.refactor.PlainToHtmlContentTypeReplacer;
import net.thevpc.pnote.model.PangaeaNoteContentType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteHtmlWysiwygService implements PangaeaNoteTypeService {

    public static final PangaeaNoteContentType RICH_HTML = PangaeaNoteContentType.of("text/html;editor=wysiwyg");
    private PangaeaNoteService service;

    public PangaeaNoteHtmlWysiwygService() {
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "simple-documents", 0);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return RICH_HTML;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        this.service = service;
        service.installTypeReplacer(new PlainToHtmlContentTypeReplacer(service));
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "datatype-rich";
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_WYSIWYG;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        String content = getContentAsString(note.getContent());
        content=extractTextFromHtml(content);
        return Arrays.asList(new StringDocumentTextNavigator("content", note, "content", content).iterator()
        );
    }

    @Override
    public PangaeaNoteEditorTypeComponent createEditor(String name, boolean compactMode, PangaeaNoteWindow sapp) {
        switch (name) {
            case PangaeaNoteTypes.EDITOR_WYSIWYG:
                return new RichEditor(compactMode, sapp);
        }
        return null;
    }

    @Override
    public boolean isEmptyContent(NutsElement element) {
        String content = service.elementToString(element);
        if (content == null || content.trim().length() == 0) {
            return true;
        }
        return extractTextFromHtml(content).trim().isEmpty();
    }

    public String getContentAsString(NutsElement e) {
        return service.elementToString(e);
    }

    private String extractTextFromHtml(String content) {
        JEditorPane ed = ShefHelper.installMin(new JEditorPane("text/html", ""));
        ed.setText(content);
//        String txt = null;
        try {
            return ed.getDocument().getText(0, ed.getDocument().getLength());
        } catch (BadLocationException ex) {
            return "";
        }

//        Tidy tidy = new Tidy();
//        org.w3c.dom.Document a = tidy.parseDOM(new StringReader(content == null ? "" : content), (Writer) null);
//        StringBuilder text = new StringBuilder();
//        Stack<Element> s = new Stack<>();
//        s.push(a.getDocumentElement());
//        while (!s.isEmpty()) {
//            Element q = s.pop();
//            switch (q.getTagName()) {
//                case "head":
//                case "style":
//                case "br": {
//                    break;
//                }
//                default: {
//                    NodeList cn = q.getChildNodes();
//                    for (int i = 0; i < cn.getLength(); i++) {
//                        org.w3c.dom.Node n = cn.item(i);
//                        if (n instanceof Text) {
//                            text.append(n.getTextContent());
//                        } else if (n instanceof Element) {
//                            s.push((Element) n);
//                        }
//                    }
//                }
//            }
//        }
//        return text.toString();
    }

    @Override
    public NutsElement createDefaultContent() {
        return service.stringToElement("");
    }
}
