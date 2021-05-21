/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.dialogs;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.constraints.*;
import net.thevpc.pnote.api.PangaeaNoteTemplate;
import net.thevpc.pnote.api.PangaeaNoteTypeService;
import net.thevpc.pnote.api.model.ContentTypeSelector;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.util.OtherUtils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vpc
 */
public class PangaeaNoteTypesList extends BorderPane {

    private PangaeaNoteFrame frame;
    private ChoiceList<SimpleItem> list;
    private ScrollPane typeDescriptionContent;
    private TextArea typeDescription;

    public PangaeaNoteTypesList(PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        List<SimpleItem> availableTypes = new ArrayList<>();
        List<String> rct = frame.config().getRecentContentTypes();
        if (rct != null) {
            List<SimpleItem> recent = new ArrayList<>();
            for (String id : rct) {
                if (frame.service().isValidContentTypeExt(id)) {
                    recent.add(new SimpleItem(false, "recent-" + id, frame.app().i18n().getString("content-type." + id),
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
        list = new ChoiceList<>(SimpleItem.class, frame.app());
        list.disabledPredicate().set(SimpleItem::isGroup);
        list.values().addAll(availableTypes.toArray(new SimpleItem[0]));
        list.selection().multipleSelection().set(false);
        list.selection().onChange(event -> {
            SimpleItem v = list.selection().get();
            if (v != null) {
                typeDescription.text().set(
                        Str.of(resolveNoteTypeDescription(getSelectedContentTypeId()))
                );
            } else {
                typeDescription.text().set(Str.empty());
            }
        });
        typeDescriptionContent = new ScrollPane(typeDescription
                = new TextArea(Str.empty(),app())
                .with((TextArea a)-> {
                    a.textContentType().set("text/html");
                    a.editable().set(false);
                })
        ).with(s->{
            s.prefSize().set(new Dimension(300, 100));
            s.anchor().set(Anchor.CENTER);
        });

        ScrollPane listWithScroll = new ScrollPane(list)
                .with((ScrollPane s) -> s.anchor().set(Anchor.LEFT));
        parentConstraints().addAll(GrowX.ALWAYS, GrowY.ALWAYS);
        children().addAll(
                listWithScroll,
                typeDescriptionContent
                );
        list.selection().set(list.values().get(1));
        System.out.println(list.selection().get());
    }

    public void addChangeListener(ChangeListener ce) {
        list.selection().onChange(e->{
            ce.stateChanged(new ChangeEvent(PangaeaNoteTypesList.this));
        });
    }

    public String resolveNoteTypeDescription(String id) {
        if (id == null || id.isEmpty() || id.equals("id")) {
            id = "none";
        }
        if (id.startsWith("recent-")) {
            id = id.substring("recent-".length());
        }
        String s = frame.app().i18n().getString("content-type." + id + ".help");
        if (s.startsWith("resource://")) {
            URL i = getClass().getClassLoader().getResource(s.substring("resource://".length()));
            if (i == null) {
                throw new IllegalArgumentException("not found resource " + s);
            }
            try (InputStream is = i.openStream()) {
                s = new String(OtherUtils.toByteArray(is));
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        String toLowerCase = s.trim().toLowerCase();
        if (!toLowerCase.startsWith("<html>")
                && !toLowerCase.startsWith("<!doctype html>")) {
            s = "<html><body>" + s + "</body></html>";
        }
        return s;
    }

    public String getSelectedContentTypeId() {
        SimpleItem a = list.selection().get();
        if (a == null) {
            return null;
        }
        if (a.isGroup()) {
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
        return new SimpleItem(true, id, frame.app().i18n().getString("content-type." + id), null, 0);
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
                            frame.app().i18n().getString("content-type." + contentTypeSelector.getContentType().toString()),
                            frame.service().getContentTypeIcon(contentTypeSelector.getContentType()),
                            contentTypeSelector.getOrder()
                    )
            );
        }

        for (PangaeaNoteTemplate template : frame.service().getTemplates()) {
            String g = template.getGroup();
            if (OtherUtils.isBlank(g)) {
                g = "templates";
            }
            List<SimpleItem> li = selectors.get(g);
            if (li == null) {
                li = new ArrayList<>();
                selectors.put(g, li);
            }
            String s = template.getLabel(frame.service());
            if (s == null) {
                s = frame.app().i18n().getString("content-type." + template.getContentType().toString());
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
        SimpleItem si=list.values().stream().filter(v->{
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
        list.selection().set(si);
    }

}
