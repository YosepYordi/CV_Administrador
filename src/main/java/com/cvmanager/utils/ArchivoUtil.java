package com.cvmanager.utils;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class ArchivoUtil {
    private ArchivoUtil() {}

    public static void ensureDirectory(Path path) throws IOException {
        Files.createDirectories(path);
    }

    public static String getSafeFileName(Part part) {
        String submitted = part == null ? "" : part.getSubmittedFileName();
        String name = submitted == null ? "archivo" : submitted.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        if (slash >= 0) name = name.substring(slash + 1);
        return UUID.randomUUID() + "-" + name.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    public static String savePart(Part part, Path directory) throws IOException {
        if (part == null || part.getSize() <= 0) return null;
        ensureDirectory(directory);
        String fileName = getSafeFileName(part);
        part.write(directory.resolve(fileName).toString());
        return fileName;
    }
}
