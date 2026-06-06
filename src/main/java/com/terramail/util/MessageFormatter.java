package com.terramail.util;

import java.util.Objects;

public class MessageFormatter {

    public static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    public static String normalizeNewlines(String text) {
        if (text == null) return "";
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }

    public static String wrapLines(String text, int lineWidth) {
        if (text == null || text.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        int lineStart = 0;
        while (lineStart < text.length()) {
            int lineEnd = Math.min(lineStart + lineWidth, text.length());
            if (lineEnd < text.length()) {
                int spacePos = text.lastIndexOf(' ', lineEnd);
                if (spacePos > lineStart) {
                    lineEnd = spacePos;
                }
            }
            result.append(text, lineStart, lineEnd);
            result.append('\n');
            lineStart = lineEnd + 1;
        }
        return result.toString();
    }

    public static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    public static boolean isValidEmailAddress(String email) {
        if (email == null || email.isEmpty()) return false;
        int atIndex = email.lastIndexOf('@');
        if (atIndex <= 0 || atIndex >= email.length() - 1) return false;
        String domain = email.substring(atIndex + 1);
        int dotIndex = domain.lastIndexOf('.');
        return dotIndex > 0 && dotIndex < domain.length() - 1;
    }
}
