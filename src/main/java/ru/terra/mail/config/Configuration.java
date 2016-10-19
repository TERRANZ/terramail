package ru.terra.mail.config;

import ru.terra.mail.core.AbstractMailProtocol;
import ru.terra.mail.core.prot.imap.ImapProtocol;

/**
 * Created by terranz on 19.10.16.
 */
public class Configuration {
    public static Configuration instance = new Configuration();

    private Configuration() {
    }

    public static Configuration getInstance() {
        return instance;
    }

    public AbstractMailProtocol getMailProtocol() {
        return new ImapProtocol();
    }
}
