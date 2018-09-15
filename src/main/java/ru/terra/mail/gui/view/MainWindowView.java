package ru.terra.mail.gui.view;

import ru.terra.mail.core.domain.MailFoldersTree;
import ru.terra.mail.core.domain.MailMessage;

import java.util.Set;

public interface MainWindowView {

    void setStatusText(String status);

    void setFoldersTreeRoot(MailFoldersTree treeRoot);

    void runOnUIThread(Runnable runnable);

    void showMessagesList(Set<MailMessage> storedMessages);
}
