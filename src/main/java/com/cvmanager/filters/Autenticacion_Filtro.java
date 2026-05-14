package com.cvmanager.filters;

import com.cvmanager.utils.Constantes;
import com.cvmanager.utils.RedirectUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/graduate/*", "/company/*", "/admin/*"})
public class Autenticacion_Filtro implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (req.getSession().getAttribute(Constantes.SESSION_USER) == null) {
            RedirectUtil.redirect(req, res, "/auth/login");
            return;
        }
        chain.doFilter(request, response);
    }
}
