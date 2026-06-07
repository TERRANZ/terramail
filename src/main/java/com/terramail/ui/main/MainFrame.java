package com.terramail.ui.main;

import com.terramail.model.Folder;
import com.terramail.model.SortOrder;
import com.terramail.model.AccountSettings;
import com.terramail.repository.AccountSettingsRepository;
import com.terramail.repository.AccountSettingsRepositoryImpl;
import com.terramail.repository.FolderRepository;
import com.terramail.repository.FolderRepositoryImpl;
import com.terramail.repository.MessageRepository;
import com.terramail.repository.MessageRepositoryImpl;
import com.terramail.service.*;
import com.terramail.ui.model.FolderTreeModel;
import com.terramail.ui.model.MessageTableModel;
import com.terramail.ui.panels.ComposePanel;
import com.terramail.ui.panels.SettingsPanel;
import com.terramail.ui.panels.SyncStatusPanel;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import com.zaxxer.hikari.HikariDataSource;

public class MainFrame extends JFrame {

    private FolderTreePanel folderTreePanel;
    private MessageTablePanel messageTablePanel;
    private MessageContentPanel messageContentPanel;
    private final MessageTableModel messageTableModel;
    private final FolderTreeModel folderTreeModel;
    private final AppState appState;
    private final EmailService emailService;
    private final MessageRepository messageRepository;
    private final FolderRepository folderRepository;
    private final AttachmentService attachmentService;
    private final AccountSettingsRepository settingsRepository;
    private final DatabaseService databaseService;
    private SettingsPanel settingsPanel;
    private JDialog settingsDialog;
    private ComposePanel composePanel;
    private JDialog composeDialog;
    private SyncStatusPanel syncStatusPanel;
    private final AccountSettings settings;

    public MainFrame(DatabaseService databaseService, AccountSettings settings) {
        this.databaseService = databaseService;
        this.settings = settings;
        this.folderTreeModel = new FolderTreeModel();
        this.messageTableModel = new MessageTableModel();

        this.folderTreePanel = new FolderTreePanel(folderTreeModel);
        this.messageTablePanel = new MessageTablePanel(messageTableModel);
        this.messageContentPanel = new MessageContentPanel();
        this.appState = new AppState();
        this.attachmentService = new AttachmentService(Path.of("attachments"));
        this.emailService = new EmailService(settings, attachmentService);

        HikariDataSource ds = databaseService.getDataSource();
        this.messageRepository = new MessageRepositoryImpl(ds);
        this.folderRepository = new FolderRepositoryImpl(ds);
        this.settingsRepository = new AccountSettingsRepositoryImpl(ds);

        initializeUI();
        loadFolders();
    }

    private void initializeUI() {
        setTitle("Terramail");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        folderTreePanel.setSelectionListener(this::onFolderSelected);
        messageTablePanel.setMessageClickListener(this::onMessageClicked);
        messageTablePanel.setSortChangeListener(this::onSortChanged);

        JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightPane.setTopComponent(messageTablePanel);
        rightPane.setBottomComponent(messageContentPanel);
        rightPane.setDividerLocation(0.35);
        rightPane.setResizeWeight(0.35);

        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPane.setLeftComponent(folderTreePanel);
        mainPane.setRightComponent(rightPane);
        mainPane.setDividerLocation(0.2);
        mainPane.setResizeWeight(0.2);

        syncStatusPanel = new SyncStatusPanel(appState);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> showSettings());
        fileMenu.add(settingsItem);

        JMenuItem composeItem = new JMenuItem("Compose");
        composeItem.addActionListener(e -> showCompose());
        fileMenu.add(composeItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem onlineItem = new JMenuItem("Online Mode");
        onlineItem.addActionListener(e -> toggleOnlineMode());
        viewMenu.add(onlineItem);

        JMenuItem syncItem = new JMenuItem("Sync");
        syncItem.addActionListener(e -> triggerSync());
        viewMenu.add(syncItem);

        JMenuItem loadFoldersItem = new JMenuItem("Load Folders");
        loadFoldersItem.addActionListener(e -> triggerLoadFolders());
        viewMenu.add(loadFoldersItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(syncStatusPanel, BorderLayout.EAST);
        bottomPanel.setPreferredSize(new Dimension(200, 25));

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(mainPane, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainContainer);
    }

    private void loadFolders() {
        List<Folder> folders = folderRepository.findByAccountId(settings.getId());
        folderTreeModel.setFolders(folders);
    }

    private void onFolderSelected(Folder folder) {
        appState.setActiveFolderId(folder.getId());
        List<SortOrder.Field> fields = List.of(SortOrder.Field.DATE, SortOrder.Field.SUBJECT, SortOrder.Field.FROM, SortOrder.Field.TO);
        SortOrder currentSort = messageTableModel.getSortOrder();
        SortOrder.Field currentField = currentSort.getField();
        SortOrder.Field firstField = fields.stream()
            .filter(f -> f == currentField)
            .findFirst()
            .orElse(fields.get(0));
        SortOrder newSort = new SortOrder(firstField, SortOrder.Direction.DESC);
        messageTableModel.setSortOrder(newSort);

        List<com.terramail.model.Message> messages = messageRepository.findByFolderId(folder.getId(), newSort);
        messageTableModel.setMessages(messages);
    }

    private void onMessageClicked(com.terramail.model.Message message) {
        messageContentPanel.displayMessage(message);
        messageRepository.updateSeen(message.getId(), true);
        message.setSeen(true);
        int row = messageTableModel.findRowIndex(message);
        if (row >= 0) {
            messageTableModel.fireTableCellUpdated(row, 5);
        }
    }

    private void onSortChanged(SortOrder sortOrder) {
        Folder selectedFolder = getSelectedFolder();
        if (selectedFolder != null) {
            List<com.terramail.model.Message> messages = messageRepository.findByFolderId(selectedFolder.getId(), sortOrder);
            messageTableModel.setMessages(messages);
        }
    }

    private Folder getSelectedFolder() {
        return folderTreePanel.getSelectedFolder();
    }

    private void showSettings() {
        if (settingsDialog == null) {
            settingsPanel = new SettingsPanel(settings, databaseService);
            settingsDialog = new JDialog(this, "Settings", true);
            settingsDialog.add(settingsPanel);
            settingsDialog.pack();
            settingsDialog.setLocationRelativeTo(this);
            settingsPanel.setSaveListener(newSettings -> {
                settingsRepository.save(newSettings);
                settingsDialog.dispose();
            });
        }
        settingsDialog.setVisible(true);
    }

    private void showCompose() {
        if (composeDialog == null) {
            composePanel = new ComposePanel();
            composeDialog = new JDialog(this, "Compose Message", true);
            composeDialog.add(composePanel);
            composeDialog.pack();
            composeDialog.setLocationRelativeTo(this);

            if (appState.isOnline()) {
                composePanel.setSendListener((to, cc, subject, body) -> {
                    boolean success = emailService.sendMessage(to, cc, subject, body);
                    if (success) {
                        JOptionPane.showMessageDialog(composeDialog, "Message sent successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(composeDialog, "Failed to send message.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } else {
                composePanel.setSendListener((to, cc, subject, body) -> {
                    String queuedId = to + "|" + cc + "|" + subject + "|" + body;
                    appState.addQueuedMessage(queuedId);
                    JOptionPane.showMessageDialog(composeDialog, "Message queued for sending. (" + appState.getQueuedMessageCount() + " queued)", "Queued", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        }
        composeDialog.setVisible(true);
    }

    private void toggleOnlineMode() {
        if (appState.isOnline()) {
            appState.setStatus(AppState.Status.OFFLINE);
        } else {
            appState.setStatus(AppState.Status.ONLINE);
        }
    }

    private void triggerSync() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    publish("Starting sync...");
                    List<Folder> folders = folderRepository.findByAccountId(settings.getId());
                    int total = folders.size();
                    for (int i = 0; i < total; i++) {
                        Folder folder = folders.get(i);
                        publish("Fetching folder " + (i + 1) + "/" + total + ": " + folder.getName());
                        List<com.terramail.model.Message> messages = emailService.fetchMessages(folder);
                        for (com.terramail.model.Message msg : messages) {
                            messageRepository.save(msg);
                        }
                        appState.setSyncStatus("Synced " + folder.getName() + " (" + messages.size() + " messages)");
                    }
                    publish("Sync complete.");

                    if (!appState.isEmpty()) {
                        publish("Sending " + appState.getQueuedMessageCount() + " queued message(s)...");
                        for (String queued : appState.getQueuedMessages()) {
                            String[] parts = queued.split("\\|", 4);
                            if (parts.length == 4) {
                                emailService.sendMessage(parts[0], parts[1], parts[2], parts[3]);
                            }
                        }
                        appState.clearQueuedMessages();
                    }
                } catch (Exception e) {
                    publish("Sync failed: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                syncStatusPanel.updateStatus(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                appState.setSyncing(false);
                loadFolders();
                Folder folder = getSelectedFolder();
                if (folder != null) {
                    onFolderSelected(folder);
                }
            }
        };
        appState.setSyncing(true);
        worker.execute();
    }

    private void triggerLoadFolders() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    publish("Loading folders from server...");
                    List<Folder> serverFolders = emailService.listAvailableFolders();
                    publish("Found " + serverFolders.size() + " folder(s) on server.");

                    for (Folder serverFolder : serverFolders) {
                        Folder existing = folderRepository.findByName(settings.getId(), serverFolder.getName());
                        if (existing == null) {
                            Folder newFolder = new Folder();
                            newFolder.setAccountId(settings.getId());
                            newFolder.setName(serverFolder.getName());
                            newFolder.setType(serverFolder.getType());
                            folderRepository.save(newFolder);
                            publish("Added folder: " + serverFolder.getName());
                        } else {
                            publish("Folder already exists: " + serverFolder.getName());
                        }
                    }
                    publish("Folder loading complete.");
                } catch (Exception e) {
                    publish("Failed to load folders: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                syncStatusPanel.updateStatus(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                loadFolders();
                Folder folder = getSelectedFolder();
                if (folder != null) {
                    onFolderSelected(folder);
                }
            }
        };
        worker.execute();
    }
}
