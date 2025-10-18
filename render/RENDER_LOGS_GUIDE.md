# Guía para Ver Logs en Render (Actualizada)

## 📋 Métodos para Ver Logs

### Método 1: Dashboard de Render (Más Fácil)

1. Ve a https://dashboard.render.com/
2. Selecciona tu servicio (`alexia-java-render`)
3. Click en la pestaña **"Logs"** en el menú lateral
4. Los logs se muestran en tiempo real
5. Usa **Ctrl+F** (o Cmd+F en Mac) para buscar texto específico
6. Haz **scroll hacia arriba** para ver logs anteriores

**Ventajas:**
- ✅ No requiere instalación
- ✅ Interfaz visual
- ✅ Logs en tiempo real

**Desventajas:**
- ❌ No puedes descargar fácilmente
- ❌ Limitado a la ventana del navegador

---

### Método 2: Render CLI (Recomendado para Debugging)

El Render CLI te permite ver logs completos desde la terminal.

#### Paso 1: Instalar Render CLI

**En Linux/Mac:**
```bash
# Opción 1: Usando npm (requiere Node.js)
npm install -g @render-cli/cli

# Opción 2: Usando curl (sin Node.js)
curl -fsSL https://render.com/install-cli.sh | bash
```

**En Windows:**
```bash
# Usando npm (requiere Node.js)
npm install -g @render-cli/cli
```

#### Paso 2: Autenticarse

```bash
# Iniciar sesión en Render
render login

# Esto abrirá tu navegador para autenticarte
# Sigue las instrucciones en pantalla
```

#### Paso 3: Ver Logs

```bash
# Ver logs en tiempo real
render logs -s alexia-java-render

# Ver logs con más líneas (últimas 500 líneas)
render logs -s alexia-java-render -n 500

# Ver logs y seguir nuevos mensajes (como tail -f)
render logs -s alexia-java-render -f

# Filtrar logs por palabra clave
render logs -s alexia-java-render | grep ERROR

# Guardar logs en un archivo
render logs -s alexia-java-render -n 1000 > logs.txt
```

#### Comandos Útiles del CLI

```bash
# Listar todos tus servicios
render services list

# Ver información del servicio
render services get alexia-java-render

# Ver deploys recientes
render deploys list -s alexia-java-render

# Forzar un nuevo deploy
render deploy -s alexia-java-render

# Ver variables de entorno
render env list -s alexia-java-render
```

**Ventajas:**
- ✅ Logs completos sin límite de scroll
- ✅ Puedes guardar logs en archivos
- ✅ Filtrar y buscar con herramientas de terminal
- ✅ Automatización con scripts

**Desventajas:**
- ❌ Requiere instalación
- ❌ Requiere conocimientos de terminal

---

## 🔍 Debugging de Errores de Conexión

### Buscar Errores Específicos

```bash
# Ver solo errores
render logs -s alexia-java-render | grep -i error

# Ver errores de conexión a base de datos
render logs -s alexia-java-render | grep -i "connection\|hikari\|postgresql"

# Ver el stack trace completo
render logs -s alexia-java-render | grep -A 20 "Exception"

# Ver las últimas 100 líneas y buscar "Caused by"
render logs -s alexia-java-render -n 100 | grep -B 5 "Caused by"
```

### Verificar Variables de Entorno

```bash
# Ver todas las variables de entorno configuradas
render env list -s alexia-java-render

# Buscar una variable específica
render env list -s alexia-java-render | grep DATABASE
```

---

## 📊 Análisis de Logs Comunes

### 1. Verificar que la aplicación inició

Busca estas líneas:
```
✓ Variables de entorno cargadas desde .env.production
Starting AlexiaApplication
```

### 2. Verificar conexión a base de datos

Busca estas líneas:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

Si ves:
```
HikariPool-1 - Exception during pool initialization
```
Significa que hay un problema de conexión.

### 3. Verificar que el servidor web inició

Busca:
```
Tomcat started on port(s): 8080
Started AlexiaApplication in X seconds
```

### 4. Errores comunes

**Error de conexión:**
```
PSQLException: The connection attempt failed
Caused by: java.net.UnknownHostException
```
**Solución**: Verifica DB_HOST, DB_PORT

**Error de autenticación:**
```
PSQLException: FATAL: password authentication failed
```
**Solución**: Verifica DB_USER, DB_PASSWORD

**Error de formato de URL:**
```
Driver claims to not accept jdbcUrl
```
**Solución**: Verifica que DATABASE_URL tenga formato correcto

---

## 💡 Tips Avanzados

### Monitorear logs en tiempo real con filtros

```bash
# Ver solo logs de Spring Boot
render logs -s alexia-java-render -f | grep "com.alexia"

# Ver logs de inicio de la aplicación
render logs -s alexia-java-render -f | grep -i "starting\|started"

# Ver logs de Hibernate/JPA
render logs -s alexia-java-render -f | grep -i "hibernate\|jpa"
```

### Guardar logs para análisis posterior

```bash
# Guardar logs con timestamp
render logs -s alexia-java-render -n 2000 > "logs_$(date +%Y%m%d_%H%M%S).txt"

# Guardar solo errores
render logs -s alexia-java-render -n 2000 | grep -i error > errors.txt
```

### Comparar logs entre deploys

```bash
# Ver logs del deploy actual
render logs -s alexia-java-render -n 500 > current_logs.txt

# Después de un nuevo deploy
render logs -s alexia-java-render -n 500 > new_logs.txt

# Comparar
diff current_logs.txt new_logs.txt
```

---

## 🚀 Workflow Recomendado para Debugging

1. **Ver logs en tiempo real:**
   ```bash
   render logs -s alexia-java-render -f
   ```

2. **Si hay un error, guardarlo:**
   ```bash
   render logs -s alexia-java-render -n 1000 > debug_logs.txt
   ```

3. **Buscar la causa raíz:**
   ```bash
   grep -i "caused by" debug_logs.txt
   ```

4. **Verificar variables de entorno:**
   ```bash
   render env list -s alexia-java-render
   ```

5. **Hacer cambios y redesplegar:**
   ```bash
   # Localmente: hacer cambios y push a GitHub
   git add .
   git commit -m "fix: descripción del cambio"
   git push origin main

   # Render redesplegará automáticamente
   # O forzar deploy:
   render deploy -s alexia-java-render
   ```

6. **Monitorear el nuevo deploy:**
   ```bash
   render logs -s alexia-java-render -f
   ```

---

## 📚 Recursos Adicionales

- **Documentación de Render CLI**: https://render.com/docs/cli
- **Troubleshooting Deploys**: https://render.com/docs/troubleshooting-deploys
- **Database Connection Issues**: https://render.com/docs/databases#connection-issues

---

**Última actualización**: 2025-10-17
