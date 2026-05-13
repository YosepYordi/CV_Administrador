package com.cvmanager.listeners;

import com.cvmanager.utils.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppContextListenerSecurityTest {
    @Test
    void rejectsMissingAdminPassword() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> AppContextListener.requireConfiguredAdminPassword(null));

        assertTrue(error.getMessage().contains("admin.password"));
    }

    @Test
    void rejectsKnownDefaultAdminPassword() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> AppContextListener.requireConfiguredAdminPassword("admin123"));

        assertTrue(error.getMessage().contains("admin.password"));
    }

    @Test
    void rejectsPlaceholderAdminPassword() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> AppContextListener.requireConfiguredAdminPassword("change_me"));

        assertTrue(error.getMessage().contains("admin.password"));
    }

    @Test
    void acceptsConfiguredNonDefaultAdminPassword() {
        assertEquals("AdminClave123!",
                AppContextListener.requireConfiguredAdminPassword(" AdminClave123! "));
    }

    @Test
    void detectsExistingAdminHashUsingUnsafeDefaultPassword() {
        assertTrue(AppContextListener.usesUnsafeAdminPasswordHash(PasswordUtil.hashPassword("admin123")));
        assertFalse(AppContextListener.usesUnsafeAdminPasswordHash(PasswordUtil.hashPassword("AdminClave123!")));
    }
}
