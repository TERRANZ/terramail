package ru.terra.mail;

import com.beust.jcommander.JCommander;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.gui.StageHelper;

/**
 * Created by terranz on 18.10.16.
 */
@EnableAutoConfiguration
@Component
@ComponentScan
public class Main extends Application {
    public static void main(String... args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);
        ctx.getBean(MailSystem.class).start();
        new JCommander(StartUpParameters.getInstance(), args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageHelper.openWindow("w_main.fxml", "Main", true);
    }
}
