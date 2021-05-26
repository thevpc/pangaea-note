package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.Alert;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.*;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.core.special.DataPane;
import net.thevpc.pnote.core.types.list.PangaeaNoteListService;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListLayout;
import net.thevpc.pnote.core.types.list.model.PangaeaNoteListModel;
import net.thevpc.pnote.core.types.plain.PangaeaNotePlainTextService;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.dialogs.EditNoteDialog;

import java.util.Set;

public class PangaeaNoteListEditorContainer extends DataPane<PangaeaNoteExt> {

    PangaeaNoteFrame frame;
    private PangaeaNoteExt currentNote;
    private PangaeaNoteListModel noteListModel;

    public PangaeaNoteListEditorContainer(PangaeaNoteFrame frame) {
        super(PangaeaNoteExt.class,
                new PangaeaNoteListEditorItemRenderer(),
                frame.app());
        this.frame = frame;
        container().get().parentConstraints().addAll(AllMargins.of(5),AllAnchors.LEFT,AllGrow.HORIZONTAL);
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
            values().setAll(currentNote.getChildren().toArray(new PangaeaNoteExt[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onDuplicateObjectAt(int pos) {
        if (currentNote != null) {
            PangaeaNoteExt p = currentNote.getChildren().get(pos);
            currentNote.addChild(pos, p.duplicate());
            values().setAll(currentNote.getChildren().toArray(new PangaeaNoteExt[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onRemoveObjectAt(int pos) {
        if (currentNote != null) {
            String s = new Alert(frame.app())
                    .with((Alert a) -> {
                        a.title().set(Str.i18n("Message.warning"));
                        a.headerText().set(Str.i18n("Message.warning"));
                        a.headerIcon().set(Str.of("warning"));
                    })
                    .setContentText(Str.i18n("Message.askDeleteObject"))
                    .withYesNoButtons()
                    .showDialog(null);

            if ("yes".equals(s)) {
                currentNote.removeChild(pos);
                values().setAll(currentNote.getChildren().toArray(new PangaeaNoteExt[0]));
                frame.treePane().fireNoteChanged(currentNote);
            }
        }
    }

    public void onMoveUpAt(int pos) {
        if (currentNote != null) {
            currentNote.moveUp(pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNoteExt[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onMoveDownAt(int pos) {
        if (currentNote != null) {
            currentNote.moveDown(pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNoteExt[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void onMoveFirstAt(int pos) {
        if (currentNote != null) {
            currentNote.moveFirst(pos);
            values().setAll(currentNote.getChildren().toArray(new PangaeaNoteExt[0]));
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
            values().setAll(currentNote.getChildren().toArray(new PangaeaNoteExt[0]));
            frame.treePane().fireNoteChanged(currentNote);
        }
    }

    public void setNote(PangaeaNoteExt note, PangaeaNoteFrame win) {
        this.currentNote = note;
        PangaeaNoteListService s = (PangaeaNoteListService) win.service().getContentTypeService(PangaeaNoteListService.LIST);
        this.noteListModel = s.elementToContent(note.getContent());
        if (this.noteListModel == null) {
            this.noteListModel = new PangaeaNoteListModel();
        }
        values().setAll(note.getChildren().toArray(new PangaeaNoteExt[0]));
        int index=0;
        for (AppComponent child : container().get().children()) {
            PangaeaNoteListEditorItem item=(PangaeaNoteListEditorItem) child;
            item.setValue(item.getValue(),index);
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
                container().get().parentConstraints().addAll(AllMargins.of(5),AllAnchors.LEFT,AllGrow.HORIZONTAL);
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
                container().get().parentConstraints().addAll(AllMargins.of(5),AllAnchors.LEFT,AllGrow.HORIZONTAL);
                break;
            }
            case TAB: {
                //componentList.setListLayout(new JComponentList.Tab());
                //TODO Fix me later!!
                paneLayout().set(TABS);
                container().get().parentConstraints().addAll(AllMargins.of(5),AllAnchors.LEFT,AllGrow.HORIZONTAL);
                break;
            }
            default: {
                paneLayout().set(VERTICAL);
                container().get().parentConstraints().addAll(AllMargins.of(5),AllAnchors.LEFT,AllGrow.HORIZONTAL);
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
    }

    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
//            this.editable = b;
        editable().set(b);
    }

}
