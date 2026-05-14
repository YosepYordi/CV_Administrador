package com.cvmanager.services;

import com.cvmanager.utils.ArchivoUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

public class Archivo_Servicio {
    public String savePhoto(Part part, ServletContext context) throws IOException {
        if (part == null || part.getSize() <= 0) return null;
        String saved = ArchivoUtil.savePart(part, uploadDirectory(context, "/uploads/photos", "photos"), photoExtension(part));
        return saved == null ? null : "/uploads/photos/" + saved;
    }

    public String saveCvPdf(Part part, ServletContext context) throws IOException {
        if (part == null || part.getSize() <= 0) return null;
        String saved = ArchivoUtil.savePart(part, uploadDirectory(context, "/uploads/cvs", "cvs"), pdfExtension(part));
        return saved == null ? null : "/uploads/cvs/" + saved;
    }

    public void deleteStoredFile(String url, ServletContext context, String contextPath) throws IOException {
        if (url == null || url.isBlank()) return;
        String webPath = url.trim();
        if (contextPath != null && !contextPath.isBlank() && webPath.startsWith(contextPath)) {
            webPath = webPath.substring(contextPath.length());
        }
        String folder;
        String fallback;
        if (webPath.startsWith("/uploads/photos/")) {
            folder = "/uploads/photos";
            fallback = "photos";
        } else if (webPath.startsWith("/uploads/cvs/")) {
            folder = "/uploads/cvs";
            fallback = "cvs";
        } else {
            return;
        }
        Path fileName = Path.of(webPath).getFileName();
        if (fileName == null) return;
        Files.deleteIfExists(uploadDirectory(context, folder, fallback).resolve(fileName).normalize());
        legacyUploadDirectory(context, folder).ifPresent(path -> {
            try {
                Files.deleteIfExists(path.resolve(fileName).normalize());
            } catch (IOException ignored) {
            }
        });
    }

    public Optional<Path> resolveStoredFile(String webPath, ServletContext context) {
        if (webPath == null || webPath.isBlank()) return Optional.empty();
        String normalized = normalizeUploadPath(webPath, context == null ? null : context.getContextPath());
        String folder;
        String fallback;
        if (normalized.startsWith("/uploads/photos/")) {
            folder = "/uploads/photos";
            fallback = "photos";
        } else if (normalized.startsWith("/uploads/cvs/")) {
            folder = "/uploads/cvs";
            fallback = "cvs";
        } else {
            return Optional.empty();
        }
        Path fileName = Path.of(normalized).getFileName();
        if (fileName == null) return Optional.empty();
        Path persistent = uploadDirectory(context, folder, fallback).resolve(fileName).normalize();
        if (Files.isRegularFile(persistent)) return Optional.of(persistent);
        Optional<Path> legacy = legacyUploadDirectory(context, folder).map(path -> path.resolve(fileName).normalize());
        return legacy.filter(Files::isRegularFile);
    }

    private Path uploadDirectory(ServletContext context, String webPath, String fallbackFolder) {
        Path persistentRoot = persistentUploadRoot(context);
        if (persistentRoot != null) return persistentRoot.resolve(fallbackFolder);
        return legacyUploadDirectory(context, webPath)
                .orElseGet(() -> Path.of(System.getProperty("java.io.tmpdir"), "cvmanager-uploads", fallbackFolder));
    }

    private Optional<Path> legacyUploadDirectory(ServletContext context, String webPath) {
        if (context == null) return Optional.empty();
        String realPath = context.getRealPath(webPath);
        if (realPath != null) return Optional.of(Path.of(realPath));
        String rootPath = context.getRealPath("/");
        if (rootPath != null && !rootPath.isBlank()) {
            String relativePath = webPath.startsWith("/") ? webPath.substring(1) : webPath;
            return Optional.of(Path.of(rootPath).resolve(relativePath));
        }
        return Optional.empty();
    }

    private Path persistentUploadRoot(ServletContext context) {
        String configured = context == null ? null : context.getInitParameter("cvmanager.uploadRoot");
        if (configured == null || configured.isBlank()) configured = System.getProperty("cvmanager.uploadRoot");
        if (configured == null || configured.isBlank()) configured = System.getenv("CVMANAGER_UPLOAD_ROOT");
        if (configured != null && !configured.isBlank()) return Path.of(configured.trim());
        String instanceRoot = System.getProperty("com.sun.aas.instanceRoot");
        if (instanceRoot != null && !instanceRoot.isBlank()) {
            return Path.of(instanceRoot).resolve("cvmanager-uploads");
        }
        return null;
    }

    private String normalizeUploadPath(String url, String contextPath) {
        String webPath = url.trim();
        if (contextPath != null && !contextPath.isBlank() && webPath.startsWith(contextPath)) {
            webPath = webPath.substring(contextPath.length());
        }
        return webPath;
    }

    private String photoExtension(Part part) throws IOException {
        byte[] header = readHeader(part, 8);
        String contentType = normalizedContentType(part);
        if (isJpeg(header) && contentTypeAllowed(contentType, "image/jpeg", "image/pjpeg")) return "jpg";
        if (isPng(header) && contentTypeAllowed(contentType, "image/png")) return "png";
        if (isGif(header) && contentTypeAllowed(contentType, "image/gif")) return "gif";
        throw new IllegalArgumentException("La foto debe ser un archivo JPG, PNG o GIF valido.");
    }

    private String pdfExtension(Part part) throws IOException {
        byte[] header = readHeader(part, 5);
        if (startsWith(header, "%PDF-".getBytes(StandardCharsets.US_ASCII))
                && contentTypeAllowed(normalizedContentType(part), "application/pdf")) {
            return "pdf";
        }
        throw new IllegalArgumentException("El CV debe ser un archivo PDF valido.");
    }

    private byte[] readHeader(Part part, int maxBytes) throws IOException {
        try (InputStream input = part.getInputStream()) {
            return input.readNBytes(maxBytes);
        }
    }

    private String normalizedContentType(Part part) {
        String contentType = part.getContentType();
        if (contentType == null) return "";
        int semicolon = contentType.indexOf(';');
        if (semicolon >= 0) contentType = contentType.substring(0, semicolon);
        return contentType.trim().toLowerCase(Locale.ROOT);
    }

    private boolean contentTypeAllowed(String actual, String... allowed) {
        if (actual == null || actual.isBlank()) return true;
        for (String value : allowed) {
            if (value.equals(actual)) return true;
        }
        return false;
    }

    private boolean isJpeg(byte[] header) {
        return header.length >= 3
                && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF;
    }

    private boolean isPng(byte[] header) {
        return startsWith(header, new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'});
    }

    private boolean isGif(byte[] header) {
        return startsWith(header, "GIF87a".getBytes(StandardCharsets.US_ASCII))
                || startsWith(header, "GIF89a".getBytes(StandardCharsets.US_ASCII));
    }

    private boolean startsWith(byte[] value, byte[] prefix) {
        if (value.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (value[i] != prefix[i]) return false;
        }
        return true;
    }
}
