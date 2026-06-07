package com.terramail.model;

import java.io.Serializable;
import java.util.Objects;

public class Folder implements Serializable {

    public enum Type {
        INBOX, SENT, DRAFTS, TRASH, CUSTOM
    }

    private long id;
    private long accountId;
    private String name;
    private Type type;
    private long parentFolderId;
    private String imapPath;

    public Folder() {
        this.parentFolderId = 0;
        this.imapPath = "";
    }

    public Folder(long accountId, String name, Type type) {
        this.accountId = accountId;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.parentFolderId = 0;
        this.imapPath = "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(long parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public String getImapPath() {
        return imapPath;
    }

    public void setImapPath(String imapPath) {
        this.imapPath = imapPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return id == folder.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Folder{id=" + id + ", name='" + name + "', type=" + type + ", parentFolderId=" + parentFolderId + "}";
    }
}
