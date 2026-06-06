package com.terramail.service;

import com.terramail.model.AccountSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseServiceTest {

    @Test
    void testInitializeWithInvalidUrl() {
        DatabaseService service = new DatabaseService();
        AccountSettings settings = new AccountSettings();
        settings.setDbUrl("jdbc:mysql://invalid.host:3306/terramail");
        settings.setDbUser("root");
        settings.setDbPassword("password");

        boolean result = service.initialize(settings);
        assertFalse(result);
    }

    @Test
    void testInitializeWithValidH2Url() {
        DatabaseService service = new DatabaseService();
        AccountSettings settings = new AccountSettings();
        settings.setDbUrl("jdbc:h2:mem:dbtest;DB_CLOSE_DELAY=-1");
        settings.setDbUser("sa");
        settings.setDbPassword("");

        boolean result = service.initialize(settings);
        assertTrue(result);
        try {
            assertTrue(service.isConnected());
        } finally {
            service.close();
            assertFalse(service.isConnected());
        }
    }

    @Test
    void testClose() {
        DatabaseService service = new DatabaseService();
        AccountSettings settings = new AccountSettings();
        settings.setDbUrl("jdbc:h2:mem:dbtest2;DB_CLOSE_DELAY=-1");
        settings.setDbUser("sa");
        settings.setDbPassword("");

        service.initialize(settings);
        service.close();
        assertFalse(service.isConnected());
    }

    @Test
    void testIsConnectedBeforeInit() {
        DatabaseService service = new DatabaseService();
        assertFalse(service.isConnected());
    }

    @Test
    void testInitializeWithNullUrl() {
        DatabaseService service = new DatabaseService();
        AccountSettings settings = new AccountSettings();
        settings.setDbUrl(null);
        settings.setDbUser(null);
        settings.setDbPassword(null);

        assertFalse(service.initialize(settings));
    }
}
