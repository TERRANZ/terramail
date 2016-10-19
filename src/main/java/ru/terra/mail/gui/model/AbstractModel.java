package ru.terra.mail.gui.model;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.terra.mail.config.Configuration;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.storage.AbstractStorage;
import ru.terra.mail.storage.StorageSingleton;

/**
 * Created by terranz on 19.10.16.
 */
public abstract class AbstractModel<Bean> {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected AbstractStorage storage = StorageSingleton.getInstance().getStorage();
	protected AbstractMailProtocol protocol = Configuration.getInstance().getMailProtocol();

	protected void performLogin() throws GeneralSecurityException, MessagingException {
		protocol.login(StartUpParameters.getInstance().getUser(), StartUpParameters.getInstance().getPass(),
				StartUpParameters.getInstance().getServ());
	}
}
