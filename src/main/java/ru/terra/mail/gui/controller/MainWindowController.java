package ru.terra.mail.gui.controller;

import javafx.application.Platform;
import ru.terra.mail.Main;
import ru.terra.mail.core.domain.MailFoldersTree;
import ru.terra.mail.gui.core.NotificationListener;
import ru.terra.mail.gui.core.NotificationManager;
import ru.terra.mail.gui.model.FoldersModel;
import ru.terra.mail.gui.model.MessagesModel;
import ru.terra.mail.gui.view.MainWindowView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindowController implements NotificationListener {

    private FoldersModel foldersModel;
    private MessagesModel messagesModel;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final MainWindowView view;

    public MainWindowController(final MainWindowView view) {
        this.view = view;
    }

    public void initialize() {
        foldersModel = Main.getContext().getBean(FoldersModel.class);
        messagesModel = Main.getContext().getBean(MessagesModel.class);
        NotificationManager.getInstance().addListener(this);

        showFolders();
    }


    private void updateStatus(String status) {
        Platform.runLater(() -> view.setStatusText(status));
    }

    public void showFolders() {
        updateStatus("Folders loading...");
        final MailFoldersTree treeRoot = foldersModel.getStoredFolders();
        if (treeRoot != null)
            view.setFoldersTreeRoot(treeRoot);

        executorService.submit(() -> {
            final MailFoldersTree result = foldersModel.getTreeRoot();
            view.runOnUIThread(() -> view.setFoldersTreeRoot(result));
        });
    }

    @Override
    public void notify(final String from, final String message) {
        updateStatus(from + " : " + message);
    }
//
//    public void showMessages(MailFolder mailFolder) {
//        updateStatus(mailFolder.getFullName());
//        updateStatus("Messages loading");
//        ObservableSet<MailMessage> storedMessages = messagesModel.getStoredMessages(mailFolder.getGuid());
//        ObservableList<MessagesTableItem> displayItems = FXCollections.observableArrayList();
//        if (storedMessages != null && storedMessages.size() > 0) {
//            displayItems.addAll(storedMessages.stream().map(MessagesTableItem::new).collect(Collectors.toList()));
//            tvMessages.setItems(displayItems);
//        }
//        MainWindowViewImpl.LoadMessagesService loadMessagesService = new MainWindowViewImpl.LoadMessagesService(mailFolder, storedMessages);
//        loadMessagesService.start();
//        loadMessagesService.setOnSucceeded((e) -> {
//            displayItems.clear();
//            displayItems.addAll(
//                    loadMessagesService.getValue().stream().map(MessagesTableItem::new).collect(Collectors.toList()));
//            tvMessages.setItems(displayItems);
//            updateStatus("Messages loaded");
//        });
//        storedMessages.addListener((InvalidationListener) observable -> {
//            displayItems.clear();
//            displayItems.addAll(storedMessages.stream().map(MessagesTableItem::new).collect(Collectors.toList()));
//            tvMessages.setItems(displayItems);
//        });
//    }
}
