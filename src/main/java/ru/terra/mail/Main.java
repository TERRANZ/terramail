package ru.terra.mail;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.terra.mail.gui.StageHelper;

/**
 * Created by terranz on 18.10.16.
 */
public class Main extends Application {
    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageHelper.openWindow("w_main.fxml", "Main", true);
    }
}
