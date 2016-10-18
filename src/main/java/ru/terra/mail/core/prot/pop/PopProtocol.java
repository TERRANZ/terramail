package ru.terra.mail.core.prot.pop;

import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.storage.entity.MailFolder;

import javax.mail.MessagingException;
import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
public class PopProtocol extends AbstractMailProtocol {

    @Override
    public void login(String user, String pass, String server) throws MessagingException {

    }

    @Override
    public List<MailFolder> listFolders() throws MessagingException {
        return null;
    }
}
