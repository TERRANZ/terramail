package com.terramail.controller;

import com.terramail.model.Attachment;
import com.terramail.model.EmailMessage;
import com.terramail.model.Recipient;
import com.terramail.util.DateTimeUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

import java.util.logging.Logger;

/**
 * Controller for the message viewer panel.
 */
public class MessageViewerController {
    private static final Logger logger = Logger.getLogger(MessageViewerController.class.getName());

    private EmailMessage currentMessage;

    @FXML
    private javafx.scene.layout.VBox emptyState;

    @FXML
    private javafx.scene.layout.VBox messageContent;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label fromLabel;

    @FXML
    private Label toLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private WebView messageBodyView;

    @FXML
    private javafx.scene.layout.VBox attachmentsSection;

    @FXML
    private ListView<Attachment> attachmentsList;

    @FXML
    private Button downloadButton;

    @FXML
    private Button downloadAllButton;

    private final ObservableList<Attachment> attachments = FXCollections.observableArrayList();

    /**
     * Initializes the controller.
     */
    public void initialize() {
        setupWebView();
        setupAttachmentsList();
        hideContent();
    }

    /**
     * Sets up the WebView for displaying HTML content.
     */
    private void setupWebView() {
        messageBodyView.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
    }

    /**
     * Sets up the attachments list.
     */
    private void setupAttachmentsList() {
        attachmentsList.setCellFactory(list -> new ListCell<Attachment>() {
            @Override
            protected void updateItem(Attachment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFilename() + " (" + item.getHumanReadableSize() + ")");
                }
            }
        });
        attachmentsList.setItems(attachments);
    }

    /**
     * Displays a message in the viewer.
     */
    public void displayMessage(EmailMessage message) {
        currentMessage = message;

        if (message == null) {
            hideContent();
            return;
        }

        showContent();

        // Display message header info
        subjectLabel.setText(message.getSubject());

        // Display sender
        String sender = "";
        if (message.getRecipients() != null && !message.getRecipients().isEmpty()) {
            for (Recipient recipient : message.getRecipients()) {
                if ("FROM".equals(recipient.getType())) {
                    sender = recipient.getDisplayString();
                    break;
                }
            }
        }
        fromLabel.setText(sender.isEmpty() ? "Unknown" : sender);

        // Display recipients
        StringBuilder toBuilder = new StringBuilder();
        if (message.getRecipients() != null) {
            for (Recipient recipient : message.getRecipients()) {
                if ("TO".equals(recipient.getType())) {
                    if (toBuilder.length() > 0) {
                        toBuilder.append(", ");
                    }
                    toBuilder.append(recipient.getDisplayString());
                }
            }
        }
        toLabel.setText(toBuilder.length() > 0 ? toBuilder.toString() : "None");

        // Display date
        dateLabel.setText(DateTimeUtil.formatDisplay(message.getReceivedDate()));

        // Display message body
        String bodyHtml = message.getBodyHtml();
        if (bodyHtml != null && !bodyHtml.isEmpty()) {
            messageBodyView.getEngine().loadContent(bodyHtml);
        } else if (message.getBodyPlain() != null && !message.getBodyPlain().isEmpty()) {
            String escaped = message.getBodyPlain()
                    .replace("&", "&")
                    .replace("<", "<")
                    .replace(">", ">")
                    .replace("\n", "<br>");
            messageBodyView.getEngine().loadContent("<html><body style=\"font-family: sans-serif; white-space: pre-wrap;\">" + escaped + "</body></html>");
        } else {
            messageBodyView.getEngine().loadContent("<html><body><p>No content</p></body></html>");
        }

        // Display attachments
        attachments.clear();
        if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
            attachments.addAll(message.getAttachments());
            attachmentsSection.setVisible(true);
            attachmentsSection.setManaged(true);
        } else {
            attachmentsSection.setVisible(false);
            attachmentsSection.setManaged(false);
        }

        // Mark as read
        if (!message.isRead()) {
            message.setRead(true);
            // In a real app, this would be handled by the service layer
        }
    }

    /**
     * Shows the message content.
     */
    private void showContent() {
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        messageContent.setVisible(true);
        messageContent.setManaged(true);
    }

    /**
     * Hides the message content.
     */
    private void hideContent() {
        emptyState.setVisible(true);
        emptyState.setManaged(true);
        messageContent.setVisible(false);
        messageContent.setManaged(false);
    }

    /**
     * Gets the current message being displayed.
     */
    public EmailMessage getCurrentMessage() {
        return currentMessage;
    }
}
