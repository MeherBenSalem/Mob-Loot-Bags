#!/usr/bin/env bash
# Start or stop dev servers/clients for QA under Xvfb with software rendering.
#   tools/qa/devrun.sh start-server <workspace> <loader>
#   tools/qa/devrun.sh start-client <workspace> <loader>
#   tools/qa/devrun.sh stop-client | stop-server
set -u
LOGS=${QA_LOGS:-/tmp/mlb-qa}
mkdir -p "$LOGS"
ROOT=$(cd "$(dirname "$0")/../.." && pwd)
JAVA_HOME=${JAVA25_HOME:-/opt/jdks/jdk-25.0.5+8}
export JAVA_HOME
case "$1" in
  start-server)
    cd "$ROOT/$2" && nohup ./gradlew ":$3:runServer" --no-daemon > "$LOGS/$3-server.log" 2>&1 &
    ;;
  start-client)
    # 26.3+ windows through SDL3, which only finds a usable visual on Xvfb through EGL.
    export DISPLAY=${DISPLAY:-:1} LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe SDL_VIDEO_FORCE_EGL=1 ${QA_CLIENT_ENV:-}
    cd "$ROOT/$2" && nohup ./gradlew ":$3:runQaClient" --no-daemon > "$LOGS/$3-client.log" 2>&1 &
    ;;
  stop-client|stop-server)
    # Only java processes are considered, so this never matches the calling shell.
    want=$([ "$1" = stop-client ] && echo 'KnotClient|clientQaClient|runQaClient|--quickPlayMultiplayer' || echo 'KnotServer|runServer')
    for pid in $(pgrep -x java); do
      if tr '\0' ' ' < "/proc/$pid/cmdline" | grep -qE "$want"; then kill "$pid"; fi
    done
    ;;
esac
