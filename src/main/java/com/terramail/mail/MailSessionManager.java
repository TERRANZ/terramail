package com.terramail.mail;

import com.terramail.config.AppConfig;
import com.terramail.model.Account;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages mail sessions for accounts.
 */
public class MailSessionManager {
    private static final Logger logger = Logger.getLogger(MailSessionManager.class.getName());
    private final Map<Long, Session> accountSessions;

    public MailSessionManager() {
        this.accountSessions = new WeakHashMap<>();
    }

    /**
     * Creates a JavaMail Session for an account's IMAP configuration.
     */
    public Session createImapSession(Account account) {
        Properties props = getImapProperties(account);
        return Session.getInstance(props, null);
    }

    /**
     * Creates a JavaMail Session for an account's SMTP configuration.
     */
    public Session createSmtpSession(Account account) {
        Properties props = getSmtpProperties(account);
        return Session.getInstance(props, null);
    }

    /**
     * Creates an IMAP Store connection for an account.
     */
    public Store createImapStore(Account account) throws MessagingException {
        Session session = createImapSession(account);
        Store store = session.getStore("imap");
        String password = account.getImapPassword();

        // Check if using OAuth2 or plain password
        if (password.startsWith("oauth2:")) {
            // OAuth2 authentication would go here
            throw new MessagingException("OAuth2 authentication not yet implemented");
        } else {
            store.connect(account.getImapHost(), account.getImapPort(),
                    account.getImapUser(), password);
        }

        logger.log(Level.INFO, "Connected to IMAP server: {0}:{1} as {2}",
                new Object[]{account.getImapHost(), account.getImapPort(), account.getImapUser()});

        return store;
    }

    /**
     * Creates an SMTP Transport connection for an account.
     */
    public Transport createSmtpTransport(Account account) throws MessagingException {
        Session session = createSmtpSession(account);
        Transport transport = session.getTransport("smtp");
        String password = account.getSmtpPassword();

        if (password.startsWith("oauth2:")) {
            throw new MessagingException("OAuth2 authentication not yet implemented");
        } else {
            transport.connect(account.getSmtpHost(), account.getSmtpPort(),
                    account.getSmtpUser(), password);
        }

        logger.log(Level.INFO, "Connected to SMTP server: {0}:{1} as {2}",
                new Object[]{account.getSmtpHost(), account.getSmtpPort(), account.getSmtpUser()});

        return transport;
    }

    /**
     * Caches a session for an account.
     */
    public void cacheSession(long accountId, Session session) {
        accountSessions.put(accountId, session);
    }

    /**
     * Gets a cached session for an account.
     */
    public Session getCachedSession(long accountId) {
        return accountSessions.get(accountId);
    }

    /**
     * Removes a cached session for an account.
     */
    public void removeSession(long accountId) {
        accountSessions.remove(accountId);
    }

    /**
     * Clears all cached sessions.
     */
    public void clearSessions() {
        accountSessions.clear();
    }

    private Properties getImapProperties(Account account) {
        Properties props = new Properties();
        props.put("mail.imap.host", account.getImapHost());
        props.put("mail.imap.port", account.getImapPort());
        props.put("mail.imap.ssl.enable", AppConfig.isImapSslEnabled());
        props.put("mail.imap.timeout", AppConfig.getImapTimeout());
        props.put("mail.imap.connectiontimeout", AppConfig.getImapTimeout());
        props.put("mail.imap.timeout", AppConfig.getImapTimeout());
        props.put("mail.imap.writetimeout", AppConfig.getImapTimeout());

        // Common IMAP settings
        props.put("mail.imap.ssl.trust", account.getImapHost());
        props.put("mail.imap.auth.mechanisms", "LOGIN");

        return props;
    }

    private Properties getSmtpProperties(Account account) {
        Properties props = new Properties();
        props.put("mail.smtp.host", account.getSmtpHost());
        props.put("mail.smtp.port", account.getSmtpPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", AppConfig.isSmtpStarttlsEnabled());
        props.put("mail.smtp.timeout", AppConfig.getSmtpTimeout());
        props.put("mail.smtp.connectiontimeout", AppConfig.getSmtpTimeout());
        props.put("mail.smtp.writetimeout", AppConfig.getSmtpTimeout());

        // SSL/TLS settings
        if (account.getSmtpPort() == 465) {
            props.put("mail.smtp.ssl.enable", "true");
        } else {
            props.put("mail.smtp.ssl.enable", "false");
        }

        return props;
    }
}
