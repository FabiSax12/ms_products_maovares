package com.maovares.ms_products.product.infraestructure.http.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maovares.ms_products.product.infraestructure.http.filter.ClientCertValidationFilter;

@Configuration
public class FilterConfig {

    @Value("${client.cert.validation.enabled:false}")
    private boolean certValidationEnabled;

    @Value("${client.cert.thumbprint:}")
    private String expectedThumbprint;

    @Bean
    public FilterRegistrationBean<ClientCertValidationFilter> clientCertValidationFilter() {
        FilterRegistrationBean<ClientCertValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ClientCertValidationFilter(certValidationEnabled, expectedThumbprint));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        registrationBean.setName("clientCertValidationFilter");
        return registrationBean;
    }
}
