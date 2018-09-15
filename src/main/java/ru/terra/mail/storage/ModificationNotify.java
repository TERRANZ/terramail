package ru.terra.mail.storage;

import ru.terra.mail.core.domain.MailFolder;

public interface ModificationNotify {
    void onModified(final MailFolder mailFolder);
}
