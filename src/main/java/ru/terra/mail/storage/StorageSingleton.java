package ru.terra.mail.storage;

import ru.terra.mail.storage.impl.JsonStorage;

/**
 * Created by terranz on 19.10.16.
 */
public class StorageSingleton {
    private static StorageSingleton instance = new StorageSingleton();
    private AbstractStorage storage;

    private StorageSingleton() {
        storage = new JsonStorage();
    }

    public static StorageSingleton getInstance() {
        return instance;
    }

    public AbstractStorage getStorage() {
        return storage;
    }
}
