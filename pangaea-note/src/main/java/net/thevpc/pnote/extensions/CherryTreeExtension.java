/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.extensions;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.thevpc.nuts.*;
import net.thevpc.pnote.api.PangaeaNoteAppExtension;
import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.types.rich.PangaeaNoteRichService;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.util.PNoteUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author vpc
 */
public class CherryTreeExtension implements PangaeaNoteFileImporter , PangaeaNoteAppExtension {

    public CherryTreeExtension() {
    }

    @Override
    public String getName() {
        return "CherryTree";
    }

    @Override
    public void onLoad(PangaeaNoteApp app) {
        app.installFileImporter(this);
    }

    @Override
    public String[] getSupportedFileExtensions() {
        return new String[]{"ctd"};
    }

    @Override
    public PangaeaNote loadNote(InputStream stream, String preferredName, String fileExtension, PangaeaNoteApp service) {
        if("ctd".equals(fileExtension)) {
            try {
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                boolean safe = true;
                if (safe) {
                    documentFactory.setExpandEntityReferences(false);
                    // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
                    // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
                    setLenientFeature(documentFactory, "http://apache.org/xml/features/disallow-doctype-decl", true);

                    // If you can't completely disable DTDs, then at least do the following:
                    // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
                    // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
                    // JDK7+ - http://xml.org/sax/features/external-general-entities
                    setLenientFeature(documentFactory, "http://xerces.apache.org/xerces-j/features.html#external-general-entities", false);
                    setLenientFeature(documentFactory, "http://xerces.apache.org/xerces2-j/features.html#external-general-entities", false);
                    setLenientFeature(documentFactory, "http://xml.org/sax/features/external-general-entities", false);

                    // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
                    // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
                    // JDK7+ - http://xml.org/sax/features/external-parameter-entities
                    setLenientFeature(documentFactory, "http://xerces.apache.org/xerces-j/features.html#external-parameter-entities", false);
                    setLenientFeature(documentFactory, "http://xml.org/sax/features/external-parameter-entities", false);
                    setLenientFeature(documentFactory, "http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities", false);

                    // Disable external DTDs as well
                    setLenientFeature(documentFactory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                    // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
                    documentFactory.setXIncludeAware(false);
                    documentFactory.setValidating(false);
                }
                DocumentBuilder b;
                NutsSession session = service.appContext().getSession();
                try {
                    b = documentFactory.newDocumentBuilder();
                } catch (ParserConfigurationException ex) {
                    throw new NutsIOException(session, ex);
                }

                b.setErrorHandler(new ErrorHandler() {
                    @Override
                    public void warning(SAXParseException exception) throws SAXException {
                        NutsLoggerOp.of(PangaeaNoteApp.class,session)
                                .level(Level.FINEST).verb(NutsLogVerb.WARNING).log(NutsMessage.plain(exception.toString()));
                    }

                    @Override
                    public void error(SAXParseException exception) throws SAXException {
                        NutsLoggerOp.of(PangaeaNoteApp.class,session)
                                .level(Level.FINEST).verb(NutsLogVerb.WARNING)
                                .error(exception)
                                .log(NutsMessage.plain(exception.toString()));
                    }

                    @Override
                    public void fatalError(SAXParseException exception) throws SAXException {
                        NutsLoggerOp.of(PangaeaNoteApp.class,session)
                                .level(Level.FINEST).verb(NutsLogVerb.WARNING)
                                .error(exception)
                                .log(NutsMessage.plain(exception.toString()));
                    }
                });

                Document doc = b.parse(stream);
                NodeList childNodes = doc.getDocumentElement().getChildNodes();
                PangaeaNote cherryDocument = new PangaeaNote();
                cherryDocument.setName(preferredName);
                cherryDocument.setContentType(PangaeaNotePlainTextService.PLAIN.toString());
                cherryDocument.setContent(service.stringToElement(""));
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node n = childNodes.item(i);
                    if (n instanceof Element) {
                        Element e = (Element) n;
//                    if (e.getTagName().equals("node") || e.getTagName().equals("rich_text")) {
                        PangaeaNote a = parseCherryTreeXmlNote(e, service);
                        if (a != null) {
                            cherryDocument.getChildren().add(a);
                        }
//                    }
                    }
                }
                return cherryDocument;
            } catch (SAXException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        throw new IllegalArgumentException("unsupported type "+fileExtension);
    }


    static class RichText {

        Map<String, String> style = new HashMap<>();
        String text;
        boolean escaped;

        public boolean isEscaped() {
            return escaped;
        }

        public RichText setEscaped(boolean escaped) {
            this.escaped = escaped;
            return this;
        }

        public Map<String, String> getStyle() {
            return style;
        }

        public RichText setStyle(Map<String, String> style) {
            this.style = style;
            return this;
        }

        public RichText addStyle(String k, String v) {
            if (!NutsBlankable.isBlank(v)) {
                this.style.put(k, v);
            }
            return this;
        }

        public String getText() {
            return text;
        }

        public RichText setText(String text) {
            this.text = text;
            return this;
        }

    }

    public PangaeaNote parseCherryTreeXmlNote(Element e, PangaeaNoteApp app) {
        switch (e.getTagName()) {
            case "node": {
                PangaeaNote nn = new PangaeaNote();
                nn.setContentType(PangaeaNoteRichService.RICH_HTML.toString());
                //custom_icon_id="0" foreground="" is_bold="False" name="commandes et factures" prog_lang="custom-colors" readonly="False" tags="" ts_creation="0.0" ts_lastsave="0.0" unique_id="5"
                //custom_icon_id="0" foreground="" is_bold="False" prog_lang="custom-colors" readonly="False" ts_creation="0.0" ts_lastsave="0.0" unique_id="5"
                NamedNodeMap attrs = e.getAttributes();
                Map<String, String> noteContentStyle = new HashMap<>();
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node attr = attrs.item(i);
                    if (attr instanceof Attr) {
                        Attr a = (Attr) attr;
                        String k = a.getName();
                        String v = a.getValue();
                        if (!NutsBlankable.isBlank(k)) {
                            switch (k) {
                                case "name": {
                                    nn.setName(v);
                                    break;
                                }
                                case "tags": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        nn.setTags(
                                                Arrays.asList(
                                                        v.split("[ ,;:]")
                                                ).stream().filter(x -> x.length() > 0)
                                                        .collect(Collectors.toSet())
                                        );
                                    }
                                    break;
                                }
                                case "custom_icon_id": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        int x = 0;
                                        try {
                                            x = Integer.parseInt(v);
                                        } catch (Exception ex) {
                                            //
                                        }
                                        if (x > 0) {
                                            x = (x - 1) % app.getAllIcons().size();
                                            nn.setIcon((String) (app.getAllIcons().toArray()[x]));
                                        }
                                    }
                                    break;
                                }
                                case "foregournd": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        nn.setTitleForeground(v);
                                    }
                                    break;
                                }
                                case "background": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        nn.setTitleBackground(v);
                                    }
                                    break;
                                }
                                case "readonly": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        nn.setReadOnly(Boolean.parseBoolean(v));
                                    }
                                    break;
                                }
                                case "is_bold": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        nn.setTitleBold(Boolean.parseBoolean(v));
                                    }
                                    break;
                                }
                                case "ts_creation": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        double d = 0;
                                        try {
                                            d = Double.parseDouble(v);
                                        } catch (Exception ex) {
                                            //ignore
                                        }
                                        if (d != 0) {
                                            long ln = Double.doubleToLongBits(d);
                                            try {
                                                nn.setCreationTime(Instant.ofEpochMilli(ln));
                                            } catch (Exception ex) {
                                                //ignore
                                            }
                                        }
//                                        nn.setTitleBold(Boolean.parseBoolean(v));
                                    }
                                    break;
                                }
                                case "ts_lastsave": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        double d = 0;
                                        try {
                                            d = Double.parseDouble(v);
                                        } catch (Exception ex) {
                                            //ignore
                                        }
                                        if (d != 0) {
                                            long ln = Double.doubleToLongBits(d);
                                            try {
                                                nn.setLastModified(Instant.ofEpochMilli(ln));
                                            } catch (Exception ex) {
                                                //ignore
                                            }
                                        }
//                                        nn.setTitleBold(Boolean.parseBoolean(v));
                                    }
                                    break;
                                }
                                case "prog_lang": {
                                    if (!NutsBlankable.isBlank(v)) {
                                        switch (v) {
                                            case "java": {
                                                nn.setContentType("text/java");
                                                break;
                                            }
                                            case "c": {
                                                nn.setContentType("text/x-csrc");
                                                break;
                                            }
                                            case "cpp": {
                                                nn.setContentType("text/x-c++src");
                                                break;
                                            }
                                            case "custom-colors": {
                                                nn.setContentType("application/html");
                                                break;
                                            }
                                            default: {
                                                nn.setContentType("text/plain");
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    if (!NutsBlankable.isBlank(v)) {
                                        nn.getProperties().put(k, v);
                                    }
                                }
                            }
                        }
                    }
                }
                List<RichText> richTexts = new ArrayList<>();
                NodeList childNodes = e.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node c = childNodes.item(i);
                    if (c instanceof Element) {
                        Element e2 = (Element) c;
                        if (e2.getTagName().equals("node")) {
                            nn.getChildren().add(parseCherryTreeXmlNote(e2, app));
                        } else if (e2.getTagName().equals("rich_text")) {
                            String link = e.getAttribute("link");
                            if (!NutsBlankable.isBlank(link)) {
                                richTexts.add(
                                        new RichText()
                                                .addStyle("foreground", e2.getAttribute("foreground"))
                                                .addStyle("background", e2.getAttribute("background"))
                                                .setText(
                                                        "<a href='" + link + "'>"
                                                        + PNoteUtils.escapeHtml(e.getTextContent())
                                                        + "/>"
                                                ).setEscaped(true)
                                );
                            } else {
                                richTexts.add(
                                        new RichText()
                                                .addStyle("foreground", e2.getAttribute("foreground"))
                                                .addStyle("background", e2.getAttribute("background"))
                                                .setText(e.getTextContent())
                                );
                            }

                        }
                    }
                }
                if (nn.getContentType().equals(PangaeaNoteRichService.RICH_HTML.toString())) {
                    nn.setContent(
                            app.stringToElement(
                            "<html><head>"
                            + (noteContentStyle.isEmpty() ? "" : ("<style>" + buildStyle(noteContentStyle) + "</style>"))
                            + "</head><body>"
                            + richTexts.stream().map(x -> {
                                String t = x.getText();
                                if (!t.isEmpty()) {
                                    if (x.style.isEmpty()) {
                                        return x.isEscaped() ? t
                                                : ("<pre>"
                                                + PNoteUtils.escapeHtml(t)
                                                + "</pre>");
                                    } else {
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("<div style='");
                                        sb.append(buildStyle(x.style));
                                        sb.append("'>");
                                        sb.append(
                                                x.isEscaped() ? t
                                                : ("<pre>"
                                                + PNoteUtils.escapeHtml(t)
                                                + "</pre>")
                                        );
                                        sb.append("</div>");
                                        return sb;
                                    }
                                }
                                return t;
                            }).collect(Collectors.joining())
                            + "</body></html>")
                    );
                } else {
                    nn.setContent(
                            app.stringToElement(richTexts.stream().map(x -> x.getText()).collect(Collectors.joining()))
                    );
                }
                return nn;
            }
            default: {
            }
        }
        return null;
    }

    private String buildStyle(Map<String, String> s) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : s.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(entry.getValue());
            sb.append("; ");
        }
        return sb.toString();
    }

    private static void setLenientFeature(DocumentBuilderFactory dbFactory, String s, boolean b) {
        try {
            dbFactory.setFeature(s, b);
        } catch (Throwable ex) {
            //
        }
    }
}
