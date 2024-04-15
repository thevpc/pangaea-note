/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.PropertyListener;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppChoiceItemContext;
import net.thevpc.echo.api.components.AppChoiceItemRenderer;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.pnote.core.types.forms.model.PangaeaNoteObject;

/**
 * @author thevpc
 */
public class PangaeaNoteFormsEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    private ChoiceList<PangaeaNoteObject> headerList;
    private FormsDataPane componentList;

    private PangaeaNoteFrame frame;
    private boolean editable = true;
    private boolean compactMode;

    public PangaeaNoteFormsEditorTypeComponent(boolean compactMode, PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        this.compactMode = compactMode;
        headerList = new ChoiceList<>(PangaeaNoteObject.class, app());
        headerList.itemRenderer().set(new AppChoiceItemRenderer<PangaeaNoteObject>() {
            @Override
            public void render(AppChoiceItemContext<PangaeaNoteObject> context) {
                PangaeaNoteObject u = context.getValue();
                if (!u.getFields().isEmpty()) {
                    String value = u.getFields().get(0).getValue();
                    context.setText((value==null|| value.trim().isEmpty())?"<empty>":value);
                }
                context.renderDefault();
            }
        });
        headerList.selection().indices().onChange(new PropertyListener() {
            @Override
            public void propertyUpdated(PropertyEvent event) {
                Integer index = event.newValue();
                if(index!=null) {
                    int max = headerList.values().size();
                    if (max <= 1 || index == 0) {
                        componentList.scrollPane.scrollTo(0f, 0f);
                    } else {
                        componentList.scrollPane.scrollTo(0f, 1f * index / max);
                    }
                }
            }
        });
        componentList = new FormsDataPane(frame, headerList);
        ScrollPane scrollPane = new ScrollPane(componentList)
                .with(v -> v.anchor().set(Anchor.CENTER));
        componentList.scrollPane=scrollPane;
        ScrollPane scrollPane2 = new ScrollPane(headerList)
                .with(v -> v.anchor().set(Anchor.LEFT));
        children().add(scrollPane);
        children().add(scrollPane2);
        prepareToolBar();
        propagateEvents();
        refreshView();
    }

    protected void installEnabler(Runnable r) {
        r.run();
        frame.app().toolkit().focusOwner().onChange(r);
        frame.treePane().tree().selection().onChange(r);
    }

    protected FormsDataPane resolveFormsDataPane() {
        AppComponent fo = frame.app().toolkit().focusOwner().get();
        if (fo != null) {
            FormsDataPane r = resolveFormsDataPane(fo);
            if (r != null) {
                return r;
            }
        }
        PangaeaNoteEditorTypeComponent ed = frame.noteEditor().getCurrentEditor();
        if (ed instanceof PangaeaNoteFormsEditorTypeComponent) {
            return ((PangaeaNoteFormsEditorTypeComponent) ed).componentList;
        }
        return null;
    }

    protected FormsDataPane resolveFormsDataPane(AppComponent component) {
        if (component == null) {
            return null;
        }
        if (component instanceof FormsDataPane) {
            return (FormsDataPane) component;
        }
        return resolveFormsDataPane(component.parent());
    }

    protected void prepareToolBar() {
        frame.findOrCreateToolBar("Forms", editToolBar -> {
            PangaeaNoteApp app = frame.app();
            editToolBar.children().add(new Button("editForm", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                FormsDataPane q = resolveFormsDataPane();
                if (q != null) {
                    if (q.editable().get()) {
                        q.onEditForm();
                    }
                }
            }, app).with(
                    x -> installEnabler(() -> {
                        FormsDataPane q = resolveFormsDataPane();
                        x.enabled().set(q != null);
                        x.visible().set(
                                !app.hideDisabled().get() || (q != null)
                        );
                    })
            ));
            editToolBar.children().add(new Button("addToObjectList", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                FormsDataPane q = resolveFormsDataPane();
                if (q != null) {
                    if (q.editable().get()) {
                        q.onAddObject();
                    }
                }
            }, app).with(
                    x -> installEnabler(() -> {
                        FormsDataPane q = resolveFormsDataPane();
                        boolean copyEnabled = false;
                        if (q != null) {
                            if (q.editable().get()) {
                                copyEnabled = true;
                            }
                        }
                        x.enabled().set(copyEnabled);
                        x.visible().set(
                                !app.hideDisabled().get() || copyEnabled
                        );
                    })
            ));

            editToolBar.children().add(new Button("clearObjectList", event -> {
                //KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()
                FormsDataPane q = resolveFormsDataPane();
                if (q != null) {
                    if (q.editable().get()) {
                        q.onRemoveAllObjects();
                    }
                }
            }, app).with(
                    x -> installEnabler(() -> {
                        FormsDataPane q = resolveFormsDataPane();
                        boolean copyEnabled = false;
                        if (q != null) {
                            if (q.editable().get()) {
                                copyEnabled = true;
                            }
                        }
                        x.enabled().set(copyEnabled);
                        x.visible().set(
                                !app.hideDisabled().get() || copyEnabled
                        );
                    })
            ));
        });
    }

    public void refreshView() {
//        bar.visible().set(
//                dynamicDocument != null
//                        && (dynamicDocument.getValues() == null || dynamicDocument.getValues().isEmpty())
//        );
//        this.invalidate();
//        this.revalidate();
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNote note) {
        componentList.setNote(note, frame);
        setEditable(!note.isReadOnly());
        refreshView();
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public void setEditable(boolean b) {
        this.componentList.editable().set(b);
        this.editable = b;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

}
