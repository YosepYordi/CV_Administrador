package com.cvmanager.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailServicio {
    private static final Logger logger = LogManager.getLogger(EmailServicio.class);

    public void sendVerificationEmail(String email) {
        logger.info("Correo de verificacion simulado para {}", email);
    }

    public void sendPasswordResetEmail(String email) {
        sendPasswordResetEmail(email, null);
    }

    public void sendPasswordResetEmail(String email, String resetUrl) {
        if (resetUrl == null || resetUrl.isBlank()) {
            logger.info("Correo de recuperacion simulado para {}", email);
            return;
        }
        logger.info("Correo de recuperacion para {}: {}", email, resetUrl);
    }
}
