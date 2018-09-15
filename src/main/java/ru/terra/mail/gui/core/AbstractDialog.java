package ru.terra.mail.gui.core;

import javafx.event.ActionEvent;

/**
 * Created by terranz on 18.10.16.
 */
public abstract class AbstractDialog<RetVal> extends AbstractUIView {
    protected DialogIsDoneListener<RetVal> dialogIsDoneListener;
    protected RetVal returnValue;

    public DialogIsDoneListener<RetVal> getDialogIsDoneListener() {
        return dialogIsDoneListener;
    }

    public void setDialogIsDoneListener(DialogIsDoneListener<RetVal> dialogIsDoneListener) {
        this.dialogIsDoneListener = dialogIsDoneListener;
    }

    public RetVal getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(RetVal returnValue) {
        this.returnValue = returnValue;
    }

    public abstract void ok(ActionEvent actionEvent);

    public void cancel(ActionEvent actionEvent) {
        currStage.close();
    }

    public abstract void loadExisting(RetVal value);

}
