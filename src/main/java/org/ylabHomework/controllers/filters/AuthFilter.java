package org.ylabHomework.controllers.filters;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        HttpSession session = httpRequest.getSession(false);

        boolean nonAuthorisedPage = path.equals("/") || path.equals("/login") || path.equals("/registration");

        if (nonAuthorisedPage || (session != null && session.getAttribute("loggedUser") != null)) {
            System.out.println("яппи");
            chain.doFilter(request, response);
        } else {
            System.out.println("антияппи");
            httpResponse.sendRedirect("/login");
        }
    }

    @Override
    public void destroy() {

    }
}
