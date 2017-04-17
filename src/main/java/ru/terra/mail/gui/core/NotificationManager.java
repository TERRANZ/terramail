package ru.terra.mail.gui.core;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vadim_Korostelev on 3/23/2017.
 */
public class NotificationManager {
    private static NotificationManager instance = new NotificationManager();
    private List<NotificationListener> listeners = new LinkedList<>();

    private NotificationManager() {

    }

    public static NotificationManager getInstance() {
        return instance;
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
