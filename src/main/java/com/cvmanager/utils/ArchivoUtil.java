package com.cvmanager.utils;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

public final class ArchivoUtil {
    private ArchivoUtil() {}

    public static void ensureDirectory(Path path) throws IOException {
        Files.createDirectories(path);
    }

    public static String getSafeFileName(Part part) {
        return UUID.randomUUID().toString();
    }

    public static String savePart(Part part, Path directory) throws IOException {
        return savePart(part, directory, "");
    }

    public static String savePart(Part part, Path directory, String extension) throws IOException {
        if (part == null || part.getSize() <= 0) return null;
        ensureDirectory(directory);
        String fileName = getSafeFileName(part) + normalizeExtension(extension);
        Path target = directory.resolve(fileName).normalize();
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    private static String normalizeExtension(String extension) {
        if (extension == null || extension.isBlank()) return "";
        String value = extension.trim();
        if (value.startsWith(".")) value = value.substring(1);
        if (!value.matches("[A-Za-z0-9]{1,10}")) {
            throw new IllegalArgumentException("Extension de archivo no permitida.");
        }
        return "." + value.toLowerCase(Locale.ROOT);
    }
}
