package com.cvmanager.admin;

import com.cvmanager.controllers.AdminServlet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminCsvExporterTest {
    @Test
    void escapesCommasQuotesAndLineBreaks() {
        List<String[]> rows = List.of(
                new String[]{"Email", "Estado"},
                new String[]{"ana@example.com", "active"},
                new String[]{"texto, con \"comillas\"\ny salto", null}
        );

        String csv = AdminServlet.CsvExporter.toCsv(rows);

        assertEquals("Email,Estado\r\nana@example.com,active\r\n\"texto, con \"\"comillas\"\" y salto\",\r\n", csv);
    }
}
