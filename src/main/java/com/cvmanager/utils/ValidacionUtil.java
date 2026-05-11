package com.cvmanager.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Optional;

public final class ValidacionUtil {
    private ValidacionUtil() {}

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return !isBlank(email) && EmailValidator.getInstance().isValid(email);
    }

    public static boolean isInstitutionalEmail(String email, String domain) {
        return isValidEmail(email) && !isBlank(domain) && email.toLowerCase().endsWith(domain.toLowerCase());
    }

    public static boolean isStrongPassword(String password) {
        if (isBlank(password) || password.length() < 8) return false;
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasLetter && hasDigit;
    }

    public static Optional<Long> parseLong(String value) {
        try { return Optional.of(Long.parseLong(value)); } catch (Exception ex) { return Optional.empty(); }
    }

    public static Optional<Integer> parseInteger(String value) {
        try { return Optional.of(Integer.parseInt(value)); } catch (Exception ex) { return Optional.empty(); }
    }

    public static String sanitize(String value) {
        if (value == null) return "";
        StringBuilder escaped = new StringBuilder();
        for (char ch : value.trim().toCharArray()) {
            switch (ch) {
                case '&':
                    escaped.append("&amp;");
                    break;
                case '<':
                    escaped.append("&lt;");
                    break;
                case '>':
                    escaped.append("&gt;");
                    break;
                case '"':
                    escaped.append("&quot;");
                    break;
                case '\'':
                    escaped.append("&#39;");
                    break;
                default:
                    escaped.append(ch);
                    break;
            }
        }
        return escaped.toString();
    }
}
