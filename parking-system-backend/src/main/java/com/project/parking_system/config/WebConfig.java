package com.project.parking_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global Web MVC Configuration.
 * This class configures Cross-Origin Resource Sharing (CORS).
 * By default, browsers block requests from one domain/port (localhost:4200)
 * to another (localhost:8080). This configuration explicitly permits it.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures CORS mappings.
     * 1. Allow calls to any endpoint starting with `/api/`.
     * 2. Allow calls ONLY from the Frontend URL defined in AppConstants.
     * 3. Allow standard REST methods (GET, POST, PUT, DELETE).
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(AppConstants.FRONTEND_URL)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
