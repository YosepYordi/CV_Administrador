package com.cvmanager.company;

import com.cvmanager.models.ContactRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContactRequestTest {
    @Test
    void newContactRequestDefaultsToPending() {
        ContactRequest request = new ContactRequest();

        assertEquals(ContactRequest.Status.PENDING, request.getStatus());
        assertEquals("pending", request.getStatus().getValue());
    }

    @Test
    void contactRequestCarriesCompanyDisplayDataForGraduateInbox() {
        ContactRequest request = new ContactRequest();

        request.setCompanyName("Tech SAC");
        request.setCompanyEmail("rrhh@tech.test");

        assertEquals("Tech SAC", request.getCompanyName());
        assertEquals("rrhh@tech.test", request.getCompanyEmail());
    }

    @Test
    void contactRequestCarriesGraduateDisplayDataForCompanyInbox() {
        ContactRequest request = new ContactRequest();

        request.setGraduateName("Yosep Ore");
        request.setGraduateEmail("yosep@instituto.edu.pe");
        request.setGraduatePhone("999888777");

        assertEquals("Yosep Ore", request.getGraduateName());
        assertEquals("yosep@instituto.edu.pe", request.getGraduateEmail());
        assertEquals("999888777", request.getGraduatePhone());
    }
}
