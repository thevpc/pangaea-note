package net.thevpc.pnote.service;

import net.thevpc.common.i18n.I18n;
import net.thevpc.common.props.Path;
import net.thevpc.echo.api.CancelException;
import net.thevpc.echo.impl.Applications;
import net.thevpc.nuts.*;
import net.thevpc.pnote.api.*;
import net.thevpc.pnote.api.model.*;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteDocumentInfo;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.core.types.file.PangaeaNoteFileService;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.core.types.rich.PangaeaNoteRichService;
import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.service.search.DefaultVNoteSearchFilter;
import net.thevpc.pnote.service.search.PangaeaNoteExtSearchResult;
import net.thevpc.pnote.service.search.SearchQuery;
import net.thevpc.pnote.service.search.VNoteSearchFilter;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import net.thevpc.pnote.service.security.PangaeaNoteObfuscatorDefault;
import net.thevpc.pnote.service.security.PasswordHandler;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PangaeaNoteService {

    public static final String SECURE_ALGO = PangaeaNoteObfuscatorDefault.ID;
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(PangaeaNoteService.class.getName());
    private static List<String> CUSTOM_ICONS = Arrays.asList(
            "bell",
            "book",
            "circle",
            "clock",
            "coffee",
            "database",
            "disc",
            "file",
            "gift",
            "heart",
            "moon",
            "network",
            "password",
            "phone",
            "smile",
            "star",
            "string",
            "sun",
            "url",
            "wifi",
            "lock",
            "linkedin",
            "facebook",
            "gmail",
            "google",
            "slack",
            "ibm",
            "microsoft",
            "lock",
            "twitter",
            "train",
            "gitlab",
            "github",
            "digit-0",
            "digit-1",
            "digit-2",
            "digit-3",
            "digit-4",
            "digit-5",
            "digit-6",
            "digit-7",
            "digit-8",
            "digit-9",
            "folder-white",
            "folder-violet",
            "folder-red",
            "folder-purple",
            "folder-pink",
            "folder-orange",
            "folder-light-green",
            "folder-light-gray",
            "folder-green",
            "folder-gray",
            "folder-gold",
            "folder-dark-gray",
            "folder-cyan",
            "folder-blue",
            "folder-black",
            "star-white",
            "star-violet",
            "star-red",
            "star-purple",
            "star-pink",
            "star-orange",
            "star-light-green",
            "star-light-gray",
            "star-green",
            "star-gray",
            "star-gold",
            "star-dark-gray",
            "star-cyan",
            "star-blue",
            "flag-white",
            "flag-violet",
            "flag-red",
            "flag-purple",
            "flag-pink",
            "flag-orange",
            "flag-light-green",
            "flag-light-gray",
            "flag-green",
            "flag-gray",
            "flag-gold",
            "flag-dark-gray",
            "flag-cyan",
            "flag-blue",
            "alert-triangle",
            "alert-triangle-2",
            "alert-triangle-3",
            "alert-octagon",
            "alert-circle",
            "alert-circle-2",
            "box",
            "briefcase",
            "bookmark",
            "info",
            "linux",
            "sliders",
            "smartphone",
            "hard-drive",
            "help",
            "glasses",
            "watch",
            "hourglass",
            "meeting",
            "family",
            "chemistry",
            "fingerprint",
            "private",
            "science",
            "open-book",
            "youtube",
            "wifi-signal-0",
            "wifi-signal-1",
            "wifi-signal-2",
            "wifi-signal-3",
            "wifi-signal-4",
            "tv",
            "truck",
            "trending-up",
            "trending-down",
            "thumbs-up",
            "thumbs-down",
            "tablet",
            "sunset",
            "sunrise",
            "wind",
            "unlock",
            "server",
            "paper-plane",
            "send",
            "receive",
            "fire"
    );
    private static List<String> TYPE_ICONS = Arrays.asList(
            "datatype.audio",
            "datatype.calendar",
            "datatype.chart",
            "datatype.checkbox",
            "datatype.combobox",
            "datatype.email",
            "datatype.image",
            "datatype.link",
            "datatype.list",
            "datatype.map",
            "datatype.money",
            "datatype.number",
            "datatype.numbered-list",
            "datatype.password",
            "datatype.pen",
            "datatype.phone",
            "datatype.tags",
            "datatype.text",
            "datatype.textarea",
            "datatype.url",
            "datatype.video"
    );
    private static List<String> CONTENT_TYPE_ICONS = Arrays.asList(
            "content-type.text/java",
            "content-type.text/javascript",
            "content-type.text/x-csrc",
            "content-type.text/x-c++src",
            "content-type.text/html",
            "content-type.application/xml",
            "content-type.text/css",
            "content-type.text/x-nuts-text-format",
            "content-type.text/markdown",
            "content-type.application/x-pangaea-note",
            "content-type.text/sql",
            "content-type.application/x-tson",
            "content-type.application/json",
            "content-type.text/x-java-properties",
            "content-type.application/x-java-jnlp-file",
            "content-type.text/plain",
            "content-type.application/pdf",
            "content-type.application/x-latex",
            "content-type.application/vnd.ms-word",
            "content-type.application/vnd.ms-excel",
            "content-type.text/xsl",
            "content-type.application/x-compress",
            "content-type.application/zip",
            "content-type.image/x-pict",
            "content-type.application/vnd.ms-powerpoint",
            "content-type.application/x-dia",
            "content-type.application/java-archive",
            "content-type.application/zip",
            "content-type.text/plain",
            "content-type.application/x-java",
            "content-type.application/octet-stream",
            "content-type.application/x-pangaea-note-forms",
            "content-type.application/x-pangaea-note-list"
    );
    private NutsApplicationContext context;
    private I18n i18n;
    private List<PangaeaContentTypeReplacer> typeReplacers = new ArrayList<>();
    private LinkedHashMap<PangaeaNoteMimeType, PangaeaNoteTemplate> extra = new LinkedHashMap<>();
    private Map<PangaeaNoteMimeType, PangaeaNoteTypeService> typeServices = new LinkedHashMap<>();
    private List<PangaeaNoteFileImporter> fileImporters = new ArrayList<>();
    private PangaeaNoteConfig config;
    private PangaeaNoteApp app; //may be null

    public PangaeaNoteService(NutsApplicationContext context, I18n i18n, PangaeaNoteApp app) {
        this.context = context;
        this.app = app;
        this.i18n = i18n;
    }

    public void installTypeReplacer(PangaeaContentTypeReplacer service) {
        typeReplacers.add(service);
    }

    public void installNoteTypeService(PangaeaNoteTypeService service) {
        if (typeServices.containsKey(service.getContentType())) {
            throw new IllegalArgumentException("already registered type service " + service.getContentType());
        }
        typeServices.put(service.getContentType(), service);
        service.onInstall(this, app);
    }

    public PangaeaNoteTypeService findContentTypeService(PangaeaNoteMimeType type) {
        PangaeaNoteTypeService s = typeServices.get(type);
        if (s != null) {
            return s;
        }
        return null;
    }

    public PangaeaNoteTypeService getContentTypeService(PangaeaNoteMimeType type) {
        PangaeaNoteTypeService s = findContentTypeService(type);
        if (s != null) {
            return s;
        }
        throw new NoSuchElementException("content type not found:" + type);
    }

    public I18n i18n() {
        return i18n;
    }

    public void register(PangaeaNoteTemplate a) {
        extra.put(a.getContentType(), a);
    }

    public List<PangaeaNoteTemplate> getTemplates() {
        return new ArrayList<>(extra.values());
    }

    public NutsElementFormat element() {
        return appContext().getWorkspace().formats().element()
                .setSession(appContext().getSession());
    }

    public ExecutorService executorService() {
        return appContext().getWorkspace().concurrent().executorService();
    }

    public NutsApplicationContext appContext() {
        return context;
    }

    public PangaeaNote unloadNode(PangaeaNote n) {
        n.setLoaded(false);
        for (PangaeaNote c : n.getChildren()) {
            unloadNode(c);
        }
        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {
            n.getChildren().clear();
        }
        return n;
    }

    public String getValidLastOpenPath() {
        String p = config.getLastOpenPath();
        if (!Applications.isBlank(p)) {
            File f = new File(p);
            if (f.isDirectory()) {
                return f.getPath();
            }
            if (f.isFile()) {
                File parentFile = f.getParentFile();
                if (parentFile != null) {
                    return parentFile.getPath();
                }
            }
        }
        return getDefaultDocumentsFolder().getPath();
    }

    public PangaeaNoteConfig config() {
        return config;
    }

    public File getDefaultDocumentsFolder() {
        return new File(appContext().getWorkspace().locations().getStoreLocation(NutsStoreLocation.VAR));
    }

    public boolean isDocumentNote(PangaeaNote n) {
        return PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType());
    }

    public String stringifyAny(Object value) {
        return element().setValue(value)
                .setContentType(NutsContentType.JSON)
                .setCompact(true)
                .format();
    }

    public <T> T parseAny(String s, Class<T> cls) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return element()
                    .setContentType(NutsContentType.JSON)
                    .parse(s, cls);
        } catch (Exception ex) {
            return null;
        }
    }

    public PangaeaNoteTemplate getTemplate(PangaeaNoteMimeType contentType) {
        return extra.get(contentType);
    }

    public boolean isValidContentTypeExt(String id) {
        if (id == null || id.trim().length() == 0) {
            return false;
        }
        PangaeaNoteMimeType c = PangaeaNoteMimeType.of(id);
        if (c == null) {
            return false;
        }
        if (extra.containsKey(c)) {
            return true;
        }
        if (typeServices.containsKey(c)) {
            return true;
        }
        return false;
    }

    public NutsElement stringToElement(String string) {
        return element().forString(string);
    }

    public String elementToString(NutsElement string) {
        if (string == null) {
            return null;
        }
        if (string.isNull()) {
            return null;
        }
        if (string.isString()) {
            return string.asString();
        }
        return string.toString();
    }

    public boolean isEmptyContent(NutsElement content) {
        if (content == null) {
            return true;
        }
        return content.isEmpty();
    }

    public void addRecentNoteType(String selectedContentTypeId) {
        if (selectedContentTypeId == null || selectedContentTypeId.length() == 0) {
            return;
        }
        List<String> recentContentTypes = new ArrayList<>();
        recentContentTypes.add(0, selectedContentTypeId);
        List<String> recentContentTypes1 = config.getRecentContentTypes();
        if (recentContentTypes1 != null) {
            for (String r : recentContentTypes1) {
                if (isValidContentTypeExt(r)) {
                    recentContentTypes.add(r);
                }
            }
        }
        recentContentTypes = new ArrayList<>(new LinkedHashSet<String>(recentContentTypes));
        int maxRecentContentTypes = 12;
        while (recentContentTypes.size() > maxRecentContentTypes) {
            recentContentTypes.remove(recentContentTypes.size() - 1);
        }
        config.setRecentContentTypes(recentContentTypes);
        saveConfig();
    }

    public void addRecentFile(String newPath) {
        config.addRecentFile(newPath);
        saveConfig();
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    public List<PangaeaNoteFileImporter> getFileImporters() {
        return fileImporters;
    }

    public Set<String> getImportExtensions() {
        TreeSet<String> extensions = new TreeSet<>();
        for (PangaeaNoteFileImporter fileImporter : fileImporters) {
            extensions.addAll(Arrays.asList(fileImporter.getSupportedFileExtensions()));
        }
        return extensions;
    }

    public PangaeaNoteFileImporter resolveFileImporter(String name) {
        String name0 = name;
        int i = name.lastIndexOf(".");
        if (i >= 0) {
            name = name.substring(i + 1);
        }
        for (PangaeaNoteFileImporter fileImporter : fileImporters) {
            for (String se : fileImporter.getSupportedFileExtensions()) {
                if (se.equals(name)) {
                    return fileImporter;
                }
            }
        }
        throw new IllegalArgumentException("unsupported file to import : " + name0);
    }

    public boolean saveDocument(PangaeaNote n, PasswordHandler handler) {
        List<SaveError> errors = new ArrayList<>();
        boolean b = false;
        String root = null;
        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {
            PangaeaNoteDocumentInfo di = getDocumentInfo(n);
            String c = di.getPath();
            if (c != null && c.trim().length() > 0) {
                root = c;
            }
        }
        try {
            b = saveDocument(n.copy(), Path.of(), handler, errors, root);
        } catch (CancelException ex) {
            return false;
        }
        if (errors.size() > 0) {
            throw new SaveException(errors, i18n);
        }
        return b;
    }

    private boolean saveDocument(PangaeaNote n, Path path, PasswordHandler passwordHandler, List<SaveError> errors, String root) {
        List<PangaeaNote> children = new ArrayList<>(n.getChildren());
        for (PangaeaNote c : children) {
            saveDocument(c, path.append(String.valueOf(c.getName())), passwordHandler, errors, root);
        }
        boolean saved = false;
        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {
            PangaeaNoteDocumentInfo di = getDocumentInfo(n);
            String c = di.getPath();
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
                        n2.setContentType(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString());
                        n2.setCreationTime(n.getCreationTime());
                        n2.setLastModified(n.getLastModified());
                        n2.setCypherInfo(ci);
                        element()
                                .setContentType(NutsContentType.JSON)
                                .setValue(n2)
                                .println(f);
                    } else {
                        element()
                                .setContentType(NutsContentType.JSON)
                                .setValue(n)
                                .println(f);
                    }
//                    if (cypherInfo != null && ((cypherInfo.getAlgo() == null) ||)

                    saved = true;
                } catch (CancelException ex) {
                    return false;
                } catch (Exception ex) {
                    errors.add(new SaveError(n, path, ex));
                }
                n.setContent(PangaeaNoteEmbeddedService.of(this).getContentValueAsElement(di));//push back the path
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

    public void loadConfig() {
        config = loadConfig(() -> {
            //default config...
            PangaeaNoteConfig c = new PangaeaNoteConfig();
            c.setIconSet("svgrepo-color");
            c.setPlaf("FlatLight");
            return c;
        });
    }

    public void saveConfig() {
        saveConfig(config);
    }

    public void saveConfig(PangaeaNoteConfig c) {
        if (c == null) {
            c = new PangaeaNoteConfig();
        }
        java.nio.file.Path configFilePath = getConfigFilePath();
        File pf = configFilePath.toFile().getParentFile();
        if (pf != null) {
            pf.mkdirs();
        }
        element()
                .setContentType(NutsContentType.JSON)
                .setValue(c)
                .println(configFilePath);
    }

    public PangaeaNoteConfig loadConfig(Supplier<PangaeaNoteConfig> defaultValue) {
        try {
            System.out.println("load config from: " + getConfigFilePath());
            PangaeaNoteConfig n = element()
                    .setContentType(NutsContentType.JSON)
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

    public java.nio.file.Path getConfigFilePath() {
        return Paths.get(appContext().getConfigFolder()).resolve("pangaea-note.config");
    }

    public PangaeaNote loadNode(PangaeaNote n, PasswordHandler passwordHandler, boolean transitive, String rootFilePath) {
        if (!n.isLoaded()) {
            if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {
                PangaeaNoteDocumentInfo di = getDocumentInfo(n);
                String nodePath = di.getPath();
                nodePath = nodePath == null ? "" : nodePath.trim();
                if (nodePath.length() > 0) {
                    PangaeaNote rawNode = element()
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
                        n.setContent(PangaeaNoteEmbeddedService.of(this).getContentValueAsElement(di));
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
        try {
            PangaeaNote n = newDocument(file.getPath());
            loadNode(n, passwordHandler, loadAll, root);
            _updateOldVersion(n);
            if (!PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {
                throw new IOException("Invalid content type. Expected " + PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString() + ". got " + n.getContentType());
            }
            return n;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void _updateOldVersion(PangaeaNote n) {
        //TODO REMOVE ME: old version
        if ("application/pangaea-note-document".equals(n.getContentType())) {
            n.setContentType(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString());
        }
        if ("text/html;editor=wysiwyg".equals(n.getContentType())) {
            n.setContentType(PangaeaNoteRichService.RICH_HTML.toString());
        }
        if ("application/pangaea-note-list".equals(n.getContentType())) {
            n.setContentType(PangaeaNoteListService.LIST.toString());
        }
        if ("application/pangaea-object-list".equals(n.getContentType())) {
            n.setContentType(PangaeaNoteFormsService.FORMS.toString());
        }
        if ("text/sh".equals(n.getContentType())) {
            n.setContentType("application/x-shellscript");
        }
        List<PangaeaNote> ch = n.getChildren();
        if (ch != null) {
            for (PangaeaNote nn : ch) {
                _updateOldVersion(nn);
            }
        }
    }

    public PangaeaNote newDocument() {
        return newDocument(null).setLoaded(true);
    }

    public PangaeaNote newDocument(String path) {
        PangaeaNote n = new PangaeaNote();
        n.setName("pangaea-note-document");
        n.setContentType(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString());
        PangaeaNoteDocumentInfo info=new PangaeaNoteDocumentInfo();
        info.setPath(path);
        n.setContent(PangaeaNoteEmbeddedService.of(this).getContentValueAsElement(info));
        return n;
    }

    public PangaeaNoteExtSearchResult search(PangaeaNoteExt n, SearchQuery query, SearchProgressMonitor monitor, PangaeaNoteFrame frame) {
        monitor.startSearch();
        PangaeaNoteExtSearchResult a = search(n, new DefaultVNoteSearchFilter(query), monitor, frame);
        monitor.completeSearch();
        return a;
    }

    public PangaeaNoteExtSearchResult search(PangaeaNoteExt n, VNoteSearchFilter filter, SearchProgressMonitor monitor, PangaeaNoteFrame frame) {
        Stream<StringSearchResult<PangaeaNoteExt>> curr = Stream.of();
        monitor.searchProgress(n);
//        List<StringSearchResult<PangaeaNoteExt>> all = new ArrayList<>();
        if (filter != null) {
            Stream<StringSearchResult<PangaeaNoteExt>> r = filter.search(n, monitor, this, frame);
            if (r != null) {
                curr = Stream.concat(curr, r);
            }
        }
        for (PangaeaNoteExt ne : n.getChildren()) {
            curr = Stream.concat(curr, search(ne, filter, monitor, frame).stream());
        }
        return new PangaeaNoteExtSearchResult(n, curr);
    }

    public boolean changeNoteContentType(PangaeaNoteExt toUpdate, String newContentType00, PangaeaNoteFrame frame) {
        String newContentType0 = newContentType00;
        PangaeaNoteMimeType oldContentType = normalizeContentType(toUpdate.getContentType());
        PangaeaNoteMimeType newContentType = normalizeContentType(newContentType0);
        if (oldContentType.equals(newContentType)) {
            if (!Objects.equals(newContentType0, toUpdate.getContentType())) {
                toUpdate.setContentType(newContentType.toString());
                return true;
            }
            return false;
        }
        if (oldContentType.equals(PangaeaContentTypes.UNSUPPORTED)) {
            toUpdate.setContentType(newContentType.toString());
            return true;
        }
        PangaeaContentTypeReplacer best = null;
        int bestLevel = -1;
        for (PangaeaContentTypeReplacer typeReplacer : typeReplacers) {
            int s = typeReplacer.getSupportLevel(toUpdate, oldContentType, newContentType, this, frame);
            if (s > 0 && s > bestLevel) {
                bestLevel = s;
                best = typeReplacer;
            }
        }
        if (best != null) {
            best.changeNoteContentType(toUpdate, oldContentType, newContentType, this);
            return true;
        }
        throw new IllegalArgumentException("unsupported type refactoring from " + oldContentType + " to " + newContentType);
    }

    public void updateNoteProperties(PangaeaNoteExt toUpdate, PangaeaNote headerValues, PangaeaNoteFrame frame) {
        PangaeaNote before = toUpdate.toNote();
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
        toUpdate.setContent(headerValues.getContent());
        prepareChildForInsertion(toUpdate.getParent(), toUpdate);
        if (headerValues.getContentType() != null && !Objects.equals(toUpdate.getContentType(), headerValues.getContentType())) {
            changeNoteContentType(toUpdate, headerValues.getContentType(), frame);
        }

        String newName = toUpdate.getName();

        PangaeaNoteExt parent = toUpdate.getParent();
        if (parent.getParent() == null) {
            //root! ignore
        } else {
            getContentTypeService(normalizeContentType(parent.getContentType()))
                    .onPostUpdateChildNoteProperties(toUpdate, before);
        }
    }

    public String prepareChildForInsertion(PangaeaNoteExt parent, PangaeaNoteExt child) {
        String name = child.getName();
        String contentType0 = child.getContentType();
        if (name == null) {
            name = "";
        }
        Pattern p = Pattern.compile("^(?<base>.*) [0-9]$");
        Matcher m = p.matcher(name);
        String base = name;
        if (m.find()) {
            base = m.group("base");
        }
        PangaeaNoteMimeType contentType = normalizeContentType(contentType0);
        if (base.isEmpty()) {
            base = i18n.getString("content-type." + contentType);
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

    public String generateNewChildName(PangaeaNote note, String name, String contentType0) {
        if (name == null) {
            name = "";
        }
        Pattern p = Pattern.compile("^(?<base>.*) [0-9]$");
        Matcher m = p.matcher(name);
        String base = name;
        if (m.find()) {
            base = m.group("base");
        }
        PangaeaNoteMimeType contentType = normalizeContentType(contentType0);
        if (base.isEmpty()) {
            base = i18n.getString("content-type." + contentType);
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

    public String getNoteIcon(PangaeaNote note) {
        return getNoteIcon(note, false);
    }

    public String getNoteIcon(PangaeaNote note, boolean expanded) {
        boolean folder = note.getChildren().size() > 0;
        return getContentTypeIcon(
                normalizeContentType(note.getContentType()),
                note.getIcon(), note.getFolderIcon(), folder, expanded);
    }

    public String getContentTypeIcon(PangaeaNoteMimeType contentType) {
        return getContentTypeIcon(contentType, null, null, false, false);
    }

    public String getContentTypeIcon(PangaeaNoteMimeType contentType, String preferredNormalIcon, String preferredFolderIcon, boolean folder, boolean expanded) {
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
        PangaeaNoteTypeService c = findContentTypeService(contentType);
        if (c != null) {
            return c.getContentTypeIcon(folder, expanded);
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

    public Set<PangaeaNoteMimeType> getBaseContentTypes() {
        LinkedHashSet<PangaeaNoteMimeType> all = new LinkedHashSet<>();
        all.addAll(typeServices.keySet());
        return all;
    }

    public Set<PangaeaNoteMimeType> getAllContentTypes() {
        LinkedHashSet<PangaeaNoteMimeType> all = new LinkedHashSet<>();
        all.addAll(typeServices.keySet());
        all.addAll(extra.values().stream().map(x -> x.getContentType()).collect(Collectors.toList()));
        return all;
    }

    public PangaeaNoteMimeType normalizeContentType(PangaeaNoteMimeType t) {
        if (t == null) {
            return PangaeaNotePlainTextService.PLAIN;
        }
        return t;
    }

    public PangaeaNoteMimeType normalizeContentType(String ct) {
        if (ct == null) {
            ct = "";
        }
        ct = ct.trim().toLowerCase();
        if (ct.isEmpty()) {
            return PangaeaNotePlainTextService.PLAIN;
        }
        PangaeaNoteMimeType ct0 = PangaeaNoteMimeType.of(ct);
        if (ct0 == null) {
            ct0 = PangaeaNotePlainTextService.PLAIN;
        }
        Set<PangaeaNoteMimeType> allct = getAllContentTypes();
        if (allct.contains(ct0)) {
            return ct0;
        }
        LOG.log(Level.WARNING, "invalid content type {0}", ct);
        return PangaeaContentTypes.UNSUPPORTED;
    }

    public String getEditorType(PangaeaNoteMimeType contentType) {
        return normalizeEditorType(contentType, null);
    }

    public String normalizeEditorType(PangaeaNoteMimeType contentType, String editorType) {
        if (editorType == null) {
            editorType = "";
        }
        editorType = editorType.trim().toLowerCase();
        PangaeaNoteTypeService s = findContentTypeService(contentType);
        if (s != null) {
            String a = s.normalizeEditorType(editorType);
            if (a != null) {
                return a;
            }
        }
//        PangaeaNoteTemplate t = extra.values().stream().filter(x->x.getId().equals(contentType)).findAny().orElse(null);
//        t.get
        return PangaeaNoteTypes.EDITOR_UNSUPPORTED;
    }

    public Set<String> getAllIcons() {
        SortedSet<String> s = new TreeSet<>();
        s.addAll(CUSTOM_ICONS);
        s.addAll(TYPE_ICONS);
        s.addAll(CONTENT_TYPE_ICONS);
        return s;
    }

    public boolean isValidIcon(String icon) {
        if (icon == null) {
            return false;
        }
        return getAllIcons().contains(icon);
    }

    public PangaeaNoteTypeService getContentTypeServiceByFileName(String fileName, String probedContentType) {
        String name = fileName.replace('\\', '/');
        int x = name.lastIndexOf('/');
        if (x >= 0) {
            name = name.substring(x + 1);
        }
        x = name.lastIndexOf('.');
        String suffix = "";
        if (x >= 0) {
            suffix = name.substring(x + 1);
        }
        PangaeaNoteTypeService best = null;
        int bestSup = -1;
        if (probedContentType == null) {
            probedContentType = "";
        }
        for (PangaeaNoteTypeService contentTypeService : getContentTypeServices()) {
            int i = contentTypeService.getFileNameSupport(fileName, suffix, probedContentType);
            if (i > 0 && i > bestSup) {
                contentTypeService.getFileNameSupport(fileName, suffix, probedContentType);
                bestSup = i;
                best = contentTypeService;
            }
        }
        return best;
    }

    public List<PangaeaNoteTypeService> getContentTypeServices() {
        return new ArrayList<PangaeaNoteTypeService>(typeServices.values());
    }

    public void setLastOpenPath(String path) {
        if (path != null) {
            File f = new File(path);
            if (f.isDirectory()) {
                config.setLastOpenPath(f.getPath());
                saveConfig();
            } else {
                File p = f.getParentFile();
                if (p != null) {
                    config.setLastOpenPath(p.getPath());
                    saveConfig();
                }
            }
        } else {
            config.setLastOpenPath(null);
            saveConfig();
        }
    }

    public void installFileImporter(PangaeaNoteFileImporter importer) {
        fileImporters.add(importer);
    }

    public PangaeaNote createNoteFromSnippet(Object snippet) {
        if (snippet instanceof String) {
            String s = (String) snippet;
            File f = Applications.asFile(s);
            if (f != null) {
                return new PangaeaNote().setName(f.getName())
                        .setContentType(PangaeaNoteFileService.FILE.toString())
                        .setContent(stringToElement(s));
            }
            URL u = Applications.asURL(s);
            if (u != null) {
                return new PangaeaNote().setName(Applications.getFileName(s))
                        .setContentType(PangaeaNoteFileService.FILE.toString())
                        .setContent(stringToElement(s));
            }
            String ct = null;
            String tempFile = null;
            try {
                try {
                    tempFile = appContext().getWorkspace().io().tmp().createTempFile("temp-snippet-");
                    Files.write(Paths.get(tempFile), s.getBytes());
                    ct = Applications.probeContentType(tempFile);
                } finally {
                    Files.delete(Paths.get(tempFile));
                }
            } catch (IOException ex) {
                //ignore!!
            }
            if (ct != null) {
                PangaeaNoteTypeService ss = getContentTypeServiceByFileName(tempFile, ct);
                if (ss != null) {
                    return new PangaeaNote().setName(i18n.getString("content-type." + ss.getContentType()))
                            .setContentType(ss.getContentType().toString())
                            .setContent(
                                    stringToElement(ss.getContentType().toString())
                            );
                }
            }
            return new PangaeaNote().setName("new-snippet")
                    .setContentType(PangaeaNotePlainTextService.PLAIN.toString());
        }
        return null;
    }

    public NutsElement documentInfoToElement(PangaeaNoteDocumentInfo info) {
        if(info==null){
            info=new PangaeaNoteDocumentInfo();
        }
        return element().toElement(info);
    }

    public PangaeaNoteDocumentInfo getDocumentInfo(PangaeaNote document) {
        NutsElement c = document.getContent();
        return PangaeaNoteEmbeddedService.of(this).getContentValueAsInfo(document.getContent());
    }

    public static class SaveError {

        PangaeaNote n;
        Exception error;
        Path path;

        public SaveError(PangaeaNote n, Path path, Exception error) {
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
}
