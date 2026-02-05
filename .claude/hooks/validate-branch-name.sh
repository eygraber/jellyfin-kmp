#!/bin/bash
# Validate branch naming per git-workflow.md:
# Branch naming: <Issue#>-<brief-description>
# Example: 123-add-user-profile

set -e

# Read JSON input from stdin
INPUT=$(cat)
COMMAND=$(echo "$INPUT" | jq -r '.tool_input.command // empty')

if [ -z "$COMMAND" ]; then
  exit 0
fi

# Check if this is a git checkout -b or git branch command (creating a new branch)
if ! echo "$COMMAND" | grep -qE '\bgit\s+(checkout\s+-b|branch)\s+'; then
  exit 0
fi

# Extract the branch name
BRANCH_NAME=$(echo "$COMMAND" | sed -E 's/.*git\s+(checkout\s+-b|branch)\s+([^ ]+).*/\2/')

# Skip if we couldn't extract a branch name
if [ -z "$BRANCH_NAME" ] || [ "$BRANCH_NAME" = "$COMMAND" ]; then
  exit 0
fi

# Allow master branches
if echo "$BRANCH_NAME" | grep -qE 'master'; then
  exit 0
fi

# Validate branch name follows <issue#>-<description> pattern
# Issue number, dash, then 1-4 kebab-case words
if ! echo "$BRANCH_NAME" | grep -qE '^[0-9]+-[a-z0-9]+(-[a-z0-9]+){0,3}$'; then
  # Output warning for Claude
  cat << EOF
{
  "continue": true,
  "systemMessage": "Note: Branch name '$BRANCH_NAME' doesn't follow the convention '<issue#>-<brief-description>' (e.g., 123-add-user-profile). Branch names should start with the GitHub issue number followed by a brief kebab-case description (3-4 words max)."
}
EOF
  exit 0
fi

exit 0
