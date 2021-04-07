/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import net.thevpc.common.swing.NamedValue;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.service.ContentTypeSelector;
import net.thevpc.pnote.service.PangaeaNoteTemplate;
import net.thevpc.pnote.service.PangaeaNoteTypeService;
import net.thevpc.pnote.model.PangaeaNoteContentType;
import net.thevpc.pnote.util.OtherUtils;

/**
 *
 * @author vpc
 */
public class PangaeaNoteTypesCombobox extends JComboBox {

    private PangaeaNoteGuiApp sapp;

    public PangaeaNoteTypesCombobox(PangaeaNoteGuiApp sapp) {
        this.sapp = sapp;
        List<NamedValue> availableTypes = new ArrayList<>();
        List<String> rct = sapp.config().getRecentContentTypes();
        if (rct != null) {
            List<NamedValue> recent = new ArrayList<>();
            for (String id : rct) {
                if (sapp.service().isValidContentTypeExt(id)) {
                    recent.add(new NamedValue(false, "recent-" + id, sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id),
                                    sapp.service().getContentTypeIcon(PangaeaNoteContentType.of(id)), 0
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
        ComboboxHelper.prepareCombobox(this, sapp.app(), availableTypes.toArray(new NamedValue[0]));
    }

    public String getSelectedContentTypeId() {
        NamedValue a = (NamedValue) getSelectedItem();
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
//                sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id),
//                sapp.service().getContentTypeIcon(id)
//        );
//    }
    protected final NamedValue createNoteTypeFamilyNameGroup(String id) {
        return new NamedValue(true, id, sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id), null, 0);
    }

    private List<NamedValue> createTypeListNamedValue() {
        List<NamedValue> availableTypes = new ArrayList<>();
        LinkedHashMap<String, List<NamedValue>> selectors = new LinkedHashMap<>();
        for (PangaeaNoteContentType s : sapp.service().getBaseContentTypes()) {
            PangaeaNoteTypeService cs = sapp.service().getContentTypeService(s);
            ContentTypeSelector contentTypeSelector = cs.getContentTypeSelector();
            String g = contentTypeSelector.getGroup();
            List<NamedValue> li = selectors.get(g);
            if (li == null) {
                li = new ArrayList<>();
                selectors.put(g, li);
            }
            li.add(
                    new NamedValue(false, contentTypeSelector.getContentType().toString(),
                            sapp.app().i18n().getString("PangaeaNoteTypeFamily." + contentTypeSelector.getContentType().toString()),
                            sapp.service().getContentTypeIcon(contentTypeSelector.getContentType()),
                            contentTypeSelector.getOrder()
                    )
            );
        }

        for (PangaeaNoteTemplate template : sapp.service().getTemplates()) {
            String g = template.getGroup();
            if (OtherUtils.isBlank(g)) {
                g = "templates";
            }
            List<NamedValue> li = selectors.get(g);
            if (li == null) {
                li = new ArrayList<>();
                selectors.put(g, li);
            }
            String s = template.getLabel(sapp.service());
            if (s == null) {
                s = sapp.app().i18n().getString("PangaeaNoteTypeFamily." + template.getContentType().toString());
            }
            NamedValue n = new NamedValue(false, template.getContentType().toString(), s, sapp.service().getContentTypeIcon(template.getContentType()),
                    template.getOrder()
            );
            li.add(n);
        }
        for (Map.Entry<String, List<NamedValue>> entry : selectors.entrySet()) {
            entry.getValue().sort((a, b) -> Integer.compare(a.getPreferredOrder(), b.getPreferredOrder()));
            availableTypes.add(createNoteTypeFamilyNameGroup(entry.getKey()));
            availableTypes.addAll(entry.getValue());
        }
        return availableTypes;
    }

    public void setSelectedContentType(PangaeaNoteContentType contentType, String editorType) {
        int s = getModel().getSize();
        for (int i = 0; i < s; i++) {
            NamedValue v = (NamedValue) getModel().getElementAt(i);
            if (v.getId() == null || v.getId().length() == 0) {
                if (contentType == null) {
                    setSelectedIndex(i);
                    return;
                }
            }
            PangaeaNoteContentType ct = sapp.service().normalizeContentType(v.getId());
            if (ct.equals(contentType)) {
                String[] ss = v.getId().split(":");
                if (ss.length == 1) {
                    setSelectedIndex(i);
                    return;
                }
                if (editorType == null || sapp.service().normalizeEditorType(ct, editorType).equals(ss[1])) {
                    setSelectedIndex(i);
                    return;
                }
            }
        }
        setSelectedItem(null);
    }

}
