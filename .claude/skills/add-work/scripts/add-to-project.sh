#!/bin/bash
# Add an issue to the GitHub project board
# Usage: ./add-to-project.sh ISSUE_NUMBER [--status STATUS]
#
# Arguments:
#   ISSUE_NUMBER   The issue number to add
#
# Options:
#   --status STATUS   Initial status: backlog (default), ready, in-progress
#
# Output: Confirmation message

set -e

OWNER="eygraber"
REPO="jellyfin-kmp"
PROJECT_NUMBER=6
PROJECT_ID="PVT_kwHOABDKXc4BOYQM"
STATUS_FIELD_ID="PVTSSF_lAHOABDKXc4BOYQMzg9GUow"

# Status option IDs
declare -A STATUS_IDS
STATUS_IDS["backlog"]="506dc4ca"
STATUS_IDS["ready"]="01d0843d"
STATUS_IDS["in-progress"]="2b058326"

ISSUE_NUMBER=""
STATUS="backlog"

while [[ $# -gt 0 ]]; do
  case $1 in
    --status)
      STATUS=$(echo "$2" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')
      shift 2
      ;;
    *)
      if [[ -z "$ISSUE_NUMBER" ]]; then
        ISSUE_NUMBER="$1"
      fi
      shift
      ;;
  esac
done

if [[ -z "$ISSUE_NUMBER" ]]; then
  echo "Usage: $0 ISSUE_NUMBER [--status STATUS]" >&2
  exit 1
fi

ISSUE_URL="https://github.com/$OWNER/$REPO/issues/$ISSUE_NUMBER"

# Add to project
ITEM_ID=$(gh project item-add "$PROJECT_NUMBER" --owner "$OWNER" --url "$ISSUE_URL" --format json | jq -r '.id')

if [[ -z "$ITEM_ID" ]] || [[ "$ITEM_ID" == "null" ]]; then
  echo "Failed to add issue to project" >&2
  exit 1
fi

# Set initial status
STATUS_OPTION_ID="${STATUS_IDS[$STATUS]}"
if [[ -n "$STATUS_OPTION_ID" ]]; then
  gh project item-edit \
    --project-id "$PROJECT_ID" \
    --id "$ITEM_ID" \
    --field-id "$STATUS_FIELD_ID" \
    --single-select-option-id "$STATUS_OPTION_ID"
fi

echo "Added #$ISSUE_NUMBER to project with status: $STATUS"
