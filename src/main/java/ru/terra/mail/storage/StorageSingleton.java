package ru.terra.mail.storage;

/**
 * Created by terranz on 19.10.16.
 */
public class StorageSingleton {
    private static StorageSingleton instance = new StorageSingleton();
    private AbstractStorage storage;

    private StorageSingleton() {
//        storage = new DBStorage();
    }

    public static StorageSingleton getInstance() {
        return instance;
    }

    public AbstractStorage getStorage() {
        return storage;
    }
}
