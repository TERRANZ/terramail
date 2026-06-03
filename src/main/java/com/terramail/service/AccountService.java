package com.terramail.service;

import com.terramail.model.Account;
import com.terramail.repository.AccountRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing email accounts.
 */
public class AccountService {
    private static final Logger logger = Logger.getLogger(AccountService.class.getName());
    private final AccountRepository accountRepository;

    public AccountService() {
        this.accountRepository = new AccountRepository();
    }

    /**
     * Creates a new account.
     */
    public Account createAccount(Account account) throws SQLException {
        if (account == null || account.getEmail() == null || account.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Account email cannot be null or empty");
        }

        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new SQLException("Account with email " + account.getEmail() + " already exists");
        }

        return accountRepository.create(account);
    }

    /**
     * Updates an existing account.
     */
    public void updateAccount(Account account) throws SQLException {
        if (account == null || account.getId() <= 0) {
            throw new IllegalArgumentException("Invalid account");
        }

        accountRepository.update(account);
    }

    /**
     * Deletes an account.
     */
    public void deleteAccount(long accountId) throws SQLException {
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new SQLException("Account not found: " + accountId);
        }

        accountRepository.delete(accountId);
        logger.log(Level.INFO, "Deleted account: {0}", account.getEmail());
    }

    /**
     * Finds an account by ID.
     */
    public Account findAccountById(long accountId) throws SQLException {
        return accountRepository.findById(accountId);
    }

    /**
     * Finds an account by email.
     */
    public Account findAccountByEmail(String email) throws SQLException {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return accountRepository.findByEmail(email);
    }

    /**
     * Gets all accounts.
     */
    public List<Account> getAllAccounts() throws SQLException {
        return accountRepository.findAll();
    }

    /**
     * Validates account configuration.
     */
    public ValidationResult validateAccount(Account account) {
        ValidationResult result = new ValidationResult();

        if (account.getName() == null || account.getName().isEmpty()) {
            result.addError("name", "Account name is required");
        }

        if (account.getEmail() == null || account.getEmail().isEmpty()) {
            result.addError("email", "Email is required");
        } else if (!account.getEmail().contains("@")) {
            result.addError("email", "Invalid email format");
        }

        if (account.getImapHost() == null || account.getImapHost().isEmpty()) {
            result.addError("imapHost", "IMAP server is required");
        }

        if (account.getImapUser() == null || account.getImapUser().isEmpty()) {
            result.addError("imapUser", "IMAP username is required");
        }

        if (account.getSmtpHost() == null || account.getSmtpHost().isEmpty()) {
            result.addError("smtpHost", "SMTP server is required");
        }

        return result;
    }

    /**
     * Result of account validation.
     */
    public static class ValidationResult {
        private final java.util.Map<String, String> errors = new java.util.HashMap<>();

        public void addError(String field, String message) {
            errors.put(field, message);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public java.util.Map<String, String> getErrors() {
            return errors;
        }

        @Override
        public String toString() {
            return "ValidationResult{valid=" + isValid() + ", errors=" + errors + "}";
        }
    }
}
