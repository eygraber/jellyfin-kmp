#!/bin/bash
# Block dangerous git commands per git-workflow.md rules:
# - Never use git merge (use git rebase instead)
# - Never git push --force to main/master
# - Never git reset --hard without explicit request

set -e

# Read JSON input from stdin
INPUT=$(cat)
COMMAND=$(echo "$INPUT" | jq -r '.tool_input.command // empty')

if [ -z "$COMMAND" ]; then
  exit 0
fi

# Block git merge (should use git rebase instead)
if echo "$COMMAND" | grep -qE '\bgit\s+merge\b'; then
  echo "Blocked: git merge is not allowed. Use 'git rebase <target-branch>' instead per git-workflow.md rules." >&2
  exit 2
fi

# Block git push --force to main/master
if echo "$COMMAND" | grep -qE '\bgit\s+push\s+.*--force.*\s*(main|master)\b|\bgit\s+push\s+--force\s+(origin\s+)?(main|master)\b'; then
  echo "Blocked: Force push to main/master is dangerous and not allowed." >&2
  exit 2
fi

# Block git reset --hard (unless it's a very targeted use)
if echo "$COMMAND" | grep -qE '\bgit\s+reset\s+--hard\b'; then
  echo "Blocked: git reset --hard is destructive. This command discards all local changes permanently. If you specifically need this, ask the user for explicit confirmation first." >&2
  exit 2
fi

# Block git clean -f (destructive)
if echo "$COMMAND" | grep -qE '\bgit\s+clean\s+-[a-zA-Z]*f'; then
  echo "Blocked: git clean -f is destructive and removes untracked files permanently. Ask the user for explicit confirmation first." >&2
  exit 2
fi

# Block git checkout . or git restore . (discards all changes)
if echo "$COMMAND" | grep -qE '\bgit\s+(checkout|restore)\s+\.\s*$'; then
  echo "Blocked: This command discards all uncommitted changes. Ask the user for explicit confirmation first." >&2
  exit 2
fi

exit 0
