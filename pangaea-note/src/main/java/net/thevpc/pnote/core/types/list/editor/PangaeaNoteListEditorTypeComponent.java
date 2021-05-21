/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Button;
import net.thevpc.echo.Panel;
import net.thevpc.echo.ScrollPane;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppColor;
import net.thevpc.echo.api.AppFont;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.Layout;
import net.thevpc.echo.constraints.ParentWrapCount;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.core.special.DataPane;
import net.thevpc.pnote.core.special.DataPaneRenderer;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListLayout;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.dialogs.EditNoteDialog;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;

import javax.swing.*;
import java.util.Set;

/**
 * @author vpc
 */
public class PangaeaNoteListEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    //    private JComponentList<PangaeaNoteExt> componentList;
    private DataPane<PangaeaNoteExt> componentList;
    private PangaeaNoteExt currentNote;
    private PangaeaNoteListModel noteListModel;
    private PangaeaNoteFrame frame;
    private boolean editable = true;
    private boolean compactMode = true;

    public PangaeaNoteListEditorTypeComponent(boolean compactMode, PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        this.compactMode = compactMode;
        componentList = new DataPane<>(PangaeaNoteExt.class,
                new PangaeaNoteExtDataPaneRenderer(this),
                frame.app()).with(p -> p.parentConstraints().addAll(Layout.VERTICAL))
        ;
        children().add(componentList);
//        componentList = new JComponentList<PangaeaNoteExt>(new JComponentListItem<PangaeaNoteExt>() {
//            @Override
//            public JComponent createComponent(int pos, int size) {
//                return new Item(win);
//            }
//
//            @Override
//            public void setComponentValue(JComponent comp, PangaeaNoteExt value, int pos, int size) {
//                Item b = (Item) comp;
//                b.setValue(value, pos, size);
//            }
//
//            @Override
//            public PangaeaNoteExt getComponentValue(JComponent comp, int pos) {
//                return ((Item) comp).getValue(pos);
//            }
//
//            @Override
//            public void uninstallComponent(JComponent comp) {
//                ((Item) comp).onUninstall();
//            }
//
//            @Override
//            public void setEditable(JComponent component, boolean editable, int pos, int size) {
//                ((Item) component).setEditable(editable);
//            }
//
//        });
        ScrollPane scrollPane = new ScrollPane(componentList);
//        scrollPane.setWheelScrollingEnabled(true);
        children().add(scrollPane);
    }

    @Override
    public AppComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteFrame win) {
        this.currentNote = note;
        PangaeaNoteListService s = (PangaeaNoteListService) win.service().getContentTypeService(PangaeaNoteListService.LIST);
        this.noteListModel = s.elementToContent(note.getContent());
        if (this.noteListModel == null) {
            this.noteListModel = new PangaeaNoteListModel();
        }
        componentList.values().setAll(note.getChildren().toArray(new PangaeaNoteExt[0]));
        PangaeaNoteListLayout layout = noteListModel.getLayout();
        if (layout == null) {
            layout = PangaeaNoteListLayout.VERTICAL;
            noteListModel.setLayout(layout);
        }
        componentList.parentConstraints().clear();
        switch (layout) {
            case VERTICAL: {
                int cr = noteListModel.getColsRows();
                if (cr <= 1) {
                    componentList.parentConstraints().addAll(Layout.VERTICAL);
                } else {
                    if (cr >= 100) {
                        cr = 100;
                    }
                    componentList.parentConstraints().addAll(Layout.VERTICAL, new ParentWrapCount(cr));
                }
                break;
            }

            case HORIZONTAL: {
                int cr = noteListModel.getColsRows();
                if (cr <= 1) {
                    componentList.parentConstraints().addAll(Layout.HORIZONTAL);
                } else {
                    if (cr >= 100) {
                        cr = 100;
                    }
                    componentList.parentConstraints().addAll(Layout.HORIZONTAL, new ParentWrapCount(cr));
                }
                break;
            }
            case TAB: {
                //componentList.setListLayout(new JComponentList.Tab());
                //TODO Fix me later!!
                componentList.parentConstraints().addAll(Layout.VERTICAL);
                break;
            }
            default: {
                componentList.parentConstraints().addAll(Layout.VERTICAL);
            }
        }
        setEditable(!note.isReadOnly());
    }

    public boolean setSelectedName(String name, boolean sel) {
        if (this.currentNote != null && this.noteListModel != null) {
            Set<String> n = this.noteListModel.getSelectedNames();
            if (sel) {
                n.add(name);
            } else {
                n.remove(name);
            }
            frame.onDocumentChanged();
            PangaeaNoteListService s = (PangaeaNoteListService) frame.service().getContentTypeService(PangaeaNoteListService.LIST);
            this.currentNote.setContent(s.contentToElement(this.noteListModel));
        }
        return false;
    }    public boolean isCompactMode() {
        return compactMode;
    }

    public boolean isSelectedIndex(String name) {
        if (this.currentNote != null && this.noteListModel != null) {
            return this.noteListModel.getSelectedNames().contains(name);
        }
        return false;
    }

    public void onAddObjectAt(int pos) {
        if (currentNote != null) {
            if (currentNote.getChildren().size() > 0) {
                PangaeaNoteExt p = currentNote.getChildren().get(pos);
                currentNote.addChild(pos, PangaeaNoteExt.of(new PangaeaNote().setContentType(p.getContentType())));
            } else {
                currentNote.addChild(pos, PangaeaNoteExt.of(new PangaeaNote().setContentType(PangaeaNotePlainTextService.PLAIN.toString())));
            }
            componentList.children().setAll(currentNote.getChildren().toArray(new AppComponent[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onDuplicateObjectAt(int pos) {
        if (currentNote != null) {
            PangaeaNoteExt p = currentNote.getChildren().get(pos);
            currentNote.addChild(pos, p.duplicate());
            componentList.children().setAll(currentNote.getChildren().toArray(new AppComponent[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onRemoveObjectAt(int pos) {
        if (currentNote != null) {
            currentNote.removeChild(pos);
            componentList.children().setAll(currentNote.getChildren().toArray(new AppComponent[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onMoveUpAt(int pos) {
        if (currentNote != null) {
            currentNote.moveUp(pos);
            componentList.children().setAll(currentNote.getChildren().toArray(new AppComponent[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onMoveDownAt(int pos) {
        if (currentNote != null) {
            currentNote.moveDown(pos);
            componentList.children().setAll(currentNote.getChildren().toArray(new AppComponent[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onMoveFirstAt(int pos) {
        if (currentNote != null) {
            currentNote.moveFirst(pos);
            componentList.children().setAll(currentNote.getChildren().toArray(new AppComponent[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onEditAt(int pos) {
        PangaeaNoteExt cc = currentNote.getChildren().get(pos);
        PangaeaNote n = new EditNoteDialog(frame, cc).showDialog();
        if (n != null) {
            frame.treePane().fireNoteChanged(cc);
//            this.invalidate();
//            this.repaint();
        }
    }

    public void onMoveLastAt(int pos) {
        if (currentNote != null) {
            currentNote.moveLast(pos);
            componentList.children().setAll(currentNote.getChildren().toArray(new AppComponent[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    private static class PangaeaNoteExtDataPaneRenderer implements DataPaneRenderer<PangaeaNoteExt> {
        private PangaeaNoteListEditorTypeComponent parent;

        public PangaeaNoteExtDataPaneRenderer(PangaeaNoteListEditorTypeComponent parent) {
            this.parent = parent;
        }

        @Override
        public void set(int index, PangaeaNoteExt value, AppComponent component) {
            ((Item) component).setValue(value, index);
        }

        @Override
        public PangaeaNoteExt get(int index, AppComponent component) {
            return ((Item) component).getValue();
        }

        @Override
        public AppComponent create() {
            return new Item(parent);
        }

        @Override
        public void dispose(AppComponent component) {
            ((Item) component).onUninstall();
        }
    }

    private static class Item extends BorderPane {

        //        private JCheckBox check;
//        private SwingApplicationUtils.Tracker stracker;
        private int pos;
        private AppFont _font;
        private AppColor _foreground;
        private AppColor _background;
        //        private ComponentBasedBorder border;
        private PangaeaNoteEditor editor;
        private CheckBox checkBox;
        private PangaeaNoteListEditorTypeComponent parent;

        public Item(PangaeaNoteListEditorTypeComponent parent) {
            super(parent.frame.app());
            this.parent = parent;
            editor = new PangaeaNoteEditor(parent.frame, true);
            Application app = parent.frame.app();
            Panel header = new HorizontalPane( app);
            checkBox = new CheckBox(null,null,app)
                    .with((CheckBox c) -> {
                        c.selected().set(parent.isSelectedIndex(editor.getNote().getName()));
                        c.selected().onChange(e -> parent.setSelectedName(editor.getNote().getName(), c.selected().get()));
                    });
            header.children().addAll(
                    checkBox
            );
//            stracker = new SwingApplicationUtils.Tracker(app);
//            ComponentBasedBorder.ComponentBasedBorderBuilder b = ComponentBasedBorder.of(this).withCheckbox();
//            border = b.install();

            ContextMenu bar = new ContextMenu(app);
            bar.children().add(new Button("addToObjectList", () -> parent.onAddObjectAt(pos), app));
            bar.children().add(new Button("duplicateInObjectList", () -> parent.onDuplicateObjectAt(pos), app));
            bar.children().addSeparator();
            bar.children().add(new Button("removeInObjectList", () -> parent.onRemoveObjectAt(pos), app));
            bar.children().addSeparator();
            bar.children().add(new Button("moveUpInObjectList", () -> parent.onMoveUpAt(pos), app));
            bar.children().add(new Button("moveDownInObjectList", () -> parent.onMoveDownAt(pos), app));
            bar.children().add(new Button("moveFirstInObjectList", () -> parent.onMoveFirstAt(pos), app));
            bar.children().add(new Button("moveLastInObjectList", () -> parent.onMoveLastAt(pos), app));
            bar.children().addSeparator();
            bar.children().add(new Button("NoteProperties", () -> parent.onEditAt(pos), app));
            this.contextMenu().set(bar);
        }

        public JMenuItem prepareButton(JMenuItem b) {
//            b.setHideActionText(true);
            return b;
        }

        public void setValue(PangaeaNoteExt value, int pos) {
            this.pos = pos;
            String s = value.getName();
            if (s == null || s.length() == 0) {
                s = "no-name";
            }
            editor.setNote(value);
            checkBox.text().set(Str.of((pos + 1) + " - " + value.getName()));
            checkBox.selected().set(parent.isSelectedIndex(value.getName()));
            if (_font == null) {
                _font = checkBox.textStyle().font().get();
            }
            if (_foreground == null) {
                _foreground = checkBox.textStyle().color().get();
            }
            if (_background == null) {
                _background = checkBox.backgroundColor().get();
            }
            //checkBox.textStyle().font().set(GuiHelper.deriveFont(_font, value.isTitleBold(), value.isTitleItalic(), value.isTitleUnderlined(), value.isTitleStriked()));
            checkBox.textStyle().font().set(
                    _font.derive(null,null,
                            value.isTitleBold()?FontWeight.BOLD :FontWeight.NORMAL,
                            value.isTitleItalic()?FontPosture.ITALIC :FontPosture.REGULAR
                    ));
            checkBox.textStyle().underline().set(value.isTitleUnderlined());
            checkBox.textStyle().strikethrough().set(value.isTitleStriked());
            Color b = Color.of(value.getTitleBackground(),app());
            checkBox.backgroundColor().set(b != null ? b : _background);
            b = Color.of(value.getTitleForeground(),app());
            checkBox.textStyle().color().set(b != null ? b : _foreground);
            String iconName = parent.frame.service().getNoteIcon(value.toNote(), value.getChildren().size() > 0, false);
            AppImage icon = parent.frame.app().iconSets().icon(iconName).get();
            //border.setIcon(icon == null ? null : ((SwingAppImage) icon).getIcon());
//            repaint();
        }

        public void setEditable(boolean editable) {
            editor.setEditable(editable);
            checkBox.enabled().set(editable);
//            for (Action action : stracker.getActions()) {
//                action.setEnabled(editable);
//            }
        }

        public PangaeaNoteExt getValue() {
            return editor.getNote();
        }

        public void onUninstall() {
            editor.uninstall();
        }

    }




    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        this.editable = b;
        componentList.editable().set(b);
    }

    @Override
    public boolean isEditable() {
        return editable;
    }


}
