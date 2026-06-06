package com.terramail.ui.main;

import com.terramail.model.Message;
import com.terramail.model.AttachmentInfo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MessageContentPanel extends JPanel {

    private final JTextArea bodyArea;
    private final JLabel subjectLabel;
    private final JLabel fromLabel;
    private final JLabel dateLabel;
    private final JLabel toLabel;
    private final JLabel ccLabel;
    private final JTextArea attachmentArea;

    public MessageContentPanel() {
        this.subjectLabel = new JLabel("(No Subject)");
        this.fromLabel = new JLabel("");
        this.dateLabel = new JLabel("");
        this.toLabel = new JLabel("");
        this.ccLabel = new JLabel("");
        this.bodyArea = new JTextArea();
        bodyArea.setEditable(false);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        bodyArea.setBackground(new Color(250, 250, 245));
        this.attachmentArea = new JTextArea();
        attachmentArea.setEditable(false);
        attachmentArea.setFont(attachmentArea.getFont().deriveFont(11f));
        attachmentArea.setBackground(new Color(245, 245, 240));
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Message"));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        subjectLabel.setFont(subjectLabel.getFont().deriveFont(Font.BOLD, 14f));

        infoPanel.add(subjectLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(fromLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(dateLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(toLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(ccLabel);

        add(infoPanel, BorderLayout.NORTH);

        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        add(bodyScroll, BorderLayout.CENTER);

        JScrollPane attachScroll = new JScrollPane(attachmentArea);
        attachScroll.setPreferredSize(new Dimension(0, 50));
        add(attachScroll, BorderLayout.SOUTH);
    }

    public void displayMessage(Message message) {
        subjectLabel.setText(message.getSubject() != null ? message.getSubject() : "(No Subject)");
        fromLabel.setText("From: " + (message.getFrom() != null ? message.getFrom() : "Unknown"));
        toLabel.setText("To: " + (message.getTo() != null ? message.getTo() : ""));
        ccLabel.setText(message.getCc() != null && !message.getCc().isEmpty() ? "CC: " + message.getCc() : "");
        dateLabel.setText("Date: " + (message.getDate() != null ? message.getDate().toString() : "Unknown"));
        bodyArea.setText(message.getBody() != null ? message.getBody() : "");

        if (message.hasAttachments()) {
            List<AttachmentInfo> attachments = message.getAttachments();
            StringBuilder sb = new StringBuilder("Attachments: ");
            for (int i = 0; i < attachments.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(attachments.get(i).getName());
            }
            attachmentArea.setText(sb.toString());
        } else {
            attachmentArea.setText("");
        }
    }
}
