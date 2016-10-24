package ru.terra.mail.storage;

/**
 * Created by terranz on 19.10.16.
 */
public class StorageSingleton {
	private static StorageSingleton instance = new StorageSingleton();
	private Storage storage;

	private StorageSingleton() {
		storage = new Storage();
	}

	public static StorageSingleton getInstance() {
		return instance;
	}

	public Storage getStorage() {
		return storage;
	}
}
