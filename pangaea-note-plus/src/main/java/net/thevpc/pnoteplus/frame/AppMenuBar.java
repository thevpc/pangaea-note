package net.thevpc.pnoteplus.frame;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class AppMenuBar extends MenuBar {
    private AppFrame frame;

    public AppMenuBar(AppFrame frame) {
        this.frame = frame;

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(
                new MenuItem("New Note"),
                new MenuItem("Exit")
        );
        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(
                new MenuItem("Undo"),
                new MenuItem("Redo")
        );
        getMenus().addAll(fileMenu, editMenu);
    }
}
