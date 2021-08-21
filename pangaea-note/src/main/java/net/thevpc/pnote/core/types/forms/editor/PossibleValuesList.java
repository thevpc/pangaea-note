package net.thevpc.pnote.core.types.forms.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.constraints.AllMargins;
import net.thevpc.echo.constraints.Anchor;
import net.thevpc.echo.constraints.ContainerGrow;

import java.util.*;

public class PossibleValuesList extends BorderPane {
    private TextArea area;
    private ScrollPane scroll;
    private Label label;

    public PossibleValuesList(Application app) {
        super(app);
        title().set(Str.i18n("Message.fieldPossibleValues.title"));
        parentConstraints().addAll(AllMargins.of(5), ContainerGrow.ALL);
        children().add(
                label = new Label(Str.i18n("Message.fieldPossibleValues.header"), app)
                        .with(
                                c -> {
                                    c.anchor().set(Anchor.TOP);
                                    c.prefSize().set(new Dimension(100,60));
                                }
                        )
        );
        children().add(
                scroll = new ScrollPane(area = new TextArea(app)).with(
                        p -> {
                            p.anchor().set(Anchor.CENTER);
                            p.title().set(Str.i18n("Message.fieldPossibleValues"));
                        }
                )
        );
        enabled().onChange(this::updateEnabled);
        editable().onChange(this::updateEditable);
        updateEnabled();
        updateEditable();
    }

    private void updateEnabled() {
        boolean en = enabled().get();
        area.editable().set(en);
        scroll.visible().set(en);
        label.text().set(
                en ? Str.i18n("Message.fieldPossibleValues.select.header") :
                        Str.i18n("Message.fieldPossibleValues.nonApplicable.header")

        );
    }

    private void updateEditable() {
        boolean en = editable().get();
        area.editable().set(en);
    }

    private List<String> normalize(List<String> values) {
        Set<String> s = new LinkedHashSet<>();
        if (values != null) {
            for (String value : values) {
                if (value == null) {
                    value = "";
                }
                value = value.trim();
                s.add(value);
            }
        }
        return new ArrayList<>(s);
    }

    public List<String> getValues() {
        return normalize(Arrays.asList(area.text().get().value().split("\n")));
    }

    public void setValues(List<String> values) {
        area.text().set(
                Str.of(String.join("\n", normalize(values)))
        );
    }
}
