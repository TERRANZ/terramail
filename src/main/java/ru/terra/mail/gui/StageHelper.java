package ru.terra.mail.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import lombok.val;
import org.springframework.stereotype.Component;
import ru.terra.mail.gui.core.AbstractDialog;
import ru.terra.mail.gui.core.AbstractUIView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
@Component
public class StageHelper {
    private static final List<Stage> openedStages = new ArrayList<>();
    private static final StageCloseEventHandler stageCloseEventHandler = new StageCloseEventHandler();

    public static <T> Pair<Parent, T> loadRoot(String fxmlFileName) {
        val fxmlFile = GUIConstants.FXML + fxmlFileName;
        val location = StageHelper.class.getResource(fxmlFile);
        Parent root = null;
        val fxmlLoader = new FXMLLoader();
        try {
            root = fxmlLoader.load(location.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>(root, fxmlLoader.getController());
    }

    public static <T extends AbstractUIView> Pair<Stage, T> openWindow(String fxmlFileName, String title, boolean closeCurr) {
        Pair<Parent, T> windowPair = loadRoot(fxmlFileName);
        val stage = new Stage();
        stage.setOnHidden(stageCloseEventHandler);
        T controller = windowPair.getValue();
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
        val stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(windowPair.getKey()));
        stage.getScene().getStylesheets().add(GUIConstants.CSS_FILE);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        stage.setResizable(false);
        stage.show();
        T dialogController = windowPair.getValue();
        dialogController.setCurrStage(stage);
        return new Pair<>(stage, dialogController);
    }

    private static class StageCloseEventHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent windowEvent) {
            openedStages.remove(windowEvent.getTarget());
            if (openedStages.isEmpty())
                System.exit(0);
        }
    }
}
