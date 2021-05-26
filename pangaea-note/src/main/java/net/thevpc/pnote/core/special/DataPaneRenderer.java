package net.thevpc.pnote.core.special;

import net.thevpc.echo.api.components.AppComponent;

public interface DataPaneRenderer<T> {
    void set(int index, T value, AppComponent component, DataPane<T> dataPane);

    T get(int index, AppComponent component, DataPane<T> dataPane);

    AppComponent create(DataPane<T> dataPane);

    void dispose(AppComponent a, DataPane<T> dataPane);
}
