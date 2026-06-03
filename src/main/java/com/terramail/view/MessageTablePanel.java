package com.terramail.view;

import com.terramail.controller.MessageTableController;
import com.terramail.model.EmailFolder;
import com.terramail.model.EmailMessage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * View component for the message table panel.
 */
public class MessageTablePanel extends Pane {

    @FXML
    private MessageTableController messageTableController;

    private final SimpleObjectProperty<EmailMessage> messageSelected = new SimpleObjectProperty<>();

    /**
     * Initializes the view component.
     */
    public void initialize() {
        // FXML will handle the initialization
    }

    /**
     * Loads a folder into the panel.
     */
    public void loadFolder(EmailFolder folder) {
        if (messageTableController != null) {
            messageTableController.loadFolder(folder);
        }
    }

    /**
     * Gets the message selected property.
     */
    public SimpleObjectProperty<EmailMessage> messageSelectedProperty() {
        return messageSelected;
    }
}
