#!/usr/bin/env bash
# Stop hook: when Claude finishes, check the code style of the Kotlin files that changed.
# Exit 0 lets Claude stop. Exit 2 sends the violations back to Claude, which fixes them and stops again.
set -u

cd "$(dirname "$0")/../.." || exit 0

# The hook fires again after Claude answers its own feedback. Stopping then avoids a loop on a violation it cannot fix.
# grep instead of jq, so the guard still works on a machine without jq.
if grep -qE '"stop_hook_active"[[:space:]]*:[[:space:]]*true'; then
  exit 0
fi

# Nothing to check when no Kotlin file changed since the last commit: a Gradle run would only cost time.
if ! git status --porcelain | grep -qE '\.kts?$'; then
  exit 0
fi

output=$(./gradlew ktlintCheck --console=plain 2>&1)
if [ $? -eq 0 ]; then
  exit 0
fi

violations=$(echo "$output" | grep -E '\.kts?:[0-9]+:[0-9]+')
{
  if [ -n "$violations" ]; then
    echo "ktlint found style violations. Run ./gradlew ktlintFormat, then fix what it could not:"
    echo "$violations"
  else
    # The build failed for another reason (no violation lines): show the end of the log instead of nothing.
    echo "./gradlew ktlintCheck failed without style violations:"
    echo "$output" | tail -20
  fi
} >&2
exit 2
