package ru.terra.mail.gui.core;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

/**
 * Created by terranz on 18.10.16.
 */
public abstract class AbstractUIController implements Initializable {
    protected SimpleDateFormat messageDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    protected Stage currStage;

    public Stage getCurrStage() {
        return currStage;
    }

    public void setCurrStage(Stage currStage) {
        this.currStage = currStage;
    }

}
