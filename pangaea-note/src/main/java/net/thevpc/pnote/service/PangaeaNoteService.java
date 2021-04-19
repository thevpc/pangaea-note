package net.thevpc.pnote.service;

import net.thevpc.pnote.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.types.notelist.PangaeaNoteListService;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.thevpc.common.i18n.I18n;
import net.thevpc.common.swing.util.CancelException;
import net.thevpc.echo.ItemPath;
import net.thevpc.nuts.NutsApplicationContext;
import net.thevpc.nuts.NutsContentType;
import net.thevpc.nuts.NutsElement;
import net.thevpc.nuts.NutsElementFormat;
import net.thevpc.nuts.NutsStoreLocation;
import net.thevpc.pnote.gui.PangaeaContentTypes;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.model.PangaeaNoteConfig;
import net.thevpc.pnote.model.PangaeaNote;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.service.search.DefaultVNoteSearchFilter;
import net.thevpc.pnote.service.search.PangaeaNoteExtSearchResult;
import net.thevpc.pnote.service.search.VNoteSearchFilter;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;
import net.thevpc.pnote.service.security.InvalidSecretException;
import net.thevpc.pnote.service.refactor.EmptySourceContentTypeReplacer;
import net.thevpc.pnote.service.refactor.PangaeaContentTypeReplacer;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.security.PangaeaNoteObfuscator;
import net.thevpc.pnote.types.c.PangaeaNoteCService;
import net.thevpc.pnote.types.cpp.PangaeaNoteCppService;
import net.thevpc.pnote.types.file.PangaeaNoteFileService;
import net.thevpc.pnote.types.html.PangaeaNoteHtmlService;
import net.thevpc.pnote.types.java.PangaeaNoteJavaService;
import net.thevpc.pnote.types.javascript.PangaeaNoteJavascriptService;
import net.thevpc.pnote.types.markdown.PangaeaNoteMarkdownService;
import net.thevpc.pnote.types.ntf.PangaeaNoteNTFService;
import net.thevpc.pnote.types.objectlist.PangaeaObjectListService;
import net.thevpc.pnote.types.pnodetembedded.PangaeaNoteEmbeddedService;
import net.thevpc.pnote.types.sh.PangaeaNoteShService;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import net.thevpc.pnote.types.html.PangaeaNoteHtmlWysiwygService;

public class PangaeaNoteService {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(PangaeaNoteService.class.getName());

    public static final String SECURE_ALGO = PangaeaNoteObfuscatorDefault.ID;
    private NutsApplicationContext context;
    private I18n i18n;
    private List<PangaeaContentTypeReplacer> typeReplacers = new ArrayList<>();
    private LinkedHashMap<PangaeaNoteContentType, PangaeaNoteTemplate> extra = new LinkedHashMap<>();
    private Map<PangaeaNoteContentType, PangaeaNoteTypeService> typeServices = new LinkedHashMap<>();

    public PangaeaNoteService(NutsApplicationContext context, I18n i18n) {
        this.context = context;
        this.i18n = i18n;
        typeReplacers.add(new EmptySourceContentTypeReplacer(this));

        installNoteTypeService(new PangaeaNotePlainTextService());
        installNoteTypeService(new PangaeaNoteHtmlWysiwygService());
        installNoteTypeService(new PangaeaNoteFileService());
        installNoteTypeService(new PangaeaNoteListService());
        installNoteTypeService(new PangaeaObjectListService());
        installNoteTypeService(new PangaeaNoteEmbeddedService());
        installNoteTypeService(new PangaeaNoteHtmlService());
        installNoteTypeService(new PangaeaNoteJavaService());
        installNoteTypeService(new PangaeaNoteCppService());
        installNoteTypeService(new PangaeaNoteCService());
        installNoteTypeService(new PangaeaNoteJavascriptService());
        installNoteTypeService(new PangaeaNoteMarkdownService());
        installNoteTypeService(new PangaeaNoteShService());
        installNoteTypeService(new PangaeaNoteNTFService());

    }

    public void installTypeReplacer(PangaeaContentTypeReplacer service) {
        typeReplacers.add(service);
    }

    public void installNoteTypeService(PangaeaNoteTypeService service) {
        typeServices.put(service.getContentType(), service);
        service.onInstall(this);
    }

    public PangaeaNoteTypeService findContentTypeService(PangaeaNoteContentType type) {
        PangaeaNoteTypeService s = typeServices.get(type);
        if (s != null) {
            return s;
        }
        return null;
    }

    public PangaeaNoteTypeService getContentTypeService(PangaeaNoteContentType type) {
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
        return getContext().getWorkspace().formats().element()
                .setSession(getContext().getSession());
    }

    public NutsApplicationContext getContext() {
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

    public File getDefaultDocumentsFolder() {
        return new File(getContext().getWorkspace().locations().getStoreLocation(NutsStoreLocation.VAR));
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

    public PangaeaNoteTemplate getTemplate(PangaeaNoteContentType contentType) {
        return extra.get(contentType);
    }

    public boolean isValidContentTypeExt(String id) {
        if (id == null || id.trim().length() == 0) {
            return false;
        }
        PangaeaNoteContentType c = PangaeaNoteContentType.of(id);
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

    public String getDocumentPath(PangaeaNoteExt selectedNote) {
        return ((PangaeaNoteEmbeddedService) getContentTypeService(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT))
                .getContentValueAsPath(selectedNote.getContent());
    }
    public String getDocumentPath(PangaeaNote selectedNote) {
        return ((PangaeaNoteEmbeddedService) getContentTypeService(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT))
                .getContentValueAsPath(selectedNote.getContent());
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
        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {

            String c = PangaeaNoteEmbeddedService.of(this).getContentValueAsPath(n.getContent());
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
        if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {
            String c = PangaeaNoteEmbeddedService.of(this).getContentValueAsPath(n.getContent());
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
                n.setContent(PangaeaNoteEmbeddedService.of(this).getContentValueAsElement(c));//push back the path
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
        element()
                .setContentType(NutsContentType.JSON)
                .setValue(c)
                .println(configFilePath);
    }

    public PangaeaNoteConfig loadConfig() {
        return loadConfig(() -> new PangaeaNoteConfig());
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

    public Path getConfigFilePath() {
        return Paths.get(getContext().getConfigFolder()).resolve("pangaea-note.config");
    }

    public PangaeaNote loadNode(PangaeaNote n, PasswordHandler passwordHandler, boolean transitive, String rootFilePath) {
        if (!n.isLoaded()) {
            if (PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {
                String nodePath = PangaeaNoteEmbeddedService.of(this).getContentValueAsPath(n.getContent());
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
                        n.setContent(PangaeaNoteEmbeddedService.of(this).getContentValueAsElement(nodePath));
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
            if (!PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString().equals(n.getContentType())) {
                throw new IOException("Invalid content type. Expected " + PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString() + ". got " + n.getContentType());
            }
            return n;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public PangaeaNote newDocument() {
        return newDocument(null).setLoaded(true);
    }

    public PangaeaNote newDocument(String path) {
        PangaeaNote n = new PangaeaNote();
        n.setName("pangaea-note-document");
        n.setContentType(PangaeaNoteEmbeddedService.PANGAEA_NOTE_DOCUMENT.toString());
        n.setContent(path == null ? null : element().forString(path));
        return n;
    }

    public PangaeaNoteExtSearchResult search(PangaeaNoteExt n, String query, SearchProgressMonitor monitor) {
        monitor.startSearch();
        PangaeaNoteExtSearchResult a = search(n, new DefaultVNoteSearchFilter(query), monitor);
        monitor.completeSearch();
        return a;
    }

    public PangaeaNoteExtSearchResult search(PangaeaNoteExt n, VNoteSearchFilter filter, SearchProgressMonitor monitor) {
        Stream<StringSearchResult<PangaeaNoteExt>> curr = Stream.of();
        monitor.searchProgress(n);
//        List<StringSearchResult<PangaeaNoteExt>> all = new ArrayList<>();
        if (filter != null) {
            Stream<StringSearchResult<PangaeaNoteExt>> r = filter.search(n, monitor, this);
            if (r != null) {
                curr = Stream.concat(curr, r);
            }
        }
        for (PangaeaNoteExt ne : n.getChildren()) {
            curr = Stream.concat(curr, search(ne, filter, monitor).stream());
        }
        return new PangaeaNoteExtSearchResult(n, curr);
    }

    public boolean changeNoteContentType(PangaeaNoteExt toUpdate, String newContentType00) {
        String newContentType0 = newContentType00;
        PangaeaNoteContentType oldContentType = normalizeContentType(toUpdate.getContentType());
        PangaeaNoteContentType newContentType = normalizeContentType(newContentType0);
        if (oldContentType.equals(newContentType)) {
            if (!Objects.equals(newContentType0, toUpdate.getContentType())) {
                toUpdate.setContentType(newContentType.toString());
                return true;
            }
            return false;
        }
        PangaeaContentTypeReplacer best = null;
        int bestLevel = -1;
        for (PangaeaContentTypeReplacer typeReplacer : typeReplacers) {
            int s = typeReplacer.getSupportLevel(toUpdate, oldContentType, newContentType);
            if (s > 0 && s > bestLevel) {
                bestLevel = s;
                best = typeReplacer;
            }
        }
        if (best != null) {
            best.changeNoteContentType(toUpdate, oldContentType, newContentType);
            return true;
        }
        throw new IllegalArgumentException("Unsupported type refactoring");
    }

    public void updateNoteProperties(PangaeaNoteExt toUpdate, PangaeaNote headerValues) {
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
        prepareChildForInsertion(toUpdate.getParent(), toUpdate);
        if (headerValues.getContentType() != null && !Objects.equals(toUpdate.getContentType(), headerValues.getContentType())) {
            changeNoteContentType(toUpdate, headerValues.getContentType());
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
        PangaeaNoteContentType contentType = normalizeContentType(contentType0);
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
        PangaeaNoteContentType contentType = normalizeContentType(contentType0);
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
        return getContentTypeIcon(
                normalizeContentType(note.getContentType()),
                note.getIcon(), note.getFolderIcon(), folder, expanded);
    }

    public String getContentTypeIcon(PangaeaNoteContentType contentType) {
        return getContentTypeIcon(contentType, null, null, false, false);
    }

    public String getContentTypeIcon(PangaeaNoteContentType contentType, String preferredNormalIcon, String preferredFolderIcon, boolean folder, boolean expanded) {
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

    public Set<PangaeaNoteContentType> getBaseContentTypes() {
        LinkedHashSet<PangaeaNoteContentType> all = new LinkedHashSet<>();
        all.addAll(typeServices.keySet());
        return all;
    }

    public Set<PangaeaNoteContentType> getAllContentTypes() {
        LinkedHashSet<PangaeaNoteContentType> all = new LinkedHashSet<>();
        all.addAll(typeServices.keySet());
        all.addAll(extra.values().stream().map(x -> x.getContentType()).collect(Collectors.toList()));
        return all;
    }

    public PangaeaNoteContentType normalizeContentType(PangaeaNoteContentType t) {
        if (t == null) {
            return PangaeaNoteContentType.of(PangaeaNotePlainTextService.PLAIN);
        }
        return t;
    }

    public PangaeaNoteContentType normalizeContentType(String ct) {
        if (ct == null) {
            ct = "";
        }
        ct = ct.trim().toLowerCase();
        if (ct.isEmpty()) {
            ct = PangaeaNotePlainTextService.PLAIN;
        }
        PangaeaNoteContentType ct0 = PangaeaNoteContentType.of(ct);
        if (ct0 == null) {
            ct0 = PangaeaNoteContentType.of(PangaeaNotePlainTextService.PLAIN);
        }
        Set<PangaeaNoteContentType> allct = getAllContentTypes();
        if (allct.contains(ct0)) {
            return ct0;
        }
        LOG.log(Level.WARNING, "invalid content type {0}", ct);
        return PangaeaContentTypes.UNSUPPORTED;
    }

    public String getEditorType(PangaeaNoteContentType contentType) {
        return normalizeEditorType(contentType, null);
    }

    public String normalizeEditorType(PangaeaNoteContentType contentType, String editorType) {
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

    public boolean isValidIcon(String icon) {
        if (icon == null) {
            return false;
        }
        return PangaeaNoteTypes.ALL_USER_ICONS.contains(icon);
    }

    public List<PangaeaNoteTypeService> getContentTypeServices() {
        return new ArrayList<PangaeaNoteTypeService>(typeServices.values());
    }
}
