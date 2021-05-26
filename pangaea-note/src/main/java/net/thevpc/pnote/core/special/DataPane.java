package net.thevpc.pnote.core.special;

import net.thevpc.common.props.PropertyEvent;
import net.thevpc.common.props.Props;
import net.thevpc.common.props.WritableList;
import net.thevpc.common.props.WritableValue;
import net.thevpc.echo.*;
import net.thevpc.echo.api.components.AppComponent;
import net.thevpc.echo.api.components.AppContainer;
import net.thevpc.echo.constraints.*;

import java.util.List;
import java.util.Objects;

public class DataPane<T> extends BorderPane {
    public static PaneLayout VERTICAL = new Vertical(1);
    public static PaneLayout HORIZONTAL = new Horizontal(1);
    public static PaneLayout TABS = new Tabs();
    private WritableValue<PaneLayout> paneLayout = Props.of("paneLayout").valueOf(PaneLayout.class, VERTICAL);
    private WritableList<T> values;
    private DataPaneRenderer<T> renderer;
    private WritableValue<AppContainer> container=Props.of("container").valueOf(AppContainer.class);

    public DataPane(Class componentType, DataPaneRenderer<T> renderer, Application app) {
        super(app);
        values = Props.of("value").listOf(componentType);
        this.renderer = renderer;
        values.onChange(this::valuesChanged);
        paneLayout().onChangeAndInit(this::updateContainer);
//        updateContainer();
    }


    public WritableValue<PaneLayout> paneLayout() {
        return paneLayout;
    }

    public WritableValue<AppContainer<AppComponent>> container() {
        return (WritableValue) container;
    }

    public void updateContainer() {
        AppContainer container = this.container.get();

        PaneLayout oldLayout = container == null ? null : (PaneLayout) container.userObjects().get(PaneLayout.class.getName());
        if (oldLayout == null) {
            oldLayout = VERTICAL;
        }
        PaneLayout newLayout = this.paneLayout.get();
        if (newLayout == null) {
            newLayout = VERTICAL;
        }
        if (container == null || !oldLayout.equals(newLayout)) {
            List<AppComponent> old = null;
            if (container != null) {
                old = container.children().toList();
                container.children().clear();
            }
            if (newLayout instanceof Vertical) {
                int columns = ((Vertical) newLayout).columns;
                GridPane p = new GridPane(columns, app());
                p.parentConstraints().addAll(
                        AllGrow.HORIZONTAL,
                        AllMargins.of(3),
                        AllFill.HORIZONTAL,
                        GrowContainer.HORIZONTAL
                );
                container = p;
            } else if (newLayout instanceof Horizontal) {
                int rows = ((Horizontal) newLayout).rows;
                if (rows == 1) {
                    HorizontalPane p = new HorizontalPane(app());
                    container = p;
                    p.parentConstraints().addAll(AllMargins.of(3),GrowContainer.HORIZONTAL);
                } else {
                    HorizontalPane p = new HorizontalPane(app());
                    p.parentConstraints().add(new ColumnCount(rows));
                    container = p;
                    p.parentConstraints().addAll(AllMargins.of(3),GrowContainer.NONE);
                }
            } else if (newLayout instanceof Tabs) {
                container = new TabPane(app());
            } else {
                throw new IllegalArgumentException("not supported yet " + newLayout);
            }
            children().clear();
            container.userObjects().put(PaneLayout.class.getName(),newLayout);
            container.anchor().set(Anchor.CENTER);
            children().add(container);
            this.container.set(container);
            if (old != null) {
                container.children().addCollection(old);
                //re-render
                for (int i = 0; i < old.size(); i++) {
                    //T value=(T) old.get(i).userObjects().get(DataPane.class.getName()+":value");
                    T value=values.get(i);
                    renderer.set(i,value,old.get(i),this);
                }
            }
        }
    }


    protected void valuesChanged(PropertyEvent event) {
        switch (event.eventType()) {
            case ADD: {
                AppComponent c = renderer.create(this);
                T newValue = event.newValue();
                c.userObjects().put(DataPane.class.getName()+":value",newValue);
                renderer.set((Integer) event.index(), newValue, c, this);
                this.container().get().children().add(c);
                break;
            }
            case REMOVE: {
                AppComponent c = this.container().get().children().removeAt((Integer) event.index());
                renderer.dispose(c, this);
                c.userObjects().put(DataPane.class.getName()+":value",null);
                break;
            }
            case UPDATE: {
                AppComponent c = this.container().get().children().get((Integer) event.index());
                T newValue = event.newValue();
                c.userObjects().put(DataPane.class.getName()+":value",newValue);
                renderer.set(event.index(), newValue, c, this);
                break;
            }
        }
    }


    public WritableList<T> values() {
        return values;
    }


    public interface PaneLayout {

    }

    public static class Vertical implements PaneLayout {
        private int columns;

        public Vertical(int columns) {
            this.columns = columns;
        }

        @Override
        public int hashCode() {
            return Objects.hash(columns);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertical vertical = (Vertical) o;
            return columns == vertical.columns;
        }
    }

    public static class Horizontal implements PaneLayout {
        private int rows;

        public Horizontal(int rows) {
            this.rows = rows;
        }

        @Override
        public int hashCode() {
            return Objects.hash(rows);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Horizontal that = (Horizontal) o;
            return rows == that.rows;
        }
    }

    public static class Tabs implements PaneLayout {
        public Tabs() {
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass().getName());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tabs that = (Tabs) o;
            return true;
        }
    }
}
