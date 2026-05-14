package com.cvmanager.controllers;

import com.cvmanager.services.Archivo_Servicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet(urlPatterns = "/uploads/*")
public class UploadServlet extends HttpServlet {
    private final Archivo_Servicio archivoServicio = new Archivo_Servicio();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank() || pathInfo.contains("..")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String uploadPath = "/uploads" + pathInfo;
        Path file = archivoServicio.resolveStoredFile(uploadPath, getServletContext()).orElse(null);
        if (file == null && uploadPath.startsWith("/uploads/photos/")) {
            request.getRequestDispatcher("/assets/images/default-avatar.png").forward(request, response);
            return;
        }
        if (file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(file.getFileName().toString());
        response.setContentType(contentType == null ? "application/octet-stream" : contentType);
        response.setHeader("Cache-Control", "public, max-age=3600");
        response.setContentLengthLong(Files.size(file));
        Files.copy(file, response.getOutputStream());
    }
}
