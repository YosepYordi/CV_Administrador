package com.cvmanager.controllers;

import com.cvmanager.dao.impl.CareerDAOImpl;
import com.cvmanager.services.Servicio_Autenticacion;
import com.cvmanager.utils.ValidacionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/auth/register")
public class RegisterServlet extends BaseServlet {
    private final Servicio_Autenticacion auth = new Servicio_Autenticacion();
    private final CareerDAOImpl careerDAO = new CareerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        loadCareers(request);
        forward(request, response, "/WEB-INF/views/auth/registro.jsp", "Crear cuenta");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = request.getParameter("role");
        try {
            if ("graduate".equals(role)) {
                auth.registrarEgresado(
                        request.getParameter("email"),
                        request.getParameter("password"),
                        request.getParameter("firstName"),
                        request.getParameter("lastName"),
                        ValidacionUtil.parseLong(request.getParameter("careerId")).orElse(null),
                        ValidacionUtil.parseInteger(request.getParameter("graduationYear")).orElse(null));
                setSuccess(request, "Cuenta de egresado creada correctamente. Ya puedes iniciar sesion.");
            } else {
                auth.registrarEmpresa(request.getParameter("email"), request.getParameter("password"));
                setSuccess(request, "Cuenta de empresa creada correctamente. Ya puedes iniciar sesion.");
            }
            redirect(request, response, "/auth/login");
        } catch (Exception ex) {
            request.setAttribute("formError", ex.getMessage());
            request.setAttribute("email", ValidacionUtil.sanitize(request.getParameter("email")));
            request.setAttribute("firstName", ValidacionUtil.sanitize(request.getParameter("firstName")));
            request.setAttribute("lastName", ValidacionUtil.sanitize(request.getParameter("lastName")));
            request.setAttribute("selectedRole", role);
            loadCareers(request);
            forward(request, response, "/WEB-INF/views/auth/registro.jsp", "Crear cuenta");
        }
    }

    private void loadCareers(HttpServletRequest request) {
        try { request.setAttribute("careers", careerDAO.findActive()); } catch (Exception ex) { request.setAttribute("careers", java.util.List.of()); }
    }
}
