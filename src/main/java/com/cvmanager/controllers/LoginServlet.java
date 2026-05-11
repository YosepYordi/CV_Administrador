package com.cvmanager.controllers;

import com.cvmanager.models.Egresados;
import com.cvmanager.models.User;
import com.cvmanager.services.Servicio_Autenticacion;
import com.cvmanager.utils.Constantes;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/auth/login")
public class LoginServlet extends BaseServlet {
    private final Servicio_Autenticacion auth = new Servicio_Autenticacion();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forward(request, response, "/WEB-INF/views/auth/login.jsp", "Iniciar sesion");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        try {
            User user = auth.autenticar(email, request.getParameter("password"));
            request.getSession().setAttribute(Constantes.SESSION_USER, user);
            if (user.isGraduate()) {
                Egresados egresado = auth.getGraduateProfile(user.getUserId()).orElse(null);
                request.getSession().setAttribute(Constantes.SESSION_GRADUATE, egresado);
                redirect(request, response, "/graduate/dashboard");
            } else if (user.isAdmin()) {
                redirect(request, response, "/admin/dashboard");
            } else {
                redirect(request, response, "/company/dashboard");
            }
        } catch (Exception ex) {
            request.setAttribute("formError", ex.getMessage());
            request.setAttribute("email", email);
            forward(request, response, "/WEB-INF/views/auth/login.jsp", "Iniciar sesion");
        }
    }
}
