package ru.terra.mail.gui.core;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

/**
 * Created by terranz on 18.10.16.
 */
@Getter
@Setter
public abstract class AbstractUIView implements Initializable {
    protected SimpleDateFormat messageDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    protected Stage currStage;
}
