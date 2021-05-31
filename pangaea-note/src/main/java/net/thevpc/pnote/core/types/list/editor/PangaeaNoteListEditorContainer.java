package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Alert;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.nuts.NutsElement;
import net.thevpc.nuts.NutsElementType;
import net.thevpc.nuts.NutsObjectElement;
import net.thevpc.nuts.NutsObjectElementBuilder;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.core.special.DataPane;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListLayout;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.dialogs.EditNoteDialog;

import java.util.Set;
import net.thevpc.pnote.gui.PangaeaNoteApp;

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
                app().addChild(currentNote, (new PangaeaNote().setContentType(p.getContentType())), pos);
            } else {
                app().addChild(currentNote, (new PangaeaNote().setContentType(PangaeaNotePlainTextService.PLAIN.toString())), pos);
            }
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onDuplicateObjectAt(int pos) {
        if (currentNote != null) {
            PangaeaNote p = currentNote.getChildren().get(pos);
            app().addDuplicateSiblingNote(p, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public PangaeaNoteApp app() {
        return (PangaeaNoteApp) super.app();
    }

    public void onRemoveObjectAt(int pos) {
        if (currentNote != null) {
            String s = new Alert(frame)
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.warning"));
                        a.headerText().set(Str.i18n("Message.warning"));
                        a.headerIcon().set(Str.of("warning"));
                    })
                    .setContentText(Str.i18n("Message.askDeleteObject"))
                    .withYesNoButtons()
                    .showDialog(null);

            if ("yes".equals(s)) {
                app().removeChildNote(currentNote, pos);
                values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
                frame.treePane().fireNoteChanged(currentNote);
            }
        }
    }

    public void onMoveUpAt(int pos) {
        if (currentNote != null) {
            app().moveUp(currentNote, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onMoveDownAt(int pos) {
        if (currentNote != null) {
            app().moveDown(currentNote, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onMoveFirstAt(int pos) {
        if (currentNote != null) {
            app().moveFirst(currentNote, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onEditAt(int pos) {
        PangaeaNote cc = currentNote.getChildren().get(pos);
        PangaeaNote n = new EditNoteDialog(frame, cc).showDialog();
        if (n != null) {
            frame.treePane().fireNoteChanged(cc);
//            this.invalidate();
//            this.repaint();
        }
    }

    public void onMoveLastAt(int pos) {
        if (currentNote != null) {
            app().moveLast(currentNote, pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNote[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void setNote(PangaeaNote note, PangaeaNoteFrame win) {
        this.currentNote = note;
        PangaeaNoteListService s = (PangaeaNoteListService) win.app().getContentTypeService(PangaeaNoteListService.LIST);
        this.noteListModel = s.elementToContent(note.getContent());
        if (this.noteListModel == null) {
            this.noteListModel = new PangaeaNoteListModel();
        }
        values().setAll(note.getChildren().toArray(new PangaeaNote[0]));
        int index = 0;
        for (AppComponent child : container().get().children()) {
            PangaeaNoteListEditorItem item = (PangaeaNoteListEditorItem) child;
            item.setValue(item.getValue(), index);
            index++;
        }
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
        setEditable(!note.isReadOnly());
    }

    public boolean isSelectedIndex(PangaeaNote note) {
        if(note==null){
            return false;
        }
        String name=note.getName();
        if (this.currentNote != null && this.noteListModel != null) {
            NutsElement cd = note.getChildData();
            if(!(cd instanceof NutsObjectElement)){
                return false;
            }
            NutsObjectElement obj = cd.asObject();
            return obj.type()== NutsElementType.BOOLEAN &&  obj.getBoolean("selected");
        }
        return false;
    }

    public boolean setSelectedName(PangaeaNote note, boolean sel) {
        if(note==null){
            return false;
        }
        if (this.currentNote != null && this.noteListModel != null) {
//            Set<String> n = this.noteListModel.getSelectedNames();
            if(isSelectedIndex(note)!=sel) {
                NutsElement cd = note.getChildData();
                NutsObjectElementBuilder cdb;
                if(cd==null || cd.isNull()){
                    cdb=app().element().forObject();
                }else if(!cd.isObject()){
                    cdb=app().element().forObject();
                    cdb.set("value",cd);
                }else{
                    cdb=app().element().forObject().add(cd.asObject());
                }
                cdb.set("selected",sel);
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
