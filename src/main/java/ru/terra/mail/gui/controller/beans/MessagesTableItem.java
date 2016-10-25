package ru.terra.mail.gui.controller.beans;

import java.util.Date;

import ru.terra.mail.storage.domain.MailMessage;

/**
 * Created by terranz on 19.10.16.
 */
public class MessagesTableItem {
	private MailMessage message;

	public MessagesTableItem(MailMessage message) {
		this.message = message;
	}

	public String getSubject() {
		return message.getSubject();
	}

	public Date getDate() {
		return message.getCreateDate();
	}

	public MailMessage getMessage() {
		return message;
	}
}
