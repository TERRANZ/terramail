package ru.terra.mail;

import com.beust.jcommander.JCommander;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.gui.StageHelper;

/**
 * Created by terranz on 18.10.16.
 */
public class Main extends Application {
    public static void main(String... args) {
        new JCommander(StartUpParameters.getInstance(), args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageHelper.openWindow("w_main.fxml", "Main", true);
    }
}
