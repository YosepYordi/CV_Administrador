package com.cvmanager.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test
    void hashPasswordVerifiesOriginalAndRejectsWrongPassword() {
        String hash = PasswordUtil.hashPassword("admin123");
        assertTrue(PasswordUtil.verifyPassword("admin123", hash));
        assertFalse(PasswordUtil.verifyPassword("wrong123", hash));
    }
}
