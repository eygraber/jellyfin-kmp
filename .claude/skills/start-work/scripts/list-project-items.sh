#!/bin/bash
# List all project items with their details
# Usage: ./list-project-items.sh [--status STATUS] [--limit N]
#
# Options:
#   --status STATUS   Filter by status (Backlog, Ready, "In Progress", Done)
#   --limit N         Limit number of results (default: 100)
#
# Output: JSON array of project items

set -e

OWNER="eygraber"
PROJECT_NUMBER=5
LIMIT=100
STATUS=""

while [[ $# -gt 0 ]]; do
  case $1 in
    --status)
      STATUS="$2"
      shift 2
      ;;
    --limit)
      LIMIT="$2"
      shift 2
      ;;
    *)
      shift
      ;;
  esac
done

result=$(gh project item-list "$PROJECT_NUMBER" --owner "$OWNER" --format json --limit "$LIMIT")

if [[ -n "$STATUS" ]]; then
  # Case-insensitive match and normalize "in-progress" to "In Progress"
  echo "$result" | jq --arg status "$STATUS" '[.items[] | select(.status | ascii_downcase == ($status | ascii_downcase | gsub("-"; " ")))]'
else
  echo "$result" | jq '.items'
fi
