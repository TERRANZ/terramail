package com.terramail.service;

import com.terramail.model.AttachmentInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttachmentServiceTest {

    @TempDir
    Path tempDir;

    private AttachmentService attachmentService;

    @BeforeEach
    void setUp() {
        attachmentService = new AttachmentService(tempDir);
    }

    @Test
    void testSaveAndGetAttachment() {
        long messageId = 1L;
        String filename = "test.txt";
        String content = "Hello World";

        InputStream is = new ByteArrayInputStream(content.getBytes());
        attachmentService.saveAttachment(messageId, filename, is);

        Path path = attachmentService.getAttachmentPath(messageId, filename);
        assertTrue(Files.exists(path));
        try {
            assertEquals(content, Files.readString(path));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testDeleteAttachments() {
        long messageId = 1L;
        attachmentService.saveAttachment(messageId, "file1.txt", new ByteArrayInputStream("data".getBytes()));
        attachmentService.saveAttachment(messageId, "file2.txt", new ByteArrayInputStream("data2".getBytes()));

        Path dir = tempDir.resolve(String.valueOf(messageId));
        assertTrue(Files.exists(dir));

        attachmentService.deleteAttachments(messageId);
        assertFalse(Files.exists(dir));
    }

    @Test
    void testSaveToZip() {
        long messageId = 1L;
        Path tempFile1 = null;
        Path tempFile2 = null;
        try {
            tempFile1 = Files.createTempFile("test1-", ".txt");
            Files.writeString(tempFile1, "Content 1");
            tempFile2 = Files.createTempFile("test2-", ".pdf");
            Files.writeString(tempFile2, "PDF content");

            List<AttachmentInfo> infoList = List.of(
                new AttachmentInfo("file1.txt", Files.size(tempFile1), "text/plain"),
                new AttachmentInfo("file2.pdf", Files.size(tempFile2), "application/pdf")
            );
            List<Path> filePaths = List.of(tempFile1, tempFile2);

            Path zipFile = attachmentService.saveToZip(messageId, infoList, filePaths);
            assertNotNull(zipFile);
            assertTrue(Files.exists(zipFile));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testSaveToZipEmpty() {
        Path zipFile = attachmentService.saveToZip(1L, List.of(), List.of());
        assertNull(zipFile);
    }

    @Test
    void testGetZipPath() {
        Path zipPath = attachmentService.getZipPath(42L);
        assertEquals(tempDir.resolve("42/attachments.zip"), zipPath);
    }

    @Test
    void testGuessContentType() {
        attachmentService.saveAttachment(1L, "test.pdf", new ByteArrayInputStream("data".getBytes()));
        Path path = attachmentService.getAttachmentPath(1L, "test.pdf");
        assertTrue(Files.exists(path));
    }

    @Test
    void testMultipleAttachmentsSameMessage() {
        long messageId = 1L;
        for (int i = 0; i < 5; i++) {
            attachmentService.saveAttachment(messageId, "file" + i + ".txt", new ByteArrayInputStream(("content" + i).getBytes()));
        }

        for (int i = 0; i < 5; i++) {
            Path path = attachmentService.getAttachmentPath(messageId, "file" + i + ".txt");
            assertTrue(Files.exists(path));
            try {
                assertEquals("content" + i, Files.readString(path));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
    }
}
