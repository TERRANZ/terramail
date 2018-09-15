package ru.terra.mail.config;

import ru.terra.mail.core.prot.AbstractMailProtocol;
import ru.terra.mail.core.prot.imap.ImapProtocol;
import ru.terra.mail.core.prot.pop.PopProtocol;

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
        if (StartUpParameters.getInstance().getProtocol().equals("imap")) {
            return new ImapProtocol();
        } else {
            return new PopProtocol();
        }
    }
}
