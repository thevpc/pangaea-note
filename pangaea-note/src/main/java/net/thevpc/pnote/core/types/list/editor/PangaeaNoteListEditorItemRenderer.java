package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.api.model.PangaeaNote;
import net.thevpc.echo.DataPane;
import net.thevpc.echo.DataPaneRenderer;
import net.thevpc.pnote.core.frame.PangaeaNoteApp;

class PangaeaNoteListEditorItemRenderer implements DataPaneRenderer<PangaeaNote> {

    public PangaeaNoteListEditorItemRenderer() {
    }

    @Override
    public void set(int index, PangaeaNote value, AppComponent component, DataPane<PangaeaNote> dataPane) {
        PangaeaNoteListEditorItem item = (PangaeaNoteListEditorItem) component;
        item.title().set(Str.of(value.getName()));
        String iconName = ((PangaeaNoteApp)component.app()).getNoteIcon(value);

        item.icon().set(Str.of(iconName));
        item.setValue(value, index);
    }

    @Override
    public PangaeaNote get(int index, AppComponent component, DataPane<PangaeaNote> dataPane) {
        return ((PangaeaNoteListEditorItem) component).getValue();
    }

    @Override
    public AppComponent create(DataPane<PangaeaNote> dataPane) {
        return new PangaeaNoteListEditorItem((PangaeaNoteListEditorContainer) dataPane);
    }

    @Override
    public void dispose(AppComponent component, DataPane<PangaeaNote> dataPane) {
        ((PangaeaNoteListEditorItem) component).onUninstall();
    }
}
