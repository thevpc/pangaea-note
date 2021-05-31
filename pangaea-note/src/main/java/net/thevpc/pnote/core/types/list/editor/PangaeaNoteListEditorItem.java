package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppColor;
import net.thevpc.echo.api.AppFont;
import net.thevpc.echo.api.AppImage;
import net.thevpc.echo.api.components.AppEventType;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.pnote.core.special.DataPane;
import net.thevpc.pnote.gui.PangaeaNoteApp;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditor;
import net.thevpc.pnote.util.OtherUtils;

import net.thevpc.pnote.api.model.PangaeaNote;

class PangaeaNoteListEditorItem extends BorderPane {

    private int pos;
    private AppFont _font;
    private AppColor _foreground;
    private AppColor _background;
    //        private ComponentBasedBorder border;
    private PangaeaNoteEditor editor;
    private CheckBox checkBox;
    private Label label;
    private ImageView icon;
    private PangaeaNoteListEditorContainer parent;

    public PangaeaNoteListEditorItem(PangaeaNoteListEditorContainer parent) {
        super(parent.frame.app());
        this.parent = parent;
        editor = new PangaeaNoteEditor(parent.frame, true);
        Application app = parent.frame.app();
        ToolBar header = new ToolBar(app);
        icon=new ImageView(app);
        checkBox = new CheckBox(null, null, app)
                .with((CheckBox c) -> {
                    c.selected().set(parent.isSelectedIndex(editor.getNote()));
                    c.selected().onChange(e -> {
                        if (editor.getNote() != null) {
                            parent.setSelectedName(editor.getNote(), c.selected().get());
                        }
                    });
                });
        label=new Label(app);
        label.events().add((e)->{
            if(e.isPrimaryMouseButton() && e.getClickCount()==1) {
                checkBox.selected().set(!checkBox.selected().get());
            }
        }, AppEventType.MOUSE_CLICKED);
        header.children().addAll(
                new HorizontalPane(app) // add to panel to render text (toolbar does not)
                    .with((HorizontalPane e)-> {
                        e.children().addAll(
                                icon.with(x->x.anchor().set(Anchor.LEFT)),
                                checkBox.with(x->x.anchor().set(Anchor.LEFT)),
                                label.with(x->x.anchor().set(Anchor.LEFT))
                        );
                    })
                ,
                new Spacer(app).expandX()
        );
        
//        header.children().add(new Button("NoteProperties", () -> parent.onEditAt(pos), app));
//        header.children().addSeparator();
//        header.children().add(new Button("duplicateInObjectList", () -> parent.onDuplicateObjectAt(pos), app));
//        header.children().addSeparator();
//        header.children().add(new Button("moveUpInObjectList", () -> parent.onMoveUpAt(pos), app));
//        header.children().add(new Button("moveDownInObjectList", () -> parent.onMoveDownAt(pos), app));
//        header.children().add(new Button("moveFirstInObjectList", () -> parent.onMoveFirstAt(pos), app));
//        header.children().add(new Button("moveLastInObjectList", () -> parent.onMoveLastAt(pos), app));
//        header.children().addSeparator();
//        header.children().add(new Button("removeInObjectList", () -> parent.onRemoveObjectAt(pos), app));

        ContextMenu _contextMenu = new ContextMenu(app);
        _contextMenu.children().add(new Button("NoteProperties", () -> parent.onEditAt(pos), app));
        _contextMenu.children().addSeparator();
        _contextMenu.children().add(new Button("duplicateInObjectList", () -> parent.onDuplicateObjectAt(pos), app));
        _contextMenu.children().addSeparator();
        _contextMenu.children().add(new Button("moveUpInObjectList", () -> parent.onMoveUpAt(pos), app));
        _contextMenu.children().add(new Button("moveDownInObjectList", () -> parent.onMoveDownAt(pos), app));
        _contextMenu.children().add(new Button("moveFirstInObjectList", () -> parent.onMoveFirstAt(pos), app));
        _contextMenu.children().add(new Button("moveLastInObjectList", () -> parent.onMoveLastAt(pos), app));
        _contextMenu.children().addSeparator();
        _contextMenu.children().add(new Button("removeNote", () -> parent.onRemoveObjectAt(pos), app));

        this.contextMenu().set(_contextMenu);
        icon.contextMenu().set(_contextMenu);
        checkBox.contextMenu().set(_contextMenu);
        label.contextMenu().set(_contextMenu);
        children().addAll(
                header
                        .with(p -> p.anchor().set(Anchor.TOP)),
                editor
                        .with(p -> p.anchor().set(Anchor.CENTER))
        );
    }
    boolean isSelectableItem(){
        return parent.isSelectableItems();
    }

    public void setValue(PangaeaNote value, int pos) {
        this.pos = pos;
        String s = value.getName();
        if (s == null || s.length() == 0) {
            s = "no-name";
        }

        String iconName = ((PangaeaNoteApp)app()).getNoteIcon(value);
        AppImage image = app().iconSets().icon(iconName,this);
        icon.image().set(image);

        checkBox.text().set(Str.empty());
        checkBox.selected().set(parent.isSelectedIndex(value));
        OtherUtils.applyTitle(value,label,false);
        String prefix;
        if(parent.isShowNumbers()){
            prefix = (pos + 1) + " - ";
        }else{
            prefix ="";
        }
        label.text().set(Str.of(prefix + s));
        label.icon().set((Str)null);
        label.icon().set((AppImage) null);
        boolean inTab = parent.paneLayout().get() instanceof DataPane.Tabs;
        label.visible().set(!inTab);
        icon.visible().set(!inTab);
        checkBox.visible().set(isSelectableItem());
        editor.setNote(value);
    }

    public void setEditable(boolean editable) {
        editor.setEditable(editable);
        checkBox.enabled().set(editable);
//            for (Action action : stracker.getActions()) {
//                action.setEnabled(editable);
//            }
    }

    public PangaeaNote getValue() {
        return editor.getNote();
    }

    public void onUninstall() {
        editor.uninstall();
    }

    public int getPos() {
        return pos;
    }
}
