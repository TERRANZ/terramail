package ru.terra.mail;

import org.apache.log4j.BasicConfigurator;

import com.beust.jcommander.JCommander;
import javafx.application.Application;
import javafx.stage.Stage;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.gui.StageHelper;

/**
 * Created by terranz on 18.10.16.
 */
public class Main extends Application {
	public static void main(String... args) {
		BasicConfigurator.configure();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final StartUpParameters parameters = StartUpParameters.getInstance();
		new JCommander(parameters, getParameters().getRaw().toArray(new String[getParameters().getRaw().size()]));
		StageHelper.openWindow("w_main.fxml", "Main", true);
	}
}
