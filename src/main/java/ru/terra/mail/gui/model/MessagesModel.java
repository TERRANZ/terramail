package ru.terra.mail.gui.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.Folder;
import javax.mail.MessagingException;

import ru.terra.mail.config.Configuration;
import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.StorageSingleton;
import ru.terra.mail.storage.entity.MailFolder;
import ru.terra.mail.storage.entity.MailMessage;

public class MessagesModel extends AbstractModel<MailMessage> {
	public List<MailMessage> getFolderMessages(MailFolder folder) {
		List<MailMessage> ret = storage.getFolderMessages(folder);
		if (ret == null) {
			ret = new ArrayList<>();
			try {
				folder.getFolder().open(Folder.READ_ONLY);
				ret.addAll((Collection<? extends MailMessage>) Arrays.stream(folder.getFolder().getMessages())
						.map(m -> new MailMessage(m, folder)).collect(Collectors.toList()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		merge(folder, ret);
		return ret;
	}

	protected void merge(MailFolder mailFolder, List<MailMessage> beans) {
		storage.storeFolderMessages(mailFolder, beans);
	}
}
