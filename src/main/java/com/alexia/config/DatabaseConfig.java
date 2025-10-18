package com.alexia.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Configuración de base de datos que construye la URL JDBC dinámicamente
 * desde variables de entorno individuales.
 * 
 * Esto permite usar tanto:
 * - Variables individuales de Render (DB_HOST, DB_PORT, DB_NAME)
 * - Variable completa SUPABASE_DB_URL (desarrollo local)
 */
@Configuration
public class DatabaseConfig {

    /**
     * Configuración para entorno de producción (Render)
     * Construye la URL JDBC desde variables individuales
     */
    @Bean
    @Profile("prod")
    public DataSource productionDataSource() {
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        
        // Debug: Print all environment variables (without password)
        System.out.println("=== DATABASE CONFIGURATION DEBUG ===");
        System.out.println("DB_HOST: " + (dbHost != null ? dbHost : "NOT SET"));
        System.out.println("DB_PORT: " + (dbPort != null ? dbPort : "NOT SET"));
        System.out.println("DB_NAME: " + (dbName != null ? dbName : "NOT SET"));
        System.out.println("DB_USER: " + (dbUser != null ? dbUser : "NOT SET"));
        System.out.println("DB_PASSWORD: " + (dbPassword != null ? "SET (length=" + dbPassword.length() + ")" : "NOT SET"));
        System.out.println("SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
        
        // Validate required variables
        if (dbHost == null || dbPort == null || dbName == null || dbUser == null || dbPassword == null) {
            String error = "Missing required database environment variables. Please check Render dashboard.";
            System.err.println("❌ " + error);
            throw new IllegalStateException(error);
        }
        
        // Construir URL JDBC desde componentes individuales
        String jdbcUrl = String.format(
            "jdbc:postgresql://%s:%s/%s?sslmode=require",
            dbHost,
            dbPort,
            dbName
        );
        
        System.out.println("✓ URL JDBC construida para producción: " + jdbcUrl);
        System.out.println("✓ Usuario: " + dbUser);
        System.out.println("====================================");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuración del pool - más conservadora para Render
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);
        config.setLeakDetectionThreshold(60000);
        
        // Deshabilitar caché de prepared statements
        config.addDataSourceProperty("cachePrepStmts", "false");
        config.addDataSourceProperty("prepStmtCacheSize", "0");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "0");
        
        // Connection test query
        config.setConnectionTestQuery("SELECT 1");
        
        return new HikariDataSource(config);
    }
    
    /**
     * Configuración para desarrollo local
     * Usa la variable SUPABASE_DB_URL directamente
     */
    @Bean
    @Profile("!prod")
    public DataSource developmentDataSource() {
        String jdbcUrl = System.getProperty("SUPABASE_DB_URL");
        String dbUser = System.getProperty("SUPABASE_DB_USER");
        String dbPassword = System.getProperty("SUPABASE_DB_PASSWORD");
        
        if (jdbcUrl == null) {
            throw new IllegalStateException(
                "SUPABASE_DB_URL no está configurada. " +
                "Asegúrate de que el archivo .env esté cargado correctamente."
            );
        }
        
        System.out.println("✓ Usando URL JDBC de desarrollo: " + 
            jdbcUrl.replaceAll("\\?.*", "?***"));
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuración del pool
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(60000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);
        
        // Deshabilitar caché de prepared statements
        config.addDataSourceProperty("cachePrepStmts", "false");
        config.addDataSourceProperty("prepStmtCacheSize", "0");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "0");
        
        return new HikariDataSource(config);
    }
}
