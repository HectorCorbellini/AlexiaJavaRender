#!/bin/bash

# ⚠️  SCRIPT DEPRECADO - YA NO ES NECESARIO
#
# Este script eliminaba manualmente el webhook de Telegram.
# Ahora esta funcionalidad está integrada en el código Java y se ejecuta
# automáticamente al iniciar el bot desde la interfaz web.
#
# NUEVA FORMA:
# 1. Inicia la aplicación: ./scripts/run_linux.sh
# 2. Abre el navegador: http://localhost:8080
# 3. Ve a la sección "Telegram"
# 4. Click en "Iniciar Bot"
# 5. El webhook se eliminará automáticamente
#
# Si aún así necesitas ejecutar este script manualmente:

echo "⚠️  ADVERTENCIA: Este script está deprecado"
echo ""
echo "El webhook de Telegram ahora se elimina automáticamente en el código Java."
echo "Solo usa este script si tienes problemas y necesitas forzar la eliminación."
echo ""
read -p "¿Deseas continuar de todas formas? (s/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Ss]$ ]]; then
    echo "❌ Operación cancelada"
    exit 0
fi

# Cargar token desde .env
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | grep TELEGRAM_BOT_TOKEN | xargs)
fi

if [ -z "$TELEGRAM_BOT_TOKEN" ]; then
    echo "❌ Error: TELEGRAM_BOT_TOKEN no encontrado en .env"
    exit 1
fi

echo "🔧 Eliminando webhook de Telegram..."

# Eliminar webhook
RESPONSE=$(curl -s -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/deleteWebhook")

if echo "$RESPONSE" | grep -q '"ok":true'; then
    echo "✅ Webhook eliminado correctamente"
else
    echo "⚠️  Respuesta: $RESPONSE"
fi

echo ""
echo "Para verificar el estado del bot:"
curl -s -X GET "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/getMe" | jq '.'
