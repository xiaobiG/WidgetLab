#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

DEFAULT_JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
DEFAULT_SDK_ROOT="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"

VARIANT="debug"
BUILD_ONLY=0
SERIAL=""

usage() {
    cat <<EOF
Usage: ./build.sh [debug|release] [options]

Variants:
  debug     Build and install debug APK (default)
  release   Build and install release APK (local release uses debug signing)

Options:
  -s, --serial <id>   Target device serial (sets ANDROID_SERIAL)
  --build-only        Build APK only, skip adb install
  -h, --help          Show this help

Examples:
  ./build.sh
  ./build.sh release
  ./build.sh debug --build-only
  ./build.sh -s emulator-5554 release
EOF
}

while [ $# -gt 0 ]; do
    case "$1" in
        debug | release)
            VARIANT="$1"
            shift
            ;;
        -s | --serial)
            if [ $# -lt 2 ]; then
                echo "Error: $1 requires a device serial" >&2
                usage >&2
                exit 1
            fi
            SERIAL="$2"
            shift 2
            ;;
        --build-only)
            BUILD_ONLY=1
            shift
            ;;
        -h | --help)
            usage
            exit 0
            ;;
        *)
            echo "Error: unknown argument: $1" >&2
            usage >&2
            exit 1
            ;;
    esac
done

export JAVA_HOME="${JAVA_HOME:-$DEFAULT_JAVA_HOME}"

if [ ! -x "$JAVA_HOME/bin/java" ]; then
    echo "Error: JAVA_HOME is invalid or java is not executable" >&2
    echo "  JAVA_HOME=$JAVA_HOME" >&2
    echo "Hint: export JAVA_HOME=\"$DEFAULT_JAVA_HOME\"" >&2
    exit 1
fi

if [ ! -f "$SCRIPT_DIR/gradlew" ]; then
    echo "Error: gradlew not found in $SCRIPT_DIR" >&2
    exit 1
fi

if [ ! -x "$SCRIPT_DIR/gradlew" ]; then
    echo "Error: gradlew is not executable" >&2
    echo "Hint: chmod +x \"$SCRIPT_DIR/gradlew\"" >&2
    exit 1
fi

adb_hint() {
    echo "Hint: ensure platform-tools is in PATH, e.g." >&2
    echo "  export PATH=\"$DEFAULT_SDK_ROOT/platform-tools:\$PATH\"" >&2
}

check_adb_device() {
    if ! command -v adb >/dev/null 2>&1; then
        echo "Error: adb not found in PATH" >&2
        adb_hint
        exit 1
    fi

    local devices_output ready_devices ready_count line serial state
    devices_output="$(adb devices 2>&1)" || {
        echo "Error: adb devices failed" >&2
        echo "$devices_output" >&2
        adb_hint
        exit 1
    }

    if echo "$devices_output" | awk 'NR > 1 && $2 == "unauthorized" { found=1 } END { exit !found }'; then
        echo "Error: unauthorized device(s) — accept USB debugging on the device" >&2
        echo "$devices_output" >&2
        exit 1
    fi

    if echo "$devices_output" | awk 'NR > 1 && $2 == "offline" { found=1 } END { exit !found }'; then
        echo "Error: offline device(s) — reconnect cable or restart adb server" >&2
        echo "$devices_output" >&2
        echo "Hint: adb kill-server && adb start-server" >&2
        exit 1
    fi

    ready_devices="$(echo "$devices_output" | awk 'NR > 1 && $2 == "device" { print $1 }')"
    ready_count="$(printf '%s\n' "$ready_devices" | grep -c . || true)"

    if [ "$ready_count" -eq 0 ]; then
        echo "Error: no ready adb device (state must be 'device')" >&2
        echo "$devices_output" >&2
        adb_hint
        exit 1
    fi

    if [ -n "$SERIAL" ]; then
        if ! echo "$ready_devices" | grep -Fxq "$SERIAL"; then
            echo "Error: device '$SERIAL' not found or not ready" >&2
            echo "$devices_output" >&2
            exit 1
        fi
        export ANDROID_SERIAL="$SERIAL"
        echo "==> Target device: $SERIAL"
    elif [ "$ready_count" -gt 1 ]; then
        echo "Error: multiple devices connected — specify one with -s <serial>" >&2
        echo "$devices_output" >&2
        echo "Ready devices:" >&2
        printf '  %s\n' $ready_devices >&2
        exit 1
    else
        SERIAL="$(printf '%s\n' "$ready_devices" | head -n 1)"
        export ANDROID_SERIAL="$SERIAL"
        echo "==> Target device: $SERIAL"
    fi
}

case "$VARIANT" in
    debug)
        if [ "$BUILD_ONLY" -eq 1 ]; then
            GRADLE_TASK=":app:assembleDebug"
        else
            GRADLE_TASK=":app:installDebug"
        fi
        ;;
    release)
        if [ "$BUILD_ONLY" -eq 1 ]; then
            GRADLE_TASK=":app:assembleRelease"
        else
            GRADLE_TASK=":app:installRelease"
        fi
        ;;
esac

if [ "$BUILD_ONLY" -eq 0 ]; then
    check_adb_device
fi

echo "==> JAVA_HOME=$JAVA_HOME"
if [ "$BUILD_ONLY" -eq 1 ]; then
    echo "==> Building $VARIANT (no install) ..."
else
    echo "==> Building and installing $VARIANT ..."
fi

./gradlew "$GRADLE_TASK" --no-daemon

if [ "$BUILD_ONLY" -eq 1 ]; then
    APK_DIR="app/build/outputs/apk/$VARIANT"
    echo "==> Done: $VARIANT APK built"
    if [ -d "$APK_DIR" ]; then
        echo "==> Output: $APK_DIR"
        ls -1 "$APK_DIR"/*.apk 2>/dev/null || true
    fi
else
    echo "==> Done: $VARIANT installed on $ANDROID_SERIAL"
fi
