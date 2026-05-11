package com.cvmanager.profile;

import com.cvmanager.controllers.ProfileServlet;
import com.cvmanager.utils.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProfileServletPasswordChangeTest {
    @Test
    void validatePasswordChangeRequiresCurrentPasswordToMatchStoredHash() {
        String hash = PasswordUtil.hashPassword("Actual123");

        String error = ProfileServlet.validatePasswordChange("wrong", "Nuevo123", "Nuevo123", hash);

        assertEquals("La contrasena actual no coincide.", error);
    }

    @Test
    void validatePasswordChangeAcceptsMatchingCurrentPasswordAndStrongConfirmation() {
        String hash = PasswordUtil.hashPassword("Actual123");

        String error = ProfileServlet.validatePasswordChange("Actual123", "Nuevo123", "Nuevo123", hash);

        assertNull(error);
    }

    @Test
    void validatePasswordChangeRequiresNewPasswordConfirmation() {
        String hash = PasswordUtil.hashPassword("Actual123");

        String error = ProfileServlet.validatePasswordChange("Actual123", "Nuevo123", "Otro1234", hash);

        assertEquals("La confirmacion de contrasena no coincide.", error);
    }
}
