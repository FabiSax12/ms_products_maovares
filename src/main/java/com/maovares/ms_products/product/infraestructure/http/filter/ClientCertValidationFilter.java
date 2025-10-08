package com.maovares.ms_products.product.infraestructure.http.filter;

import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import com.maovares.ms_products.product.infraestructure.http.exception.InvalidCertificateException;

import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
public class ClientCertValidationFilter implements Filter {
     private static final String EXPECTED_THUMBPRINT = System.getenv("CLIENT_CERT_THUMBPRINT");

     @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        String clientIp = getClientIpAddress(httpReq);
        
        log.debug("Starting client certificate validation for request from IP: {}", clientIp);
        
        try {
            String certHeader = httpReq.getHeader("X-ARR-ClientCert");

            if (certHeader == null) {
                log.warn("Missing client certificate header for request from IP: {}", clientIp);
                throw new InvalidCertificateException("Missing client certificate header");
            }

            log.debug("Client certificate header found, decoding certificate");
            byte[] decoded = Base64.getDecoder().decode(certHeader);
            X509Certificate cert = (X509Certificate) CertificateFactory
                    .getInstance("X.509")
                    .generateCertificate(new java.io.ByteArrayInputStream(decoded));

            // Calcular thumbprint SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String thumbprint = bytesToHex(md.digest(cert.getEncoded()));
            
            log.debug("Certificate thumbprint calculated: {}", thumbprint);
            log.debug("Expected thumbprint: {}", EXPECTED_THUMBPRINT);

            if (!thumbprint.equalsIgnoreCase(EXPECTED_THUMBPRINT)) {
                log.error("Invalid client certificate thumbprint for request from IP: {} - Received: {}, Expected: {}", 
                         clientIp, thumbprint, EXPECTED_THUMBPRINT);
                throw new InvalidCertificateException("Invalid client certificate thumbprint");
            }

            log.info("Client certificate validation successful for request from IP: {}", clientIp);
            chain.doFilter(request, response);

        } catch (InvalidCertificateException e) {
            log.error("Client certificate validation failed for request from IP: {} - Reason: {}", 
                     clientIp, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during client certificate validation for request from IP: {} - Error: {}", 
                     clientIp, e.getMessage(), e);
            throw new InvalidCertificateException("Error validating client certificate", e);
        }
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

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}