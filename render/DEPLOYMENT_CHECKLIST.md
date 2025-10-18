# Render Deployment Checklist

## ✅ Pre-Deployment Verification

Before deploying to Render, ensure all these items are completed:

### 1. Code & Configuration

- [x] `DatabaseConfig.java` created for programmatic DataSource configuration
- [x] `AlexiaApplication.java` loads `.env` only in development
- [x] `application.properties` has Actuator configuration
- [x] `pom.xml` includes `spring-boot-starter-actuator` dependency
- [x] `Dockerfile` does NOT copy `.env.production`
- [x] `render.yaml` updated with individual DB variables (not `DATABASE_URL`)

### 2. Health Check Endpoint

- [x] Spring Boot Actuator dependency added
- [x] `/actuator/health` endpoint configured
- [x] `render.yaml` healthCheckPath points to `/actuator/health`

**Test locally:**
```bash
mvn spring-boot:run
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

### 3. Environment Variables (Render Dashboard)

Configure these in Render Dashboard → Environment:

#### Database (Required)
- [ ] `DB_HOST` = `aws-0-us-west-1.pooler.supabase.com`
- [ ] `DB_PORT` = `6543`
- [ ] `DB_NAME` = `postgres`
- [ ] `DB_USER` = `postgres.hgcesbylhkjoxtymxysy`
- [ ] `DB_PASSWORD` = `your_password`

#### Application (Required)
- [ ] `SPRING_PROFILES_ACTIVE` = `prod`
- [ ] `TELEGRAM_BOT_TOKEN` = `your_token`
- [ ] `TELEGRAM_BOT_USERNAME` = `your_bot_username`

#### Grok AI (Required)
- [ ] `GROK_API_KEY` = `your_api_key`
- [ ] `GROK_API_URL` = `https://api.groq.com/openai/v1/chat/completions`
- [ ] `GROK_MODEL` = `llama-3.1-8b-instant`

#### Optional (Auto-configured)
- [ ] `PORT` = `8080` (Render sets this automatically)
- [ ] `JAVA_OPTS` = `-Xmx512m -Xms256m` (already in render.yaml)

### 4. Database Setup

- [ ] Supabase database created
- [ ] Connection pooler enabled (port 6543)
- [ ] Network restrictions allow all access
- [ ] Tables created (run SQL scripts from `../database/` folder):
  - [ ] `connection_test`
  - [ ] `telegram_messages`
  - [ ] `bot_commands`
  - [ ] `businesses`

### 5. GitHub Repository

- [ ] Code pushed to GitHub
- [ ] `.env` file is in `.gitignore` (not pushed)
- [ ] `.env.production` file is in `.gitignore` (not pushed)
- [ ] `Dockerfile` present in root
- [ ] `render.yaml` present in root

### 6. Render Service Configuration

- [ ] Service type: Web Service
- [ ] Environment: Docker
- [ ] Branch: `main`
- [ ] Auto-deploy: Enabled
- [ ] Health check path: `/actuator/health`
- [ ] Region: Oregon (or closest to users)
- [ ] Plan: Free (or higher)

---

## 🚀 Deployment Steps

### Step 1: Verify Local Build
```bash
# Clean and build
mvn clean package -DskipTests

# Verify JAR created
ls -lh target/alexia-1.0.0.jar

# Test locally
mvn spring-boot:run

# Test health endpoint
curl http://localhost:8080/actuator/health
```

### Step 2: Test Docker Build Locally (Optional)
```bash
# Build Docker image
docker build -t alexia-test .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=aws-0-us-west-1.pooler.supabase.com \
  -e DB_PORT=6543 \
  -e DB_NAME=postgres \
  -e DB_USER=postgres.hgcesbylhkjoxtymxysy \
  -e DB_PASSWORD=your_password \
  -e TELEGRAM_BOT_TOKEN=your_token \
  -e TELEGRAM_BOT_USERNAME=your_bot \
  -e GROK_API_KEY=your_key \
  alexia-test

# Test health
curl http://localhost:8080/actuator/health
```

### Step 3: Push to GitHub
```bash
git add .
git commit -m "feat: add Actuator for Render health checks"
git push origin main
```

### Step 4: Create Render Service

1. Go to https://dashboard.render.com/
2. Click **New +** → **Web Service**
3. Connect GitHub repository
4. Render will auto-detect `Dockerfile` and `render.yaml`
5. Click **Apply** to use configuration
6. Add environment variables (see checklist above)
7. Click **Create Web Service**

### Step 5: Monitor Deployment

1. Watch build logs in Render Dashboard
2. Wait for "Live" status (5-10 minutes)
3. Check health endpoint: `https://your-app.onrender.com/actuator/health`
4. Test dashboard: `https://your-app.onrender.com/`
5. Test Telegram bot

---

## 🔍 Post-Deployment Verification

### Health Check
```bash
curl https://your-app.onrender.com/actuator/health
# Expected: {"status":"UP"}
```

### Database Connection
Check logs for:
```
✓ URL JDBC construida para producción: jdbc:postgresql://***
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

### Application Started
Check logs for:
```
Started AlexiaApplication in X seconds
Tomcat started on port(s): 8080
✓ Alexia Application Started Successfully!
```

### Telegram Bot
1. Send `/start` to your bot
2. Check logs for message processing
3. Verify response received

---

## 🐛 Common Issues & Solutions

### Issue: Health check failing
**Symptoms:** Render shows "Unhealthy" status

**Solutions:**
1. Verify Actuator dependency in `pom.xml`
2. Check `/actuator/health` endpoint locally
3. Ensure `management.endpoints.web.exposure.include=health` in properties
4. Check Render logs for startup errors

### Issue: Database connection failed
**Symptoms:** `PSQLException: connection refused` or `authentication failed`

**Solutions:**
1. Verify all DB_* variables are set correctly
2. Use connection pooler (port 6543) not direct (5432)
3. Check Supabase network restrictions
4. Verify password is correct (no extra spaces)

### Issue: Application won't start
**Symptoms:** Build succeeds but app crashes on startup

**Solutions:**
1. Check Render logs for stack trace
2. Verify `SPRING_PROFILES_ACTIVE=prod`
3. Ensure all required environment variables are set
4. Check memory limits (increase `JAVA_OPTS` if needed)

### Issue: Telegram bot not responding
**Symptoms:** Bot receives messages but doesn't respond

**Solutions:**
1. Verify `TELEGRAM_BOT_TOKEN` is correct
2. Check bot is not running elsewhere (webhook conflict)
3. Review Telegram-related logs
4. Ensure `TELEGRAM_BOT_USERNAME` matches your bot

---

## 📊 Monitoring

### Render Dashboard
- **Metrics**: CPU, Memory, Response time
- **Logs**: Real-time application logs
- **Events**: Deployment history

### Health Endpoint
```bash
# Check health
curl https://your-app.onrender.com/actuator/health

# Check info (if exposed)
curl https://your-app.onrender.com/actuator/info
```

### Database Monitoring
- Supabase Dashboard → Database → Connections
- Monitor active connections
- Check query performance

---

## 🔄 Redeployment

### Automatic (Recommended)
Push to `main` branch → Render auto-deploys

### Manual
Render Dashboard → Manual Deploy → Deploy latest commit

### Rollback
Render Dashboard → Events → Redeploy previous version

---

## 📚 Additional Resources

- [Render Documentation](https://render.com/docs)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Supabase Connection Pooling](https://supabase.com/docs/guides/database/connecting-to-postgres#connection-pooler)
- [Telegram Bot API](https://core.telegram.org/bots/api)

---

**Last Updated:** 2025-10-18  
**Status:** Ready for Deployment ✅
