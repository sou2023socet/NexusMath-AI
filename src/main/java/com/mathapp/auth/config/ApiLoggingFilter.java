package com.mathapp.auth.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.mathapp.auth.logging.LogService;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiLoggingFilter extends OncePerRequestFilter {

    private final LogService logService;

    public ApiLoggingFilter(LogService logService) {
        this.logService = logService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        logService.apiCall(request.getMethod(), request.getRequestURI(), null);

        filterChain.doFilter(request, response);

        int status = response.getStatus();
        long time = System.currentTimeMillis() - start;

        if (status >= 400) {
            logService.apiError(request.getMethod(), request.getRequestURI(), status, null);
        } else {
            logService.api(request.getMethod(), request.getRequestURI(), status, time);
        }

    }

}
