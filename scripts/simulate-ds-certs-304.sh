#!/usr/bin/env bash
#
# simulate-ds-certs-304.sh
#
# Ages the SDK's cached 3DS2 DS-certificate bundle so the next payment-screen
# prefetch treats it as stale and issues a *conditional* request
# (If-None-Match / If-Modified-Since). If the CDN content is unchanged the
# server answers 304 Not Modified and the repository just bumps `fetchedAt`
# while keeping the cached entries — which is the path this script lets you
# exercise on a real device/emulator without waiting out the 24h TTL.
#
# Requires: a *debuggable* build of the examples app installed, and adb on PATH.
# Pure bash + adb — no perl/python/jq.
#
# Usage:
#   scripts/simulate-ds-certs-304.sh                # age cache (fetchedAt = 0)
#   scripts/simulate-ds-certs-304.sh 1700000000000  # set fetchedAt to given epoch-ms
#   scripts/simulate-ds-certs-304.sh show           # print current cache metadata
#   PKG=com.example.app scripts/simulate-ds-certs-304.sh
#
set -euo pipefail

PKG="${PKG:-com.judokit.android.examples}"
PREFS="shared_prefs/judokit_ds_certs.xml"
ARG="${1:-0}"

die() { echo "error: $*" >&2; exit 1; }

command -v adb >/dev/null || die "adb not found on PATH"
[ "$(adb get-state 2>/dev/null || true)" = "device" ] \
    || die "need exactly one device/emulator (see: adb devices; set ANDROID_SERIAL to pick one)"
adb shell pm path "$PKG" >/dev/null 2>&1 || die "package '$PKG' is not installed"

# Read the prefs file as the app user. `run-as <pkg> cat <relative-path>` needs
# no device-side shell, so there is nothing for adb's arg re-splitting to break.
# run-as' cwd is the app's data dir, hence the relative path.
read_prefs() { adb exec-out run-as "$PKG" cat "$PREFS" 2>/dev/null; }

# Write stdin back to the prefs file *as the app user*. The redirection has to
# happen inside the run-as'd shell, so the whole snippet is single-quoted at the
# device-shell level ("'...'") — otherwise adb hands `>` to the plain shell user
# and the write is denied.
write_prefs() { adb shell run-as "$PKG" sh -c "'cat > $PREFS'"; }

# Pull one JSON scalar out of $CONTENT_NORM (quotes already normalised to ").
# $1 = key name. Echoes the value, or nothing if absent.
json_scalar() {
    local key="$1" re
    re="\"$key\"[[:space:]]*:[[:space:]]*\"([^\"]*)\""          # "key":"string"
    if [[ $CONTENT_NORM =~ $re ]]; then printf '%s\n' "${BASH_REMATCH[1]}"; return; fi
    re="\"$key\"[[:space:]]*:[[:space:]]*(-?[0-9]+)"            # "key":number
    if [[ $CONTENT_NORM =~ $re ]]; then printf '%s\n' "${BASH_REMATCH[1]}"; fi
}

show_meta() {
    local k v
    for k in etag lastModified fetchedAt maxAgeMs; do
        v="$(json_scalar "$k")"
        printf '  %-13s %s\n' "$k" "${v:-<absent>}"
    done
}

# --- load current cache ------------------------------------------------------
CONTENT="$(read_prefs || true)"
CONTENT="${CONTENT//$'\r'/}"                                   # strip stray CRs
if [ -z "$CONTENT" ]; then
    if adb shell run-as "$PKG" true 2>&1 | grep -q "not debuggable"; then
        die "package '$PKG' is not debuggable — run-as cannot reach its files. Install a debug build."
    fi
    die "DS-cert cache not found or empty ($PREFS).
Run a card payment in the app once so the SDK fetches and caches the certs, then re-run."
fi

# Normalised copy for parsing: &quot; / &#34; -> "
CONTENT_NORM="${CONTENT//&quot;/\"}"
CONTENT_NORM="${CONTENT_NORM//&#34;/\"}"

if [ "$ARG" = "show" ]; then
    echo "current DS-cert cache ($PKG):"
    show_meta
    exit 0
fi

case "$ARG" in
    ''|*[!0-9]*) die "fetchedAt must be an epoch-ms integer (or 'show'); got '$ARG'" ;;
esac

CURRENT="$(json_scalar fetchedAt)"
[ -n "$CURRENT" ] || die "could not find 'fetchedAt' in $PREFS — cache JSON layout may have shifted"

echo "before:"
show_meta

# --- stop app so the edit is not masked by its in-memory prefs / clobbered --
echo "> force-stopping $PKG"
adb shell am force-stop "$PKG"

# --- rewrite fetchedAt (literal replace, every quote flavour) ---------------
NEW_CONTENT="$CONTENT"
for q in '"' '&quot;' '&#34;'; do
    NEW_CONTENT="${NEW_CONTENT//${q}fetchedAt${q}:${CURRENT}/${q}fetchedAt${q}:${ARG}}"
done
[ "$NEW_CONTENT" != "$CONTENT" ] || die "rewrite produced no change — cache JSON layout may have shifted"

# --- push it back ----------------------------------------------------------
echo "> writing $PREFS"
printf '%s' "$NEW_CONTENT" | write_prefs

# --- verify ----------------------------------------------------------------
CONTENT="$(read_prefs || true)"; CONTENT="${CONTENT//$'\r'/}"
CONTENT_NORM="${CONTENT//&quot;/\"}"; CONTENT_NORM="${CONTENT_NORM//&#34;/\"}"
got="$(json_scalar fetchedAt)"
[ "$got" = "$ARG" ] || die "write-back verification failed (fetchedAt is '$got', expected '$ARG')"
echo "after:"
show_meta

cat <<EOF

Done. fetchedAt = ${ARG} (stale).

Next: open the app and start a card payment. On the payment screen the DS-cert
prefetch runs, sees a stale cache and sends:
    If-None-Match: <etag>
    If-Modified-Since: <lastModified>
Unchanged CDN content -> HTTP 304 -> entries kept, fetchedAt bumped to ~now.

Watch the '.../judokit/ds-certs' call in Chucker, then verify with:
    scripts/simulate-ds-certs-304.sh show
EOF
