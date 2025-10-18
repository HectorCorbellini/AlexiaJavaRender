# Render Deployment Documentation

This folder contains all documentation related to deploying the Alexia application on Render.com.

## 📁 Files Overview

### Deployment Guides

- **[RENDER_PASOS.md](RENDER_PASOS.md)** - Complete step-by-step deployment guide
  - Prerequisites and preparation
  - Docker configuration
  - Environment variables setup
  - Troubleshooting common issues
  - Quick deployment summary

- **[RENDER_ENV_VARS.md](RENDER_ENV_VARS.md)** - Environment variables reference
  - Complete list of required variables
  - Configuration examples
  - Security best practices
  - Configuration checklist

### Tools & Utilities

- **[RENDER_LOGS_GUIDE.md](RENDER_LOGS_GUIDE.md)** - Log viewing and debugging
  - Dashboard log viewing
  - Render CLI installation and usage
  - Log filtering and searching
  - Debugging workflow

- **[render_cli_guide.md](render_cli_guide.md)** - Render CLI installation
  - Installation methods (script, manual, Homebrew)
  - Authentication setup
  - Useful commands
  - Troubleshooting

### Architecture & Strategy

- **[JAVA_ENV_SOLUTION.md](JAVA_ENV_SOLUTION.md)** - Java-based environment variable solution
  - DatabaseConfig.java implementation
  - Profile-based configuration (dev vs prod)
  - No .env files in Docker images
  - Migration guide from old approach

- **[ENV_STRATEGY_ANALYSIS.md](ENV_STRATEGY_ANALYSIS.md)** - Environment variable strategy analysis
  - Current problem identification
  - Multiple solution approaches
  - Comparison and recommendations
  - Implementation steps

## 🚀 Quick Start

1. **Read first**: [RENDER_PASOS.md](RENDER_PASOS.md) for complete deployment guide
2. **Configure variables**: Use [RENDER_ENV_VARS.md](RENDER_ENV_VARS.md) as reference
3. **Understand architecture**: Read [JAVA_ENV_SOLUTION.md](JAVA_ENV_SOLUTION.md) to understand how environment variables are handled

## 🔑 Key Concepts

### Environment Variables Strategy

The application uses a **Java-based approach** for environment variables:

- **Production (Render)**: Variables set in Render Dashboard → `DatabaseConfig.java` constructs JDBC URL
- **Development (Local)**: Variables from `.env` file → Loaded by `AlexiaApplication.java`
- **No secrets in Docker**: `.env.production` is NOT copied into the image

### Required Variables for Render

```bash
# Database (individual components)
DB_HOST=aws-0-us-west-1.pooler.supabase.com
DB_PORT=6543
DB_NAME=postgres
DB_USER=postgres.PROJECT_ID
DB_PASSWORD=your_password

# Application
SPRING_PROFILES_ACTIVE=prod
TELEGRAM_BOT_TOKEN=your_token
TELEGRAM_BOT_USERNAME=your_bot_username
GROK_API_KEY=your_api_key
```

See [RENDER_ENV_VARS.md](RENDER_ENV_VARS.md) for complete list.

## 📚 Documentation Structure

```
render/
├── README.md                    ← You are here
├── RENDER_PASOS.md             ← Main deployment guide
├── RENDER_ENV_VARS.md          ← Environment variables reference
├── RENDER_LOGS_GUIDE.md        ← Log viewing and debugging
├── render_cli_guide.md         ← CLI installation
├── JAVA_ENV_SOLUTION.md        ← Java-based env solution (current)
└── ENV_STRATEGY_ANALYSIS.md   ← Strategy analysis and alternatives
```

## 🔄 Recent Changes

### 2025-10-18: Java-Based Environment Solution

- ✅ Created `DatabaseConfig.java` for programmatic DataSource configuration
- ✅ Removed `.env.production` from Docker image
- ✅ Updated all documentation to reflect new approach
- ✅ Consolidated all Render docs in this folder

### Key Benefits

- No secrets baked into Docker images
- Works with Render's individual environment variables
- Type-safe, programmatic configuration
- Easy to validate and extend

## 🆘 Troubleshooting

If you encounter issues:

1. Check [RENDER_PASOS.md](RENDER_PASOS.md) → "Solución de Problemas Comunes"
2. View logs using [RENDER_LOGS_GUIDE.md](RENDER_LOGS_GUIDE.md)
3. Verify environment variables in [RENDER_ENV_VARS.md](RENDER_ENV_VARS.md)

## 📖 Related Documentation

- **Main README**: `../README.md` - Project overview
- **Database Setup**: `../database/` - SQL scripts for table creation
- **Changelog**: `../CHANGELOG.md` - Project history
- **Refactoring**: `../PUPPY_REFACTORING.md` - Migration from LinuxMint

---

**Last Updated**: 2025-10-18  
**Maintained by**: Alexia Team
