package ru.terra.mail.core.domain;

import com.beust.jcommander.internal.Lists;
import lombok.Data;

import java.util.List;

@Data
public class MailFoldersTree {
    private MailFolder currentFolder;
    private List<MailFoldersTree> childrens = Lists.newArrayList();

    public MailFoldersTree(final MailFolder currentFolder) {
        this.currentFolder = currentFolder;
    }
}
