package net.thevpc.pnote.core.special;

import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.Props;
import net.thevpc.common.props.WritableList;
import net.thevpc.echo.Application;
import net.thevpc.echo.Panel;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.constraints.Layout;

public class DataPane<T> extends Panel {
    private WritableList<T> values;
    private DataPaneRenderer<T> renderer;

    public DataPane(Class componentType, DataPaneRenderer<T> renderer, Application app) {
        super(Layout.VERTICAL, app);
        values = Props.of("value").listOf(componentType);
        this.renderer = renderer;
        values.onChange(event -> valuesChanged(event));
    }

    protected void valuesChanged(PropertyEvent event) {
        switch (event.eventType()) {
            case ADD: {
                AppComponent c = renderer.create();
                renderer.set((Integer) event.index(), event.newValue(), c);
                children().add(c);
                break;
            }
            case REMOVE: {
                AppComponent c = children().removeAt((Integer) event.index());
                renderer.dispose(c);
                break;
            }
            case UPDATE: {
                AppComponent c = children().get((Integer) event.index());
                renderer.set(event.index(), event.newValue(), c);
                break;
            }
        }
    }


    public WritableList<T> values() {
        return values;
    }

}
