package ru.terra.mail.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArchiveWorker {
    private static ArchiveWorker instance = new ArchiveWorker();
    private FileSystem zipfs;
    private ExecutorService saverService = Executors.newFixedThreadPool(1);
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ArchiveWorker() {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        Path p = Paths.get("attachments.zip");
        try {
            if (!Files.exists(p)) {
                createEmptyZip(p.toFile());
            }
            zipfs = FileSystems.newFileSystem(p, null);
        } catch (Exception e) {
            logger.error("Unable to create ZIP filesystem", e);
        }
    }

    public static ArchiveWorker getInstance() {
        if (instance == null) {
            instance = new ArchiveWorker();
        }
        return instance;
    }

    public void saveAttachment(String folder, String name, InputStream stream) {
        try {
            if (!Files.exists(zipfs.getPath(folder))) {
                Files.createDirectories(zipfs.getPath(folder));
            }
            Files.copy(stream, zipfs.getPath(name));
        } catch (Exception e) {
            logger.error("Unable to save file " + name, e);
        }
    }

    public void createEmptyZip(File zipFile) {
        try {
            byte[] EmptyZip = {80, 75, 05, 06, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00};
            FileOutputStream fos = new FileOutputStream(zipFile);
            fos.write(EmptyZip, 0, 22);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            zipfs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance = null;
    }
}
