package ru.terra.mail.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import org.springframework.stereotype.Component;
import ru.terra.mail.gui.core.AbstractDialog;
import ru.terra.mail.gui.core.AbstractUIView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
@Component
public class StageHelper {
    private static List<Stage> openedStages = new ArrayList<>();
    private static StageCloseEventHandler stageCloseEventHandler = new StageCloseEventHandler();

    public static <T> Pair<Parent, T> loadRoot(String fxmlFileName) {
        String fxmlFile = GUIConstants.FXML + fxmlFileName;
        URL location = StageHelper.class.getResource(fxmlFile);
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            root = (Parent) fxmlLoader.load(location.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>(root, fxmlLoader.getController());
    }

    public static <T extends AbstractUIView> Pair<Stage, T> openWindow(String fxmlFileName, String title, boolean closeCurr) {
        Pair<Parent, T> windowPair = loadRoot(fxmlFileName);
        Stage stage = new Stage();
        stage.setOnHidden(stageCloseEventHandler);
        T controller = (T) windowPair.getValue();
        controller.setCurrStage(stage);
        openedStages.add(stage);
        stage.setTitle(title);
        stage.setScene(new Scene(windowPair.getKey()));
//        stage.getIcons().add(new Image(StageHelper.class.getResourceAsStream(GUIConstants.IMAGES + "logo.png")));
        stage.getScene().getStylesheets().add(GUIConstants.CSS_FILE);
        stage.show();

        return new Pair<>(stage, controller);
    }

    public static <T extends AbstractDialog> Pair<Stage, T> openDialog(String fxmlFileName, String title, Stage parent) {
        Pair<Parent, T> windowPair = loadRoot(fxmlFileName);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(windowPair.getKey()));
        stage.getScene().getStylesheets().add(GUIConstants.CSS_FILE);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        stage.setResizable(false);
        stage.show();
        T dialogController = (T) windowPair.getValue();
        dialogController.setCurrStage(stage);
        return new Pair<>(stage, dialogController);
    }

    private static class StageCloseEventHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent windowEvent) {
            openedStages.remove(windowEvent.getTarget());
            if (openedStages.size() == 0)
                System.exit(0);
        }
    }
}
