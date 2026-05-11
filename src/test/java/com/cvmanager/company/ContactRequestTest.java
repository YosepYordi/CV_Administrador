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
}
