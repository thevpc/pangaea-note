package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Alert;
import net.thevpc.echo.api.AppAlertResult;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.AllAnchors;
import net.thevpc.echo.constraints.AllGrow;
import net.thevpc.echo.constraints.AllMargins;
import net.thevpc.nuts.NutsElement;
import net.thevpc.nuts.NutsElementType;
import net.thevpc.nuts.NutsObjectElement;
import net.thevpc.nuts.NutsObjectElementBuilder;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;
import net.thevpc.echo.DataPane;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListLayout;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;

public class PangaeaNoteListEditorContainer extends DataPane<PangaeaNote> {

    PangaeaNoteFrame frame;
    private PangaeaNote currentNote;
    private PangaeaNoteListModel noteListModel;

    public PangaeaNoteListEditorContainer(PangaeaNoteFrame frame) {
        super(PangaeaNote.class,
                new PangaeaNoteListEditorItemRenderer(),
                frame.app());
        this.frame = frame;
        container().get().parentConstraints().addAll(AllMargins.of(5), AllAnchors.LEFT, AllGrow.HORIZONTAL);
    }

    public boolean isSelectableItems() {
        if (this.currentNote != null && this.noteListModel != null) {
            return this.noteListModel.isSelectableItems();
        }
        return false;
    }

    public boolean isShowNumbers() {
        if (this.currentNote != null && this.noteListModel != null) {
            return this.noteListModel.isShowNumbers();
        }
        return false;
    }

    public boolean isStrikeSelected() {
        if (this.currentNote != null && this.noteListModel != null) {
            return this.noteListModel.isStrikeSelected();
        }
        return false;
    }

    public void onAddObjectAt(int pos) {
        if (currentNote != null) {
            if (currentNote.getChildren().size() > 0) {
                PangaeaNote p = currentNote.getChildren().get(pos);
                frame.treePane().addNoteChild(currentNote, (new PangaeaNote().setContentType(p.getContentType())), pos);
            } else {
                frame.treePane().addNoteChild(currentNote, (new PangaeaNote().setContentType(PangaeaNotePlainTextService.PLAIN.toString())), pos);
            }
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            frame.treePane().updateNote(currentNote);
        }
    }

    public void onDuplicateObjectAt(int pos) {
        if (currentNote != null) {
            PangaeaNote p = currentNote.getChildren().get(pos);
            frame.treePane().addDuplicateSiblingNote(p, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
        }
    }

    public void renameNote(int pos) {
        if (currentNote != null) {
            PangaeaNote p = currentNote.getChildren().get(pos);
            frame.renameNote(p);
            //values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
        }
    }

    public void strikeThroughNote(int pos) {
        if (currentNote != null) {
            PangaeaNote p = currentNote.getChildren().get(pos);
            frame.strikeThroughNote(p);
            onUpdateAt(pos);
        }
    }

    public void boldNote(int pos) {
        if (currentNote != null) {
            PangaeaNote p = currentNote.getChildren().get(pos);
            frame.boldNote(p);
            onUpdateAt(pos);
        }
    }

    public PangaeaNoteApp app() {
        return (PangaeaNoteApp) super.app();
    }

    public void onRemoveObjectAt(int pos) {
        if (currentNote != null) {
            AppAlertResult s = new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.warning"));
                        a.headerText().set(Str.i18n("Message.warning"));
                        a.headerIcon().set(Str.of("warning"));
                    })
                    .setContentText(Str.i18n("Message.askDeleteObject"))
                    .withYesNoButtons()
                    .showDialog();

            if (s.isYesButton()) {
                frame.treePane().removeNodeChild(currentNote, pos);
                values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            }
        }
    }

    public void onMoveUpAt(int pos) {
        if (currentNote != null) {
            frame.treePane().moveUp(currentNote, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
        }
    }

    public void onMoveDownAt(int pos) {
        if (currentNote != null) {
            frame.treePane().moveDown(currentNote, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
        }
    }

    public void onMoveFirstAt(int pos) {
        if (currentNote != null) {
            frame.treePane().moveFirst(currentNote, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
        }
    }

    public void onUpdateAt(int pos) {
        PangaeaNoteListEditorItem item = (PangaeaNoteListEditorItem) container().get().children().get(pos);
        item.setValue(item.getValue(), pos);
    }

    public void onEditAt(int pos) {
        PangaeaNote cc = currentNote.getChildren().get(pos);
        if (frame.editNote(cc)) {
            onUpdateAt(pos);
        }
    }

    public void onMoveLastAt(int pos) {
        if (currentNote != null) {
            frame.treePane().moveLast(currentNote, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            frame.treePane().updateNote(currentNote);
        }
    }

    public void setNote(PangaeaNote note, PangaeaNoteFrame win) {
        this.currentNote = note;
        PangaeaNoteListService s = (PangaeaNoteListService) win.app().getContentTypeService(PangaeaNoteListService.LIST);
        this.noteListModel = s.elementToContent(note.getContent());
        if (this.noteListModel == null) {
            this.noteListModel = new PangaeaNoteListModel();
        }
        values().clear();
//        values().setAll(note.getChildren().toArray(new PangaeaNote[0]));
//        int index = 0;
//        for (AppComponent child : renderedComponents()) {
//            PangaeaNoteListEditorItem item = (PangaeaNoteListEditorItem) child;
//            item.setValue(item.getValue(), index);
//            index++;
//        }
        PangaeaNoteListLayout layout = noteListModel.getLayout();
        if (layout == null) {
            layout = PangaeaNoteListLayout.VERTICAL;
            noteListModel.setLayout(layout);
        }
        switch (layout) {
            case VERTICAL: {
                int cr = noteListModel.getColsRows();
                if (cr <= 1) {
                    paneLayout().set(VERTICAL);
                } else {
                    if (cr >= 100) {
                        cr = 100;
                    }
                    paneLayout().set(new Vertical(cr));
                }
                container().get().parentConstraints().addAll(AllMargins.of(5), AllAnchors.LEFT, AllGrow.HORIZONTAL);
                break;
            }

            case HORIZONTAL: {
                int cr = noteListModel.getColsRows();
                if (cr <= 1) {
                    paneLayout().set(HORIZONTAL);
                } else {
                    if (cr >= 100) {
                        cr = 100;
                    }
                    paneLayout().set(new Horizontal(cr));
                }
                container().get().parentConstraints().addAll(AllMargins.of(5), AllAnchors.LEFT, AllGrow.HORIZONTAL);
                break;
            }
            case TAB: {
                //componentList.setListLayout(new JComponentList.Tab());
                //TODO Fix me later!!
                paneLayout().set(TABS);
                container().get().parentConstraints().addAll(AllMargins.of(5), AllAnchors.LEFT, AllGrow.HORIZONTAL);
                break;
            }
            default: {
                paneLayout().set(VERTICAL);
                container().get().parentConstraints().addAll(AllMargins.of(5), AllAnchors.LEFT, AllGrow.HORIZONTAL);
            }
        }
        values().setAll(note.getChildren().toArray(new PangaeaNote[0]));
        int index = 0;
        for (AppComponent child : renderedComponents()) {
            PangaeaNoteListEditorItem item = (PangaeaNoteListEditorItem) child;
            item.setValue(item.getValue(), index);
            index++;
        }
        setEditable(!note.isReadOnly());
    }

    public boolean isSelectedIndex(PangaeaNote note) {
        if (note == null) {
            return false;
        }
        String name = note.getName();
        if (this.currentNote != null && this.noteListModel != null) {
            NutsElement cd = note.getChildData();
            if (!(cd instanceof NutsObjectElement)) {
                return false;
            }
            NutsObjectElement obj = cd.asObject();
            return obj.type() == NutsElementType.BOOLEAN && obj.getBoolean("selected");
        }
        return false;
    }

    public boolean setSelectedName(PangaeaNote note, boolean sel) {
        if (note == null) {
            return false;
        }
        if (this.currentNote != null && this.noteListModel != null) {
//            Set<String> n = this.noteListModel.getSelectedNames();
            if (isSelectedIndex(note) != sel) {
                NutsElement cd = note.getChildData();
                NutsObjectElementBuilder cdb;
                if (cd == null || cd.isNull()) {
                    cdb = app().elem().forObject();
                } else if (!cd.isObject()) {
                    cdb = app().elem().forObject();
                    cdb.set("value", cd);
                } else {
                    cdb = app().elem().forObject().add(cd.asObject());
                }
                cdb.set("selected", sel);
                note.setChildData(cdb.build());
                app().fireNoteChanged(note);
                frame.onDocumentChanged();
                PangaeaNoteListService s = (PangaeaNoteListService) frame.app().getContentTypeService(PangaeaNoteListService.LIST);
                this.currentNote.setContent(s.contentToElement(this.noteListModel));
            }
        }
        return false;
    }

    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
//            this.editable = b;
        editable().set(b);
    }

}
