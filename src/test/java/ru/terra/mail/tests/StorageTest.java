package ru.terra.mail.tests;

import junit.framework.TestCase;
import ru.terra.mail.TestConstants;
import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.core.prot.imap.ImapProtocol;
import ru.terra.mail.storage.StorageSingleton;
import ru.terra.mail.storage.entity.MailFolder;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Created by terranz on 19.10.16.
 */
public class StorageTest extends TestCase {
    public void testJsonStorage() throws GeneralSecurityException, MessagingException {
        AbstractMailProtocol imapProtocol = new ImapProtocol();
        imapProtocol.login(TestConstants.TEST_USER, TestConstants.TEST_PASS, TestConstants.TEST_SERV);
        List<MailFolder> mailFolderList = imapProtocol.listFolders();
        StorageSingleton.getInstance().getStorage().storeFolders(mailFolderList);
        List<MailFolder> folders = StorageSingleton.getInstance().getStorage().getRootFolders();
        System.out.println(folders.size());
    }
}
