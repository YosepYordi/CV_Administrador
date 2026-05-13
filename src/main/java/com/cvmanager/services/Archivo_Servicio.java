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
    }

    private Path uploadDirectory(ServletContext context, String webPath, String fallbackFolder) {
        String realPath = context.getRealPath(webPath);
        if (realPath != null) return Path.of(realPath);
        return Path.of(System.getProperty("java.io.tmpdir"), "cvmanager-uploads", fallbackFolder);
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
