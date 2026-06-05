package com.terramail.view;

import com.terramail.controller.FolderTreeController;
import com.terramail.model.EmailFolder;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

/**
 * View component for the folder tree panel.
 */
public class FolderTreePanel extends Pane {

    @FXML
    private TreeView<?> folderTreeView;

    @FXML
    private FolderTreeController folderTreeController;

    private final SimpleObjectProperty<EmailFolder> folderSelected = new SimpleObjectProperty<>();

    /**
     * Initializes the view component.
     */
    public void initialize() {
        // FXML will handle the initialization
    }

    /**
     * Refreshes the folder tree view.
     */
    public void refresh() {
        if (folderTreeController != null) {
            folderTreeController.refresh();
        }
    }

    /**
     * Loads a folder into the panel.
     */
    public void loadFolder(EmailFolder folder) {
        if (folderTreeController != null) {
            // The controller handles loading folders
        }
    }

    /**
     * Gets the folder selected property.
     */
    public SimpleObjectProperty<EmailFolder> folderSelectedProperty() {
        return folderSelected;
    }

    /**
     * Gets the currently selected folder.
     */
    public EmailFolder getSelectedFolder() {
        return folderSelected.get();
    }
}
