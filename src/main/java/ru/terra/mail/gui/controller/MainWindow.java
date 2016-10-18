package ru.terra.mail.gui.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.gui.controller.beans.FoldersTreeItem;
import ru.terra.mail.gui.core.AbstractUIController;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.StorageSingleton;

import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void loadFolders() {
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        return null;
                    }
                };
            }
        }.start();
    }
}
