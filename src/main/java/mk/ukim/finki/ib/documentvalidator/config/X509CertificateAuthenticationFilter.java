package mk.ukim.finki.ib.documentvalidator.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class X509CertificateAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If user is not authenticated and not accessing the login page
        if (auth == null || !auth.isAuthenticated()) {
            String requestURI = httpRequest.getRequestURI();

            // Check if the request is not for the login page
            if (!requestURI.equals("/login") && !requestURI.startsWith("/register/**") && !requestURI.startsWith("/register")) {
                httpResponse.sendRedirect("/login");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}