package com.maovares.ms_products.product.infraestructure.http.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1) // Ejecutar antes que otros filtros
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // Obtener o generar correlation ID
            String correlationId = getOrGenerateCorrelationId(httpRequest);
            
            // Agregar al MDC para logging
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            
            // Agregar header a la respuesta
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
            
            // Log de inicio de request
            log.info("Incoming request: {} {} from {} - User-Agent: {}", 
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(),
                    getClientIpAddress(httpRequest),
                    httpRequest.getHeader("User-Agent"));
            
            long startTime = System.currentTimeMillis();
            
            // Continuar con la cadena de filtros
            chain.doFilter(request, response);
            
            // Log de fin de request
            long duration = System.currentTimeMillis() - startTime;
            log.info("Request completed: {} {} - Status: {} - Duration: {}ms", 
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    duration);
                    
        } finally {
            // Limpiar MDC
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }

    private String getOrGenerateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
            log.debug("Generated new correlation ID: {}", correlationId);
        } else {
            log.debug("Using existing correlation ID: {}", correlationId);
        }
        
        return correlationId;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
