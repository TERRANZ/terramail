package com.terramail.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AppStateTest {

    private AppState appState;

    @BeforeEach
    void setUp() {
        appState = new AppState();
    }

    @Test
    void testInitialState() {
        assertEquals(AppState.Status.OFFLINE, appState.getStatus());
        assertFalse(appState.isOnline());
        assertFalse(appState.isSyncing());
        assertEquals("Ready", appState.getSyncStatus());
        assertTrue(appState.isEmpty());
    }

    @Test
    void testSetOnlineStatus() {
        appState.setStatus(AppState.Status.ONLINE);
        assertTrue(appState.isOnline());
        assertEquals(AppState.Status.ONLINE, appState.getStatus());
    }

    @Test
    void testSetOfflineStatus() {
        appState.setStatus(AppState.Status.ONLINE);
        appState.setStatus(AppState.Status.OFFLINE);
        assertFalse(appState.isOnline());
    }

    @Test
    void testSetSyncing() {
        appState.setSyncing(true);
        assertTrue(appState.isSyncing());
        appState.setSyncing(false);
        assertFalse(appState.isSyncing());
    }

    @Test
    void testSyncStatus() {
        appState.setSyncStatus("Syncing...");
        assertEquals("Syncing...", appState.getSyncStatus());
    }

    @Test
    void testSetActiveAccountId() {
        appState.setActiveAccountId(42L);
        assertEquals(42L, appState.getActiveAccountId());
    }

    @Test
    void testSetActiveFolderId() {
        appState.setActiveFolderId(100L);
        assertEquals(100L, appState.getActiveFolderId());
    }

    @Test
    void testQueueMessage() {
        assertTrue(appState.isEmpty());
        appState.addQueuedMessage("message1");
        assertEquals(1, appState.getQueuedMessageCount());
        assertFalse(appState.isEmpty());
    }

    @Test
    void testQueueMultipleMessages() {
        appState.addQueuedMessage("msg1");
        appState.addQueuedMessage("msg2");
        appState.addQueuedMessage("msg3");
        assertEquals(3, appState.getQueuedMessageCount());
        List<String> queued = appState.getQueuedMessages();
        assertEquals(3, queued.size());
    }

    @Test
    void testRemoveQueuedMessage() {
        appState.addQueuedMessage("msg1");
        appState.addQueuedMessage("msg2");
        boolean removed = appState.removeQueuedMessage("msg1");
        assertTrue(removed);
        assertEquals(1, appState.getQueuedMessageCount());
    }

    @Test
    void testClearQueuedMessages() {
        appState.addQueuedMessage("msg1");
        appState.addQueuedMessage("msg2");
        appState.clearQueuedMessages();
        assertTrue(appState.isEmpty());
        assertEquals(0, appState.getQueuedMessageCount());
    }

    @Test
    void testStatusListener() {
        AtomicReference<String> captured = new AtomicReference<>();
        appState.addStatusListener(captured::set);

        appState.setSyncStatus("Syncing...");
        assertEquals("Syncing...", captured.get());
    }

    @Test
    void testStatusListenerTyped() {
        AtomicReference<AppState.Status> captured = new AtomicReference<>();
        appState.addStatusChangeListener(captured::set);

        appState.setStatus(AppState.Status.ONLINE);
        assertEquals(AppState.Status.ONLINE, captured.get());
    }

    @Test
    void testNullStatusThrows() {
        assertThrows(NullPointerException.class, () -> appState.setStatus(null));
    }
}
