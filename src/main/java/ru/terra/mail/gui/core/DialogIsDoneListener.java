package ru.terra.mail.gui.core;

/**
 * Created by terranz on 18.10.16.
 */
public interface DialogIsDoneListener<T> {
    public void dialogIsDone(T ret, String... strings);

    public void cancel();
}
