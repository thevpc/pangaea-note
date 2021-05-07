/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor;

import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.PangaeaNoteEditorService;
import net.thevpc.pnote.gui.editor.editorcomponents.empty.EmpyNNodtEditorTypeComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.unsupported.UnsupportedEditorTypeComponent;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.echo.Application;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.util.DefaultObjectListModel;
import net.thevpc.common.swing.list.ObjectListModel;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.PangaeaNoteAppExtensionHandler;
import net.thevpc.pnote.api.PangaeaNoteExtEditorListener;
import net.thevpc.pnote.api.model.PangaeaNoteMimeType;

/**
 *
 * @author vpc
 */
public class PangaeaNoteEditor extends JPanel {

    private Map<String, PangaeaNoteEditorTypeComponent> components = new LinkedHashMap<String, PangaeaNoteEditorTypeComponent>();
    private PangaeaNoteEditorTypeComponent currentEditor;
    private PangaeaNoteExt currentNote;
    private JPanel container;
    private String editorTypeI18nPrefix = "EditorType";
    private List<PangaeaNoteExtEditorListener> listeners = new ArrayList<>();
    private EditorOnLocalChangePropertyListenerImpl editorOnLocalChangePropertyListenerImpl = new EditorOnLocalChangePropertyListenerImpl();
    private PangaeaNoteWindow win;
    private Application app;
    private boolean compactMode;
    private boolean editable;

    public PangaeaNoteEditor(PangaeaNoteWindow win, boolean compactMode) {
        super(new CardLayout());
        this.compactMode = compactMode;
        this.win = win;
        this.app = win.app();
        this.setBorder(null);
        container = this;
//        add(container, BorderLayout.CENTER);

        components.put("empty", new EmpyNNodtEditorTypeComponent(win));
        components.put(PangaeaNoteTypes.EDITOR_UNSUPPORTED, new UnsupportedEditorTypeComponent());

        for (Map.Entry<String, PangaeaNoteEditorTypeComponent> entry : components.entrySet()) {
            container.add(entry.getValue().component(), entry.getKey());
        }

        showEditor("empty");

        app.i18n().locale().listeners().add(editorOnLocalChangePropertyListenerImpl);
    }

    public void uninstall() {
        app.i18n().locale().listeners().remove(editorOnLocalChangePropertyListenerImpl);
        for (Map.Entry<String, PangaeaNoteEditorTypeComponent> entry : components.entrySet()) {
            PangaeaNoteEditorTypeComponent c = entry.getValue();
            c.uninstall();
            for (PangaeaNoteAppExtensionHandler appExtension : win.papp().getLoadedAppExtensions()) {
                appExtension.getExtension().uninstallNoteEditorTypeComponent(null, c, win);
            }
        }
    }

    public void addListener(PangaeaNoteExtEditorListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public PangaeaNoteEditorTypeComponent createEditor(String name) {
        for (PangaeaNoteEditorService t : win.papp().getEditorServices()) {
            PangaeaNoteEditorTypeComponent c = t.createEditor(name, compactMode, win);
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
            container.add(c.component(), name);
            for (PangaeaNoteAppExtensionHandler appExtension : win.papp().getLoadedAppExtensions()) {
                appExtension.getExtension().installNoteEditorTypeComponent(null, c, win);
            }
            return name;
        }
        return PangaeaNoteTypes.EDITOR_UNSUPPORTED;
    }

    public PangaeaNoteEditorTypeComponent getEditor(String name) {
        return components.get(getEditorName(name));
    }

    public void showEditor(String name) {
        String okName = getEditorName(name);
        this.currentEditor = getEditor(okName);
        ((CardLayout) container.getLayout()).show(container, okName);
    }

    public PangaeaNoteEditorTypeComponent editorComponent() {
        return currentEditor;
    }
    

    public ObjectListModel createEditorTypeModel(PangaeaNoteMimeType contentType) {
        contentType = win.service().normalizeContentType(contentType);
        String all = win.service().getEditorType(contentType);
        return new DefaultObjectListModel(
                Arrays.asList(all),
                x -> app.i18n().getString(editorTypeI18nPrefix + "." + x)
        );
    }

    public PangaeaNoteExt getNote() {
        return currentNote;
    }

    public void setNote(PangaeaNoteExt note) {
        this.currentNote = note;
        if (note == null) {
            showEditor("empty");
        } else {
            PangaeaNoteMimeType contentType = win.service().normalizeContentType(note.getContentType());
            String editorType = win.service().normalizeEditorType(contentType, note.getEditorType());
            getEditor(editorType).setNote(note, win);//TODO FIX ME
            showEditor(editorType);
        }
    }

    private class EditorOnLocalChangePropertyListenerImpl implements PropertyListener {

        public EditorOnLocalChangePropertyListenerImpl() {
        }

        @Override
        public void propertyUpdated(PropertyEvent e) {
        }
    }

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
