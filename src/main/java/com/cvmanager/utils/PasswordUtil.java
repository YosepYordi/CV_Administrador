package com.cvmanager.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {}
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null || hash.isBlank()) return false;
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }
}
