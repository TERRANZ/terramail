package com.terramail.util;

import java.util.regex.Pattern;

/**
 * Utility class for mail-related operations.
 */
public class MailUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private MailUtil() {
    }

    /**
     * Validates an email address format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Sanitizes a subject line for display.
     */
    public static String sanitizeSubject(String subject) {
        if (subject == null || subject.isEmpty()) {
            return "(No Subject)";
        }
        // Remove control characters but preserve printable text
        return subject.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
    }

    /**
     * Truncates a string to the specified length.
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        if (maxLength <= 3) {
            return str.substring(0, maxLength);
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Formats bytes to human-readable size.
     */
    public static String formatSize(long bytes) {
        if (bytes <= 0) {
            return "0 B";
        }
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        digitGroups = Math.min(digitGroups, units.length - 1);
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * Extracts the display name from a recipient string.
     * Format: "Display Name" <email> or email
     */
    public static String extractDisplayName(String recipient) {
        if (recipient == null || recipient.isEmpty()) {
            return "";
        }

        recipient = recipient.trim();

        // Check for "Name" <email> format
        int quoteStart = recipient.indexOf('"');
        if (quoteStart >= 0) {
            int quoteEnd = recipient.indexOf('"', quoteStart + 1);
            if (quoteEnd > quoteStart) {
                return recipient.substring(quoteStart + 1, quoteEnd);
            }
        }

        // Check for <email> format
        int angleStart = recipient.indexOf('<');
        if (angleStart > 0) {
            return recipient.substring(0, angleStart).trim();
        }

        // Just email or plain text
        int angleBracket = recipient.indexOf('<');
        if (angleBracket >= 0) {
            return recipient.substring(0, angleBracket).trim();
        }

        // Return as-is if no special format detected
        return recipient;
    }

    /**
     * Extracts the email address from a recipient string.
     * Format: "Name" <email> or <email> or email
     */
    public static String extractEmailAddress(String recipient) {
        if (recipient == null || recipient.isEmpty()) {
            return "";
        }

        recipient = recipient.trim();

        int angleStart = recipient.lastIndexOf('<');
        int angleEnd = recipient.lastIndexOf('>');

        if (angleStart >= 0 && angleEnd > angleStart) {
            return recipient.substring(angleStart + 1, angleEnd).trim();
        }

        // Return as-is if it looks like an email
        if (isValidEmail(recipient)) {
            return recipient;
        }

        // Try to extract email from "Name <email>" format
        if (angleStart >= 0) {
            int prevAngleEnd = recipient.lastIndexOf('>', angleStart - 1);
            if (prevAngleEnd >= 0) {
                return recipient.substring(prevAngleEnd + 1).trim();
            }
        }

        return recipient;
    }

    /**
     * Creates a simple message ID.
     */
    public static String createMessageId() {
        return String.format("<%s@terramail>", java.util.UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * Escapes special characters for SQL LIKE clauses.
     */
    public static String escapeLike(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
