/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import net.thevpc.more.shef.ShefHelper;
import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.core.types.diagram.editor.DiagramEditorService;
import net.thevpc.diagram4j.model.JDiagramModel;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.service.search.strsearch.DocumentTextPart;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.service.AbstractPangaeaNoteTypeService;

/**
 *
 * @author vpc
 */
public class PangaeaNoteDiaService extends AbstractPangaeaNoteTypeService {

    public static final String DIAGRAM_EDITOR = "diagram";
    public static final PangaeaNoteMimeType DIAGRAM = PangaeaNoteMimeType.of("application/x-pangaea-note-diagram");

    public PangaeaNoteDiaService() {
        super(DIAGRAM);
    }

    @Override
    public ContentTypeSelector getContentTypeSelector() {
        return new ContentTypeSelector(getContentType(), "composed-documents", 0);
    }

    @Override
    public PangaeaNoteMimeType getContentType() {
        return DIAGRAM;
    }

    @Override
    public void onInstall(PangaeaNoteService service, PangaeaNoteApp app) {
        super.onInstall(service, app);
        app.installEditorService(new DiagramEditorService());
    }

    public String normalizeEditorType(String editorType) {
        return DIAGRAM_EDITOR;
    }

    @Override
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note) {
        String content = getContentAsString(note.getContent());
        content = extractTextFromDiagram(content);
        return Arrays.asList(
                new StringDocumentTextNavigator<PangaeaNoteExt>("content", note, "content", content).iterator()
        );
    }

    @Override
    public boolean isEmptyContent(NutsElement element) {
        String content = service().elementToString(element);
        if (content == null || content.trim().length() == 0) {
            return true;
        }
        return extractTextFromDiagram(content).trim().isEmpty();
    }

    public NutsElement diagramToElement(JDiagramModel elem) {
        return service().element().toElement(elem);
    }

    public JDiagramModel elementToDiagram(NutsElement elem) {
        if (elem.isNull() || elem.isString()) {
            return new JDiagramModel();
        }
        JDiagramModel a = service().element().convert(elem, JDiagramModel.class);
        if (a == null) {
            return new JDiagramModel();
        }
        return a;
    }

    private String extractTextFromDiagram(String content) {
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
        JDiagramModel dm = new JDiagramModel();
        dm.getConfig().setFillColor("random");
        dm.getConfig().setLineColor("random");
        dm.getConfig().setLineStroke("basic:");
        return diagramToElement(dm);
    }
}
