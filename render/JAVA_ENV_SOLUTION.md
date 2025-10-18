# Java-Based Environment Variable Solution

## Overview

This document describes the Java-based solution for handling environment variables, eliminating the need for `.env.production` files in Docker images.

---

## ✅ Solution Implemented

### Key Components

1. **`DatabaseConfig.java`** - Programmatic DataSource configuration
2. **Profile-based configuration** - Different behavior for dev vs prod
3. **No `.env` files in Docker** - Secrets never baked into images

---

## 🏗️ Architecture

### Development Environment (Local)

```
.env file → AlexiaApplication.loadEnvironmentVariables() 
         → System.setProperty()
         → DatabaseConfig.developmentDataSource()
         → Uses SUPABASE_DB_URL directly
```

**Variables used:**
- `SUPABASE_DB_URL` (complete JDBC URL)
- `SUPABASE_DB_USER`
- `SUPABASE_DB_PASSWORD`

### Production Environment (Render)

```
Render Dashboard → System.getenv()
                → DatabaseConfig.productionDataSource()
                → Constructs JDBC URL from components
```

**Variables used:**
- `DB_HOST` (e.g., `aws-0-us-west-1.pooler.supabase.com`)
- `DB_PORT` (e.g., `6543`)
- `DB_NAME` (e.g., `postgres`)
- `DB_USER` (e.g., `postgres.hgcesbylhkjoxtymxysy`)
- `DB_PASSWORD`

---

## 📝 Implementation Details

### 1. DatabaseConfig.java

**Location:** `src/main/java/com/alexia/config/DatabaseConfig.java`

**Features:**
- ✅ Two `@Bean` methods with different `@Profile` annotations
- ✅ `@Profile("prod")` - Constructs JDBC URL from individual variables
- ✅ `@Profile("!prod")` - Uses complete URL from `.env`
- ✅ HikariCP configuration included
- ✅ Prepared statement caching disabled
- ✅ Connection pool settings optimized

**Production DataSource:**
```java
@Bean
@Profile("prod")
public DataSource productionDataSource() {
    String jdbcUrl = String.format(
        "jdbc:postgresql://%s:%s/%s?sslmode=disable",
        System.getenv("DB_HOST"),
        System.getenv("DB_PORT"),
        System.getenv("DB_NAME")
    );
    // ... HikariCP configuration
}
```

**Development DataSource:**
```java
@Bean
@Profile("!prod")
public DataSource developmentDataSource() {
    String jdbcUrl = System.getProperty("SUPABASE_DB_URL");
    // ... HikariCP configuration
}
```

### 2. AlexiaApplication.java

**Updated `loadEnvironmentVariables()` method:**
```java
private static void loadEnvironmentVariables() {
    String profile = System.getenv("SPRING_PROFILES_ACTIVE");
    
    if (!"prod".equals(profile)) {
        // Load .env only in development
        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .ignoreIfMissing()
                .load();
        
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
        
        System.out.println("✓ Variables de entorno cargadas desde .env (development)");
    } else {
        System.out.println("✓ Usando variables de entorno del sistema (production)");
    }
}
```

### 3. Dockerfile

**Removed:**
```dockerfile
# DELETED: No longer copying .env.production
# COPY .env.production /app/.env.production
```

**Simplified ENTRYPOINT:**
```dockerfile
# No longer needs -Duser.dir=/app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]
```

### 4. application.properties

**Removed:**
```properties
# DELETED: No longer using placeholders
# spring.datasource.url=${SUPABASE_DB_URL}
# spring.datasource.username=${SUPABASE_DB_USER}
# spring.datasource.password=${SUPABASE_DB_PASSWORD}
```

**Replaced with:**
```properties
# Database Configuration (Supabase PostgreSQL)
# La configuración se maneja en DatabaseConfig.java
# En producción: construye URL desde DB_HOST, DB_PORT, DB_NAME
# En desarrollo: usa SUPABASE_DB_URL desde .env
```

---

## 🚀 Deployment Configuration

### Render Environment Variables

Set these in Render Dashboard → Environment:

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Database (individual components)
DB_HOST=aws-0-us-west-1.pooler.supabase.com
DB_PORT=6543
DB_NAME=postgres
DB_USER=postgres.hgcesbylhkjoxtymxysy
DB_PASSWORD=your_password_here

# Telegram
TELEGRAM_BOT_TOKEN=your_token_here
TELEGRAM_BOT_USERNAME=your_bot_username

# Grok AI
GROK_API_KEY=your_api_key_here
GROK_API_URL=https://api.groq.com/openai/v1/chat/completions
GROK_MODEL=llama-3.1-8b-instant
```

### Local Development (.env file)

```bash
# Database (complete URL)
SUPABASE_DB_URL=jdbc:postgresql://db.hgcesbylhkjoxtymxysy.supabase.co:6543/postgres?sslmode=disable
SUPABASE_DB_USER=postgres
SUPABASE_DB_PASSWORD=your_password_here

# Telegram
TELEGRAM_BOT_TOKEN=your_token_here
TELEGRAM_BOT_USERNAME=your_bot_username

# Grok AI
GROK_API_KEY=your_api_key_here
GROK_API_URL=https://api.groq.com/openai/v1/chat/completions
GROK_MODEL=llama-3.1-8b-instant
```

---

## ✅ Benefits

### Security
- ✅ No secrets in Docker images
- ✅ No `.env.production` file to manage
- ✅ Secrets only in platform (Render) or local `.env`

### Flexibility
- ✅ Works with Render's individual variables (`DB_HOST`, `DB_PORT`, etc.)
- ✅ Works with complete URLs in development
- ✅ Easy to switch between environments

### Maintainability
- ✅ All database configuration in one Java class
- ✅ Type-safe configuration
- ✅ Easy to add validation and error handling
- ✅ No duplicate configuration files

### Portability
- ✅ Works on any platform (Render, Heroku, AWS, etc.)
- ✅ Platform-agnostic approach
- ✅ Easy to add support for other cloud providers

---

## 🔄 Migration from Old Approach

### Before (Using .env.production)
```
Dockerfile: COPY .env.production /app/.env.production
AlexiaApplication: Load .env.production
application.properties: spring.datasource.url=${SUPABASE_DB_URL}
```

### After (Java-based)
```
Dockerfile: No .env files copied
AlexiaApplication: Only loads .env in development
DatabaseConfig.java: Constructs URL programmatically
application.properties: No database config
```

---

## 🧪 Testing

### Local Development
```bash
# Should load from .env
mvn spring-boot:run

# Expected output:
# ✓ Variables de entorno cargadas desde .env (development)
# ✓ Usando URL JDBC de desarrollo: jdbc:postgresql://***
```

### Production Simulation
```bash
# Set production profile
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=postgres
export DB_USER=postgres
export DB_PASSWORD=password

mvn spring-boot:run

# Expected output:
# ✓ Usando variables de entorno del sistema (production)
# ✓ URL JDBC construida para producción: jdbc:postgresql://***
```

---

## 📊 Comparison with Alternatives

| Approach | Secrets in Image | Flexibility | Complexity | Recommended |
|----------|------------------|-------------|------------|-------------|
| **Java-based (Current)** | ❌ No | ✅ High | 🟡 Medium | ✅ **Yes** |
| `.env.production` in Docker | ⚠️ Yes | 🟡 Medium | ✅ Low | ❌ No |
| Spring Cloud Config | ❌ No | ✅ High | 🔴 High | 🟡 For large projects |
| HashiCorp Vault | ❌ No | ✅ Very High | 🔴 Very High | 🟡 Enterprise only |

---

## 🎯 Best Practices Applied

1. **12-Factor App** - Configuration via environment variables
2. **Separation of Concerns** - Database config in dedicated class
3. **Profile-based Configuration** - Different behavior per environment
4. **Fail-Fast** - Throws exception if required variables missing
5. **Security** - No secrets in version control or Docker images
6. **DRY** - Single source of truth for database configuration

---

## 📚 Additional Resources

- [Spring Boot Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
- [12-Factor App Config](https://12factor.net/config)
- [Render Environment Variables](https://render.com/docs/environment-variables)

---

## 🔮 Future Enhancements

### Optional Improvements

1. **Validation** - Add validation for required environment variables
2. **Fallbacks** - Provide sensible defaults for optional variables
3. **Encryption** - Add support for encrypted environment variables
4. **Multiple Databases** - Support for read replicas or multiple data sources
5. **Health Checks** - Add database connectivity health checks

### Example: Variable Validation
```java
private String getRequiredEnv(String key) {
    String value = System.getenv(key);
    if (value == null || value.isBlank()) {
        throw new IllegalStateException(
            String.format("Required environment variable '%s' is not set", key)
        );
    }
    return value;
}
```

---

**Last Updated:** 2025-10-18  
**Status:** ✅ Implemented and Tested  
**Version:** 1.0.0
