#!/bin/bash
# Set priority label on an issue
# Usage: ./set-issue-priority.sh ISSUE_NUMBER PRIORITY
#
# Arguments:
#   ISSUE_NUMBER   The issue number to update
#   PRIORITY       Priority level: p0, p1, p2, p3, or none (to remove)
#
# Output: Confirmation message

set -e

if [[ -z "$1" ]] || [[ -z "$2" ]]; then
  echo "Usage: $0 ISSUE_NUMBER PRIORITY" >&2
  echo "PRIORITY must be one of: p0, p1, p2, p3, none" >&2
  exit 1
fi

ISSUE_NUMBER="$1"
PRIORITY=$(echo "$2" | tr '[:upper:]' '[:lower:]')

# All priority labels
ALL_PRIORITIES="priority:p0,priority:p1,priority:p2,priority:p3"

# Remove all existing priority labels first
gh issue edit "$ISSUE_NUMBER" --remove-label "$ALL_PRIORITIES" 2>/dev/null || true

if [[ "$PRIORITY" != "none" ]]; then
  # Validate priority
  if [[ ! "$PRIORITY" =~ ^p[0-3]$ ]]; then
    echo "Invalid priority: $2" >&2
    echo "PRIORITY must be one of: p0, p1, p2, p3, none" >&2
    exit 1
  fi

  # Add new priority label
  gh issue edit "$ISSUE_NUMBER" --add-label "priority:$PRIORITY"
  echo "Set #$ISSUE_NUMBER priority to $PRIORITY"
else
  echo "Removed priority from #$ISSUE_NUMBER"
fi
