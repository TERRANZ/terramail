package ru.terra.mail.core.prot.imap;

import com.sun.mail.util.MailSSLSocketFactory;
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
    public void login(String user, String pass, String server) throws MessagingException, GeneralSecurityException {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.socketFactory", sf);
        props.put("mail.mime.ignoreunknownencoding", "true");//ignore Unknown encoding: 8-bit exception
        Session session = Session.getInstance(props, null);
        this.store = session.getStore();
        store.connect(server, user, pass);
    }
}
