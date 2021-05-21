package net.thevpc.pnote.core.special;

import net.thevpc.echo.api.components.AppComponent;

public interface DataPaneRenderer<T> {
    void set(int index, T value, AppComponent component);

    T get(int index, AppComponent component);

    AppComponent create();

    void dispose(AppComponent a);
}
