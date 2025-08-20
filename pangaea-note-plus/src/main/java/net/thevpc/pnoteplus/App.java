package net.thevpc.pnoteplus;
import javafx.application.Application;
import javafx.stage.Stage;
import net.thevpc.pnoteplus.frame.AppFrame;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppFrame b=new AppFrame(primaryStage,this);
        b.showStage();
        b.logPane().println("Application started...");

    }


    public static void main(String[] args) {
        launch(args);
    }
}
