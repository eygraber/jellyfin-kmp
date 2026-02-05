#!/bin/bash
# Run ./check before git push per CLAUDE.md:
# "ALWAYS run ./check before creating PRs"

set -e

# Read JSON input from stdin
INPUT=$(cat)
COMMAND=$(echo "$INPUT" | jq -r '.tool_input.command // empty')

if [ -z "$COMMAND" ]; then
  exit 0
fi

# Only intercept git push commands
if ! echo "$COMMAND" | grep -qE '\bgit\s+push\b'; then
  exit 0
fi

cd "$CLAUDE_PROJECT_DIR"

# Run ./check and capture result
if ! ./check; then
  echo "Blocked: ./check failed. Fix the issues before pushing." >&2
  exit 2
fi

exit 0
