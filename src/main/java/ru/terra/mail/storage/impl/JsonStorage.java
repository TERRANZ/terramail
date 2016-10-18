package ru.terra.mail.storage.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.entity.MailFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
public class JsonStorage extends AbstractStorage {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<MailFolder> getRootFolders() {
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream("mailbox.json"), Charset.forName("UTF-8"));
            return new ObjectMapper().readValue(reader, new TypeReference<List<MailFolder>>() {
            });
        } catch (Exception e) {
            logger.error("Exception while reading db", e);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("Unable to close reader", e);
                }
        }
        return null;
    }

    @Override
    public void storeFolders(List<MailFolder> mailFolders) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("mailbox.json"), "UTF-8"));
            new ObjectMapper().writeValue(out, mailFolders);
            out.close();
        } catch (IOException e) {
            logger.error("Unable to save db", e);
        }
    }

    @Override
    public List<MailFolder> getFolderMessages(MailFolder mailFolder) {
        return null;
    }
}
