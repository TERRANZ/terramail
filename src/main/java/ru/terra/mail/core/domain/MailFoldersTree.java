package ru.terra.mail.core.domain;

import com.beust.jcommander.internal.Lists;

import java.util.List;

public class MailFoldersTree {
    private MailFolder currentFolder;
    private List<MailFoldersTree> childrens = Lists.newArrayList();

    public MailFoldersTree(final MailFolder currentFolder) {
        this.currentFolder = currentFolder;
    }

    public MailFolder getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(final MailFolder currentFolder) {
        this.currentFolder = currentFolder;
    }

    public List<MailFoldersTree> getChildrens() {
        return childrens;
    }

    public void setChildrens(final List<MailFoldersTree> childrens) {
        this.childrens = childrens;
    }
}
