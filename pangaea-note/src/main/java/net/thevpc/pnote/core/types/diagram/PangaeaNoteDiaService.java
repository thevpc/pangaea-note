/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.thevpc.nuts.NutsElement;
import net.thevpc.pnote.core.types.diagram.editor.DiagramEditorService;
import net.thevpc.diagram4j.model.JDiagramModel;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
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
    public List<? extends Iterator<DocumentTextPart<PangaeaNoteExt>>> resolveTextNavigators(PangaeaNoteExt note, PangaeaNoteFrame frame) {
        String content = getContentAsString(note.getContent());
        content = extractTextFromDiagram(content);
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
        return content;
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
