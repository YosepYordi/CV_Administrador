package com.cvmanager.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class RedirectUtil {
    private RedirectUtil() {
    }

    public static void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        String location = request.getContextPath() + normalizedPath;
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", response.encodeRedirectURL(location));
    }
}
