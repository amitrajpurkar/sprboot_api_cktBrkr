#!/bin/bash

# ============================================================
# start-all.sh - Start Backend and Frontend servers
# 
# Defaults:
#   Backend:  Spring Boot (Gradle) on http://localhost:8080
#   Frontend: React (npm)          on http://localhost:3000
#
# The script checks port availability first. If a default port
# is occupied, it automatically finds the next available port.
#
# Press Ctrl+C to shut down both servers gracefully.
# ============================================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

DEFAULT_BACKEND_PORT=8080
DEFAULT_FRONTEND_PORT=3000

# PIDs for cleanup
BACKEND_PID=""
FRONTEND_PID=""

# Flag to track if we patched package.json proxy
PROXY_PATCHED=false
ORIGINAL_PROXY_PORT=""

# ----------------------------------------------------------
# Check if a port is available (returns 0 if available)
# ----------------------------------------------------------
is_port_available() {
    local port=$1
    if lsof -i :"$port" -sTCP:LISTEN >/dev/null 2>&1; then
        return 1  # port is in use
    fi
    return 0  # port is available
}

# ----------------------------------------------------------
# Find an available port starting from the given port
# ----------------------------------------------------------
find_available_port() {
    local port=$1
    local max_attempts=20
    local attempt=0

    while [ $attempt -lt $max_attempts ]; do
        if is_port_available "$port"; then
            echo "$port"
            return 0
        fi
        local occupant
        occupant=$(lsof -i :"$port" -sTCP:LISTEN -t 2>/dev/null | head -1)
        local proc_name
        proc_name=$(ps -p "$occupant" -o comm= 2>/dev/null)
        echo "  Port $port is in use by PID $occupant ($proc_name)" >&2
        port=$((port + 1))
        attempt=$((attempt + 1))
    done

    echo ""
    return 1  # could not find an available port
}

# ----------------------------------------------------------
# Cleanup: stop servers, restore package.json if patched
# ----------------------------------------------------------
cleanup() {
    echo ""
    echo "============================================"
    echo "Shutting down servers..."
    echo "============================================"

    if [ -n "$FRONTEND_PID" ] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
        echo "Stopping Frontend (PID: $FRONTEND_PID)..."
        kill "$FRONTEND_PID" 2>/dev/null
        wait "$FRONTEND_PID" 2>/dev/null
        echo "Frontend stopped."
    fi

    if [ -n "$BACKEND_PID" ] && kill -0 "$BACKEND_PID" 2>/dev/null; then
        echo "Stopping Backend (PID: $BACKEND_PID)..."
        kill "$BACKEND_PID" 2>/dev/null
        wait "$BACKEND_PID" 2>/dev/null
        echo "Backend stopped."
    fi

    # Restore package.json proxy if we patched it
    if [ "$PROXY_PATCHED" = true ] && [ -n "$ORIGINAL_PROXY_PORT" ]; then
        echo "Restoring frontend proxy to original port $ORIGINAL_PROXY_PORT ..."
        sed -i '' "s|\"proxy\": \"http://localhost:${BACKEND_PORT}\"|\"proxy\": \"http://localhost:${ORIGINAL_PROXY_PORT}\"|" \
            "$SCRIPT_DIR/frontend/package.json"
        echo "package.json restored."
    fi

    echo "All servers shut down. Goodbye!"
    exit 0
}

trap cleanup SIGINT SIGTERM

echo "============================================"
echo "  Starting Full-Stack Application"
echo "============================================"
echo ""

# ----------------------------------------------------------
# Resolve backend port
# ----------------------------------------------------------
echo "[Port Check] Checking backend port $DEFAULT_BACKEND_PORT ..."
BACKEND_PORT=$(find_available_port $DEFAULT_BACKEND_PORT)
if [ -z "$BACKEND_PORT" ]; then
    echo "[ERROR] Could not find an available port for the backend (tried $DEFAULT_BACKEND_PORT–$((DEFAULT_BACKEND_PORT + 19))). Aborting."
    exit 1
fi
if [ "$BACKEND_PORT" -ne "$DEFAULT_BACKEND_PORT" ]; then
    echo "[Port Check] Backend default port $DEFAULT_BACKEND_PORT is busy. Will use port $BACKEND_PORT instead."
else
    echo "[Port Check] Backend port $BACKEND_PORT is available."
fi
echo ""

# ----------------------------------------------------------
# Resolve frontend port
# ----------------------------------------------------------
echo "[Port Check] Checking frontend port $DEFAULT_FRONTEND_PORT ..."
FRONTEND_PORT=$(find_available_port $DEFAULT_FRONTEND_PORT)
if [ -z "$FRONTEND_PORT" ]; then
    echo "[ERROR] Could not find an available port for the frontend (tried $DEFAULT_FRONTEND_PORT–$((DEFAULT_FRONTEND_PORT + 19))). Aborting."
    exit 1
fi
if [ "$FRONTEND_PORT" -ne "$DEFAULT_FRONTEND_PORT" ]; then
    echo "[Port Check] Frontend default port $DEFAULT_FRONTEND_PORT is busy. Will use port $FRONTEND_PORT instead."
else
    echo "[Port Check] Frontend port $FRONTEND_PORT is available."
fi
echo ""

# ----------------------------------------------------------
# Patch frontend proxy if backend port changed
# ----------------------------------------------------------
if [ "$BACKEND_PORT" -ne "$DEFAULT_BACKEND_PORT" ]; then
    ORIGINAL_PROXY_PORT=$DEFAULT_BACKEND_PORT
    echo "[Config] Updating frontend proxy in package.json to point to backend port $BACKEND_PORT ..."
    sed -i '' "s|\"proxy\": \"http://localhost:${DEFAULT_BACKEND_PORT}\"|\"proxy\": \"http://localhost:${BACKEND_PORT}\"|" \
        "$SCRIPT_DIR/frontend/package.json"
    PROXY_PATCHED=true
    echo "[Config] Frontend proxy updated (will be restored on shutdown)."
    echo ""
fi

# --- Start Backend ---
echo "[Backend] Starting Spring Boot on http://localhost:$BACKEND_PORT ..."
cd "$SCRIPT_DIR" && ./gradlew bootRun --args="--server.port=$BACKEND_PORT" 2>&1 | sed 's/^/[Backend]  /' &
BACKEND_PID=$!
echo "[Backend] PID: $BACKEND_PID"
echo ""

# Give backend a few seconds head-start
sleep 5

# --- Start Frontend ---
echo "[Frontend] Starting React on http://localhost:$FRONTEND_PORT ..."
cd "$SCRIPT_DIR/frontend" && PORT=$FRONTEND_PORT npm start 2>&1 | sed 's/^/[Frontend] /' &
FRONTEND_PID=$!
echo "[Frontend] PID: $FRONTEND_PID"
echo ""

echo "============================================"
echo "  Both servers are starting up."
echo "  Backend:  http://localhost:$BACKEND_PORT"
echo "  Frontend: http://localhost:$FRONTEND_PORT"
echo "  Swagger:  http://localhost:$BACKEND_PORT/swagger-ui.html"
echo ""
echo "  Press Ctrl+C to stop all servers."
echo "============================================"
echo ""

# Wait for both background processes; if either exits, keep waiting for the other
wait
