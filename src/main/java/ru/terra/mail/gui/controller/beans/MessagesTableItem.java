package ru.terra.mail.gui.controller.beans;

import ru.terra.mail.storage.entity.MailMessage;

public class MessagesTableItem {
	private String subject, date;

	public MessagesTableItem(MailMessage message) {
		this.subject = message.getSubject();
		this.date = message.getCreateDate().toString();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
