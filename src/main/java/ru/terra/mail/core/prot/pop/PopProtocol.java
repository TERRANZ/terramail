package ru.terra.mail.core.prot.pop;

import lombok.val;
import ru.terra.mail.core.prot.AbstractMailProtocol;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.Properties;

/**
 * Created by terranz on 18.10.16.
 */
public class PopProtocol extends AbstractMailProtocol {

    @Override
    public void login(String user, String pass, String server) throws MessagingException {
        val props = new Properties();
        props.setProperty("mail.store.protocol", "pop3");
        props.put("mail.mime.ignoreunknownencoding", "true");//ignore Unknown encoding: 8-bit exception
        val session = Session.getInstance(props, null);
        this.store = session.getStore();
        store.connect(server, user, pass);
    }

}
