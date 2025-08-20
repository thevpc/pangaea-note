package net.thevpc.pnoteplus.frame;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

public class AppTreeView extends TreeView<String> {
    private AppFrame frame;
    public AppTreeView(AppFrame frame) {
        this.frame=frame;
        // --- Left Tree ---
        TreeItem<String> rootItem = new TreeItem<>("Notes");
        rootItem.setExpanded(true);
        for (int i = 1; i <= 500; i++) {
            TreeItem<String> note = new TreeItem<>("Note " + i);
            rootItem.getChildren().add(note);
        }

        getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                frame.setMainContent(new Label("Editing: " + newSel.getValue()));
                frame.log("Selected: " + newSel.getValue() + "\n");
            }
        });
        setCellFactory(tv -> {
            TreeCell<String> cell = new TreeCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setGraphic(null);
                }
            };

            cell.setOnMouseClicked(e -> {
                // Dynamically create context menu
                if (e.getButton() == MouseButton.SECONDARY && !cell.isEmpty()) {
                    ContextMenu dynamicMenu = new ContextMenu();

                    TreeItem<String> selectedItem = cell.getTreeItem();

                    if (selectedItem == getRoot()) {
                        MenuItem addNote = new MenuItem("Add Note");
                        dynamicMenu.getItems().add(addNote);
                        addNote.setOnAction(ae -> System.out.println("Adding note to root"));
                    } else {
                        MenuItem editNote = new MenuItem("Edit Note");
                        MenuItem deleteNote = new MenuItem("Delete Note");
                        dynamicMenu.getItems().addAll(editNote, deleteNote);

                        editNote.setOnAction(ae -> System.out.println("Editing: " + selectedItem.getValue()));
                        deleteNote.setOnAction(ae -> selectedItem.getParent().getChildren().remove(selectedItem));
                    }

                    dynamicMenu.show(cell, e.getScreenX(), e.getScreenY());
                }
            });

            return cell;
        });
    }
}
