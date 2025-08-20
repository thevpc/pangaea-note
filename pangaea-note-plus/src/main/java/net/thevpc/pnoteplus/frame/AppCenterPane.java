package net.thevpc.pnoteplus.frame;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class AppCenterPane extends BorderPane {
    AppFrame frame;

    public AppCenterPane(AppFrame frame) {
        this.frame = frame;
        Label placeholder = new Label("Select a note to edit...");
        setCenter(placeholder);
    }
}
