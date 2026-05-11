package com.cvmanager.services;

import com.cvmanager.utils.ArchivoUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Archivo_Servicio {
    public String savePhoto(Part part, ServletContext context) throws IOException {
        if (part == null || part.getSize() <= 0) return null;
        String saved = ArchivoUtil.savePart(part, uploadDirectory(context, "/uploads/photos", "photos"));
        return saved == null ? null : "/uploads/photos/" + saved;
    }

    public String saveCvPdf(Part part, ServletContext context) throws IOException {
        if (part == null || part.getSize() <= 0) return null;
        String saved = ArchivoUtil.savePart(part, uploadDirectory(context, "/uploads/cvs", "cvs"));
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
}
