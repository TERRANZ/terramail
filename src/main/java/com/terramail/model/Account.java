package com.terramail.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an email account configuration.
 */
public class Account {
    private long id;
    private String name;
    private String email;
    private String imapHost;
    private int imapPort;
    private String imapUser;
    private String imapPassword;
    private String smtpHost;
    private int smtpPort;
    private String smtpUser;
    private String smtpPassword;
    private boolean imapEnabled;
    private boolean smtpEnabled;
    private String displayName;
    private String organization;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Account() {
        this.id = 0;
        this.name = "";
        this.email = "";
        this.imapHost = "";
        this.imapPort = 993;
        this.imapUser = "";
        this.imapPassword = "";
        this.smtpHost = "";
        this.smtpPort = 587;
        this.smtpUser = "";
        this.smtpPassword = "";
        this.imapEnabled = true;
        this.smtpEnabled = true;
        this.displayName = "";
        this.organization = "";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Account(String name, String email, String imapHost, int imapPort,
                   String imapUser, String imapPassword, String smtpHost, int smtpPort,
                   String smtpUser, String smtpPassword) {
        this();
        this.name = name;
        this.email = email;
        this.imapHost = imapHost;
        this.imapPort = imapPort;
        this.imapUser = imapUser;
        this.imapPassword = imapPassword;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUser = smtpUser;
        this.smtpPassword = smtpPassword;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImapHost() {
        return imapHost;
    }

    public void setImapHost(String imapHost) {
        this.imapHost = imapHost;
    }

    public int getImapPort() {
        return imapPort;
    }

    public void setImapPort(int imapPort) {
        this.imapPort = imapPort;
    }

    public String getImapUser() {
        return imapUser;
    }

    public void setImapUser(String imapUser) {
        this.imapUser = imapUser;
    }

    public String getImapPassword() {
        return imapPassword;
    }

    public void setImapPassword(String imapPassword) {
        this.imapPassword = imapPassword;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUser() {
        return smtpUser;
    }

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public boolean isImapEnabled() {
        return imapEnabled;
    }

    public void setImapEnabled(boolean imapEnabled) {
        this.imapEnabled = imapEnabled;
    }

    public boolean isSmtpEnabled() {
        return smtpEnabled;
    }

    public void setSmtpEnabled(boolean smtpEnabled) {
        this.smtpEnabled = smtpEnabled;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImapHostWithDefaults() {
        return imapHost != null ? imapHost : "";
    }

    public String getSmtpHostWithDefaults() {
        return smtpHost != null ? smtpHost : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return email.equals(account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imapHost='" + imapHost + '\'' +
                ", smtpHost='" + smtpHost + '\'' +
                '}';
    }
}
