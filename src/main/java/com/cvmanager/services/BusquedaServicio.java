package com.cvmanager.services;

import com.cvmanager.dao.impl.CVDAOImpl;
import com.cvmanager.dao.impl.CareerDAOImpl;
import com.cvmanager.dao.interfaces.CVDAO;
import com.cvmanager.dao.interfaces.CareerDAO;
import com.cvmanager.models.CV;
import com.cvmanager.models.Career;
import com.cvmanager.models.SearchCriteria;

import java.sql.SQLException;
import java.util.List;

public class BusquedaServicio {
    private final CVDAO cvDAO;
    private final CareerDAO careerDAO;

    public BusquedaServicio() {
        this(new CVDAOImpl(), new CareerDAOImpl());
    }

    public BusquedaServicio(CVDAO cvDAO, CareerDAO careerDAO) {
        this.cvDAO = cvDAO;
        this.careerDAO = careerDAO;
    }

    public List<CV> buscar(SearchCriteria criteria) throws SQLException {
        return cvDAO.search(criteria);
    }

    public List<Career> listarCarrerasActivas() throws SQLException {
        return careerDAO.findActive();
    }

    public List<CV> cvsRecientes() throws SQLException {
        return cvDAO.findPublished(8);
    }
}
