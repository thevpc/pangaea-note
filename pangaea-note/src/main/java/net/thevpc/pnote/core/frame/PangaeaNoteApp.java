/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame;

import net.thevpc.common.props.Props;
import net.thevpc.common.props.WritableList;
import net.thevpc.echo.*;
import net.thevpc.echo.iconset.NoIconSet;
import net.thevpc.echo.impl.DefaultApplication;
import net.thevpc.nuts.*;
import net.thevpc.pnote.api.PangaeaNoteAppExtension;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.api.PangaeaNoteFileViewerManager;
import net.thevpc.pnote.api.model.PangaeaNoteConfig;
import net.thevpc.pnote.core.CorePangaeaNoteApp;
import net.thevpc.pnote.extensions.CherryTreeExtension;
import net.thevpc.pnote.extensions.CsvExtension;
import net.thevpc.pnote.extensions.Tess4JPangaeaNoteAppExtension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.thevpc.common.i18n.I18n;
import net.thevpc.common.props.Path;
import net.thevpc.echo.api.CancelException;
import net.thevpc.echo.impl.Applications;
import net.thevpc.pnote.api.InvalidSecretException;
import net.thevpc.pnote.api.PangaeaContentTypeReplacer;
import net.thevpc.pnote.api.PangaeaNoteFileImporter;
import net.thevpc.pnote.api.PangaeaNoteCypher;
import net.thevpc.pnote.api.PangaeaNoteTemplate;
import net.thevpc.pnote.api.PangaeaNoteTypeService;
import net.thevpc.pnote.api.model.CypherInfo;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteDocumentInfo;
import net.thevpc.pnote.core.types.embedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.core.types.file.PangaeaNoteFileService;
import net.thevpc.pnote.core.types.forms.PangaeaNoteFormsService;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.core.types.rich.PangaeaNoteRichService;
import net.thevpc.pnote.service.search.DefaultVNoteSearchFilter;
import net.thevpc.pnote.service.search.PangaeaNoteExtSearchResult;
import net.thevpc.echo.SearchQuery;
import net.thevpc.pnote.service.search.VNoteSearchFilter;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import net.thevpc.pnote.service.security.PangaeaNoteCypher_v100;
import net.thevpc.pnote.service.security.PangaeaNoteCypher_v101;
import net.thevpc.pnote.service.security.PasswordHandler;

/**
 * @author vpc
 */
public class PangaeaNoteApp extends DefaultApplication {

    private NutsApplicationContext appContext;
    private WritableList<PangaeaNoteFrame> windows = Props.of("windows").listOf(PangaeaNoteFrame.class);
    private CorePangaeaNoteApp core = new CorePangaeaNoteApp();
    private List<PangaeaNoteAppExtensionHandler> appExtensions = new ArrayList<>();
    private List<PangaeaNoteAppExtensionListener> appExtensionsListeners = new ArrayList<>();
    private List<PangaeaNoteEditorService> editorServices = new ArrayList<>();
    private List<PangaeaNoteFileViewerManager> viewers = new ArrayList<>();
    private AtomicInteger windowIndex = new AtomicInteger(1);
    public static final String[] SOURCE_CONTENT_TYPES = {
        "text/java;text/x-java",
        "text/x-csrc;text/x-c++hdr",
        "text/x-c++src",
        "text/javascript",
        "application/x-shellscript;ext=sh,zsh,tsh",
        "text/markdown;group=simple-documents",
        "text/x-nuts-text-format;group=simple-documents;ext=ntf",
        "application/x-hadra",
        "application/x-bibtex;ext=bib",
        "text/xml",
        "application/x-tex;application/x-latex;ext=tex,latex",
        "application/sql;ext=sql", //        "text/html",
        "text/css;ext=css", //        "text/html",
        "application/json;ext=json",
    };
    public static final String SECURE_ALGO = PangaeaNoteCypher_v101.ID;
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(PangaeaNoteApp.class.getName());
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
            "fire",
            "percent-000",
            "percent-010",
            "percent-020",
            "percent-030",
            "percent-040",
            "percent-050",
            "percent-060",
            "percent-070",
            "percent-080",
            "percent-090",
            "percent-100"
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
            "content-type.application/x-tex",
            "content-type.application/sql",
            "content-type.application/octet-stream",
            "content-type.application/x-pangaea-note-forms",
            "content-type.application/x-pangaea-note-list"
    );
    private List<PangaeaContentTypeReplacer> typeReplacers = new ArrayList<>();
    private LinkedHashMap<PangaeaNoteMimeType, PangaeaNoteTemplate> extra = new LinkedHashMap<>();
    private Map<PangaeaNoteMimeType, PangaeaNoteTypeService> typeServices = new LinkedHashMap<>();
    private List<PangaeaNoteFileImporter> fileImporters = new ArrayList<>();
    private Map<String, PangaeaNoteCypher> cyphers = new HashMap<>();
    private PangaeaNoteConfig config;
    private int maxRecentContentTypes = 4;

    public PangaeaNoteApp(NutsApplicationContext appContext) {
        super("swing");
        this.appContext = appContext;
        hideDisabled().set(true);
        this.executorService().set(appContext.getSession().concurrent().executorService());
        registerCypher(new PangaeaNoteCypher_v100(appContext));
        registerCypher(new PangaeaNoteCypher_v101(appContext));
        this.appExtensions.add(new PangaeaNoteAppExtensionHandlerImpl(this, () -> core.asExtension()) {
            {
                checkLoaded();
            }

            @Override
            public void setDisabled(boolean b) {
                //
            }
        });
        addExtension(() -> new Tess4JPangaeaNoteAppExtension());
        addExtension(() -> new CherryTreeExtension());
        addExtension(() -> new CsvExtension());
        for (PangaeaNoteAppExtensionHandler appExtension : appExtensions) {
            appExtension.checkLoaded();
        }
    }

    public void registerCypher(PangaeaNoteCypher o){
        cyphers.put(o.getId(),o);
    }

    public void installEditorService(PangaeaNoteEditorService s) {
        editorServices.add(s);
        s.onInstall(this);
    }

    public WritableList<PangaeaNoteFrame> getWindows() {
        return windows;
    }

    public void newFrame() {
        newFrame(false);
    }

    private PangaeaNoteFrame newFrame(boolean withSplash) {
        boolean main = (mainFrame().get() == null);
        String id = main ? "mainFrame" : "frame-" + windowIndex.getAndAdd(1);
        PangaeaNoteFrame w = new PangaeaNoteFrame(id, this, withSplash);
        w.mainFrame().set(main);
        if (main) {
            mainFrame().set(w);
        } else {
            components().add(w, id);
        }
        windows.add(w);
        state().onChange(event -> {
            AppState s = state().get();
            if (s == AppState.CLOSED) {
                windows.remove(w);
                if (windows.size() == 0) {
//                    quit0();
                } else {
                    if (w.mainFrame().get()) {
                        windows.get(0).mainFrame().set(true);
                    }
                }
            }
        });
        w.run();
        return w;
    }

    public void quit() {
        int size;
        while ((size = windows.size()) > 0) {
            PangaeaNoteFrame a = windows.get(0);
            a.close();
            int size2 = windows.size();
            if (size2 >= size) {
                break;
            }
        }
//        quit0();
    }

    public void run() {
        loadConfig();
//        app = new DefaultApplication("swing");
//        new AppSwingxConfigurator().configure(app);
        i18n().locales().addAll(Locale.ENGLISH, Locale.FRENCH);
        i18n().bundles().add("net.thevpc.pnote.messages.pnote-locale-independent");
        i18n().bundles().add("net.thevpc.pnote.messages.pnote-messages");
        i18n().bundles().add("net.thevpc.echo.app-locale-independent");
        i18n().bundles().add("net.thevpc.echo.app");
        i18n().defaultValue().set(new Function<String, String>() {
            @Override
            public String apply(String t) {
                if (t != null) {
                    if (t.endsWith(".tooltip")) {
                        return null;
                    }
                    if (t.endsWith(".largeIcon")) {
                        return "";
                    }
                }
                LOG.log(Level.FINEST, "I18n String not found: {0}", t);
                return "NotFound(" + t + ")";
            }
        });
        iconSets().add(new NoIconSet("no-icon"));
        iconSets().add().name("svgrepo-color").path("/net/thevpc/pnote/iconsets/svgrepo-color").build();
        iconSets().add().name("feather-black").path("/net/thevpc/pnote/iconsets/feather").build();
        for (Object[] r : new Object[][]{
            {"white", Color.WHITE(this)},
            {"black", Color.BLACK(this)},
            {"blue", new Color(22, 60, 90, this)},
            {"cyan", new Color(32, 99, 155, this)},
            {"green", new Color(60, 174, 163, this)},
            {"yellow", new Color(246, 213, 92, this)},
            {"red", new Color(237, 85, 59, this)},}) {
            String n = (String) r[0];
            Color c = (Color) r[1];
            iconSets().add().name("feather-" + n).path("/net/thevpc/pnote/iconsets/feather")
                    .replaceColor(Color.BLACK(this), c).build();
        }
        PangaeaNoteConfig config = config();
        //initialize UI from config before loading the window...
        if (iconSets().containsKey(config.getIconSet())) {
            iconSets().id().set(config.getIconSet());
        } else {
            iconSets().id().set(iconSets().values().get(0).getId());
        }
        if (config.getLocale() != null && config.getLocale().length() > 0) {
            i18n().locale().set(new Locale(config.getLocale()));
        }
        if (config.getPlaf() != null && config.getPlaf().length() > 0) {
            toolkit().applyPlaf(config.getPlaf());
        }
        plaf().onChange(x -> {
            //update config only if we are main window
            //if(mainFrame().get()) {
            config().setPlaf(x.newValue());
            saveConfig();
            //}
        });
        start();
        newFrame(true);
        waitFor();
//        try {
//            waitings.acquire();
//            waitings.acquire();
//        } catch (InterruptedException ex) {
//            //
//        }
    }

    public void addExtension(Supplier<PangaeaNoteAppExtension> extension) {
        PangaeaNoteAppExtensionHandlerImpl a = new PangaeaNoteAppExtensionHandlerImpl(this, extension);
        appExtensions.add(a);
        a.addListener("status", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                for (PangaeaNoteAppExtensionListener li : appExtensionsListeners) {
                    li.onExtensionStatusChanged(a, (PangaeaNoteAppExtensionStatus) evt.getOldValue(), (PangaeaNoteAppExtensionStatus) evt.getNewValue());
                }
            }
        });
        for (PangaeaNoteAppExtensionListener li : appExtensionsListeners) {
            li.onExtensionAdded(a);
        }
    }

    public List<PangaeaNoteAppExtensionHandler> getLoadedAppExtensions() {
        return getAppExtensions().stream().filter(x -> x.checkLoaded()).collect(Collectors.toList());
    }

    public NutsApplicationContext nutsAppContext() {
        return appContext;
    }

    public NutsWorkspace getNutsWorkspace() {
        return appContext.getWorkspace();
    }

    public NutsSession getNutsSession() {
        return appContext.getSession();
    }

    public List<PangaeaNoteAppExtensionHandler> getAppExtensions() {
        return appExtensions;
    }

    public List<PangaeaNoteEditorService> getEditorServices() {
        return editorServices;
    }

    public List<PangaeaNoteFileViewerManager> getViewers() {
        return viewers;
    }

    public void installViewer(PangaeaNoteFileViewerManager v) {
        viewers.add(v);
    }

    public void installTypeReplacer(PangaeaContentTypeReplacer service) {
        typeReplacers.add(service);
    }

    public void installNoteTypeService(PangaeaNoteTypeService service) {
        if (typeServices.containsKey(service.getContentType())) {
            throw new IllegalArgumentException("already registered type service " + service.getContentType());
        }
        typeServices.put(service.getContentType(), service);
        service.onInstall(this);
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

    public void register(PangaeaNoteTemplate a) {
        extra.put(a.getContentType(), a);
    }

    public List<PangaeaNoteTemplate> getTemplates() {
        return new ArrayList<>(extra.values());
    }

    public NutsElementFormat elem() {
        return appContext().getSession().elem()
                .setSession(appContext().getSession());
    }

    public NutsApplicationContext appContext() {
        return appContext;
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
        if (!NutsBlankable.isBlank(p)) {
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
        return new File(appContext().getSession().locations().getStoreLocation(NutsStoreLocation.VAR));
    }

    public String stringifyAny(Object value) {
        return elem().setValue(value)
                .setContentType(NutsContentType.JSON)
                .setCompact(true)
                .setNtf(false)
                .format().filteredText();
    }

    public <T> T parseAny(String s, Class<T> cls) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return elem()
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
        return elem().forString(string);
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
            throw new SaveException(errors, i18n());
        }
        return b;
    }

    private boolean saveDocument(PangaeaNote document, Path path, PasswordHandler passwordHandler, List<SaveError> errors, String root) {
        List<PangaeaNote> children = new ArrayList<>(document.getChildren());
        for (PangaeaNote c : children) {
            saveDocument(c, path.append(String.valueOf(c.getName())), passwordHandler, errors, root);
        }
        boolean saved = false;
        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(document.getContentType())) {
            PangaeaNoteDocumentInfo di = getDocumentInfo(document);
            //file path is not stored within the document
            PangaeaNoteDocumentInfo di2 = di.copy();
            di2.setPath(null);
            String c = di.getPath();
            if (c != null && c.trim().length() > 0) {
                c = c.trim();
                try {
                    File f = new File(c);
                    File pf = f.getParentFile();
                    if (pf != null) {
                        pf.mkdirs();
                    }

                    if (document.getVersion() == null || document.getVersion().length() == 0) {
                        document.setVersion(appContext().getAppVersion().toString());
                    }
                    Instant now = Instant.now();
                    if (document.getCreationTime() == null) {
                        document.setCreationTime(now);
                    }
                    document.setLastModified(now);
                    setDocumentInfo(document, di2);
                    document.setLoaded(false);
                    CypherInfo cypherInfo = document.getCypherInfo();
                    PangaeaNoteCypher obs = resolveCypherImpl(cypherInfo == null ? null : cypherInfo.getAlgo());
                    if (obs != null) {
                        document.setCypherInfo(new CypherInfo(cypherInfo.getAlgo(), ""));
                        CypherInfo ci = obs.encrypt(document, () -> passwordHandler.askForSavePassword(f.getPath(), root));
                        document.setCypherInfo(ci);
                        PangaeaNote n2 = new PangaeaNote();
                        n2.setContentType(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString());
                        n2.setCreationTime(document.getCreationTime());
                        n2.setLastModified(document.getLastModified());
                        n2.setCypherInfo(ci);
                        elem()
                                .setContentType(NutsContentType.JSON)
                                .setValue(n2)
                                .println(f);
                    } else {
                        elem()
                                .setContentType(NutsContentType.JSON)
                                .setValue(document)
                                .println(f);
                    }
//                    if (cypherInfo != null && ((cypherInfo.getAlgo() == null) ||)

                    saved = true;
                } catch (CancelException ex) {
                    return false;
                } catch (Exception ex) {
                    errors.add(new SaveError(document, path, ex));
                }
                //push back the path
                setDocumentInfo(document, di);
            } else {
                errors.add(new SaveError(document, path, new IOException("missing file path for " + document.getName())));
            }
            if (path.size() > 0) {
                unloadNode(document);
            }
        }
        return saved;
    }

    public PangaeaNoteCypher resolveCypherImpl(String algo) {
        if (algo == null) {
            return null;
        }
        return cyphers.get(algo.trim());
    }

    public void loadConfig() {
        config = loadConfig(() -> {
            //default config...
            PangaeaNoteConfig c = new PangaeaNoteConfig();
            c.setIconSet("svgrepo-color");
            c.setPlaf("FlatLight");
            return c;
        });
        List<String> recentContentTypes = config.getRecentContentTypes();
        if (recentContentTypes == null) {
            recentContentTypes = new ArrayList<>();
            config.setRecentContentTypes(recentContentTypes);
        }
        while (recentContentTypes.size() > maxRecentContentTypes) {
            recentContentTypes.remove(recentContentTypes.size() - 1);
        }
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
        elem()
                .setContentType(NutsContentType.JSON)
                .setValue(c)
                .println(configFilePath);
    }

    public PangaeaNoteConfig loadConfig(Supplier<PangaeaNoteConfig> defaultValue) {
        try {
            PangaeaNoteConfig n = elem()
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
                if (di == null || di.getPath() == null) {
                    throw new IllegalArgumentException("invalid note; missing path.");
                }

                String nodePath = di.getPath();
                nodePath = nodePath == null ? "" : nodePath.trim();
                if (nodePath.length() > 0) {
                    PangaeaNote rawNode = elem()
                            .setContentType(NutsContentType.JSON).setSession(appContext().getSession())
                            .parse(new File(nodePath), PangaeaNote.class);
                    n.copyFrom(rawNode);
                    CypherInfo cypherInfo = n.getCypherInfo();
                    PangaeaNoteCypher impl = resolveCypherImpl(cypherInfo == null ? null : cypherInfo.getAlgo());
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
                    }
                    PangaeaNoteDocumentInfo loadedInfo = getDocumentInfo(n, true);
                    loadedInfo.setPath(nodePath);
                    setDocumentInfo(n, loadedInfo);
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
        PangaeaNoteDocumentInfo info = new PangaeaNoteDocumentInfo();
        info.setPath(path);
        setDocumentInfo(n, info);
        return n;
    }

    public PangaeaNoteExtSearchResult search(PangaeaNote n, SearchQuery query, SearchProgressMonitor monitor, PangaeaNoteFrame frame) {
        monitor.startSearch();
        PangaeaNoteExtSearchResult a = search(n, new DefaultVNoteSearchFilter(query), monitor, frame);
        monitor.completeSearch();
        return a;
    }

    public PangaeaNoteExtSearchResult search(PangaeaNote n, VNoteSearchFilter filter, SearchProgressMonitor monitor, PangaeaNoteFrame frame) {
        Stream<StringSearchResult<PangaeaNote>> curr = Stream.of();
        monitor.searchProgress(n);
//        List<StringSearchResult<PangaeaNoteExt>> all = new ArrayList<>();
        if (filter != null) {
            Stream<StringSearchResult<PangaeaNote>> r = filter.search(n, monitor, this, frame);
            if (r != null) {
                curr = Stream.concat(curr, r);
            }
        }
        for (PangaeaNote ne : n.getChildren()) {
            curr = Stream.concat(curr, search(ne, filter, monitor, frame).stream());
        }
        return new PangaeaNoteExtSearchResult(n, curr);
    }

    public boolean changeNoteContentType(PangaeaNote toUpdate, String newContentType00, PangaeaNoteFrame frame) {
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
            int s = typeReplacer.getSupportLevel(toUpdate, oldContentType, newContentType, this);
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
                    tempFile = appContext().getSession().io().tmp().createTempFile("temp-snippet-");
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
                    return new PangaeaNote().setName(i18n().getString("content-type." + ss.getContentType()))
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
        if (info == null) {
            info = new PangaeaNoteDocumentInfo();
        }
        return elem().toElement(info);
    }

    public void setDocumentInfo(PangaeaNote document, PangaeaNoteDocumentInfo d) {
        if (d == null) {
            document.setContent(null);
        } else {
            document.setContent(documentInfoToElement(d));
        }
    }

    public PangaeaNoteDocumentInfo getDocumentInfo(PangaeaNote document, boolean autoCreate) {
        PangaeaNoteDocumentInfo d = getDocumentInfo(document);
        if (d == null && autoCreate) {
            d = new PangaeaNoteDocumentInfo();
        }
        return d;
    }

    public PangaeaNoteDocumentInfo getDocumentInfo(PangaeaNote document) {
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

    public String[] getSourceTypes() {
        List<String> all = new ArrayList<>();
        for (String string : PangaeaNoteApp.SOURCE_CONTENT_TYPES) {
            PangaeaNoteMimeType c = PangaeaNoteMimeType.of(string);
            all.add(PangaeaNoteMimeType.of(c.getMajor() + "/" + c.getMinor()).toString());
        }
        return all.toArray(new String[0]);
    }

    public boolean isDocumentNote(PangaeaNote note) {
        return note != null && PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(note.getContentType());
    }

    /**
     * return true if this is a new document (document unsaved yet and not
     * loaded from file)
     */
    public boolean isNewDocumentNote(PangaeaNote note) {
        return isDocumentNote(note) && isEmptyContent(note.getContent());
    }

    public void fireNoteChanged(PangaeaNote note) {
        //TODO
    }

    public void fireNoteStructureChanged(PangaeaNote note) {
        //TODO
    }
}
