package ru.terra.mail.storage;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Paths;

import static java.nio.file.Files.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Service
public class ArchiveManager {
    public void saveAttachment(final String folder, final String name, final InputStream stream) {
        // Implementation to save attachment
//        log.info("Saving attachment: {} in folder: {}", name, folder);
        validateAndCreateFolder(folder);
        try {
            saveStreamToFile(name, stream);
        } catch (Exception e) {
            log.error("Failed to save attachment: {}", name, e);
        }
    }

    private void validateAndCreateFolder(String folder) {
        val path = Paths.get(folder);
        if (!exists(path)) {
            try {
                createDirectories(path);
//                log.info("Created folder: {}", folder);
            } catch (Exception e) {
                log.error("Failed to create folder: {}", folder, e);
            }
        } else {
//            log.info("Folder already exists: {}", folder);
        }
    }

    private void saveStreamToFile(String name, InputStream stream) throws Exception {
        val filePath = Paths.get(name);
        copy(stream, filePath, REPLACE_EXISTING);
//        log.info("Saved file: {}", name);
    }

    public void close() {

    }
}
