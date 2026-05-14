package com.cvmanager.services;

import com.cvmanager.utils.DBConnection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.Part;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OllamaCVImportService {
    private static final int MIN_TEXT_LENGTH = 40;
    private static final int MAX_TEXT_LENGTH = 5000;

    private final HttpClient httpClient;
    private final CVImportJsonParser parser;
    private final String endpoint;
    private final String model;

    public OllamaCVImportService() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(), new CVImportJsonParser(),
                env("OLLAMA_CHAT_URL", DBConnection.property("ollama.chat.url", "http://localhost:11434/api/chat")),
                env("OLLAMA_MODEL", DBConnection.property("ollama.model", "gemma4:e2b")));
    }

    OllamaCVImportService(HttpClient httpClient, CVImportJsonParser parser, String endpoint, String model) {
        this.httpClient = httpClient;
        this.parser = parser;
        this.endpoint = endpoint;
        this.model = model;
    }

    public CVImportDraft importFromPdf(Part pdfPart) throws IOException, InterruptedException {
        if (pdfPart == null || pdfPart.getSize() == 0) {
            throw new IllegalArgumentException("Selecciona un PDF para importar con IA.");
        }
        String text = extractText(pdfPart);
        if (text.length() < MIN_TEXT_LENGTH) {
            throw new IllegalArgumentException("No pude leer suficiente texto del PDF. Usa un PDF con texto seleccionable.");
        }
        return importFromText(text);
    }

    CVImportDraft importFromText(String cvText) throws IOException, InterruptedException {
        String requestBody = buildRequestBody(trimForModel(cvText));
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .timeout(Duration.ofMinutes(8))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Ollama respondio con estado " + response.statusCode() + ".");
        }
        String content = responseContent(response.body());
        return parser.parse(content);
    }

    private String extractText(Part pdfPart) throws IOException {
        try (PDDocument document = PDDocument.load(pdfPart.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).replaceAll("\\s+", " ").trim();
        }
    }

    private String buildRequestBody(String cvText) {
        return Json.createObjectBuilder()
                .add("model", model)
                .add("stream", false)
                .add("format", "json")
                .add("options", Json.createObjectBuilder()
                        .add("temperature", 0.1)
                        .add("num_ctx", 4096)
                        .add("num_predict", 1200))
                .add("messages", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("role", "system")
                                .add("content", systemPrompt()))
                        .add(Json.createObjectBuilder()
                                .add("role", "user")
                                .add("content", "Texto extraido del CV PDF:\n" + cvText)))
                .build()
                .toString();
    }

    private String systemPrompt() {
        return """
                Eres un extractor de datos de CV. Devuelve solo JSON valido y compacto, sin Markdown.
                No inventes datos que no aparecen en el CV. Si un dato falta, usa cadena vacia.
                Usa fechas ISO yyyy-MM-dd cuando puedas inferir dia, mes y anio; si no, usa cadena vacia.
                No dividas una misma educacion, experiencia o certificacion en varios objetos.
                No conviertas frases sueltas, viñetas partidas o encabezados en objetos.
                Si una experiencia dice "sin experiencia", deja experience como arreglo vacio.
                Si una certificacion no tiene entidad emisora, fecha, codigo ni URL, no la incluyas.
                Si una educacion no tiene institucion/carrera y al menos campo, fecha o descripcion, no la incluyas.
                Limita cada arreglo a maximo 6 objetos y resume textos largos en una sola oracion.
                El JSON debe tener exactamente estas claves:
                title, professionalSummary, education, experience, skills, languages, certifications.
                education: institution, degree, fieldOfStudy, startDate, endDate, description.
                experience: company, position, startDate, endDate, employmentType, responsibilities, achievements.
                skills: name, category, level. category debe ser technical, soft u other. level debe ser 1 a 5.
                languages: name, level, certifications. level debe ser basic, intermediate, advanced o native.
                certifications: name, issuingOrganization, issueDate, expirationDate, credentialId, credentialUrl.
                """;
    }

    private String responseContent(String responseBody) {
        try (JsonReader reader = Json.createReader(new StringReader(responseBody))) {
            JsonObject root = reader.readObject();
            if (root.containsKey("error")) {
                throw new IllegalStateException("Ollama: " + root.getString("error", "error desconocido"));
            }
            JsonObject message = root.getJsonObject("message");
            if (message == null) throw new IllegalStateException("Ollama no devolvio mensaje.");
            return message.getString("content", "");
        }
    }

    private String trimForModel(String text) {
        if (text == null) return "";
        String cleaned = text.replaceAll("\\s+", " ").trim();
        return cleaned.length() <= MAX_TEXT_LENGTH ? cleaned : cleaned.substring(0, MAX_TEXT_LENGTH);
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
