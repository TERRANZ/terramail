package ru.terra.mail.config;

import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.core.prot.imap.ImapProtocol;

/**
 * Created by terranz on 19.10.16.
 */
public class MailConfiguration {
    public static MailConfiguration instance = new MailConfiguration();

    private MailConfiguration() {
    }

    public static MailConfiguration getInstance() {
        return instance;
    }

    public AbstractMailProtocol getMailProtocol() {
        return new ImapProtocol();
    }
}
