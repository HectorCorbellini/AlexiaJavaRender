# Variables de Entorno para Render

Este documento lista todas las variables de entorno necesarias para desplegar Alexia en Render.

## 📋 Variables Requeridas

### 1. Configuración de Base de Datos

**IMPORTANTE**: La aplicación construye la URL JDBC automáticamente desde variables individuales.

```bash
# Host de la base de datos (usar connection pooler de Supabase)
DB_HOST=aws-0-us-west-1.pooler.supabase.com

# Puerto de la base de datos (6543 para pooler, 5432 para directo)
DB_PORT=6543

# Nombre de la base de datos
DB_NAME=postgres

# Usuario de la base de datos (formato: postgres.PROJECT_ID para pooler)
DB_USER=postgres.hgcesbylhkjoxtymxysy

# Contraseña de la base de datos
DB_PASSWORD=tu_contraseña_segura
```

**Ejemplo completo con Supabase:**
```bash
DB_HOST=aws-0-us-west-1.pooler.supabase.com
DB_PORT=6543
DB_NAME=postgres
DB_USER=postgres.hgcesbylhkjoxtymxysy
DB_PASSWORD=Placita2010$
```

**Nota**: La aplicación usa `DatabaseConfig.java` para construir automáticamente:
```
jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres?sslmode=disable
```

### 2. Configuración de Telegram

```bash
# Token del bot de Telegram (obtener de @BotFather)
TELEGRAM_BOT_TOKEN=1234567890:ABCdefGHIjklMNOpqrsTUVwxyz123456789
```

### 3. Configuración de Spring Boot

```bash
# Perfil activo de Spring (siempre 'prod' en Render)
SPRING_PROFILES_ACTIVE=prod

# Puerto del servidor (Render lo asigna automáticamente)
PORT=8080
```

### 4. Optimización de Java (Opcional)

```bash
# Opciones de JVM para optimizar memoria en plan gratuito
JAVA_OPTS=-Xmx512m -Xms256m
```

---

## 🔧 Cómo Configurar en Render

### Opción 1: Desde el Dashboard

1. Ve a tu servicio en [Render Dashboard](https://dashboard.render.com/)
2. Click en **Environment** en el menú lateral
3. Agrega cada variable con el botón **Add Environment Variable**
4. Guarda los cambios

### Opción 2: Desde render.yaml

Las variables ya están definidas en `render.yaml` con `sync: false`, lo que significa que debes configurarlas manualmente en el dashboard por seguridad.

---

## 🔒 Seguridad

⚠️ **IMPORTANTE:**
- **NUNCA** subas archivos `.env` al repositorio
- **NUNCA** hardcodees credenciales en el código
- Usa variables de entorno para todos los datos sensibles
- Mantén tus tokens y contraseñas seguros

---

## 📝 Checklist de Configuración

Antes de desplegar, verifica que tengas:

- [ ] `DB_HOST` configurado
- [ ] `DB_PORT` configurado
- [ ] `DB_NAME` configurado
- [ ] `DB_USER` configurado
- [ ] `DB_PASSWORD` configurado
- [ ] `TELEGRAM_BOT_TOKEN` configurado
- [ ] `TELEGRAM_BOT_USERNAME` configurado
- [ ] `GROK_API_KEY` configurado
- [ ] `SPRING_PROFILES_ACTIVE=prod` configurado
- [ ] Base de datos creada y accesible
- [ ] Tablas de la base de datos creadas (ver `../database/` folder)

---

## 🆘 Solución de Problemas

### Error: "Could not connect to database"
- Verifica que `DB_HOST`, `DB_PORT`, `DB_NAME` estén correctos
- Asegúrate de que la base de datos acepte conexiones externas
- Revisa que `DB_USER` y `DB_PASSWORD` sean correctos
- Para Supabase, usa el connection pooler (puerto 6543) en lugar del directo (5432)

### Error: "Telegram bot token is invalid"
- Verifica que `TELEGRAM_BOT_TOKEN` esté correcto
- Asegúrate de que el token no tenga espacios al inicio o final
- Confirma que el bot esté activo en Telegram

### Error: "Port already in use"
- Render asigna el puerto automáticamente vía `$PORT`
- No es necesario configurar `PORT` manualmente en Render

---

## 📚 Referencias

- [Render Environment Variables](https://render.com/docs/environment-variables)
- [Supabase Connection Strings](https://supabase.com/docs/guides/database/connecting-to-postgres)
- [Telegram Bot API](https://core.telegram.org/bots/api)
