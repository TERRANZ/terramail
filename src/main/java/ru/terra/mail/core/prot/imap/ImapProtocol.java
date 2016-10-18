package ru.terra.mail.core.prot.imap;

import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.storage.entity.MailFolder;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by terranz on 18.10.16.
 */
public class ImapProtocol extends AbstractMailProtocol {
    @Override
    public void login(String user, String pass, String server) throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(props, null);
        this.store = session.getStore();
        store.connect(server, user, pass);
    }

    @Override
    public List<MailFolder> listFolders() throws MessagingException {
        return Arrays.stream(store.getPersonalNamespaces()).map(f -> {
            MailFolder mailFolder = new MailFolder();
            mailFolder.setName(f.getName());
            return mailFolder;
        }).collect(Collectors.toList());
    }
}
