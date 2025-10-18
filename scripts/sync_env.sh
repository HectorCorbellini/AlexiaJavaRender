#!/bin/bash

# Script to sync environment variables from a local .env.production file to Render.

# --- Configuration ---
# The name of your service on Render.
SERVICE_NAME="alexia-java-render"

# The local environment file to read from.
ENV_FILE=".env.production"

# --- Script ---

set -e # Exit immediately if a command exits with a non-zero status.

if ! command -v render &> /dev/null; then
    echo "Error: Render CLI is not installed. Please install it first."
    exit 1
fi

if [ -z "$RENDER_API_KEY" ]; then
    echo "Error: RENDER_API_KEY environment variable is not set."
    echo "Please get your API key from https://dashboard.render.com/account/api-keys"
    exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
    echo "Error: Environment file not found at '$ENV_FILE'"
    exit 1
fi

echo "Syncing environment variables to Render service '$SERVICE_NAME'..."

# Read the .env file, filter out comments and empty lines, and update Render
while IFS='=' read -r key value || [ -n "$key" ]; do
    # Skip comments and empty lines
    if [[ "$key" =~ ^# ]] || [ -z "$key" ]; then
        continue
    fi

    echo "- Setting variable: $key"
    render env update --service "$SERVICE_NAME" --key "$key" --value "$value"

done < "$ENV_FILE"

echo "
✅ Environment variables synced successfully!"
