#!/bin/bash
# Run detekt after editing Kotlin files per code-quality.md:
# "Use Detekt for static analysis"

set -e

# Read JSON input from stdin
INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# Only run for Kotlin files
if ! echo "$FILE_PATH" | grep -qE '\.kt$'; then
  exit 0
fi

# Skip test files - detekt is less critical there
if echo "$FILE_PATH" | grep -qE '/test/|/androidTest/'; then
  exit 0
fi

cd "$CLAUDE_PROJECT_DIR"

# Run detekt (the fast version without type resolution)
./gradlew detektAll 2>/dev/null || true

exit 0
