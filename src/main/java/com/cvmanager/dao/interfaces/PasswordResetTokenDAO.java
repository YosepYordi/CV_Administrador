package com.cvmanager.dao.interfaces;

import com.cvmanager.models.PasswordResetToken;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenDAO {
    Long create(Long userId, String tokenHash, LocalDateTime expiresAt) throws SQLException;

    Optional<PasswordResetToken> findValidByTokenHash(String tokenHash, LocalDateTime now) throws SQLException;

    boolean markUsed(Long tokenId, LocalDateTime usedAt) throws SQLException;
}
