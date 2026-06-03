package com.terramail.controller;

import com.terramail.view.FolderTreePanel;
import com.terramail.view.MessageTablePanel;
import com.terramail.view.MessageViewerPanel;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;

/**
 * Controller for the main base view layout.
 */
public class BaseViewController {

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private FolderTreePanel folderTreePanel;

    @FXML
    private MessageTablePanel messageTablePanel;

    @FXML
    private MessageViewerPanel messageViewerPanel;

    @FXML
    public void initialize() {
        // Initialize the view components
        if (folderTreePanel != null) {
            folderTreePanel.initialize();
        }

        if (messageTablePanel != null) {
            messageTablePanel.initialize();
        }

        if (messageViewerPanel != null) {
            messageViewerPanel.initialize();
        }

        // Link the components together
        linkComponents();
    }

    /**
     * Links the components together so they can communicate.
     */
    private void linkComponents() {
        // When a folder is selected in the tree, load messages in the table
        if (folderTreePanel != null && messageTablePanel != null) {
            folderTreePanel.folderSelectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    messageTablePanel.loadFolder(newVal);
                }
            });
        }

        // When a message is selected in the table, display it in the viewer
        if (messageTablePanel != null && messageViewerPanel != null) {
            messageTablePanel.messageSelectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    messageViewerPanel.displayMessage(newVal);
                }
            });
        }
    }

    /**
     * Gets the folder tree panel.
     */
    public FolderTreePanel getFolderTreePanel() {
        return folderTreePanel;
    }

    /**
     * Gets the message table panel.
     */
    public MessageTablePanel getMessageTablePanel() {
        return messageTablePanel;
    }

    /**
     * Gets the message viewer panel.
     */
    public MessageViewerPanel getMessageViewerPanel() {
        return messageViewerPanel;
    }

    /**
     * Refreshes all panels.
     */
    public void refresh() {
        if (folderTreePanel != null) {
            folderTreePanel.refresh();
        }
    }
}
