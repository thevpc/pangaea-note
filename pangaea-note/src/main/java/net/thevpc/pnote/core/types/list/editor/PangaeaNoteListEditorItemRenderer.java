package net.thevpc.pnote.core.types.list.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.pnote.api.model.PangaeaNoteExt;
import net.thevpc.pnote.core.special.DataPane;
import net.thevpc.pnote.core.special.DataPaneRenderer;
import net.thevpc.pnote.gui.PangaeaNoteApp;

class PangaeaNoteListEditorItemRenderer implements DataPaneRenderer<PangaeaNoteExt> {

    public PangaeaNoteListEditorItemRenderer() {
    }

    @Override
    public void set(int index, PangaeaNoteExt value, AppComponent component, DataPane<PangaeaNoteExt> dataPane) {
        PangaeaNoteListEditorItem item = (PangaeaNoteListEditorItem) component;
        item.title().set(Str.of(value.getName()));
        String iconName = ((PangaeaNoteApp)component.app()).service().getNoteIcon(value.toNote());

        item.smallIcon().set(Str.of(iconName));
        item.setValue(value, index);
    }

    @Override
    public PangaeaNoteExt get(int index, AppComponent component, DataPane<PangaeaNoteExt> dataPane) {
        return ((PangaeaNoteListEditorItem) component).getValue();
    }

    @Override
    public AppComponent create(DataPane<PangaeaNoteExt> dataPane) {
        return new PangaeaNoteListEditorItem((PangaeaNoteListEditorContainer) dataPane);
    }

    @Override
    public void dispose(AppComponent component, DataPane<PangaeaNoteExt> dataPane) {
        ((PangaeaNoteListEditorItem) component).onUninstall();
    }
}
