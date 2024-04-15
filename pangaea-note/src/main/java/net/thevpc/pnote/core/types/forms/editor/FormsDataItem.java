package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.BorderPane;
import net.thevpc.echo.Label;
import net.thevpc.echo.Spacer;
import net.thevpc.echo.ToolBar;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.pnote.core.frame.PangaeaNoteFrame;

public class FormsDataItem extends BorderPane {

    private PangaeaNoteObjectComponent e;
    private ToolBar bar;
    private int pos;
    private FormsDataPane formsDataPane;

    public FormsDataItem(PangaeaNoteFrame win, FormsDataPane formsDataPane0) {
        super(win.app());
        this.formsDataPane = formsDataPane0;
        e = new PangaeaNoteObjectComponent(this, formsDataPane0.dynamicObjectTrackerAdapter, win)
                .with(v -> v.anchor().set(Anchor.CENTER));
        bar = new ToolBar(win.app()).with(v -> v.anchor().set(Anchor.TOP));

//            bar.children().add(new Button("addToObjectList", () -> formsDataPane.onAddObjectAt(pos), app()));
        bar.children().add(new Spacer(app()).expandX());
//            bar.children().add(new Button("duplicateInObjectList", () -> formsDataPane.onDuplicateObjectAt(pos), app()));
//            bar.children().addSeparator();
//            bar.children().add(new Button("moveUpInObjectList", () -> formsDataPane.onMoveUpAt(pos), app()));
//            bar.children().add(new Button("moveDownInObjectList", () -> formsDataPane.onMoveDownAt(pos), app()));
//            bar.children().add(new Button("moveFirstInObjectList", () -> formsDataPane.onMoveFirstAt(pos), app()));
//            bar.children().add(new Button("moveLastInObjectList", () -> formsDataPane.onMoveLastAt(pos), app()));
//            bar.children().addSeparator();
//            bar.children().add(new Button("removeInObjectList", () -> formsDataPane.onRemoveObjectAt(pos), app()));
        children().add(new Label(Str.of("Hello"), win.app()));
        children().add(bar);
        children().add(e);
    }

    public void onMoveUp() {
        formsDataPane.onMoveUpAt(pos);
    }

    public void onMoveDown() {
        formsDataPane.onMoveDownAt(pos);
    }

    public void onMoveFirst() {
        formsDataPane.onMoveFirstAt(pos);
    }

    public void onMoveLast() {
        formsDataPane.onMoveLastAt(pos);
    }

    public void onDuplicateObject() {
        formsDataPane.onDuplicateObjectAt(pos);
    }

    public void onRemoveObject() {
        formsDataPane.onRemoveObjectAt(pos);
    }

    public void setEditable(boolean b) {
        e.setEditable(b);
        bar.enabled().set(b);
    }

    public void setValue(PangaeaNoteObjectExt value, int pos, int size) {
        this.pos = pos;
        e.setObject(value);
    }

    public PangaeaNoteObjectExt getValue(int pos) {
        return new PangaeaNoteObjectExt(e.getObject(), formsDataPane.dynamicDocument.getDescriptor(), formsDataPane.dynamicDocument);
    }

    public void onUninstall() {
        e.uninstall();
//            stracker.unregisterAll();
    }
}
