/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.sourcecode.html;

import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.types.sourcecode.html.refactor.PlainToHtmlContentTypeReplacer;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteSourceCodeService;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteHtmlService extends AbstractPangaeaNoteSourceCodeService {

    public static final PangaeaNoteMimeType HTML = PangaeaNoteMimeType.of("text/html");

    public PangaeaNoteHtmlService() {
        super(HTML, "sources", new String[0], new String[]{".html", ".htm"});
    }

    @Override
    public void onInstall(PangaeaNoteApp app) {
        super.onInstall(app);
        app.installTypeReplacer(new PlainToHtmlContentTypeReplacer());
    }

    @Override
    public boolean isEmptyContent(NutsElement element) {
        String content = app().elementToString(element);
        if (content == null || content.trim().length() == 0) {
            return true;
        }
        return extractTextFromHtml(content).trim().isEmpty();
    }

    private String extractTextFromHtml(String content) {
        return content;
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

}
