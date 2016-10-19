package ru.terra.mail.storage.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.entity.MailFolder;
import ru.terra.mail.storage.entity.MailMessage;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
public class JsonStorage extends AbstractStorage {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public JsonStorage() {
		File folder = new File("jsonstorage");
		if (!folder.exists())
			folder.mkdirs();
	}

	@Override
	public List<MailFolder> getRootFolders() {
		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream("jsonstorage/folders.json"), Charset.forName("UTF-8"));
			return new ObjectMapper().readValue(reader, new TypeReference<List<MailFolder>>() {
			});
		} catch (Exception e) {
//			logger.error("Exception while reading db", e);
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
			Writer out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream("jsonstorage/folders.json"), "UTF-8"));
			new ObjectMapper().writeValue(out, mailFolders);
			out.close();
		} catch (IOException e) {
			logger.error("Unable to save db", e);
		}
	}

	@Override
	public List<MailMessage> getFolderMessages(MailFolder mailFolder) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(
					new FileInputStream("jsonstorage/messages_" + mailFolder.getFullName() + ".json"),
					Charset.forName("UTF-8"));
			return new ObjectMapper().readValue(reader, new TypeReference<List<MailMessage>>() {
			});
		} catch (Exception e) {
//			logger.error("Exception while reading db", e);
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
	public void storeFolderMessages(MailFolder mailFolder, List<MailMessage> messages) {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("jsonstorage/messages_" + mailFolder.getFullName() + ".json"), "UTF-8"));
			new ObjectMapper().writeValue(out, messages);
			out.close();
		} catch (IOException e) {
			logger.error("Unable to save db", e);
		}
	}
}
