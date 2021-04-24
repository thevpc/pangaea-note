/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.html;

import java.io.StringReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.types.html.refactor.PlainToHtmlContentTypeReplacer;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Tidy;

/**
 *
 * @author vpc
 */
public class PangaeaNoteHtmlService implements PangaeaNoteTypeService {

    public static final PangaeaNoteContentType HTML = PangaeaNoteContentType.of("text/html");

    private PangaeaNoteService service;

    public PangaeaNoteHtmlService() {
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "sources", 0);
    }

    @Override
    public PangaeaNoteContentType getContentType() {
        return HTML;
    }

    @Override
    public void onInstall(PangaeaNoteService service) {
        this.service = service;
        service.installTypeReplacer(new PlainToHtmlContentTypeReplacer(service));
    }

    @Override
    public String getContentTypeIcon(boolean folder, boolean expanded) {
        return "file-html";
    }

    public String normalizeEditorType(String editorType) {
        return PangaeaNoteTypes.EDITOR_SOURCE;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        return Arrays.asList(new StringDocumentTextNavigator("content", note, "content", getContentAsString(note.getContent())).iterator()
        );
    }

    public String getContentAsString(NutsElement e) {
        return service.elementToString(e);
    }

    @Override
    public boolean isEmptyContent(NutsElement element) {
        String content = service.elementToString(element);
        if (content == null || content.trim().length() == 0) {
            return true;
        }
        return extractTextFromHtml(content).trim().isEmpty();
    }

    private String extractTextFromHtml(String content) {
        Tidy tidy = new Tidy();
        org.w3c.dom.Document a = tidy.parseDOM(new StringReader(content == null ? "" : content), (Writer) null);
        StringBuilder text = new StringBuilder();
        Stack<Element> s = new Stack<>();
        s.push(a.getDocumentElement());
        while (!s.isEmpty()) {
            Element q = s.pop();
            switch (q.getTagName()) {
                case "head":
                case "style":
                case "br": {
                    break;
                }
                default: {
                    NodeList cn = q.getChildNodes();
                    for (int i = 0; i < cn.getLength(); i++) {
                        org.w3c.dom.Node n = cn.item(i);
                        if (n instanceof Text) {
                            text.append(n.getTextContent());
                        } else if (n instanceof Element) {
                            s.push((Element) n);
                        }
                    }
                }
            }
        }
        return text.toString();
    }

    @Override
    public NutsElement createDefaultContent() {
        return service.stringToElement("");
    }

}
