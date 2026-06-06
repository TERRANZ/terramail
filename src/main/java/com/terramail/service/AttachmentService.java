package com.terramail.service;

import com.terramail.model.AttachmentInfo;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AttachmentService {

    private final Path attachmentsDir;

    public AttachmentService(Path attachmentsDir) {
        this.attachmentsDir = attachmentsDir;
        if (!Files.exists(attachmentsDir)) {
            try {
                Files.createDirectories(attachmentsDir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create attachments directory", e);
            }
        }
    }

    public List<AttachmentInfo> extractFromZip(long messageId, Path zipFile) {
        List<AttachmentInfo> attachments = new ArrayList<>();
        Path messageDir = attachmentsDir.resolve(String.valueOf(messageId));

        if (!Files.exists(zipFile)) {
            return attachments;
        }

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            Files.createDirectories(messageDir);
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    Path outputPath = messageDir.resolve(entry.getName());
                    Files.createDirectories(outputPath.getParent());
                    try (OutputStream os = Files.newOutputStream(outputPath)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                    attachments.add(new AttachmentInfo(
                        entry.getName(),
                        entry.getSize(),
                        guessContentType(entry.getName())
                    ));
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract attachments for message " + messageId, e);
        }
        return attachments;
    }

    public Path saveToZip(long messageId, List<AttachmentInfo> attachmentInfos, List<Path> filePaths) {
        if (attachmentInfos == null || attachmentInfos.isEmpty()) {
            return null;
        }
        if (filePaths == null || filePaths.size() != attachmentInfos.size()) {
            throw new IllegalArgumentException("filePaths size must match attachmentInfos size");
        }

        Path messageDir = attachmentsDir.resolve(String.valueOf(messageId));
        Path zipFile = messageDir.resolve("attachments.zip");

        try {
            Files.createDirectories(messageDir);
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
                for (int i = 0; i < filePaths.size(); i++) {
                    Path file = filePaths.get(i);
                    if (Files.exists(file)) {
                        ZipEntry entry = new ZipEntry(attachmentInfos.get(i).getName());
                        zos.putNextEntry(entry);
                        try (InputStream is = Files.newInputStream(file)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = is.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                        }
                        zos.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save attachments zip for message " + messageId, e);
        }
        return zipFile;
    }

    public void saveAttachment(long messageId, String filename, InputStream content) {
        Path messageDir = attachmentsDir.resolve(String.valueOf(messageId));
        try {
            Files.createDirectories(messageDir);
            Path outputPath = messageDir.resolve(filename);
            Files.createDirectories(outputPath.getParent());
            try (OutputStream os = Files.newOutputStream(outputPath)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = content.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save attachment for message " + messageId, e);
        }
    }

    public Path getAttachmentPath(long messageId, String filename) {
        return attachmentsDir.resolve(String.valueOf(messageId)).resolve(filename);
    }

    public Path getZipPath(long messageId) {
        return attachmentsDir.resolve(String.valueOf(messageId)).resolve("attachments.zip");
    }

    public void deleteAttachments(long messageId) {
        Path messageDir = attachmentsDir.resolve(String.valueOf(messageId));
        try {
            if (Files.exists(messageDir)) {
                deleteDirectory(messageDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete attachments for message " + messageId, e);
        }
    }

    private void deleteDirectory(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        }
    }

    private String guessContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".txt")) return "text/plain";
        if (lower.endsWith(".doc")) return "application/msword";
        if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (lower.endsWith(".xls")) return "application/vnd.ms-excel";
        if (lower.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (lower.endsWith(".zip")) return "application/zip";
        if (lower.endsWith(".csv")) return "text/csv";
        if (lower.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }
}
