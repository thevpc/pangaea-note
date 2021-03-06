/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.frame.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.BorderPane;
import net.thevpc.echo.Panel;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.core.types.empty.PangaeaNoteEmptyNoteEditorTypeComponent;
import net.thevpc.pnote.core.types.unsupported.PangaeaNoteUnsupportedEditorTypeComponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.thevpc.echo.Application;
import net.thevpc.pnote.core.frame.PangaeaNoteTypes;
import net.thevpc.pnote.core.frame.PangaeaNoteAppExtensionHandler;
import net.thevpc.pnote.api.PangaeaNoteExtEditorListener;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

/**
 *
 * @author thevpc
 */
public class PangaeaNoteEditor extends BorderPane {

    private Map<String, PangaeaNoteEditorTypeComponent> components = new LinkedHashMap<String, PangaeaNoteEditorTypeComponent>();
    private PangaeaNoteEditorTypeComponent currentEditor;
    private PangaeaNote currentNote;
    private Panel container;
    private String editorTypeI18nPrefix = "EditorType";
    private List<PangaeaNoteExtEditorListener> listeners = new ArrayList<>();
//    private EditorOnLocalChangePropertyListenerImpl editorOnLocalChangePropertyListenerImpl = new EditorOnLocalChangePropertyListenerImpl();
    private PangaeaNoteFrame frame;
    private Application app;
    private boolean compactMode;
    private boolean editable;

    public PangaeaNoteEditor(PangaeaNoteFrame frame, boolean compactMode) {
        super("PangaeaNoteEditor", frame.app());
        title().set(Str.i18n("Content"));
        anchor().set(Anchor.CENTER);
        this.compactMode = compactMode;
        this.frame = frame;
        this.app = frame.app();
        container = this;
//        add(container, BorderLayout.CENTER);

        components.put("empty", new PangaeaNoteEmptyNoteEditorTypeComponent(frame));
        components.put(PangaeaNoteTypes.EDITOR_UNSUPPORTED, new PangaeaNoteUnsupportedEditorTypeComponent(app()));

//        for (Map.Entry<String, PangaeaNoteEditorTypeComponent> entry : components.entrySet()) {
//            container.add(entry.getValue().component(), entry.getKey());
//        }
        showEditor("empty");

//        app.i18n().locale().onChange(editorOnLocalChangePropertyListenerImpl);
    }

    @Override
    public PangaeaNoteApp app() {
        return (PangaeaNoteApp) super.app();
    }

    public void uninstall() {
//        app.i18n().locale().events().remove(editorOnLocalChangePropertyListenerImpl);
        for (Map.Entry<String, PangaeaNoteEditorTypeComponent> entry : components.entrySet()) {
            PangaeaNoteEditorTypeComponent c = entry.getValue();
            c.uninstall();
            for (PangaeaNoteAppExtensionHandler appExtension : frame.app().getLoadedAppExtensions()) {
                appExtension.getExtension().uninstallNoteEditorTypeComponent(null, c, frame);
            }
        }
    }

    public void addListener(PangaeaNoteExtEditorListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public PangaeaNoteEditorTypeComponent createEditor(String name) {
        for (PangaeaNoteEditorService t : frame.app().getEditorServices()) {
            PangaeaNoteEditorTypeComponent c = t.createEditor(name, compactMode, frame);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public String getEditorName(String name) {
        PangaeaNoteEditorTypeComponent n = components.get(name);
        if (n != null) {
            return name;
        }
        PangaeaNoteEditorTypeComponent c = createEditor(name);
        if (c != null) {
            components.put(name, c);
            for (PangaeaNoteAppExtensionHandler appExtension : frame.app().getLoadedAppExtensions()) {
                appExtension.getExtension().installNoteEditorTypeComponent(null, c, frame);
            }
            return name;
        }
        return PangaeaNoteTypes.EDITOR_UNSUPPORTED;
    }

    public PangaeaNoteEditorTypeComponent getEditor(String name) {
        return components.get(getEditorName(name));
    }

    public void showEditor(String name) {
        if (currentEditor != null) {
            container.children().remove(currentEditor);
        }
        String okName = getEditorName(name);
        this.currentEditor = getEditor(okName);
        container.children().add(currentEditor);
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        if (currentEditor != null) {
            currentEditor.requestFocus();
        }
    }

    public PangaeaNoteEditorTypeComponent getCurrentEditor() {
        return currentEditor;
    }

    public PangaeaNoteEditorTypeComponent editorComponent() {
        return currentEditor;
    }

//    public ObjectListModel createEditorTypeModel(PangaeaNoteMimeType contentType) {
//        contentType = win.service().normalizeContentType(contentType);
//        String all = win.service().getEditorType(contentType);
//        return new DefaultObjectListModel(
//                Arrays.asList(all),
//                x -> app.i18n().getString(editorTypeI18nPrefix + "." + x)
//        );
//    }
    public PangaeaNote getNote() {
        return currentNote;
    }

    public void setNote(PangaeaNote note) {
        this.currentNote = note;
        if (note == null) {
            showEditor("empty");
        } else {
            PangaeaNoteMimeType contentType = frame.app().normalizeContentType(note.getContentType());
            String editorType = frame.app().normalizeEditorType(contentType, note.getEditorType());
            PangaeaNoteEditorTypeComponent editor = getEditor(editorType);
            editor.setNote(note);
            showEditor(editorType);
//            editor.requestFocus();
        }
    }

//    private class EditorOnLocalChangePropertyListenerImpl implements PropertyListener {
//
//        public EditorOnLocalChangePropertyListenerImpl() {
//        }
//
//        @Override
//        public void propertyUpdated(PropertyEvent e) {
//        }
//    }
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        for (PangaeaNoteEditorTypeComponent component : components.values()) {
            component.setEditable(editable);
        }
    }

}
