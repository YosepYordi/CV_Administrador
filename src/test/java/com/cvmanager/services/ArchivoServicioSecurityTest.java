package com.cvmanager.services;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchivoServicioSecurityTest {
    private static final byte[] PNG_BYTES = new byte[]{
            (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n',
            0, 0, 0, 0
    };

    @TempDir
    Path tempDir;

    @Test
    void savePhotoRejectsScriptPayloadEvenWhenClientClaimsImage() {
        Archivo_Servicio servicio = new Archivo_Servicio();
        byte[] jspPayload = "<% out.println(\"pwned\"); %>".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.savePhoto(part("shell.jsp", "image/jpeg", jspPayload), context(tempDir)));

        assertTrue(error.getMessage().contains("foto"));
    }

    @Test
    void savePhotoUsesServerChosenImageExtension() throws Exception {
        Archivo_Servicio servicio = new Archivo_Servicio();

        String saved = servicio.savePhoto(part("../shell.jsp", "image/png", PNG_BYTES), context(tempDir));

        assertTrue(saved.matches("/uploads/photos/[0-9a-f\\-]{36}\\.png"));
        assertFalse(saved.contains("shell"));
        assertTrue(Files.exists(storedPath(tempDir, saved)));
    }

    @Test
    void savePhotoStreamsFileWithoutDelegatingToContainerWrite() throws Exception {
        Archivo_Servicio servicio = new Archivo_Servicio();

        String saved = servicio.savePhoto(partThatRejectsWrite("photo.png", "image/png", PNG_BYTES), context(tempDir));

        assertTrue(saved.matches("/uploads/photos/[0-9a-f\\-]{36}\\.png"));
        assertArrayEquals(PNG_BYTES, Files.readAllBytes(storedPath(tempDir, saved)));
    }

    @Test
    void savePhotoUsesWebRootWhenUploadDirectoryDoesNotExistYet() throws Exception {
        Archivo_Servicio servicio = new Archivo_Servicio();

        String saved = servicio.savePhoto(part("photo.png", "image/png", PNG_BYTES), contextWithMissingUploadDir(tempDir));

        assertTrue(saved.matches("/uploads/photos/[0-9a-f\\-]{36}\\.png"));
        assertTrue(Files.exists(storedPath(tempDir, saved)));
    }

    @Test
    void saveCvPdfRejectsScriptPayloadEvenWhenClientClaimsPdf() {
        Archivo_Servicio servicio = new Archivo_Servicio();
        byte[] jspPayload = "<% out.println(\"pwned\"); %>".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.saveCvPdf(part("cv.jsp", "application/pdf", jspPayload), context(tempDir)));

        assertTrue(error.getMessage().contains("PDF"));
    }

    @Test
    void saveCvPdfUsesServerChosenPdfExtension() throws Exception {
        Archivo_Servicio servicio = new Archivo_Servicio();
        byte[] pdfBytes = "%PDF-1.4\n1 0 obj\n<<>>\nendobj\n".getBytes(StandardCharsets.US_ASCII);

        String saved = servicio.saveCvPdf(part("cv.jsp", "application/pdf", pdfBytes), context(tempDir));

        assertTrue(saved.matches("/uploads/cvs/[0-9a-f\\-]{36}\\.pdf"));
        assertFalse(saved.contains("cv.jsp"));
        assertTrue(Files.exists(storedPath(tempDir, saved)));
    }

    private static Path storedPath(Path root, String saved) {
        return root.resolve(saved.substring(1).replace("/", java.io.File.separator));
    }

    private static ServletContext context(Path root) {
        return (ServletContext) Proxy.newProxyInstance(
                ServletContext.class.getClassLoader(),
                new Class<?>[]{ServletContext.class},
                (proxy, method, args) -> {
                    if ("getRealPath".equals(method.getName())) {
                        String webPath = ((String) args[0]).replaceFirst("^/", "");
                        return root.resolve(webPath).toString();
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private static ServletContext contextWithMissingUploadDir(Path root) {
        return (ServletContext) Proxy.newProxyInstance(
                ServletContext.class.getClassLoader(),
                new Class<?>[]{ServletContext.class},
                (proxy, method, args) -> {
                    if ("getRealPath".equals(method.getName())) {
                        String webPath = (String) args[0];
                        if ("/".equals(webPath)) return root.toString();
                        if (webPath.startsWith("/uploads/")) return null;
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private static Part part(String submittedFileName, String contentType, byte[] bytes) {
        return (Part) Proxy.newProxyInstance(
                Part.class.getClassLoader(),
                new Class<?>[]{Part.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getSubmittedFileName":
                            return submittedFileName;
                        case "getContentType":
                            return contentType;
                        case "getSize":
                            return (long) bytes.length;
                        case "getInputStream":
                            return new ByteArrayInputStream(bytes);
                        case "write":
                            Path target = Path.of((String) args[0]);
                            Files.createDirectories(target.getParent());
                            Files.write(target, bytes);
                            return null;
                        case "delete":
                            return null;
                        case "getHeaderNames":
                            return List.of();
                        case "getHeaders":
                            return List.of();
                        default:
                            return defaultValue(method.getReturnType());
                    }
                });
    }

    private static Part partThatRejectsWrite(String submittedFileName, String contentType, byte[] bytes) {
        return (Part) Proxy.newProxyInstance(
                Part.class.getClassLoader(),
                new Class<?>[]{Part.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getSubmittedFileName":
                            return submittedFileName;
                        case "getContentType":
                            return contentType;
                        case "getSize":
                            return (long) bytes.length;
                        case "getInputStream":
                            return new ByteArrayInputStream(bytes);
                        case "write":
                            throw new IllegalStateException("Container rejected absolute path");
                        case "delete":
                            return null;
                        case "getHeaderNames":
                            return List.of();
                        case "getHeaders":
                            return List.of();
                        default:
                            return defaultValue(method.getReturnType());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == Collection.class) return List.of();
        return null;
    }
}
