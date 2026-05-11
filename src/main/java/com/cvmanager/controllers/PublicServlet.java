package com.cvmanager.controllers;

import com.cvmanager.services.BusquedaServicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/public/home", "/public/about", "/public/contact"})
public class PublicServlet extends BaseServlet {
    private final BusquedaServicio busquedaServicio = new BusquedaServicio();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getServletPath();
            if ("/public/about".equals(path)) {
                forward(request, response, "/WEB-INF/views/public/Acerca_de.jsp", "Acerca del proyecto");
                return;
            }
            if ("/public/contact".equals(path)) {
                forward(request, response, "/WEB-INF/views/public/contactos.jsp", "Contacto");
                return;
            }
            request.setAttribute("careers", busquedaServicio.listarCarrerasActivas());
            request.setAttribute("recentCvs", busquedaServicio.cvsRecientes());
            forward(request, response, "/WEB-INF/views/public/home.jsp", "Plataforma de empleabilidad y CV");
        } catch (Exception ex) {
            request.setAttribute("recentCvs", java.util.List.of());
            request.setAttribute("databaseWarning", "No fue posible cargar informacion de MySQL: " + ex.getMessage());
            forward(request, response, "/WEB-INF/views/public/home.jsp", "Plataforma de empleabilidad y CV");
        }
    }
}
