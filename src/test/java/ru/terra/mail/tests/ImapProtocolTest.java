package ru.terra.mail.tests;

import junit.framework.TestCase;
import ru.terra.mail.TestConstants;
import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.core.prot.imap.ImapProtocol;
import ru.terra.mail.storage.entity.MailFolder;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Created by terranz on 18.10.16.
 */
public class ImapProtocolTest extends TestCase {
    public void testLoginAndList() throws MessagingException, GeneralSecurityException {
        AbstractMailProtocol imapProtocol = new ImapProtocol();
        imapProtocol.login(TestConstants.TEST_USER, TestConstants.TEST_PASS, TestConstants.TEST_SERV);
        List<MailFolder> mailFolderList = imapProtocol.listFolders();
        System.out.println(mailFolderList.size());
    }
}
