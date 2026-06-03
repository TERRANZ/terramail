package com.terramail.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an email folder within an account.
 */
public class EmailFolder {
    private long id;
    private long accountId;
    private String name;
    private String path;
    private String uniqueIdPrefix;
    private int messageCount;
    private boolean isSubscribed;
    private boolean isSynched;
    private LocalDateTime lastSynched;
    private LocalDateTime createdAt;

    public EmailFolder() {
        this.id = 0;
        this.name = "";
        this.path = "";
        this.uniqueIdPrefix = "";
        this.messageCount = 0;
        this.isSubscribed = true;
        this.isSynched = false;
        this.lastSynched = null;
        this.createdAt = LocalDateTime.now();
    }

    public EmailFolder(long accountId, String name, String path) {
        this();
        this.accountId = accountId;
        this.name = name;
        this.path = path;
    }

    public EmailFolder(String name) {
        this();
        this.name = name;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUniqueIdPrefix() {
        return uniqueIdPrefix;
    }

    public void setUniqueIdPrefix(String uniqueIdPrefix) {
        this.uniqueIdPrefix = uniqueIdPrefix;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public boolean isSynched() {
        return isSynched;
    }

    public void setSynched(boolean synched) {
        isSynched = synched;
    }

    public LocalDateTime getLastSynched() {
        return lastSynched;
    }

    public void setLastSynched(LocalDateTime lastSynched) {
        this.lastSynched = lastSynched;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailFolder that = (EmailFolder) o;
        return accountId == that.accountId && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, name);
    }

    @Override
    public String toString() {
        return "EmailFolder{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", messageCount=" + messageCount +
                ", isSynched=" + isSynched +
                '}';
    }
}
