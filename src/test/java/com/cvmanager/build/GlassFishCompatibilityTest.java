package com.cvmanager.build;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class GlassFishCompatibilityTest {
    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();

    @Test
    void sourceUsesJakartaServletApisForGlassFish() throws IOException {
        assertNoTextInFiles(PROJECT_ROOT.resolve("src/main/java"), "javax.servlet");
    }

    @Test
    void jspViewsUseJakartaJstlTagUris() throws IOException {
        assertNoTextInFiles(PROJECT_ROOT.resolve("src/main/webapp"), "http://java.sun.com/jsp/jstl/core");
        assertContainsAny(PROJECT_ROOT.resolve("src/main/webapp"), "jakarta.tags.core");
    }

    @Test
    void mavenUsesJakartaWebApi() throws IOException {
        String pom = Files.readString(PROJECT_ROOT.resolve("pom.xml"));
        assertTrue(pom.contains("jakarta.jakartaee-web-api"));
        assertFalse(pom.contains("javax.servlet-api"));
        assertFalse(pom.contains("<artifactId>jstl</artifactId>"));
    }

    private static void assertNoTextInFiles(Path root, String forbiddenText) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java")
                            || path.toString().endsWith(".jsp")
                            || path.toString().endsWith(".xml"))
                    .forEach(path -> assertFileDoesNotContain(path, forbiddenText));
        }
    }

    private static void assertContainsAny(Path root, String requiredText) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            boolean found = files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".jsp"))
                    .anyMatch(path -> fileContains(path, requiredText));
            assertTrue(found, "Expected at least one JSP to use " + requiredText);
        }
    }

    private static void assertFileDoesNotContain(Path path, String text) {
        assertFalse(fileContains(path, text), () -> path + " must not contain " + text);
    }

    private static boolean fileContains(Path path, String text) {
        try {
            return Files.readString(path).contains(text);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + path, e);
        }
    }
}
