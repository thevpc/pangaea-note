package net.thevpc.pnoteplus.frame;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.thevpc.pnoteplus.App;

public class AppFrame {
    private App app;
    private AppMenuBar menuBar;
    private AppTreeView treeView;
    private Stage stage;
    private AppLogPane logPane;
    private AppCenterPane contentPane;

    public AppFrame(Stage stage, App app) {
        this.app = app;
        this.stage = stage;
        menuBar = new AppMenuBar(this);

        treeView = new AppTreeView(this);
        treeView.setPrefWidth(200);
        // --- Central Content Placeholder ---
        contentPane = new AppCenterPane(this);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(treeView, contentPane);
        splitPane.setDividerPositions(0.25); // initial 25% width

// --- Toggle Button to Collapse/Expand Tree ---
        Button toggleButton = new Button("☰"); // hamburger icon
        toggleButton.setOnAction(e -> {
            if (splitPane.getDividerPositions()[0] > 0) {
                splitPane.setDividerPositions(0.0); // collapse left
            } else {
                splitPane.setDividerPositions(0.25); // restore left width
            }
        });

        // --- Collapsible Bottom Log ---

        logPane = new AppLogPane(this);


        // --- Layout ---
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(menuBar);
        mainPane.setCenter(splitPane);
        mainPane.setBottom(logPane);
        BorderPane.setMargin(treeView, new Insets(5));
        BorderPane.setMargin(contentPane, new Insets(5));
        BorderPane.setMargin(logPane, new Insets(5));
        Scene scene = new Scene(mainPane, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Pangaea Note Plus");

        // Apply a style class to the root pane
        mainPane.getStyleClass().add("zoomable");
        final DoubleProperty fontSize = new SimpleDoubleProperty(12);

// Bind the font size to a CSS variable
        fontSize.addListener((obs, oldVal, newVal) -> {
            mainPane.setStyle("-fx-font-size: " + newVal + "px;");
        });
        scene.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isControlDown()) {
                double deltaY = e.getDeltaY();
                System.out.println(deltaY);
                double delta = Math.signum(deltaY);
                if(delta!=0) {
                    double newSize = fontSize.get() + delta;
                    if (newSize >= 8 && newSize <= 48) { // limits
                        fontSize.set(newSize);
                    }
                }
                e.consume();
            }
        });
    }

    public AppLogPane logPane() {
        return logPane;
    }

    public void log(String node){
        logPane().println(node);
    }

    public void setMainContent(Node node){
        contentPane.setCenter(node);
    }

    public void showStage(){
        stage.show();
    }
}
