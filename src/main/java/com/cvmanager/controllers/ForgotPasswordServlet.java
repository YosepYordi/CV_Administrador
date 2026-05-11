package com.cvmanager.controllers;

import com.cvmanager.services.Servicio_Autenticacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/auth/forgot-password", "/auth/reset-password"})
public class ForgotPasswordServlet extends BaseServlet {
    private final Servicio_Autenticacion auth = new Servicio_Autenticacion();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isResetRequest(request)) {
            request.setAttribute("token", request.getParameter("token"));
            forward(request, response, "/WEB-INF/views/auth/resetPassword.jsp", "Restablecer contrasena");
            return;
        }
        forward(request, response, "/WEB-INF/views/auth/Olvidado_Password.jsp", "Recuperar contrasena");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isResetRequest(request)) {
            resetPassword(request, response);
            return;
        }
        requestRecovery(request, response);
    }

    private void requestRecovery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            auth.solicitarRecuperacion(request.getParameter("email"), baseUrl(request));
            setSuccess(request, "Se registraron instrucciones de recuperacion para tu correo.");
            redirect(request, response, "/auth/login");
        } catch (Exception ex) {
            request.setAttribute("formError", ex.getMessage());
            forward(request, response, "/WEB-INF/views/auth/Olvidado_Password.jsp", "Recuperar contrasena");
        }
    }

    private void resetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        try {
            auth.restablecerPassword(token, request.getParameter("password"), request.getParameter("confirmPassword"));
            setSuccess(request, "Tu contrasena fue actualizada. Inicia sesion con la nueva clave.");
            redirect(request, response, "/auth/login");
        } catch (Exception ex) {
            request.setAttribute("token", token);
            request.setAttribute("formError", ex.getMessage());
            forward(request, response, "/WEB-INF/views/auth/resetPassword.jsp", "Restablecer contrasena");
        }
    }

    private boolean isResetRequest(HttpServletRequest request) {
        return "/auth/reset-password".equals(request.getServletPath());
    }

    private String baseUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder()
                .append(request.getScheme())
                .append("://")
                .append(request.getServerName());
        int port = request.getServerPort();
        boolean defaultPort = ("http".equals(request.getScheme()) && port == 80)
                || ("https".equals(request.getScheme()) && port == 443);
        if (!defaultPort) {
            url.append(':').append(port);
        }
        url.append(request.getContextPath());
        return url.toString();
    }
}
