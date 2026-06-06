package com.terramail.ui.panels;

import com.terramail.service.AppState;

import javax.swing.*;
import java.awt.*;

public class SyncStatusPanel extends JPanel {

    private final JLabel statusLabel;
    private final AppState appState;

    public SyncStatusPanel(AppState appState) {
        this.appState = appState;
        this.statusLabel = new JLabel("Offline");
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));
        statusLabel.setForeground(Color.GRAY);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        add(statusLabel);

        appState.addStatusListener(this::updateStatus);
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
        if (appState.isSyncing()) {
            statusLabel.setForeground(Color.BLUE);
        } else if ("Sync complete.".equals(status)) {
            statusLabel.setForeground(Color.GREEN);
        } else if (status.startsWith("Sync failed")) {
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setForeground(Color.GRAY);
        }
    }
}
