package com.terramail.ui.panels;

import javax.swing.*;
import java.awt.*;

public class ComposePanel extends JPanel {

    private final JTextField toField;
    private final JTextField ccField;
    private final JTextField subjectField;
    private final JTextArea bodyArea;
    private SendAction sendListener;

    @FunctionalInterface
    public interface SendAction {
        void accept(String to, String cc, String subject, String body);
    }

    public ComposePanel() {
        this.toField = new JTextField(30);
        this.ccField = new JTextField(30);
        this.subjectField = new JTextField(30);
        this.bodyArea = new JTextArea(10, 30);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyScroll.setPreferredSize(new Dimension(400, 150));

        gbc.gridy = 0;
        formPanel.add(new JLabel("To:"), gbc);
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        formPanel.add(toField, gbc);

        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("CC:"), gbc);
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        formPanel.add(ccField, gbc);

        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        formPanel.add(subjectField, gbc);

        gbc.gridy = 6;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Message:"), gbc);
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.BOTH;
        formPanel.add(bodyScroll, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sendButton = new JButton("Send");
        JButton closeButton = new JButton("Close");
        sendButton.addActionListener(e -> sendMessage());
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.windowForComponent(ComposePanel.this);
            if (window != null) window.dispose();
        });
        buttonPanel.add(sendButton);
        buttonPanel.add(closeButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String to = toField.getText().trim();
        String cc = ccField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText();

        if (to.isBlank()) {
            JOptionPane.showMessageDialog(this, "Recipient is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (sendListener != null) {
            sendListener.accept(to, cc, subject, body);
        }
    }

    public void setSendListener(SendAction listener) {
        this.sendListener = listener;
    }
}
