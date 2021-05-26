/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.thevpc.common.i18n.Str;
import net.thevpc.echo.ComboBox;
import net.thevpc.echo.SimpleItem;
import net.thevpc.echo.impl.Applications;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.api.PangaeaNoteTemplate;
import net.thevpc.pnote.api.PangaeaNoteTypeService;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteTypesComboBox extends ComboBox<SimpleItem> {

    private PangaeaNoteFrame frame;

    public PangaeaNoteTypesComboBox(PangaeaNoteFrame frame) {
        super(SimpleItem.class, frame.app());
        this.frame = frame;
        List<SimpleItem> availableTypes = new ArrayList<>();
        List<String> rct = frame.config().getRecentContentTypes();
        if (rct != null) {
            List<SimpleItem> recent = new ArrayList<>();
            for (String id : rct) {
                if (frame.service().isValidContentTypeExt(id)) {
                    recent.add(new SimpleItem(false, "recent-" + id, Str.i18n("content-type." + id),
                                    frame.service().getContentTypeIcon(PangaeaNoteMimeType.of(id)), 0
                            )
                    );
                }
            }
            if (recent.size() > 0) {
                availableTypes.add(createNoteTypeFamilyNameGroup("recent-documents"));
                availableTypes.addAll(recent);
            }
        }

        availableTypes.addAll(createTypeListNamedValue());
        this.disabledPredicate().set(SimpleItem::isGroup);
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

    private List<SimpleItem> createTypeListNamedValue() {
        List<SimpleItem> availableTypes = new ArrayList<>();
        LinkedHashMap<String, List<SimpleItem>> selectors = new LinkedHashMap<>();
        for (PangaeaNoteMimeType s : frame.service().getBaseContentTypes()) {
            PangaeaNoteTypeService cs = frame.service().getContentTypeService(s);
            ContentTypeSelector contentTypeSelector = cs.getContentTypeSelector();
            String g = contentTypeSelector.getGroup();
            List<SimpleItem> li = selectors.get(g);
            if (li == null) {
                li = new ArrayList<>();
                selectors.put(g, li);
            }
            li.add(
                    new SimpleItem(false, contentTypeSelector.getContentType().toString(),
                            Str.i18n("content-type." + contentTypeSelector.getContentType().toString()),
                            frame.service().getContentTypeIcon(contentTypeSelector.getContentType()),
                            contentTypeSelector.getOrder()
                    )
            );
        }

        for (PangaeaNoteTemplate template : frame.service().getTemplates()) {
            String g = template.getGroup();
            if (Applications.isBlank(g)) {
                g = "templates";
            }
            List<SimpleItem> li = selectors.get(g);
            if (li == null) {
                li = new ArrayList<>();
                selectors.put(g, li);
            }
            Str s = template.getLabel(frame.service());
            if (s == null) {
                s = Str.i18n("content-type." + template.getContentType().toString());
            }
            SimpleItem n = new SimpleItem(false, template.getContentType().toString(), s, frame.service().getContentTypeIcon(template.getContentType()),
                    template.getOrder()
            );
            li.add(n);
        }
        for (Map.Entry<String, List<SimpleItem>> entry : selectors.entrySet()) {
            entry.getValue().sort((a, b) -> Integer.compare(a.getPreferredOrder(), b.getPreferredOrder()));
            availableTypes.add(createNoteTypeFamilyNameGroup(entry.getKey()));
            availableTypes.addAll(entry.getValue());
        }
        return availableTypes;
    }

    public void setSelectedContentType(PangaeaNoteMimeType contentType, String editorType) {
        SimpleItem si=this.values().stream().filter(v->{
            if (v.getId() == null || v.getId().length() == 0) {
                if (contentType == null) {
                    return true;
                }
            }
            PangaeaNoteMimeType ct = frame.service().normalizeContentType(v.getId());
            if (ct.equals(contentType)) {
                String[] ss = v.getId().split(":");
                if (ss.length == 1) {
                    return true;
                }
                if (editorType == null || frame.service().normalizeEditorType(ct, editorType).equals(ss[1])) {
                    return true;
                }
            }
            return false;
        }).findFirst().orElse(null);
        this.selection().set(si);
    }

}
