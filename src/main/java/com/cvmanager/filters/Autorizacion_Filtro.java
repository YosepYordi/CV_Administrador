package com.cvmanager.filters;

import com.cvmanager.models.User;
import com.cvmanager.utils.Constantes;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/graduate/*", "/company/*", "/admin/*"})
public class Autorizacion_Filtro implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        User user = (User) req.getSession().getAttribute(Constantes.SESSION_USER);
        String path = req.getServletPath();
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }
        if ((path.startsWith("/admin") && !user.isAdmin()) ||
            (path.startsWith("/company") && !user.isCompany()) ||
            (path.startsWith("/graduate") && !user.isGraduate())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }
}
