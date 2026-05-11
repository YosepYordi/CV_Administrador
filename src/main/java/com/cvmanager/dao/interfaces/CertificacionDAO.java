package com.cvmanager.dao.interfaces;

import com.cvmanager.models.Certificacion;

import java.sql.SQLException;
import java.util.List;

public interface CertificacionDAO {
    List<Certificacion> findByCvId(Long cvId) throws SQLException;
    void replaceAll(Long cvId, List<Certificacion> items) throws SQLException;
}
