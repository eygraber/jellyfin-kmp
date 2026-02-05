#!/bin/bash
# Auto-format Kotlin files after edit per code-quality.md:
# "Format violations should be fixed automatically with ./format script"

set -e

# Read JSON input from stdin
INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# Only format Kotlin files
if ! echo "$FILE_PATH" | grep -qE '\.kt$|\.kts$'; then
  exit 0
fi

# Run the project's format script
cd "$CLAUDE_PROJECT_DIR"
./format 2>/dev/null || true

exit 0
