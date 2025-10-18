# Render CLI Installation and Usage Guide (Actualizado)

## 1. Install Go (required)

```bash
# Descargar y extraer Go (requiere sudo)
curl -LO https://go.dev/dl/go1.25.0.linux-amd64.tar.gz
sudo rm -rf /usr/local/go
sudo tar -C /usr/local -xzf go1.25.0.linux-amd64.tar.gz
rm go1.25.0.linux-amd64.tar.gz

# Agregar Go al PATH temporalmente (agrega a tu ~/.bashrc para hacerlo permanente)
export PATH=$PATH:/usr/local/go/bin
```

## 2. Install Render CLI

```bash
# Clona el repositorio oficial
git clone https://github.com/render-oss/cli.git
cd cli

# Construye el ejecutable (requiere sudo)
sudo /usr/local/go/bin/go build -o /usr/local/bin/render
```
## 3. Configuración Inicial

```bash
# Inicia sesión en tu cuenta de Render
render login

# Configura tu workspace (reemplaza "My Workspace" con el tuyo)
render workspace set "My Workspace" --output json
cat logs.txt | grep ERROR
```

---

Para más información, consulta la documentación oficial: https://render.com/docs/cli

To install and use the Render CLI on Linux Mint or other systems, the easiest method is to run the official installer script, which works for both Linux and macOS.  
Open your terminal and execute the following command:

```
curl -fsSL https://raw.githubusercontent.com/render-oss/cli/refs/heads/main/bin/install.sh | sh
```

After the installation finishes, verify that it was successful:

```
render --version
render services
```

If the version number appears, the CLI is properly installed.

---

## 2. Manual Installation (Alternative)

This method is useful for continuous integration or when you want to use a specific version.

```
curl -L https://github.com/render-oss/cli/releases/download/v1.1.0/cli_1.1.0_linux_amd64.zip -o render.zip
unzip render.zip
sudo mv cli_v1.1.0 /usr/local/bin/render
sudo chmod +x /usr/local/bin/render
```

Then confirm installation:

```
render --version
```

---

## 3. Homebrew Installation

If you have Homebrew installed, you can install Render CLI easily:

```
brew update
brew install render
```

This works on macOS and on Linux distributions that support Homebrew.

---

## 4. Logging In and Authentication

### Interactive Login (Normal Use)

```
render login
```

This opens your default browser and lets you authenticate with your Render account.

### Non-Interactive (For Automation or CI)

1. Create an API key from your Render Dashboard → *Account Settings* → *API Keys*.
2. Set it as an environment variable:

```
export RENDER_API_KEY="rnd_XXXXXXXXXXXXXXXX"
```

Add this line to your `~/.bashrc` or `~/.profile` if you want the key to load automatically every time.

---

## 5. Useful Commands

```
render services                # List all services in your current workspace
render deploys create <ID>     # Trigger a deployment
render psql <DATABASE_ID>      # Open a PostgreSQL session to a Render database
render workspace set <ID>      # Switch between workspaces
```

Use `--output json` for automation and `--confirm` to skip prompts.

---

## 6. Linux Mint Specific Tips

- If `render` isn’t recognized, make sure `/usr/local/bin` is in your PATH.
- If `render login` fails to open a browser, authenticate manually with an API key.

---

## 7. Troubleshooting

- **Problem:** `command not found`  
  **Solution:** Ensure `/usr/local/bin` is in your PATH.

- **Problem:** Browser doesn’t open during login  
  **Solution:** Use API key authentication.

- **Problem:** Using in CI  
  **Solution:** Always use API keys, not interactive logins.

---

## 8. Summary

Installing the Render CLI is straightforward with the official script. Once it’s set up, you can manage your services, deployments, and databases directly from the terminal.  
For automated environments, use an API key instead of interactive login.  
On Linux Mint, ensure the binary path is correct, and you’ll be ready to control your Render apps efficiently from the command line.
