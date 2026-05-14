package com.cvmanager.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BaseServletRedirectTest {
    @Test
    void redirectUsesContextRelativeLocationForForwardedHttpsRequests() throws IOException {
        TestServlet servlet = new TestServlet();
        CapturingResponse response = new CapturingResponse();

        servlet.exposedRedirect(requestWithContextPath("/cvmanager"), response.proxy(), "/public/home");

        assertEquals(HttpServletResponse.SC_FOUND, response.status);
        assertEquals("/cvmanager/public/home", response.location);
        assertFalse(response.sendRedirectCalled);
    }

    private static HttpServletRequest requestWithContextPath(String contextPath) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class[]{HttpServletRequest.class},
                (proxy, method, args) -> {
                    if ("getContextPath".equals(method.getName())) {
                        return contextPath;
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0d;
        if (type == char.class) return '\0';
        return null;
    }

    private static class TestServlet extends BaseServlet {
        void exposedRedirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
            redirect(request, response, path);
        }
    }

    private static class CapturingResponse {
        int status;
        String location;
        boolean sendRedirectCalled;

        HttpServletResponse proxy() {
            return (HttpServletResponse) Proxy.newProxyInstance(
                    HttpServletResponse.class.getClassLoader(),
                    new Class[]{HttpServletResponse.class},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "setStatus":
                                status = (Integer) args[0];
                                return null;
                            case "setHeader":
                                if ("Location".equals(args[0])) {
                                    location = (String) args[1];
                                }
                                return null;
                            case "sendRedirect":
                                sendRedirectCalled = true;
                                status = HttpServletResponse.SC_FOUND;
                                location = "https://pvtddbzb-8080.brs.devtunnels.ms:8080" + args[0];
                                return null;
                            case "encodeRedirectURL":
                                return args[0];
                            default:
                                return defaultValue(method.getReturnType());
                        }
                    });
        }
    }
}
