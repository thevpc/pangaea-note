/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JComboBox;
import net.thevpc.common.swing.NamedValue;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.service.PangaeaNoteTemplate;

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
                recent.add(
                        new NamedValue(false, "recent:" + id, sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id),
                                sapp.service().getContentTypeIcon(id)
                        )
                );
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
        return a.getId();
    }

    protected final NamedValue createNoteTypeFamilyNameValue(String id) {
        return new NamedValue(false, id,
                sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id),
                sapp.service().getContentTypeIcon(id)
        );
    }

    protected final NamedValue createNoteTypeFamilyNameGroup(String id) {
        return new NamedValue(true, id, sapp.app().i18n().getString("PangaeaNoteTypeFamily." + id), null);
    }

    protected final NamedValue createIconValue(String id) {
        return new NamedValue(false, id,
                sapp.app().i18n().getString("Icon." + id),
                id);
    }

    private List<NamedValue> createTypeListNamedValue() {
        List<NamedValue> availableTypes = new ArrayList<>();
//        availableTypes.add(createNoteTypeFamilyNameGroup("quick-strings"));
//        for (String s : new String[]{PangaeaNoteTypes.STRING, PangaeaNoteTypes.PASSWORD}) {
//            availableTypes.add(createNoteTypeFamilyNameValue(s));
//        }
        availableTypes.add(createNoteTypeFamilyNameGroup("simple-documents"));
        for (String s : new String[]{PangaeaNoteTypes.PLAIN, PangaeaNoteTypes.RICH_HTML}) {
            availableTypes.add(createNoteTypeFamilyNameValue(s));
        }
//        availableTypes.add(createNoteTypeFamilyNameGroup("lists"));
        availableTypes.add(createNoteTypeFamilyNameValue(PangaeaNoteTypes.NOTE_LIST));
        availableTypes.add(createNoteTypeFamilyNameValue(PangaeaNoteTypes.OBJECT_LIST));
        availableTypes.add(createNoteTypeFamilyNameValue(PangaeaNoteTypes.FILE));

        for (PangaeaNoteTemplate value : sapp.service().getTemplates()) {
            String s = value.getLabel(sapp.service());
            if (s == null) {
                s = sapp.app().i18n().getString("PangaeaNoteTypeFamily." + value.getId());
            }
            NamedValue n = new NamedValue(false, value.getId(), s, sapp.service().getContentTypeIcon(value.getId()));
            availableTypes.add(n);
        }

//        if (extra.size() > 0) {
//            availableTypes.add(createNoteTypeFamilyNameGroup("custom"));
//            for (PangaeaNoteTemplate value : extra.values()) {
//                String s = value.getLabel(sapp);
//                if (s == null) {
//                    s = sapp.app().i18n().getString("PangaeaNoteTypeFamily." + value.getId());
//                }
//                NamedValue n = new NamedValue(false, value.getId(), s, null);
//                availableTypes.add(n);
//            }
//        }
        availableTypes.add(createNoteTypeFamilyNameGroup("sources"));
        for (String s : new String[]{
            PangaeaNoteTypes.SOURCE_HTML,
            PangaeaNoteTypes.SOURCE_MARKDOWN,
            PangaeaNoteTypes.SOURCE_NUTS_TEXT_FORMAT,
            PangaeaNoteTypes.JAVA,
            PangaeaNoteTypes.JAVASCRIPT,
            PangaeaNoteTypes.C,
            PangaeaNoteTypes.CPP,}) {
            availableTypes.add(createNoteTypeFamilyNameValue(s));
        }
        return availableTypes;
    }

    public void setSelectedContentType(String contentType, String editorType) {
        int s = getModel().getSize();
        for (int i = 0; i < s; i++) {
            NamedValue v = (NamedValue) getModel().getElementAt(i);
            if (v.getId() == null || v.getId().length() == 0) {
                if (contentType == null) {
                    setSelectedIndex(i);
                    return;
                }
            }
            String ct = sapp.service().normalizeContentType(v.getId());
            if (ct.equals(contentType)) {
                String[] ss = v.getId().split(":");
                if(ss.length==1){
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
