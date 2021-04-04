package net.thevpc.pnote.service;

import net.thevpc.pnote.service.security.PangaeaNoteObfuscatorDefault;
import net.thevpc.pnote.service.security.PasswordHandler;
import net.thevpc.pnote.model.CypherInfo;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.thevpc.common.i18n.I18n;
import net.thevpc.common.swing.util.CancelException;
import net.thevpc.echo.ItemPath;
import net.thevpc.nuts.NutsApplicationContext;
import net.thevpc.nuts.NutsContentType;
import net.thevpc.nuts.NutsIOException;
import net.thevpc.nuts.NutsLogVerb;
import net.thevpc.nuts.NutsStoreLocation;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.C;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.CPP;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.FILE;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.HTML;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.JAVA;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.JAVASCRIPT;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.MARKDOWN;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.NOTE_LIST;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.NUTS_TEXT_FORMAT;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.OBJECT_LIST;
import static net.thevpc.pnote.gui.PangaeaNoteTypes.PLAIN;
import net.thevpc.pnote.service.templates.EthernetConnectionTemplate;
import net.thevpc.pnote.service.templates.UrlCardTemplate;
import net.thevpc.pnote.service.templates.WifiConnectionTemplate;
import net.thevpc.pnote.model.PangageaNoteObjectDocument;
import net.thevpc.pnote.model.PangaeaNoteField;
import net.thevpc.pnote.model.PangaeaNoteObject;
import net.thevpc.pnote.model.PangaeaNoteConfig;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteObjectDescriptor;
import net.thevpc.pnote.model.PangaeaNoteFieldDescriptor;
import net.thevpc.pnote.model.PangaeaNoteFieldType;
import net.thevpc.pnote.model.PangageaNoteListModel;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.search.DefaultVNoteSearchFilter;
import net.thevpc.pnote.service.search.PangaeaNoteExtSearchResult;
import net.thevpc.pnote.service.search.VNoteSearchFilter;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import net.thevpc.pnote.service.security.InvalidSecretException;
import net.thevpc.pnote.service.templates.BankAccountTemplate;
import net.thevpc.pnote.service.templates.CreditCardAccountTemplate;
import net.thevpc.pnote.service.templates.UrlBookmarkTemplate;
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
import static net.thevpc.pnote.gui.PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT;
import net.thevpc.pnote.service.security.PangaeaNoteObfuscator;

public class PangaeaNoteService {

    public static final String SECURE_ALGO = PangaeaNoteObfuscatorDefault.ID;
    private NutsApplicationContext context;
    private I18n i18n;
    private LinkedHashMap<String, PangaeaNoteTemplate> extra = new LinkedHashMap<>();

    public PangaeaNoteService(NutsApplicationContext context, I18n i18n) {
        this.context = context;
        this.i18n = i18n;
        register(new UrlCardTemplate());
        register(new EthernetConnectionTemplate());
        register(new WifiConnectionTemplate());
        register(new UrlBookmarkTemplate());
        register(new BankAccountTemplate());
        register(new CreditCardAccountTemplate());
    }

    public I18n i18n() {
        return i18n;
    }

    private void register(PangaeaNoteTemplate a) {
        extra.put(a.getId(), a);
    }

    public List<PangaeaNoteTemplate> getTemplates() {
        return new ArrayList<>(extra.values());
    }

    public NutsApplicationContext getContext() {
        return context;
    }

    public PangaeaNote unloadNode(PangaeaNote n) {
        n.setLoaded(false);
        for (PangaeaNote c : n.getChildren()) {
            unloadNode(c);
        }
        if (PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT.equals(n.getContentType())) {
            n.getChildren().clear();
        }
        return n;
    }

    public File getDefaultDocumentsFolder() {
        return new File(getContext().getWorkspace().locations().getStoreLocation(NutsStoreLocation.VAR));
    }

    public PangaeaNote createSampleDocumentNote() {
        PangaeaNote n = PangaeaNote.newDocument();
        for (String contentType : PangaeaNoteTypes.ALL_CONTENT_TYPES) {
            PangaeaNote cc = new PangaeaNote().setName(
                    i18n.getString("PangaeaNoteTypeFamily." + contentType)
            ).setContentType(contentType);
            if (PangaeaNoteTypes.NOTE_LIST.equals(contentType)) {
                for (int i = 0; i < 5; i++) {
                    PangaeaNote cc2 = new PangaeaNote().setName(PangaeaNoteTypes.PLAIN).setContentType(PangaeaNoteTypes.PLAIN);
                    cc.getChildren().add(cc2);
                }
//                for (String contentType2 : PangaeaNoteTypes.ALL_CONTENT_TYPES) {
//                    PangaeaNote cc2 = new PangaeaNote().setName(contentType2).setContentType(contentType2);
//                    cc.getChildren().add(cc2);
//                }

            } else if (PangaeaNoteTypes.OBJECT_LIST.equals(contentType)) {
                PangaeaNoteObjectDescriptor od = new PangaeaNoteObjectDescriptor();
                od.setName("example");
                List<PangaeaNoteFieldDescriptor> fields = new ArrayList<>();
                fields.add(new PangaeaNoteFieldDescriptor()
                        .setName("name of the website")
                        .setType(PangaeaNoteFieldType.TEXT)
                );
                fields.add(new PangaeaNoteFieldDescriptor()
                        .setName("protocol")
                        .setType(PangaeaNoteFieldType.COMBOBOX)
                        .setValues(new ArrayList<>(Arrays.asList("http", "ftp")))
                );
                fields.add(new PangaeaNoteFieldDescriptor()
                        .setName("description")
                        .setType(PangaeaNoteFieldType.TEXTAREA)
                );
                fields.add(new PangaeaNoteFieldDescriptor()
                        .setName("types")
                        .setType(PangaeaNoteFieldType.CHECKBOX)
                        .setValues(new ArrayList<>(Arrays.asList("top", "down", "left", "right")))
                );
                od.setFields(fields);
                PangageaNoteObjectDocument dd = new PangageaNoteObjectDocument();
                dd.setDescriptor(od);
                //add some dynamic values;
                List<PangaeaNoteObject> os = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    PangaeaNoteObject d = new PangaeaNoteObject();
                    d.addField(new PangaeaNoteField().setName("name of the website").setValue("my website " + (i + 1)));
                    d.addField(new PangaeaNoteField().setName("protocol").setValue("http"));
                    d.addField(new PangaeaNoteField().setName("types").setValue("top\nright"));
                    os.add(d);
                }
                dd.setValues(os);
                cc.setContent(stringifyDescriptor(dd));
            }
            n.getChildren().add(cc);
        }
        n.getChildren().add(new PangaeaNote().setName("unknown-type").setContentType("unknown-type")
        );
        n.getChildren().add(new PangaeaNote().setName("with-icon").setIcon("star")
        );
        return n;
    }

    public boolean isDocumentNote(PangaeaNote n) {
        return PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT.equals(n.getContentType());
    }

    public String stringifyNoteListInfo(PangageaNoteListModel value) {
        return stringifyAny(value);
    }

    public String stringifyDescriptor(PangageaNoteObjectDocument value) {
        return stringifyAny(value);
    }

    public String stringifyAny(Object value) {
        return context.getWorkspace().formats().element().setValue(value)
                .setContentType(NutsContentType.JSON)
                .setSession(context.getSession())
                .setCompact(true)
                .format();
    }

    public PangageaNoteListModel parseNoteListModel(String s) {
        return parseAny(s, PangageaNoteListModel.class);
    }

    public PangageaNoteObjectDocument parseObjectDocument(String s) {
        return parseAny(s, PangageaNoteObjectDocument.class);
    }

    public <T> T parseAny(String s, Class<T> cls) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return context.getWorkspace().formats().element()
                    .setSession(context.getSession())
                    .setContentType(NutsContentType.JSON)
                    .parse(s, cls);
        } catch (Exception ex) {
            return null;
        }
    }

    public PangaeaNoteTemplate getTemplate(String contentType) {
        return extra.get(contentType);
    }

    public static class SaveError {

        PangaeaNote n;
        Exception error;
        ItemPath path;

        public SaveError(PangaeaNote n, ItemPath path, Exception error) {
            this.n = n;
            this.path = path;
            this.error = error;
        }

    }

    public static class SaveException extends RuntimeException {

        private List<SaveError> errors = new ArrayList<>();

        public SaveException(List<SaveError> errors, I18n i18n) {
            super(buildMessage(errors, i18n));
            this.errors = new ArrayList<>(errors);
        }

        private static String buildMessage(List<SaveError> errors, I18n i18n) {
            for (SaveError error : errors) {
                if (error.path.size() == 0) {
                    String m = error.error.getMessage();
                    if (m == null || m.length() < 3) {
                        m = error.error.toString();
                    }
                    return m;
                }
            }
            return MessageFormat.format(
                    i18n.getString("Message.saveError"), errors.size()
            );
        }

        public List<SaveError> getErrors() {
            return errors;
        }
    }

    public boolean saveDocument(PangaeaNote n, PasswordHandler handler) {
        List<SaveError> errors = new ArrayList<>();
        boolean b = false;
        String root = null;
        if (PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT.equals(n.getContentType())) {
            String c = n.getContent();
            if (c != null && c.trim().length() > 0) {
                root = c;
            }
        }
        try {
            b = saveDocument(n.copy(), ItemPath.of(), handler, errors, root);
        } catch (CancelException ex) {
            return false;
        }
        if (errors.size() > 0) {
            throw new SaveException(errors, i18n);
        }
        return b;
    }

    private boolean saveDocument(PangaeaNote n, ItemPath path, PasswordHandler passwordHandler, List<SaveError> errors, String root) {
        List<PangaeaNote> children = new ArrayList<>(n.getChildren());
        for (PangaeaNote c : children) {
            saveDocument(c, path.child(String.valueOf(c.getName())), passwordHandler, errors, root);
        }
        boolean saved = false;
        if (PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT.equals(n.getContentType())) {
            String c = n.getContent();
            if (c != null && c.trim().length() > 0) {
                c = c.trim();
                try {
                    File f = new File(c);
                    File pf = f.getParentFile();
                    if (pf != null) {
                        pf.mkdirs();
                    }

                    if (n.getVersion() == null || n.getVersion().length() == 0) {
                        n.setVersion(context.getAppVersion().toString());
                    }
                    Instant now = Instant.now();
                    if (n.getCreationTime() == null) {
                        n.setCreationTime(now);
                    }
                    n.setLastModified(now);
                    n.setContent(null);
                    n.setLoaded(false);
                    CypherInfo cypherInfo = n.getCypherInfo();
                    PangaeaNoteObfuscator obs = resolveCypherImpl(cypherInfo == null ? null : cypherInfo.getAlgo());
                    if (obs != null) {
                        n.setCypherInfo(new CypherInfo(cypherInfo.getAlgo(), ""));
                        CypherInfo ci = obs.encrypt(n, () -> passwordHandler.askForSavePassword(f.getPath(), root));
                        n.setCypherInfo(ci);
                        PangaeaNote n2 = new PangaeaNote();
                        n2.setContentType(PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT);
                        n2.setCreationTime(n.getCreationTime());
                        n2.setLastModified(n.getLastModified());
                        n2.setCypherInfo(ci);
                        getContext().getWorkspace().formats().element()
                                .setContentType(NutsContentType.JSON)
                                .setValue(n2)
                                .setSession(context.getSession())
                                .println(f);
                    } else {
                        getContext().getWorkspace().formats().element()
                                .setContentType(NutsContentType.JSON)
                                .setValue(n)
                                .setSession(context.getSession())
                                .println(f);
                    }
//                    if (cypherInfo != null && ((cypherInfo.getAlgo() == null) ||)

                    saved = true;
                } catch (CancelException ex) {
                    return false;
                } catch (Exception ex) {
                    errors.add(new SaveError(n, path, ex));
                }
                n.setContent(c);//push back the path
            } else {
                errors.add(new SaveError(n, path, new IOException("missing file path for " + n.getName())));
            }
            if (path.size() > 0) {
                unloadNode(n);
            }
        }
        return saved;
    }

    public PangaeaNoteObfuscator resolveCypherImpl(String algo) {
        if (algo == null) {
            return null;
        }
        switch (algo) {
            case PangaeaNoteService.SECURE_ALGO:
                return new PangaeaNoteObfuscatorDefault(context);
        }
        return null;
    }

    public void saveConfig(PangaeaNoteConfig c) {
        if (c == null) {
            c = new PangaeaNoteConfig();
        }
        Path configFilePath = getConfigFilePath();
        File pf = configFilePath.toFile().getParentFile();
        if (pf != null) {
            pf.mkdirs();
        }
        getContext().getWorkspace().formats().element()
                .setContentType(NutsContentType.JSON)
                .setValue(c)
                .setSession(context.getSession())
                .println(configFilePath);
    }

    public PangaeaNoteConfig loadConfig() {
        return loadConfig(() -> new PangaeaNoteConfig());
    }

    public PangaeaNoteConfig loadConfig(Supplier<PangaeaNoteConfig> defaultValue) {
        try {
            PangaeaNoteConfig n = getContext().getWorkspace().formats().element()
                    .setContentType(NutsContentType.JSON).setSession(context.getSession())
                    .parse(getConfigFilePath(),
                            PangaeaNoteConfig.class);
            if (n != null) {
                return n;
            }
        } catch (Exception ex) {
            //
        }
        return defaultValue == null ? null : defaultValue.get();
    }

    public Path getConfigFilePath() {
        return Paths.get(getContext().getConfigFolder()).resolve("pangaea-note.config");
    }

    public PangaeaNote loadNode(PangaeaNote n, PasswordHandler passwordHandler, boolean transitive, String rootFilePath) {
        if (!n.isLoaded()) {
            if (PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT.equals(n.getContentType())) {
                String nodePath = n.getContent();
                nodePath = nodePath == null ? "" : nodePath.trim();
                if (nodePath.length() > 0) {
                    PangaeaNote rawNode = getContext().getWorkspace().formats().element()
                            .setContentType(NutsContentType.JSON).setSession(context.getSession())
                            .parse(new File(nodePath), PangaeaNote.class);
                    n.copyFrom(rawNode);
                    CypherInfo cypherInfo = n.getCypherInfo();
                    PangaeaNoteObfuscator impl = resolveCypherImpl(cypherInfo == null ? null : cypherInfo.getAlgo());
                    if (impl != null) {
                        PangaeaNote a = null;
                        while (true) {
                            try {
                                String c0 = nodePath;
                                a = impl.decrypt(cypherInfo, n, () -> passwordHandler.askForLoadPassword(c0, rootFilePath));
                                break;
                            } catch (InvalidSecretException ex) {
                                if (!passwordHandler.reTypePasswordOnError()) {
                                    throw ex;
                                }
                            }
                        }
                        if (a != null) {
                            a.setCypherInfo(new CypherInfo(cypherInfo.getAlgo(), ""));
                        }
                        n.copyFrom(a);
                        n.setContent(nodePath);
                    }
                }
            }
            n.setLoaded(true);
        }
        if (transitive) {
            for (PangaeaNote c : n.getChildren()) {
                loadNode(c, passwordHandler, true, rootFilePath);
            }
        }
        return n;
    }

//    public void loadNode(PangaeaNoteExt note, PasswordHandler passwordHandler, boolean loadAll) {
//        if (!note.isLoaded()) {
//            if (PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT.equals(note.getContentType())) {
//                PangaeaNote o = loadDocument(new File(note.getContent()), passwordHandler, loadAll);
//                note.removeAllChildren();//TODO FIX ME
//                for (PangaeaNote c : o.getChildren()) {
//                    note.addChild(PangaeaNoteExt.of(c));
//                }
//                if (o.error == null) {
//                    note.setLoaded(true);
//                }
//                note.error = o.error;
//                note.setLoaded(o.isLoaded());
//            } else {
//                note.setLoaded(true);
//                if (loadAll) {
//                    for (PangaeaNoteExt c : note.getChildren()) {
//                        loadNode(c, passwordHandler, true);
//                    }
//                }
//            }
//        } else {
//            if (loadAll) {
//                for (PangaeaNoteExt c : note.getChildren()) {
//                    loadNode(c, passwordHandler, true);
//                }
//            }
//        }
//    }
    public PangaeaNote loadDocument(File file, PasswordHandler passwordHandler) {
        return loadDocument(file, passwordHandler, false, file.getPath());
    }

    public PangaeaNote loadDocument(File file, PasswordHandler passwordHandler, boolean loadAll, String root) {
//        PangaeaNote n = getContext().getWorkspace().formats().element()
//                .setContentType(NutsContentType.JSON).setSession(context.getSession())
//                .parse(file, PangaeaNote.class);
        try {
            PangaeaNote n = PangaeaNote.newDocument(file.getPath());
            loadNode(n, passwordHandler, loadAll, root);
            if (!PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT.equals(n.getContentType())) {
                throw new IOException("Invalid content type. Expected " + PangaeaNoteTypes.PANGAEA_NOTE_DOCUMENT + ". got " + n.getContentType());
            }
            return n;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private static void setLenientFeature(DocumentBuilderFactory dbFactory, String s, boolean b) {
        try {
            dbFactory.setFeature(s, b);
        } catch (Exception ex) {
            //
        }
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
            try {
                b = documentFactory.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                throw new NutsIOException(context.getSession().getWorkspace(), ex);
            }

            b.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    context.getSession().getWorkspace().log().of(PangaeaNoteService.class).with().session(context.getSession())
                            .level(Level.FINEST).verb(NutsLogVerb.WARNING).log(exception.toString());
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    context.getSession().getWorkspace().log().of(PangaeaNoteService.class).with().session(context.getSession())
                            .level(Level.FINEST).verb(NutsLogVerb.WARNING).log(exception.toString());
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    context.getSession().getWorkspace().log().of(PangaeaNoteService.class).with().session(context.getSession())
                            .level(Level.FINEST).verb(NutsLogVerb.WARNING).log(exception.toString());
                }
            });

            Document doc = b.parse(file);
            NodeList childNodes = doc.getDocumentElement().getChildNodes();
            PangaeaNote cherryDocument = PangaeaNote.newDocument();
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
                nn.setContent(PangaeaNoteTypes.HTML);
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
                                        nn.setTitleBold(Boolean.parseBoolean(v));
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
                                        nn.setTitleBold(Boolean.parseBoolean(v));
                                    }
                                    break;
                                }
                                case "prog_lang": {
                                    if (!OtherUtils.isBlank(v)) {
                                        switch (v) {
                                            case "java": {
                                                nn.setContentType(PangaeaNoteTypes.JAVA);
                                                break;
                                            }
                                            case "c": {
                                                nn.setContentType(PangaeaNoteTypes.C);
                                                break;
                                            }
                                            case "cpp": {
                                                nn.setContentType(PangaeaNoteTypes.CPP);
                                                break;
                                            }
                                            case "custom-colors": {
                                                nn.setContentType(PangaeaNoteTypes.HTML);
                                                break;
                                            }
                                            default: {
                                                nn.setContentType(PangaeaNoteTypes.PLAIN);
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
                if (nn.getContentType().equals(PangaeaNoteTypes.HTML)) {
                    nn.setContent(
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
                            + "</body></html>"
                    );
                } else {
                    nn.setContent(
                            richTexts.stream().map(x -> x.getText()).collect(Collectors.joining())
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

    public PangaeaNoteExtSearchResult search(PangaeaNoteExt n, String query) {
        return search(n, new DefaultVNoteSearchFilter(query));
    }

    public PangaeaNoteExtSearchResult search(PangaeaNoteExt n, VNoteSearchFilter filter) {
        Stream<StringSearchResult<PangaeaNoteExt>> curr = Stream.of();

        List<StringSearchResult<PangaeaNoteExt>> all = new ArrayList<>();
        if (filter != null) {
            Stream<StringSearchResult<PangaeaNoteExt>> r = filter.search(n, this);
            if (r != null) {
                curr = Stream.concat(curr, r);
            }
        }
        for (PangaeaNoteExt ne : n.getChildren()) {
            curr = Stream.concat(curr, search(ne, filter).stream());
        }
        return new PangaeaNoteExtSearchResult(n, curr);
    }

    public void updateNoteProperties(PangaeaNoteExt toUpdate, PangaeaNote headerValues) {
        String oldName = toUpdate.getName();
        toUpdate.setName(headerValues.getName());
        toUpdate.setIcon(headerValues.getIcon());
        toUpdate.setReadOnly(headerValues.isReadOnly());
        toUpdate.setTitleForeground(headerValues.getTitleForeground());
        toUpdate.setTitleBackground(headerValues.getTitleBackground());
        toUpdate.setTitleBold(headerValues.isTitleBold());
        toUpdate.setTitleItalic(headerValues.isTitleItalic());
        toUpdate.setTitleUnderlined(headerValues.isTitleUnderlined());
        toUpdate.setTitleStriked(headerValues.isTitleStriked());
        prepareChildForInsertion(toUpdate.getParent(), toUpdate);
        String newName = toUpdate.getName();
        if (PangaeaNoteTypes.NOTE_LIST.equals(toUpdate.getParent().getContentType())) {
            PangageaNoteListModel oldModel = parseNoteListModel(toUpdate.getParent().getContent());
            if (oldModel == null) {
                oldModel = new PangageaNoteListModel();
            }
            if (oldModel.getSelectedNames().contains(oldName)) {
                oldModel.getSelectedNames().remove(oldName);
                oldModel.getSelectedNames().add(newName);
                toUpdate.getParent().setContent(stringifyNoteListInfo(oldModel));
            }
        }
    }

    public String prepareChildForInsertion(PangaeaNoteExt parent, PangaeaNoteExt child) {
        String name = child.getName();
        String contentType = child.getContentType();
        if (name == null) {
            name = "";
        }
        Pattern p = Pattern.compile("^(?<base>.*) [0-9]$");
        Matcher m = p.matcher(name);
        String base = name;
        if (m.find()) {
            base = m.group("base");
        }
        contentType = normalizeContentType(contentType);
        if (base.isEmpty()) {
            base = i18n.getString("PangaeaNoteTypeFamily." + contentType);
        }
        Set<String> existingNames = parent.getChildren() == null ? new HashSet<>()
                : parent.getChildren().stream()
                        .filter(x -> x != child) // !!!
                        .map(x -> x.getName() == null ? "" : x.getName())
                        .collect(Collectors.toSet());
        int i = 1;
        while (true) {
            String n = (i == 1) ? base : base + (" " + i);
            if (!existingNames.contains(n)) {
                return n;
            }
            i++;
        }
    }

    public String generateNewChildName(PangaeaNote note, String name, String contentType) {
        if (name == null) {
            name = "";
        }
        Pattern p = Pattern.compile("^(?<base>.*) [0-9]$");
        Matcher m = p.matcher(name);
        String base = name;
        if (m.find()) {
            base = m.group("base");
        }
        contentType = normalizeContentType(contentType);
        if (base.isEmpty()) {
            base = i18n.getString("PangaeaNoteTypeFamily." + contentType);
        }
        Set<String> existingNames = note.getChildren() == null ? new HashSet<>()
                : note.getChildren().stream().map(x -> x.getName() == null ? "" : x.getName())
                        .collect(Collectors.toSet());
        int i = 1;
        while (true) {
            String n = (i == 1) ? base : base + (" " + i);
            if (!existingNames.contains(n)) {
                return n;
            }
            i++;
        }
    }

    public String getNoteIcon(PangaeaNote note, boolean folder, boolean expanded) {
        return getContentTypeIcon(note.getContentType(), note.getIcon(), note.getFolderIcon(), folder, expanded);
    }

    public String getContentTypeIcon(String contentType) {
        return getContentTypeIcon(contentType, null, null, false, false);
    }

    public String getContentTypeIcon(String contentType, String preferredNormalIcon, String preferredFolderIcon, boolean folder, boolean expanded) {
        String icon;
        if (folder) {
            icon = preferredFolderIcon;
            icon = icon == null ? "" : icon.toLowerCase().trim();
            if (isValidIcon(icon)) {
                return icon;
            }
            icon = preferredNormalIcon;
            icon = icon == null ? "" : icon.toLowerCase().trim();
            if (isValidIcon(icon)) {
                return icon;
            }
            if (expanded) {
                return "folder-open";
            } else {
                return "folder-closed";
            }
        }
        icon = preferredNormalIcon;
        icon = icon == null ? "" : icon.toLowerCase().trim();
        if (isValidIcon(icon)) {
            return icon;
        }
        contentType = normalizeContentType(contentType);
        switch (contentType) {
            case PLAIN:
                return "file-text";
            case HTML:
                return "file-html";
            case MARKDOWN:
                return "file-markdown";
            case NUTS_TEXT_FORMAT:
                return "file-nuts-text-format";
            case JAVA:
                return "file-java";
            case JAVASCRIPT:
                return "file-javascript";
            case C:
                return "file-c";
            case CPP:
                return "file-cpp";
            case PANGAEA_NOTE_DOCUMENT:
                return "file-pnote";
            case FILE:
                return "file";
            case NOTE_LIST:
                return "pangaea-note-list";
            case OBJECT_LIST:
                return "pangaea-object-list";
        }
        PangaeaNoteTemplate ct = extra.get(contentType);
        if (ct != null) {
            String s = ct.getIcon();
            if (isValidIcon(s)) {
                return s;
            }
        }
        return "unknown";
    }

    public String normalizeContentType(String ct) {
        if (ct == null) {
            ct = "";
        }
        ct = ct.trim().toLowerCase();
        if (ct.isEmpty()) {
            ct = PLAIN;
        }
        if (PangaeaNoteTypes.ALL_CONTENT_TYPES.contains(ct)) {
            return ct;
        }
        for (PangaeaNoteTemplate value : extra.values()) {
            if (value.getId().equals(ct)) {
                return ct;
            }
        }

        if (ct.contains(":")) {
            ct = ct.substring(0, ct.indexOf(':'));
            return normalizeContentType(ct);
        }
        if (!ct.contains("/")) {
            for (String t : PangaeaNoteTypes.ALL_CONTENT_TYPES) {
                if (t.endsWith("/" + ct)) {
                    return t;
                }
            }
            for (PangaeaNoteTemplate value : extra.values()) {
                String t = value.getId();
                if (t.endsWith("/" + ct)) {
                    return t;
                }
            }
        }
        return PangaeaNoteTypes.UNSUPPORTED;
    }

    public String[] getEditorTypes(String contentType) {
        return normalizeEditorTypes(contentType, null);
    }

    public String normalizeEditorType(String contentType, String editorType) {
        return normalizeEditorTypes(contentType, editorType)[0];
    }

    public String[] normalizeEditorTypes(String contentType, String editorType) {
        if (editorType == null) {
            editorType = "";
        }
        editorType = editorType.trim().toLowerCase();
        switch (normalizeContentType(contentType)) {
            case HTML:
            case NUTS_TEXT_FORMAT:
            case MARKDOWN: {
                if (editorType.isEmpty()) {
                    return new String[]{PangaeaNoteTypes.EDITOR_WYSIWYG, PangaeaNoteTypes.EDITOR_SOURCE};
                }
                switch (editorType) {
                    case PangaeaNoteTypes.EDITOR_WYSIWYG: {
                        return new String[]{PangaeaNoteTypes.EDITOR_WYSIWYG};
                    }
                    case PangaeaNoteTypes.EDITOR_SOURCE: {
                        return new String[]{PangaeaNoteTypes.EDITOR_SOURCE};
                    }
                    default: {
                        return new String[]{PangaeaNoteTypes.EDITOR_WYSIWYG};
                    }
                }
            }
            case PLAIN:
            case JAVA:
            case C:
            case CPP:
            case JAVASCRIPT: {
                return new String[]{PangaeaNoteTypes.EDITOR_SOURCE};
            }
            case NOTE_LIST: {
                return new String[]{PangaeaNoteTypes.EDITOR_NOTE_LIST};
            }
            case OBJECT_LIST: {
                return new String[]{PangaeaNoteTypes.EDITOR_OBJECT_LIST};
            }
            case FILE: {
                return new String[]{PangaeaNoteTypes.EDITOR_FILE};
            }
            case PANGAEA_NOTE_DOCUMENT: {
                return new String[]{PangaeaNoteTypes.EDITOR_PANGAEA_NOTE_DOCUMENT};
            }
            default: {
                return new String[]{PangaeaNoteTypes.EDITOR_UNSUPPORTED};
            }
        }
    }

    public boolean isValidIcon(String icon) {
        if (icon == null) {
            return false;
        }
        return PangaeaNoteTypes.ALL_USER_ICONS.contains(icon);
    }
}
