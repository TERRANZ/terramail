package ru.terra.mail.gui.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import ru.terra.mail.gui.core.AbstractUIView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by terranz on 21.10.16.
 */
public class MailSourceWindow extends AbstractUIView {
    @FXML
    public TextArea taMailSource;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void loadMailSource(String source) {
        this.taMailSource.setText(source);
    }

}
