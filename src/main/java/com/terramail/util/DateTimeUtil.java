package com.terramail.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date/time operations.
 */
public class DateTimeUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    private DateTimeUtil() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    public static String formatDateOnly(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_ONLY_FORMATTER);
    }

    public static String formatDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static String formatShort(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter;
        LocalDateTime now = now();
        if (dateTime.toLocalDate().equals(now().toLocalDate())) {
            formatter = DateTimeFormatter.ofPattern("HH:mm");
        } else if (dateTime.getYear() == now().getYear()) {
            formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm");
        } else {
            formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        }
        return dateTime.format(formatter);
    }

    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            // Try ISO format first
            return LocalDateTime.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            try {
                // Try default format
                return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
            } catch (DateTimeParseException e2) {
                try {
                    // Try common email date formats
                    return LocalDateTime.parse(
                            parseEmailDate(dateTimeStr),
                            DEFAULT_FORMATTER
                    );
                } catch (DateTimeParseException e3) {
                    return null;
                }
            }
        }
    }

    /**
     * Parse common email date format: "Mon, 01 Jan 2024 12:00:00 +0000 (UTC)"
     */
    private static String parseEmailDate(String dateStr) {
        if (dateStr == null) return null;

        // Remove timezone info for simplicity
        int parenIndex = dateStr.indexOf('(');
        if (parenIndex > 0) {
            dateStr = dateStr.substring(0, parenIndex).trim();
        }

        // Remove leading/trailing commas
        dateStr = dateStr.replaceAll("^,|,$", "").trim();

        return dateStr;
    }

    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId zone) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(zone);
    }

    public static LocalDateTime fromZonedDateTime(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        return zonedDateTime.toLocalDateTime();
    }

    public static Duration between(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return Duration.ZERO;
        }
        return Duration.between(start, end);
    }

    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        return between(start, end).getSeconds();
    }
}
