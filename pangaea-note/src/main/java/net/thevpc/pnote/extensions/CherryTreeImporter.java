/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.extensions;

import java.io.File;
import java.io.IOException;
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
import net.thevpc.nuts.NutsIOException;
import net.thevpc.nuts.NutsLogVerb;
import net.thevpc.nuts.NutsSession;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.service.PangaeaNoteService;
import net.thevpc.pnote.types.c.PangaeaNoteCService;
import net.thevpc.pnote.types.cpp.PangaeaNoteCppService;
import net.thevpc.pnote.types.html.PangaeaNoteHtmlWysiwygService;
import net.thevpc.pnote.types.java.PangaeaNoteJavaService;
import net.thevpc.pnote.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.util.OtherUtils;
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
public class CherryTreeImporter {

    private PangaeaNoteService service;

    public CherryTreeImporter(PangaeaNoteService service) {
        this.service = service;
    }

    public PangaeaNote loadCherryTreeXmlFile(File file) {
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
            NutsSession session = service.getContext().getSession();
            try {
                b = documentFactory.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                throw new NutsIOException(session, ex);
            }

            b.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    session.getWorkspace().log().of(PangaeaNoteService.class).with().session(session)
                            .level(Level.FINEST).verb(NutsLogVerb.WARNING).log(exception.toString());
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    session.getWorkspace().log().of(PangaeaNoteService.class).with().session(session)
                            .level(Level.FINEST).verb(NutsLogVerb.WARNING).log(exception.toString());
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    session.getWorkspace().log().of(PangaeaNoteService.class).with().session(session)
                            .level(Level.FINEST).verb(NutsLogVerb.WARNING).log(exception.toString());
                }
            });

            Document doc = b.parse(file);
            NodeList childNodes = doc.getDocumentElement().getChildNodes();
            PangaeaNote cherryDocument = service.newDocument();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node n = childNodes.item(i);
                if (n instanceof Element) {
                    Element e = (Element) n;
//                    if (e.getTagName().equals("node") || e.getTagName().equals("rich_text")) {
                    PangaeaNote a = parseCherryTreeXmlNote(e);
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
            if (!OtherUtils.isBlank(v)) {
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

    public PangaeaNote parseCherryTreeXmlNote(org.w3c.dom.Element e) {
        switch (e.getTagName()) {
            case "node": {
                PangaeaNote nn = new PangaeaNote();
                nn.setContentType(PangaeaNoteHtmlWysiwygService.RICH_HTML.toString());
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
                        if (!OtherUtils.isBlank(k)) {
                            switch (k) {
                                case "name": {
                                    nn.setName(v);
                                    break;
                                }
                                case "tags": {
                                    if (!OtherUtils.isBlank(v)) {
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
                                    if (!OtherUtils.isBlank(v)) {
                                        int x = 0;
                                        try {
                                            x = Integer.parseInt(v);
                                        } catch (Exception ex) {
                                            //
                                        }
                                        if (x > 0) {
                                            x = (x - 1) % PangaeaNoteTypes.ALL_USER_ICONS.size();
                                            nn.setIcon((String) (PangaeaNoteTypes.ALL_USER_ICONS.toArray()[x]));
                                        }
                                    }
                                    break;
                                }
                                case "foregournd": {
                                    if (!OtherUtils.isBlank(v)) {
                                        nn.setTitleForeground(v);
                                    }
                                    break;
                                }
                                case "background": {
                                    if (!OtherUtils.isBlank(v)) {
                                        nn.setTitleBackground(v);
                                    }
                                    break;
                                }
                                case "readonly": {
                                    if (!OtherUtils.isBlank(v)) {
                                        nn.setReadOnly(Boolean.parseBoolean(v));
                                    }
                                    break;
                                }
                                case "is_bold": {
                                    if (!OtherUtils.isBlank(v)) {
                                        nn.setTitleBold(Boolean.parseBoolean(v));
                                    }
                                    break;
                                }
                                case "ts_creation": {
                                    if (!OtherUtils.isBlank(v)) {
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
                                    if (!OtherUtils.isBlank(v)) {
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
                                    if (!OtherUtils.isBlank(v)) {
                                        switch (v) {
                                            case "java": {
                                                nn.setContentType(PangaeaNoteJavaService.JAVA);
                                                break;
                                            }
                                            case "c": {
                                                nn.setContentType(PangaeaNoteCService.C);
                                                break;
                                            }
                                            case "cpp": {
                                                nn.setContentType(PangaeaNoteCppService.CPP);
                                                break;
                                            }
                                            case "custom-colors": {
                                                nn.setContentType(PangaeaNoteHtmlWysiwygService.RICH_HTML.toString());
                                                break;
                                            }
                                            default: {
                                                nn.setContentType(PangaeaNotePlainTextService.PLAIN);
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                default: {
                                    if (!OtherUtils.isBlank(v)) {
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
                            nn.getChildren().add(parseCherryTreeXmlNote(e2));
                        } else if (e2.getTagName().equals("rich_text")) {
                            String link = e.getAttribute("link");
                            if (!OtherUtils.isBlank(link)) {
                                richTexts.add(
                                        new RichText()
                                                .addStyle("foreground", e2.getAttribute("foreground"))
                                                .addStyle("background", e2.getAttribute("background"))
                                                .setText(
                                                        "<a href='" + link + "'>"
                                                        + OtherUtils.escapeHtml(e.getTextContent())
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
                if (nn.getContentType().equals(PangaeaNoteHtmlWysiwygService.RICH_HTML.toString())) {
                    nn.setContent(
                            service.stringToElement(
                            "<html><head>"
                            + (noteContentStyle.isEmpty() ? "" : ("<style>" + buildStyle(noteContentStyle) + "</style>"))
                            + "</head><body>"
                            + richTexts.stream().map(x -> {
                                String t = x.getText();
                                if (!t.isEmpty()) {
                                    if (x.style.isEmpty()) {
                                        return x.isEscaped() ? t
                                                : ("<pre>"
                                                + OtherUtils.escapeHtml(t)
                                                + "</pre>");
                                    } else {
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("<div style='");
                                        sb.append(buildStyle(x.style));
                                        sb.append("'>");
                                        sb.append(
                                                x.isEscaped() ? t
                                                : ("<pre>"
                                                + OtherUtils.escapeHtml(t)
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
                            service.stringToElement(richTexts.stream().map(x -> x.getText()).collect(Collectors.joining()))
                    );
                }
                return nn;
            }
            default: {
//                System.out.println("ignored");
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
