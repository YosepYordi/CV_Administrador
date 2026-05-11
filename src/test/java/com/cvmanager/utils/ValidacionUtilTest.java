package com.cvmanager.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionUtilTest {
    @Test
    void validatesInstitutionalEmailDomain() {
        assertTrue(ValidacionUtil.isInstitutionalEmail("alumno@instituto.edu.pe", "@instituto.edu.pe"));
        assertFalse(ValidacionUtil.isInstitutionalEmail("alumno@gmail.com", "@instituto.edu.pe"));
    }

    @Test
    void strongPasswordRequiresLengthLettersAndNumbers() {
        assertTrue(ValidacionUtil.isStrongPassword("clave123"));
        assertFalse(ValidacionUtil.isStrongPassword("short1"));
        assertFalse(ValidacionUtil.isStrongPassword("sololetras"));
    }

    @Test
    void sanitizeEscapesHtmlControlCharacters() {
        assertEquals("&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt; &amp;",
                ValidacionUtil.sanitize(" <script>alert('x')</script> & "));
    }
}
