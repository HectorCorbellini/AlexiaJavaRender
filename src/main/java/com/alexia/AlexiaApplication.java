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
     * - En Render: carga desde /etc/secrets/.env (Secret Files)
     * - En desarrollo local: carga desde .env en el directorio raíz
     * - En otros entornos de producción: usa variables del sistema
     */
    private static void loadEnvironmentVariables() {
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        
        // Intentar cargar desde /etc/secrets/.env (Render Secret Files)
        java.io.File secretsEnv = new java.io.File("/etc/secrets/.env");
        if (secretsEnv.exists()) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .directory("/etc/secrets")
                        .filename(".env")
                        .ignoreIfMissing()
                        .load();
                
                dotenv.entries().forEach(entry -> 
                    System.setProperty(entry.getKey(), entry.getValue())
                );
                
                System.out.println("✓ Variables de entorno cargadas desde /etc/secrets/.env (Render Secret File)");
                return;
            } catch (Exception e) {
                System.err.println("⚠ No se pudo cargar /etc/secrets/.env: " + e.getMessage());
            }
        }
        
        // Solo cargar .env local en desarrollo (no en producción)
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
