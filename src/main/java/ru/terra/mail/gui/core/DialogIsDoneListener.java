package ru.terra.mail.gui.core;

/**
 * Created by terranz on 18.10.16.
 */
public interface DialogIsDoneListener<T> {
    void dialogIsDone(T ret, String... strings);

    void cancel();
}
