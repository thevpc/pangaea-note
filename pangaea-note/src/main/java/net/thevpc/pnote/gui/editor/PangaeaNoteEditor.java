/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.editor;

import net.thevpc.pnote.gui.editor.editorcomponents.empty.EmpyNNodtEditorTypeComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.unsupported.UnsupportedEditorTypeComponent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JPanel;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.echo.Application;
import net.thevpc.pnote.gui.PangaeaNoteTypes;
import net.thevpc.pnote.gui.PangaeaNoteGuiApp;
import net.thevpc.pnote.gui.util.DefaultObjectListModel;
import net.thevpc.common.swing.JTabbedButtons;
import net.thevpc.common.swing.ObjectListModel;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.common.swing.ObjectListModelListener;
import net.thevpc.pnote.gui.PangaeaNoteAppExtensionHandler;
import net.thevpc.pnote.service.PangaeaNoteExtEditorListener;
import net.thevpc.pnote.service.PangaeaNoteTypeService;

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
    private JTabbedButtons noteEditorsSelector /*{
        @Override
        protected void updateButton(JButton b, int pos, int size) {
            b.setOpaque(false);
            b.setBackground(null);
            b.setMargin(new Insets(0, 0, 0, 0));
            b.setBorder(new RoundedBorder(5));
        }
    }*/;
    private Component editorsSelectorSuffix;
    private List<PangaeaNoteExtEditorListener> listeners = new ArrayList<>();
    private EditorOnLocalChangePropertyListenerImpl editorOnLocalChangePropertyListenerImpl = new EditorOnLocalChangePropertyListenerImpl();
    private PangaeaNoteGuiApp sapp;
    private Application app;
    private boolean compactMode;
    private boolean editable;

    public PangaeaNoteEditor(PangaeaNoteGuiApp sapp, boolean compactMode) {
        super(new CardLayout());
        this.compactMode = compactMode;
        this.sapp = sapp;
        this.app = sapp.app();
        this.setBorder(null);
        container = this;
//        add(container, BorderLayout.CENTER);

        if (!compactMode) {
            Box hb = Box.createHorizontalBox();
            noteEditorsSelector = new JTabbedButtons();
            editorsSelectorSuffix = Box.createHorizontalStrut(5);
            hb.add(noteEditorsSelector);
            hb.add(editorsSelectorSuffix);
            hb.add(Box.createHorizontalGlue());
            add(hb, BorderLayout.NORTH);
            noteEditorsSelector.addListener(new ObjectListModelListener() {
                @Override
                public void onSelected(Object component, int index) {
                    String editorType = (String) component;
                    if (currentNote != null) {
                        currentNote.setEditorType(editorType);
                        setNote(currentNote);
                    }
                }
            });
        }

        components.put("empty", new EmpyNNodtEditorTypeComponent());
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
            for (PangaeaNoteAppExtensionHandler appExtension : sapp.getLoadedAppExtensions()) {
                appExtension.getExtension().uninstallNoteEditorTypeComponent(null, c, sapp);
            }
        }
    }

    public void addListener(PangaeaNoteExtEditorListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public PangaeaNoteEditorTypeComponent createEditor(String name) {
        for (PangaeaNoteTypeService t : sapp.service().getContentTypeServices()) {
            PangaeaNoteEditorTypeComponent c=t.createEditor(name,compactMode,sapp);
            if(c!=null){
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
            for (PangaeaNoteAppExtensionHandler appExtension : sapp.getLoadedAppExtensions()) {
                appExtension.getExtension().installNoteEditorTypeComponent(null, c, sapp);
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

    public ObjectListModel createEditorTypeModel(String contentType) {
        contentType = sapp.service().normalizeContentType(contentType);
        String[] all = sapp.service().getEditorTypes(contentType);
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
            String contentType = sapp.service().normalizeContentType(note.getContentType());
            String editorType = sapp.service().normalizeEditorType(contentType, note.getEditorType());
            String[] all = sapp.service().getEditorTypes(contentType);
            if (!compactMode) {
                if (all.length == 0 || all.length == 1) {
                    noteEditorsSelector.setVisible(true);
                    editorsSelectorSuffix.setVisible(true);
                } else {
                    noteEditorsSelector.setVisible(true);
                    editorsSelectorSuffix.setVisible(true);
                    noteEditorsSelector.setModel(createEditorTypeModel(contentType));
                }
            }
            getEditor(editorType).setNote(note, sapp);//TODO FIX ME
            showEditor(editorType);
        }
    }

    private class EditorOnLocalChangePropertyListenerImpl implements PropertyListener {

        public EditorOnLocalChangePropertyListenerImpl() {
        }

        @Override
        public void propertyUpdated(PropertyEvent e) {
            if (!compactMode) {
                noteEditorsSelector.setModel(createEditorTypeModel(
                        currentNote == null ? null : currentNote.getContentType()
                ));
            }
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
