/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.ComboBox;
import net.thevpc.echo.SimpleItem;
import net.thevpc.nuts.NutsBlankable;
import net.thevpc.nuts.NutsUtilStrings;
import net.thevpc.pnote.api.PangaeaNoteTemplate;
import net.thevpc.pnote.api.PangaeaNoteTypeService;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author thevpc
 */
public class PangaeaNoteTypesComboBox extends ComboBox<SimpleItem> {

    private PangaeaNoteFrame frame;
    private Options options;

    public PangaeaNoteTypesComboBox(PangaeaNoteFrame frame) {
        this(frame, null);
    }

    public PangaeaNoteTypesComboBox(PangaeaNoteFrame frame, Options options) {
        super(SimpleItem.class, frame.app());
        this.frame = frame;
        this.options = options == null ? new Options() : options;
        List<SimpleItem> availableTypes = new ArrayList<>();
        if (this.options.showRecent) {
            List<String> rct = frame.config().getRecentContentTypes();
            if (rct != null) {
                List<SimpleItem> recent = new ArrayList<>();
                for (String id : rct) {
                    if (frame.app().isValidContentTypeExt(id)) {
                        if (accept(this.options,PangaeaNoteMimeType.of(id), "recent-documents")) {
                            recent.add(new SimpleItem(false, "recent-" + id, Str.i18n("content-type." + id),
                                            frame.app().getContentTypeIcon(PangaeaNoteMimeType.of(id)), 0
                                    )
                            );
                        }
                    }
                }
                if (recent.size() > 0) {
                    if (this.options.showGroups) {
                        availableTypes.add(createNoteTypeFamilyNameGroup("recent-documents"));
                    }
                    availableTypes.addAll(recent);
                }
            }
        }

        availableTypes.addAll(createTypeListNamedValue(this.options));
        this.selection().disablePredicate().set(SimpleItem::isGroup);
        this.values().addAll(availableTypes.toArray(new SimpleItem[0]));
        this.selection().multipleSelection().set(false);
    }

    public String getSelectedContentTypeId() {
        SimpleItem a = selection().get();
        if (a == null) {
            return null;
        }
        String id = a.getId();
        if (id.startsWith("recent-")) {
            id = id.substring("recent-".length());
        }
        return id;
    }

    //    protected final NamedValue createNoteTypeFamilyNameValue(String id) {
//        return new NamedValue(false, id,
//                win.app().i18n().getString("content-type." + id),
//                win.service().getContentTypeIcon(id)
//        );
//    }
    protected final SimpleItem createNoteTypeFamilyNameGroup(String id) {
        return new SimpleItem(true, id, Str.i18n("content-type." + id), null, 0);
    }

    private List<SimpleItem> createTypeListNamedValue(Options options) {
        List<SimpleItem> availableTypes = new ArrayList<>();
        LinkedHashMap<String, List<SimpleItem>> selectors = new LinkedHashMap<>();
        for (PangaeaNoteMimeType s : frame.app().getBaseContentTypes()) {
            PangaeaNoteTypeService cs = frame.app().getContentTypeService(s);
            ContentTypeSelector contentTypeSelector = cs.getContentTypeSelector();
            String g = contentTypeSelector.getGroup();
            if (accept(options,contentTypeSelector.getContentType(), g)) {
                List<SimpleItem> li = selectors.get(g);
                if (li == null) {
                    li = new ArrayList<>();
                    selectors.put(g, li);
                }
                li.add(
                        new SimpleItem(false, contentTypeSelector.getContentType().toString(),
                                Str.i18n("content-type." + contentTypeSelector.getContentType().toString()),
                                frame.app().getContentTypeIcon(contentTypeSelector.getContentType()),
                                contentTypeSelector.getOrder()
                        )
                );
            }
        }

        if (options.showTemplates) {
            for (PangaeaNoteTemplate template : frame.app().getTemplates()) {
                String g = template.getGroup();
                if (NutsBlankable.isBlank(g)) {
                    g = "templates";
                }
                if (accept(options,template.getContentType(), g)) {
                    List<SimpleItem> li = selectors.get(g);
                    if (li == null) {
                        li = new ArrayList<>();
                        selectors.put(g, li);
                    }
                    Str s = template.getLabel(frame.app());
                    if (s == null) {
                        s = Str.i18n("content-type." + template.getContentType().toString());
                    }
                    SimpleItem n = new SimpleItem(false, template.getContentType().toString(), s, frame.app().getContentTypeIcon(template.getContentType()),
                            template.getOrder()
                    );
                    li.add(n);
                }
            }
        }
        for (Map.Entry<String, List<SimpleItem>> entry : selectors.entrySet()) {
            entry.getValue().sort((a, b) -> Integer.compare(a.getPreferredOrder(), b.getPreferredOrder()));
            if (options.showGroups) {
                availableTypes.add(createNoteTypeFamilyNameGroup(entry.getKey()));
            }
            availableTypes.addAll(entry.getValue());
        }
        return availableTypes;
    }

    public void setSelectedContentType(PangaeaNoteMimeType contentType, String editorType) {
        SimpleItem si = this.values().stream().filter(v -> {
            if (v.getId() == null || v.getId().length() == 0) {
                if (contentType == null) {
                    return true;
                }
            }
            PangaeaNoteMimeType ct = frame.app().normalizeContentType(v.getId());
            if (ct.equals(contentType)) {
                String[] ss = v.getId().split(":");
                if (ss.length == 1) {
                    return true;
                }
                if (editorType == null || frame.app().normalizeEditorType(ct, editorType).equals(ss[1])) {
                    return true;
                }
            }
            return false;
        }).findFirst().orElse(null);
        this.selection().set(si);
    }

    private boolean accept(Options options, PangaeaNoteMimeType t, String group) {
        if (t == null) {
            return false;
        }
        Filter filter = options.filter;
        if (filter == null) {
            return true;
        }
        return filter.accept(t, group);
    }

    public interface Filter {
        boolean accept(PangaeaNoteMimeType mimeType, String group);
    }

    public static class Options {
        private boolean showRecent = true;
        private boolean showGroups = true;
        private boolean showTemplates = true;
        private Filter filter;

        public boolean isShowRecent() {
            return showRecent;
        }

        public Options setShowRecent(boolean showRecent) {
            this.showRecent = showRecent;
            return this;
        }

        public boolean isShowGroups() {
            return showGroups;
        }

        public Options setShowGroups(boolean showGroups) {
            this.showGroups = showGroups;
            return this;
        }

        public boolean isShowTemplates() {
            return showTemplates;
        }

        public Options setShowTemplates(boolean showTemplates) {
            this.showTemplates = showTemplates;
            return this;
        }

        public Filter getFilter() {
            return filter;
        }

        public Options setFilter(Filter filter) {
            this.filter = filter;
            return this;
        }
    }

}
