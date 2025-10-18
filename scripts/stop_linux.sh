#!/bin/bash

# Script para detener completamente la aplicación Alexia
# Detiene todos los procesos relacionados de forma segura y limpia conexiones

echo "🛑 Deteniendo aplicación Alexia..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 1. Buscar procesos de spring-boot:run
echo "🔍 Buscando procesos de Maven/Spring Boot..."
SPRING_PIDS=$(pgrep -f "spring-boot:run")

if [ -n "$SPRING_PIDS" ]; then
    echo "⚠️  Encontrados procesos Maven/Spring Boot (PIDs: $SPRING_PIDS)"
    echo "🛑 Deteniendo procesos Maven/Spring Boot (SIGTERM)..."
    pkill -15 -f "spring-boot:run"
    sleep 5
    
    # Verificar si se detuvieron
    REMAINING=$(pgrep -f "spring-boot:run")
    if [ -n "$REMAINING" ]; then
        echo "⚠️  Algunos procesos no respondieron, forzando cierre (SIGKILL)..."
        pkill -9 -f "spring-boot:run"
        sleep 2
    fi
    echo "✓ Procesos Maven/Spring Boot detenidos"
else
    echo "✓ No hay procesos Maven/Spring Boot en ejecución"
fi

# 2. Buscar procesos de AlexiaApplication
echo ""
echo "🔍 Buscando procesos de AlexiaApplication..."
ALEXIA_PIDS=$(pgrep -f "AlexiaApplication")

if [ -n "$ALEXIA_PIDS" ]; then
    echo "⚠️  Encontrados procesos AlexiaApplication (PIDs: $ALEXIA_PIDS)"
    echo "🛑 Deteniendo procesos AlexiaApplication (SIGTERM)..."
    pkill -15 -f "AlexiaApplication"
    sleep 5
    
    # Verificar si se detuvieron
    REMAINING=$(pgrep -f "AlexiaApplication")
    if [ -n "$REMAINING" ]; then
        echo "⚠️  Algunos procesos no respondieron, forzando cierre (SIGKILL)..."
        pkill -9 -f "AlexiaApplication"
        sleep 2
    fi
    echo "✓ Procesos AlexiaApplication detenidos"
else
    echo "✓ No hay procesos AlexiaApplication en ejecución"
fi

# 3. Buscar procesos Java relacionados con el proyecto
echo ""
echo "🔍 Buscando otros procesos Java del proyecto..."
JAVA_PIDS=$(ps aux | grep -i "javaDos-" | grep -v grep | awk '{print $2}')

if [ -n "$JAVA_PIDS" ]; then
    echo "⚠️  Encontrados procesos Java del proyecto (PIDs: $JAVA_PIDS)"
    echo "🛑 Deteniendo procesos Java del proyecto (SIGTERM)..."
    echo "$JAVA_PIDS" | xargs kill -15 2>/dev/null
    sleep 5
    
    # Verificar si se detuvieron
    REMAINING=$(ps aux | grep -i "javaDos-" | grep -v grep | awk '{print $2}')
    if [ -n "$REMAINING" ]; then
        echo "⚠️  Algunos procesos no respondieron, forzando cierre (SIGKILL)..."
        echo "$REMAINING" | xargs kill -9 2>/dev/null
        sleep 2
    fi
    echo "✓ Procesos Java del proyecto detenidos"
else
    echo "✓ No hay otros procesos Java del proyecto en ejecución"
fi

# 4. Limpiar conexiones de red en TIME_WAIT (opcional, requiere permisos)
echo ""
echo "🔍 Verificando conexiones de red..."
CONNECTIONS=$(netstat -an 2>/dev/null | grep -E "8080|ESTABLISHED|TIME_WAIT" | grep -v "LISTEN" | wc -l)
if [ "$CONNECTIONS" -gt 0 ]; then
    echo "⚠️  Encontradas $CONNECTIONS conexiones activas en puerto 8080"
    echo "💡 Estas conexiones se limpiarán automáticamente en 1-2 minutos"
else
    echo "✓ No hay conexiones activas en puerto 8080"
fi

# 5. Esperar un momento para que Telegram libere la sesión
echo ""
echo "⏳ Esperando 3 segundos para liberar recursos..."
sleep 3

# 6. Verificación final
echo ""
echo "🔍 Verificación final..."
ALL_PIDS=$(pgrep -f "spring-boot:run|AlexiaApplication|javaDos-")

if [ -z "$ALL_PIDS" ]; then
    echo "✅ Todos los procesos de Alexia han sido detenidos correctamente"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "✓ Aplicación Alexia detenida completamente"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "💡 Recomendación: Espera 30 segundos antes de volver a ejecutar"
    echo "   para que Telegram libere completamente la sesión del bot."
    echo ""
    exit 0
else
    echo "⚠️  ADVERTENCIA: Aún quedan procesos en ejecución (PIDs: $ALL_PIDS)"
    echo ""
    echo "Detalles de los procesos:"
    ps aux | grep -E "spring-boot:run|AlexiaApplication|javaDos-" | grep -v grep
    echo ""
    echo "Para forzar el cierre de todos los procesos, ejecuta:"
    echo "  kill -9 $ALL_PIDS"
    echo ""
    echo "O reinicia el sistema si el problema persiste."
    exit 1
fi
