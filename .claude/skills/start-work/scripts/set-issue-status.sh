#!/bin/bash
# Set the status of an issue on the project board
# Usage: ./set-issue-status.sh ISSUE_NUMBER STATUS
#
# Arguments:
#   ISSUE_NUMBER   The issue number to update
#   STATUS         Target status: backlog, ready, in-progress, in-review, done
#
# Output: Confirmation message

set -e

OWNER="eygraber"
PROJECT_ID="PVT_kwHOABDKXc4BOYQM"
PROJECT_NUMBER=6
STATUS_FIELD_ID="PVTSSF_lAHOABDKXc4BOYQMzg9GUow"

# Status option IDs
declare -A STATUS_IDS
STATUS_IDS["backlog"]="506dc4ca"
STATUS_IDS["ready"]="01d0843d"
STATUS_IDS["in-progress"]="2b058326"
STATUS_IDS["in-review"]="dec70db3"
STATUS_IDS["done"]="61ff0272"

if [[ -z "$1" ]] || [[ -z "$2" ]]; then
  echo "Usage: $0 ISSUE_NUMBER STATUS" >&2
  echo "STATUS must be one of: backlog, ready, in-progress, in-review, done" >&2
  exit 1
fi

ISSUE_NUMBER="$1"
STATUS=$(echo "$2" | tr '[:upper:]' '[:lower:]' | tr ' ' '-')

if [[ -z "${STATUS_IDS[$STATUS]}" ]]; then
  echo "Invalid status: $2" >&2
  echo "STATUS must be one of: backlog, ready, in-progress, in-review, done" >&2
  exit 1
fi

STATUS_OPTION_ID="${STATUS_IDS[$STATUS]}"

# Get the project item ID for this issue
ITEM_ID=$(gh project item-list "$PROJECT_NUMBER" --owner "$OWNER" --format json --limit 100 | \
  jq -r --argjson num "$ISSUE_NUMBER" '.items[] | select(.content.number == $num) | .id')

if [[ -z "$ITEM_ID" ]]; then
  echo "Issue #$ISSUE_NUMBER not found in project" >&2
  exit 1
fi

# Update the status
gh project item-edit \
  --project-id "$PROJECT_ID" \
  --id "$ITEM_ID" \
  --field-id "$STATUS_FIELD_ID" \
  --single-select-option-id "$STATUS_OPTION_ID"

echo "Updated #$ISSUE_NUMBER to status: $2"
