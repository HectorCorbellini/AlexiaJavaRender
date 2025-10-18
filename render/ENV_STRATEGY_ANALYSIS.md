# Environment Variables Strategy Analysis

## Current Problem

The project uses two `.env` files because Render.com doesn't properly support loading environment variables from the platform's dashboard into the Spring Boot application:

- `.env` - Local development
- `.env.production` - Copied into Docker image for Render deployment

**Root Cause**: The `AlexiaApplication.java` loads `.env.production` first (if it exists), which works locally but creates a workaround dependency on bundling secrets in the Docker image.

---

## Issues with Current Approach

### 1. **Security Risk**
- `.env.production` is copied into the Docker image (line 29 of Dockerfile)
- Secrets are baked into the container image
- Anyone with access to the image can extract credentials

### 2. **Deployment Complexity**
- Need to maintain two separate `.env` files
- Must remember to update both files when credentials change
- `.env.production` must be excluded from git but included in Docker build

### 3. **Render Platform Misuse**
- Not leveraging Render's native environment variable system
- Defeats the purpose of platform-managed secrets

---

## Better Solutions

### ✅ **Solution 1: Spring Boot Profiles with System Environment Variables (RECOMMENDED)**

**How it works:**
- Remove `.env.production` from Docker image
- Use Spring Boot's native environment variable resolution
- Render injects environment variables as system environment variables
- Spring Boot automatically picks them up

**Implementation:**

#### Step 1: Update `application.properties`
```properties
# Already correct - uses ${VARIABLE} placeholders
spring.datasource.url=${SUPABASE_DB_URL}
spring.datasource.username=${SUPABASE_DB_USER}
spring.datasource.password=${SUPABASE_DB_PASSWORD}
```

#### Step 2: Modify `AlexiaApplication.java`
```java
private static void loadEnvironmentVariables() {
    // Only load .env in non-production environments
    String profile = System.getenv("SPRING_PROFILES_ACTIVE");
    
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
```

#### Step 3: Update `Dockerfile`
```dockerfile
# Remove this line:
# COPY .env.production /app/.env.production

# Keep everything else the same
```

#### Step 4: Configure Render Dashboard
Set these environment variables in Render's dashboard:
- `SUPABASE_DB_URL`
- `SUPABASE_DB_USER`
- `SUPABASE_DB_PASSWORD`
- `TELEGRAM_BOT_TOKEN`
- `TELEGRAM_BOT_USERNAME`
- `GROK_API_KEY`
- `GROK_API_URL`
- `GROK_MODEL`

**Advantages:**
- ✅ No secrets in Docker image
- ✅ Render's native environment variable management
- ✅ Single source of truth for production config
- ✅ Easy to update secrets without rebuilding image
- ✅ Follows 12-factor app methodology

**Disadvantages:**
- ⚠️ Must configure variables in Render dashboard (one-time setup)

---

### ✅ **Solution 2: Spring Cloud Config Server**

**How it works:**
- Centralized configuration server
- Applications fetch config at startup
- Supports encryption and versioning

**Implementation:**
```yaml
# bootstrap.yml
spring:
  cloud:
    config:
      uri: https://config-server.example.com
      name: alexia
      profile: ${SPRING_PROFILES_ACTIVE}
```

**Advantages:**
- ✅ Centralized configuration management
- ✅ Encryption support
- ✅ Version control for configs
- ✅ Dynamic refresh without restart

**Disadvantages:**
- ⚠️ Requires additional infrastructure (config server)
- ⚠️ More complex setup
- ⚠️ Overkill for single application

---

### ✅ **Solution 3: HashiCorp Vault Integration**

**How it works:**
- Secrets stored in Vault
- Application fetches secrets at runtime
- Automatic secret rotation

**Implementation:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

**Advantages:**
- ✅ Enterprise-grade secret management
- ✅ Automatic rotation
- ✅ Audit logging
- ✅ Fine-grained access control

**Disadvantages:**
- ⚠️ Requires Vault infrastructure
- ⚠️ Additional cost
- ⚠️ Complex setup
- ⚠️ Overkill for small projects

---

### ✅ **Solution 4: Render Secret Files (Alternative)**

**How it works:**
- Upload `.env` as a Render Secret File
- Mounted at runtime, not baked into image
- Render manages the file securely

**Implementation:**
1. In Render dashboard: Settings → Secret Files
2. Upload `.env.production` as secret file
3. Mount path: `/app/.env.production`
4. Application reads from mounted file

**Advantages:**
- ✅ Secrets not in Docker image
- ✅ Minimal code changes
- ✅ Render manages encryption

**Disadvantages:**
- ⚠️ Still using file-based config (not ideal)
- ⚠️ Less flexible than environment variables

---

## Recommended Implementation Plan

### Phase 1: Immediate Fix (Solution 1)
1. Modify `AlexiaApplication.java` to skip `.env` loading in production
2. Remove `.env.production` from Dockerfile
3. Configure all variables in Render dashboard
4. Test deployment

### Phase 2: Future Enhancement (Optional)
If the project grows and needs more sophisticated config management:
- Consider Spring Cloud Config for multi-environment management
- Consider Vault for sensitive credential rotation

---

## Migration Steps for Solution 1

### 1. Update AlexiaApplication.java
```java
private static void loadEnvironmentVariables() {
    String profile = System.getenv("SPRING_PROFILES_ACTIVE");
    
    // Only load .env file in development (not production)
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
```

### 2. Update Dockerfile
```dockerfile
# Remove this line:
# COPY .env.production /app/.env.production

# The rest stays the same
```

### 3. Configure Render Environment Variables
In Render Dashboard → Environment:
```
SUPABASE_DB_URL=[your_supabase_db_url]
SUPABASE_DB_USER=postgres
SUPABASE_DB_PASSWORD=[your_supabase_password]
TELEGRAM_BOT_TOKEN=[your_telegram_token]
TELEGRAM_BOT_USERNAME=ukoquique_bot
GROK_API_KEY=[your_grok_api_key]
GROK_API_URL=https://api.groq.com/openai/v1/chat/completions
GROK_MODEL=llama-3.1-8b-instant
SPRING_PROFILES_ACTIVE=prod
```

### 4. Delete .env.production (Optional)
Since it's no longer needed:
```bash
rm .env.production
```

Or keep it for reference but ensure it's in `.gitignore`.

---

## Testing the Solution

### Local Development
```bash
# Should load from .env
mvn spring-boot:run
# Output: ✓ Variables de entorno cargadas desde .env (development)
```

### Production (Render)
```bash
# Should use system environment variables
# Output: ✓ Usando variables de entorno del sistema (production)
```

---

## Conclusion

**Recommended**: Solution 1 (Spring Boot Profiles with System Environment Variables)

**Reasoning**:
- Follows industry best practices (12-factor app)
- No secrets in Docker images
- Leverages Render's native capabilities
- Simple to implement and maintain
- No additional infrastructure required
- Easy to debug and audit

**Next Steps**:
1. Implement the code changes
2. Configure Render environment variables
3. Test deployment
4. Remove `.env.production` from repository
5. Update documentation
