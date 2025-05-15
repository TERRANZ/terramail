package ru.terra.mail.core.prot.imap;

import lombok.val;
import ru.terra.mail.core.prot.AbstractMailProtocol;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * Created by terranz on 18.10.16.
 */
public class ImapProtocol extends AbstractMailProtocol {
    @Override
    public void login(final String user, final String pass, String server) throws MessagingException {
        val props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.mime.ignoreunknownencoding", "true");//ignore Unknown encoding: 8-bit exception
        val session = Session.getInstance(props, null);
        this.store = session.getStore();
        store.connect(server, user, pass);
    }
}
