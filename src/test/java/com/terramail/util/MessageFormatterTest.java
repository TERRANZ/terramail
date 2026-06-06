package com.terramail.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageFormatterTest {

    @Test
    void testTruncateShorter() {
        assertEquals("Hello", MessageFormatter.truncate("Hello", 10));
    }

    @Test
    void testTruncateExactLength() {
        assertEquals("Hello", MessageFormatter.truncate("Hello", 5));
    }

    @Test
    void testTruncateLonger() {
        String result = MessageFormatter.truncate("Hello World", 10);
        assertEquals("Hello Wo...", result);
    }

    @Test
    void testTruncateNull() {
        assertEquals("", MessageFormatter.truncate(null, 10));
    }

    @Test
    void testNormalizeNewlinesCrlf() {
        assertEquals("Line1\nLine2", MessageFormatter.normalizeNewlines("Line1\r\nLine2"));
    }

    @Test
    void testNormalizeNewlinesCr() {
        assertEquals("Line1\nLine2", MessageFormatter.normalizeNewlines("Line1\rLine2"));
    }

    @Test
    void testNormalizeNewlinesLf() {
        assertEquals("Line1\nLine2", MessageFormatter.normalizeNewlines("Line1\nLine2"));
    }

    @Test
    void testNormalizeNewlinesNull() {
        assertEquals("", MessageFormatter.normalizeNewlines(null));
    }

    @Test
    void testEscapeHtml() {
        String input = "5 < 10 & 3 > 1";
        String result = MessageFormatter.escapeHtml(input);
        assertEquals("5 &lt; 10 &amp; 3 &gt; 1", result);
    }

    @Test
    void testEscapeHtmlNull() {
        assertEquals("", MessageFormatter.escapeHtml(null));
    }

    @Test
    void testEscapeHtmlEmpty() {
        assertEquals("", MessageFormatter.escapeHtml(""));
    }

    @Test
    void testIsValidEmailValid() {
        assertTrue(MessageFormatter.isValidEmailAddress("user@example.com"));
    }

    @Test
    void testIsValidEmailMissingAt() {
        assertFalse(MessageFormatter.isValidEmailAddress("userexample.com"));
    }

    @Test
    void testIsValidEmailMissingDomain() {
        assertFalse(MessageFormatter.isValidEmailAddress("user@"));
    }

    @Test
    void testIsValidEmailEmpty() {
        assertFalse(MessageFormatter.isValidEmailAddress(""));
    }

    @Test
    void testIsValidEmailNull() {
        assertFalse(MessageFormatter.isValidEmailAddress(null));
    }

    @Test
    void testWrapLines() {
        String input = "Hello World This is a test";
        String result = MessageFormatter.wrapLines(input, 10);
        assertTrue(result.contains("\n"));
    }

    @Test
    void testWrapLinesEmpty() {
        assertEquals("", MessageFormatter.wrapLines("", 10));
    }

    @Test
    void testWrapLinesNull() {
        assertEquals("", MessageFormatter.wrapLines(null, 10));
    }
}
