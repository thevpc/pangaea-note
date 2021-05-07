/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.html;

import java.io.StringReader;
import java.io.Writer;
import java.util.Stack;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.core.types.sourcecode.html.refactor.PlainToHtmlContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Tidy;

/**
 *
 * @author vpc
 */
public class PangaeaNoteHtmlService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteMimeType HTML = PangaeaNoteMimeType.of("text/html");

    public PangaeaNoteHtmlService() {
        super(HTML);
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "sources", 0);
    }
    @Override
    public void onInstall(PangaeaNoteService service, PangaeaNoteApp app) {
        super.onInstall(service, app);
        service.installTypeReplacer(new PlainToHtmlContentTypeReplacer());
    }

    @Override
    public boolean isEmptyContent(NutsElement element) {
        String content = service().elementToString(element);
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


}
