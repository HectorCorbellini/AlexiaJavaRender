# Guía de Despliegue en Render

## 📋 Requisitos Previos

- [x] Cuenta en [Render](https://render.com)
- [x] Aplicación Spring Boot funcional localmente
- [x] Repositorio en GitHub/GitLab
- [x] Base de datos configurada (Supabase o PostgreSQL en Render)

## ✅ Preparación Completada

- [x] **Dockerfile** creado para despliegue con Docker
- [x] **render.yaml** configurado para usar Docker
- [x] **application-prod.properties** creado con configuración de Render
- [x] **pom.xml** actualizado con mainClass
- [x] **.renderignore** creado para excluir archivos innecesarios
- [x] **RENDER_ENV_VARS.md** creado con documentación de variables de entorno
- [x] **Webhook de Telegram** - Eliminación automática integrada en el código
- [x] **Scripts eliminados** - Ya no se requieren scripts bash para iniciar
- [x] **Compilación verificada** - JAR generado exitosamente
- [x] **Prueba local exitosa** - Aplicación funciona sin scripts

## 🚀 Pasos para el Despliegue

### 1. Preparar la Aplicación

#### 1.1 Actualizar `application.properties`

Crea o modifica `src/main/resources/application-prod.properties`:

```properties
# Configuración de la base de datos
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# Configuración del puerto para Render
server.port=${PORT:8080}

# Configuración de Telegram
bot.token=${TELEGRAM_BOT_TOKEN}

# Configuración de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Logging
logging.level.org.springframework=INFO
logging.level.com.alexia=DEBUG
```

#### 1.2 Actualizar `pom.xml`

Asegúrate de que el `pom.xml` incluya el plugin de Spring Boot Maven:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <mainClass>com.alexia.AlexiaApplication</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 2. Configuración con Docker

**✅ Ya está configurado**

El proyecto incluye:
- **Dockerfile**: Imagen optimizada con Java 17
- **render.yaml**: Configurado para usar Docker
- **Multi-stage build**: Reduce el tamaño de la imagen final

Render detectará automáticamente el Dockerfile y construirá la imagen.

### 3. Configuración de Telegram

**✅ No se requiere configuración adicional**

La aplicación elimina automáticamente cualquier webhook de Telegram al iniciar el bot, permitiendo el uso de long polling. Esto está integrado en el código Java, por lo que **no necesitas ejecutar scripts bash** ni configurar webhooks manualmente.

### 4. Desplegar en Render

1. **Inicia sesión** en [Render Dashboard](https://dashboard.render.com/)
2. Haz clic en **New +** → **Web Service**
3. Conecta tu repositorio de GitHub/GitLab
4. Configura el servicio:
   - **Name**: alexia (o el nombre que prefieras)
   - **Region**: Elige la más cercana a tus usuarios
   - **Branch**: main
   - **Environment**: Docker (Render lo detectará automáticamente)
   - **Instance Type**: Free (para empezar)
   
   ⚠️ **Importante**: Render detectará el `Dockerfile` y `render.yaml` automáticamente

5. **Variables de Entorno (Actualizado)**:
   ```
   SPRING_PROFILES_ACTIVE=prod
   DB_HOST=aws-0-us-west-1.pooler.supabase.com
   DB_PORT=6543
   DB_NAME=postgres
   DB_USER=postgres.hgcesbylhkjoxtymxysy
   DB_PASSWORD=[your_supabase_password]
   TELEGRAM_BOT_TOKEN=[your_telegram_token]
   TELEGRAM_BOT_USERNAME=ukoquique_bot
   GROK_API_KEY=[your_grok_api_key]
   GROK_API_URL=https://api.groq.com/openai/v1/chat/completions
   GROK_MODEL=llama-3.1-8b-instant
   ```

   **Notas importantes**:
   - La aplicación usa `DatabaseConfig.java` para construir automáticamente la URL JDBC desde las variables individuales
   - Usamos el connection pooler de Supabase (puerto 6543) en lugar del host directo (puerto 5432)
   - No se necesita `.env.production` - todas las variables vienen del dashboard de Render


6. Haz clic en **Create Web Service**

### 5. Configurar Base de Datos (si no usas Supabase)

1. En Render Dashboard, haz clic en **New +** → **PostgreSQL**
2. Configura la base de datos:
   - **Name**: alexia-db
   - **Database**: alexia
   - **User**: usuario (lo generará Render)
   - **Region**: Misma que tu web service

3. Usa las credenciales generadas en las variables de entorno del web service.

## 🔍 Solución de Problemas Comunes

### La aplicación no inicia
- Verifica los logs en el dashboard de Render
- Asegúrate de que el puerto esté configurado correctamente
- Revisa las variables de entorno

### Problemas de Conexión a la Base de Datos
- **Verifica las credenciales**: La causa más común es una contraseña, host, puerto o usuario incorrecto.
- **Resetea la contraseña**: Si no estás seguro, resetea la contraseña de la base de datos en Supabase.
- **Verifica las restricciones de red**: En Supabase, ve a **Settings -> Database -> Network Restrictions** y asegúrate de que **"Allow all access"** esté activado.

### La aplicación se desconecta después de inactividad
- El plan gratuito de Render duerme las aplicaciones después de 15 minutos de inactividad
- Considera usar un servicio de monitoreo gratuito como UptimeRobot para mantenerla activa

## 📈 Escalando tu Aplicación

1. **Actualiza el Plan**: Cambia de Free a Starter o Professional según necesites
2. **Escalado Horizontal**: Añade más instancias si el tráfico aumenta
3. **Base de Datos**: Considera actualizar el plan de la base de datos

## 🔒 Seguridad

- Nunca subas archivos `.env` o `application.properties` con credenciales
- Usa variables de entorno para datos sensibles
- Configura HTTPS en Render (viene por defecto)

## 📚 Recursos Adicionales

- [Documentación de Render para Java](https://render.com/docs/deploy-java-spring-boot)
- [Configuración de PostgreSQL en Render](https://render.com/docs/deploy-postgres)
- [Variables de Entorno en Render](https://render.com/docs/environment-variables)

---

## 🚀 Despliegue Rápido (Resumen)

### Paso 1: ✅ Código subido a GitHub
**Estado**: ✅ COMPLETADO

El proyecto ya está disponible en:
```
https://github.com/HectorCorbellini/AlexiaJavaRender
```

Rama: `main`
Archivos subidos: 205 objetos (116.15 KiB)

### Paso 2: Crear servicio en Render
1. Ve a https://dashboard.render.com/
2. Click en **New +** → **Web Service**
3. Selecciona **Connect a repository**
4. Busca y selecciona: `HectorCorbellini/AlexiaJavaRender`
5. Render detectará automáticamente el `Dockerfile` y `render.yaml`
6. Click en **Apply** para usar la configuración automática

**Configuración sugerida**:
- **Name**: `alexia-java-render` (o el nombre que prefieras)
- **Region**: Oregon (US West) o la más cercana a tus usuarios
- **Branch**: `main`
- **Environment**: Docker (detectado automáticamente)
- **Instance Type**: Free (para empezar)

### Paso 3: Configurar variables de entorno
En el dashboard de Render, agrega estas variables de entorno (ver `RENDER_ENV_VARS.md` para detalles completos):

**Variables requeridas**:
```
SPRING_PROFILES_ACTIVE=prod
DB_HOST=aws-0-us-west-1.pooler.supabase.com
DB_PORT=6543
DB_NAME=postgres
DB_USER=postgres.hgcesbylhkjoxtymxysy
DB_PASSWORD=[tu_password_supabase]
TELEGRAM_BOT_TOKEN=[tu_token_de_telegram]
TELEGRAM_BOT_USERNAME=[tu_bot_username]
```

**Variables para Grok AI**:
```
GROK_API_KEY=[tu_api_key_de_groq]
GROK_API_URL=https://api.groq.com/openai/v1/chat/completions
GROK_MODEL=llama-3.1-8b-instant
```

**Nota**: La aplicación construye automáticamente la URL JDBC desde DB_HOST, DB_PORT y DB_NAME usando `DatabaseConfig.java`

### Paso 4: Desplegar
1. Revisa la configuración final
2. Click en **Create Web Service**
3. Render comenzará a:
   - Clonar el repositorio
   - Construir la imagen Docker
   - Desplegar la aplicación
4. Espera a que se complete el despliegue (5-10 minutos aproximadamente)

**Monitorea el progreso** en la pestaña "Logs" del dashboard de Render.

### Paso 5: Verificar el despliegue
1. **Verifica el estado**: El servicio debe mostrar "Live" en verde
2. **Abre la URL**: Click en la URL proporcionada por Render (ej: `https://alexia-java-render.onrender.com`)
3. **Prueba el dashboard**: Verifica que la interfaz de Vaadin cargue correctamente
4. **Inicia el bot**: Ve a la sección de Telegram en el dashboard y haz click en "Iniciar Bot"
5. **Prueba el bot**: Envía un mensaje a tu bot de Telegram (ej: `/start` o `Hola`)
6. **Verifica los logs**: Revisa que los mensajes se registren en la base de datos

### Paso 6: Configuración post-despliegue (Opcional)
- **Dominio personalizado**: Configura un dominio propio en la sección "Settings" → "Custom Domain"
- **Health checks**: Render los configurará automáticamente para `/actuator/health`
- **Auto-deploy**: Cada push a `main` desplegará automáticamente
- **Monitoreo**: Usa UptimeRobot (gratuito) para mantener activa la aplicación en el plan Free

---

## 📦 Archivos Creados

Los siguientes archivos fueron creados para el despliegue:

| Archivo | Descripción |
|---------|-------------|
| `Dockerfile` | Imagen Docker optimizada con Java 17 |
| `render.yaml` | Configuración de despliegue de Render |
| `.renderignore` | Archivos a excluir del despliegue |
| `application-prod.properties` | Configuración de producción |
| `RENDER_ENV_VARS.md` | Documentación de variables de entorno |

---

**Nota**: Esta guía asume que ya tienes configurada tu aplicación Spring Boot localmente. Si necesitas ayuda con algún paso específico, no dudes en preguntar.
