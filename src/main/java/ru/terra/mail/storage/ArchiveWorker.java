package ru.terra.mail.storage;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.FileSystems.newFileSystem;
import static java.nio.file.Files.*;

public class ArchiveWorker {
    private static ArchiveWorker instance = new ArchiveWorker();
    private FileSystem zipfs;
    private final ExecutorService saverService = Executors.newFixedThreadPool(1);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ArchiveWorker() {
        val p = Paths.get("attachments.zip");
        try {
            if (!exists(p)) {
                createEmptyZip(p.toFile());
            }
            zipfs = newFileSystem(p);
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

    public void saveAttachment(final String folder, final String name, final InputStream stream) {
        try {
            if (!exists(zipfs.getPath(folder))) {
                createDirectories(zipfs.getPath(folder));
            }
            copy(stream, zipfs.getPath(name));
        } catch (Exception e) {
            logger.error("Unable to save file " + name, e);
        }
    }

    private void createEmptyZip(final File zipFile) {
        try {
            final byte[] EmptyZip = {80, 75, 05, 06, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00};
            final FileOutputStream fos = new FileOutputStream(zipFile);
            fos.write(EmptyZip, 0, 22);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close() {
        try {
            zipfs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance = null;
    }
}
