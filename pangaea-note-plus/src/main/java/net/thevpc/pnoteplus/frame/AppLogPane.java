package net.thevpc.pnoteplus.frame;

import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;

public class AppLogPane extends TitledPane {
    AppFrame app;
    TextArea textArea;
    AppLogPane(AppFrame app) {
        this.app = app;
        textArea=new TextArea();
        textArea.setPrefRowCount(5);
        textArea.setEditable(false);
        this.setText("Log");
        this.setContent(textArea);
        setCollapsible(true);
        setExpanded(false);
    }

    public void println(String s) {
        textArea.appendText(s+"\n");
    }
}
