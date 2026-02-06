#!/bin/bash
# Get all issues matching a scope definition
# Usage: ./get-scope-issues.sh "<scope>"
#
# Arguments:
#   scope   Scope definition: "epic #N", "label:X", "all ready", "bug", etc.
#
# Output: JSON array of issues with number, title, status, priority, labels

set -e

OWNER="eygraber"
REPO="jellyfin-kmp"
PROJECT_NUMBER=6
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
START_WORK_SCRIPTS="$SCRIPT_DIR/../../start-work/scripts"

SCOPE="$1"

if [[ -z "$SCOPE" ]]; then
  echo "Usage: $0 \"<scope>\"" >&2
  echo "Examples:" >&2
  echo "  $0 \"epic #1\"" >&2
  echo "  $0 \"label:area:auth\"" >&2
  echo "  $0 \"all ready\"" >&2
  echo "  $0 \"bug\"" >&2
  exit 1
fi

# Get all project items
ALL_ITEMS=$("$START_WORK_SCRIPTS/list-project-items.sh" 2>/dev/null || echo "[]")

# Parse scope and filter
SCOPE_LOWER=$(echo "$SCOPE" | tr '[:upper:]' '[:lower:]')

# Check for epic scope
if [[ "$SCOPE_LOWER" =~ epic[[:space:]]*#?([0-9]+) ]]; then
  EPIC_NUMBER="${BASH_REMATCH[1]}"
  # Get epic subissues
  SUBISSUES=$("$START_WORK_SCRIPTS/get-epic-subissues.sh" "$EPIC_NUMBER" 2>/dev/null || echo "[]")

  # Filter project items to only epic subissues
  echo "$ALL_ITEMS" | jq --argjson subs "$SUBISSUES" '
    [.[] | select(.number as $n | $subs | index($n))]
  '
  exit 0
fi

# Check for "all ready" scope
if [[ "$SCOPE_LOWER" == "all ready" ]] || [[ "$SCOPE_LOWER" == "all" ]]; then
  # Return all items in Ready status (or all if just "all")
  if [[ "$SCOPE_LOWER" == "all ready" ]]; then
    echo "$ALL_ITEMS" | jq '[.[] | select(.status == "Ready")]'
  else
    echo "$ALL_ITEMS"
  fi
  exit 0
fi

# Check for label scope (label:X or type shorthand)
LABEL_FILTER=""
case "$SCOPE_LOWER" in
  label:*)
    LABEL_FILTER="${SCOPE#label:}"
    ;;
  bug)
    LABEL_FILTER="bug"
    ;;
  feature)
    LABEL_FILTER="type:feature"
    ;;
  task)
    LABEL_FILTER="type:task"
    ;;
  chore)
    LABEL_FILTER="type:chore"
    ;;
  priority:p[0-3])
    LABEL_FILTER="$SCOPE_LOWER"
    ;;
esac

if [[ -n "$LABEL_FILTER" ]]; then
  echo "$ALL_ITEMS" | jq --arg label "$LABEL_FILTER" '
    [.[] | select(.labels | any(. == $label))]
  '
  exit 0
fi

# Default: treat as keyword search in title
echo "$ALL_ITEMS" | jq --arg keyword "$SCOPE_LOWER" '
  [.[] | select(.title | ascii_downcase | contains($keyword))]
'
