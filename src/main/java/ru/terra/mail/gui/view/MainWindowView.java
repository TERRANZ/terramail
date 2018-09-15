package ru.terra.mail.gui.view;

import ru.terra.mail.core.domain.MailFoldersTree;

public interface MainWindowView {

    void setStatusText(String status);

    void setFoldersTreeRoot(MailFoldersTree treeRoot);

    void runOnUIThread(Runnable runnable);
}
