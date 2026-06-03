package com.terramail.view;

import com.terramail.controller.MessageViewerController;
import com.terramail.model.EmailMessage;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * View component for the message viewer panel.
 */
public class MessageViewerPanel extends Pane {

    @FXML
    private MessageViewerController messageViewerController;

    /**
     * Initializes the view component.
     */
    public void initialize() {
        // FXML will handle the initialization
    }

    /**
     * Displays a message in the viewer.
     */
    public void displayMessage(EmailMessage message) {
        if (messageViewerController != null) {
            messageViewerController.displayMessage(message);
        }
    }
}
