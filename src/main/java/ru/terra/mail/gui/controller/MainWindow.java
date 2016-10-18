package ru.terra.mail.gui.controller;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.config.Configuration;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.gui.core.AbstractUIController;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.StorageSingleton;
import ru.terra.mail.storage.entity.MailFolder;

import javax.mail.MessagingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by terranz on 18.10.16.
 */
public class MainWindow extends AbstractUIController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @FXML
    private TreeView<FoldersTreeItem> tvFolders;

    private TreeItem<FoldersTreeItem> treeRoot;
    private AbstractStorage storage = StorageSingleton.getInstance().getStorage();
    private AbstractMailProtocol protocol = Configuration.getInstance().getMailProtocol();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFolders();
    }

    private void loadFolders() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        List<MailFolder> storedFolders = storage.getRootFolders();
                        if (storedFolders == null) {
                            boolean loggedIn = false;
                            try {
                                performLogin();
                                loggedIn = true;
                            } catch (Exception e) {
                                logger.error("Unable to login", e);
                            }
                            if (loggedIn) {
                                storedFolders = protocol.listFolders();
                                storage.storeFolders(storedFolders);
                            }
                        }
                        treeRoot = new TreeItem<>(new FoldersTreeItem(storedFolders.get(0)));
                        storedFolders.get(0).getChildFolders().forEach(cf -> processFolder(treeRoot, cf));
                        Platform.runLater(() -> tvFolders.setRoot(treeRoot));
                        return null;
                    }
                };
            }
        }.start();
    }

    private TreeItem<FoldersTreeItem> processFolder(TreeItem<FoldersTreeItem> parent, MailFolder mailFolder) {
        TreeItem<FoldersTreeItem> ret = new TreeItem<>(new FoldersTreeItem(mailFolder));
        parent.getChildren().add(ret);
        mailFolder.getChildFolders().forEach(cf -> processFolder(ret, cf));
        return ret;
    }

    private void performLogin() throws GeneralSecurityException, MessagingException {
        protocol.login(StartUpParameters.getInstance().getUser(), StartUpParameters.getInstance().getPass(), StartUpParameters.getInstance().getServ());
    }
}
