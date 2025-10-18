package com.alexia;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Alexia.
 * Punto de entrada para Spring Boot.
 * 
 * @author Alexia Team
 * @version 1.0
 * @since 2025-10-14
 */
@SpringBootApplication
public class AlexiaApplication {

    public static void main(String[] args) {
        // Cargar variables de entorno ANTES de iniciar Spring
        loadEnvironmentVariables();
        
        SpringApplication.run(AlexiaApplication.class, args);
        System.out.println("✓ Alexia Application Started Successfully!");
        System.out.println("✓ Dashboard available at: http://localhost:8080");
    }
    
    /**
     * Carga las variables de entorno desde archivo .env
     * Solo en desarrollo (no en producción)
     * 
     * En producción, las variables se obtienen directamente del sistema
     * (Render, Docker, etc.) sin necesidad de archivos .env
     */
    private static void loadEnvironmentVariables() {
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        
        // Solo cargar .env en desarrollo (no en producción)
        if (!"prod".equals(profile)) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .filename(".env")
                        .ignoreIfMissing()
                        .load();
                
                dotenv.entries().forEach(entry -> 
                    System.setProperty(entry.getKey(), entry.getValue())
                );
                
                System.out.println("✓ Variables de entorno cargadas desde .env (development)");
            } catch (Exception e) {
                System.err.println("⚠ No se pudo cargar archivo .env: " + e.getMessage());
            }
        } else {
            System.out.println("✓ Usando variables de entorno del sistema (production)");
        }
    }
}
