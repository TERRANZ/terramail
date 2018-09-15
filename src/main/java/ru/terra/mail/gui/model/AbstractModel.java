package ru.terra.mail.gui.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.terra.mail.config.MailConfiguration;
import ru.terra.mail.config.StartUpParameters;
import ru.terra.mail.core.prot.AbstractMailProtocol;
import ru.terra.mail.storage.AbstractStorage;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;

/**
 * Created by terranz on 19.10.16.
 */
@Component
public abstract class AbstractModel<Bean> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected AbstractMailProtocol protocol = MailConfiguration.getInstance().getMailProtocol();
    @Autowired
    private AbstractStorage storage;

    protected void performLogin() throws GeneralSecurityException, MessagingException {
        protocol.login(StartUpParameters.getInstance().getUser(), StartUpParameters.getInstance().getPass(),
                StartUpParameters.getInstance().getServ());
    }

    protected AbstractStorage getStorage() {
        return storage;
    }
}
