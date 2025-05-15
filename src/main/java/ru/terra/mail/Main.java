package ru.terra.mail;

import com.beust.jcommander.JCommander;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.gui.StageHelper;

/**
 * Created by terranz on 18.10.16.
 */
@SpringBootApplication
@Component
@EnableJpaRepositories(basePackages = "ru.terra.mail")
public class Main extends Application {
    @Getter
    public static ConfigurableApplicationContext context;

    public static void main(String... args) {
        context = SpringApplication.run(Main.class, args);
        context.getBean(MailSystem.class).start();
        new JCommander(StartUpParameters.getInstance(), args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StageHelper.openWindow("w_main.fxml", "Main", true);
    }
}
