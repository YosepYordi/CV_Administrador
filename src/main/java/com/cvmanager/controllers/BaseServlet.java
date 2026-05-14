package com.cvmanager.controllers;

import com.cvmanager.models.User;
import com.cvmanager.utils.Constantes;
import com.cvmanager.utils.RedirectUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected void forward(HttpServletRequest request, HttpServletResponse response, String view, String title)
            throws ServletException, IOException {
        request.setAttribute("pageTitle", title);
        moveFlash(request);
        request.getRequestDispatcher(view).forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        RedirectUtil.redirect(request, response, path);
    }

    protected User currentUser(HttpServletRequest request) {
        Object user = request.getSession().getAttribute(Constantes.SESSION_USER);
        return user instanceof User ? (User) user : null;
    }

    protected void setSuccess(HttpServletRequest request, String message) {
        request.getSession().setAttribute(Constantes.SESSION_FLASH_SUCCESS, message);
    }

    protected void setError(HttpServletRequest request, String message) {
        request.getSession().setAttribute(Constantes.SESSION_FLASH_ERROR, message);
    }

    private void moveFlash(HttpServletRequest request) {
        Object databaseWarning = getServletContext().getAttribute("databaseWarning");
        if (databaseWarning != null) request.setAttribute("databaseWarning", databaseWarning);
        Object success = request.getSession().getAttribute(Constantes.SESSION_FLASH_SUCCESS);
        Object error = request.getSession().getAttribute(Constantes.SESSION_FLASH_ERROR);
        if (success != null) {
            request.setAttribute("flashSuccess", success);
            request.getSession().removeAttribute(Constantes.SESSION_FLASH_SUCCESS);
        }
        if (error != null) {
            request.setAttribute("flashError", error);
            request.getSession().removeAttribute(Constantes.SESSION_FLASH_ERROR);
        }
    }
}
