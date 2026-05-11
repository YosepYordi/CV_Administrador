package com.cvmanager.dao.interfaces;

import com.cvmanager.models.DeletionSummary;

import java.sql.SQLException;

public interface AccountDeletionDAO {
    DeletionSummary deleteUserCompletely(Long userId) throws SQLException;
    DeletionSummary deleteCvByGraduateId(Long graduateId) throws SQLException;
}
