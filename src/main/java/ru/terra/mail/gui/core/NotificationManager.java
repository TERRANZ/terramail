package ru.terra.mail.gui.core;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vadim_Korostelev on 3/23/2017.
 */
public class NotificationManager {
    @Getter
    private static final NotificationManager instance = new NotificationManager();
    private final List<NotificationListener> listeners = new LinkedList<>();

    private NotificationManager() {

    }

    public void notify(String from, String message) {
        listeners.forEach(l -> l.notify(from, message));
    }

    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }
}
