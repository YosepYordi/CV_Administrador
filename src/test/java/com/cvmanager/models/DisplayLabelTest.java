package com.cvmanager.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DisplayLabelTest {
    @Test
    void userRoleLabelsAreShownInSpanish() {
        assertEquals("Egresado", User.Role.GRADUATE.getLabel());
        assertEquals("Empresa", User.Role.COMPANY.getLabel());
        assertEquals("Administrador", User.Role.ADMIN.getLabel());
    }

    @Test
    void userStatusLabelsAreShownInSpanish() {
        assertEquals("Activo", User.Status.ACTIVE.getLabel());
        assertEquals("Inactivo", User.Status.INACTIVE.getLabel());
        assertEquals("Pendiente", User.Status.PENDING.getLabel());
    }

    @Test
    void contactRequestStatusLabelsAreShownInSpanish() {
        assertEquals("Pendiente", ContactRequest.Status.PENDING.getLabel());
        assertEquals("Aceptada", ContactRequest.Status.ACCEPTED.getLabel());
        assertEquals("Rechazada", ContactRequest.Status.REJECTED.getLabel());
    }
}
