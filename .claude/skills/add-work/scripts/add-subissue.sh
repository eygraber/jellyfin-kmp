#!/bin/bash
# Add an issue as a subissue of an epic
# Usage: ./add-subissue.sh SUBISSUE_NUMBER EPIC_NUMBER
#
# Arguments:
#   SUBISSUE_NUMBER   The issue number to add as subissue
#   EPIC_NUMBER       The epic issue number (parent)
#
# Output: Confirmation message

set -e

OWNER="eygraber"
REPO="super-do"

if [[ -z "$1" ]] || [[ -z "$2" ]]; then
  echo "Usage: $0 SUBISSUE_NUMBER EPIC_NUMBER" >&2
  echo "Example: $0 45 1  # Add issue 45 as subissue of epic 1" >&2
  exit 1
fi

SUBISSUE_NUMBER="$1"
EPIC_NUMBER="$2"

# Get node IDs
SUBISSUE_ID=$(gh api "repos/$OWNER/$REPO/issues/$SUBISSUE_NUMBER" --jq '.node_id')
EPIC_ID=$(gh api "repos/$OWNER/$REPO/issues/$EPIC_NUMBER" --jq '.node_id')

if [[ -z "$SUBISSUE_ID" ]] || [[ "$SUBISSUE_ID" == "null" ]]; then
  echo "Issue #$SUBISSUE_NUMBER not found" >&2
  exit 1
fi

if [[ -z "$EPIC_ID" ]] || [[ "$EPIC_ID" == "null" ]]; then
  echo "Epic #$EPIC_NUMBER not found" >&2
  exit 1
fi

# Add subissue relationship
gh api graphql -f query="
mutation {
  addSubIssue(input: {issueId: \"$EPIC_ID\", subIssueId: \"$SUBISSUE_ID\"}) {
    issue { number title }
    subIssue { number title }
  }
}" | jq -r '"Added #\(.data.addSubIssue.subIssue.number) as subissue of #\(.data.addSubIssue.issue.number)"'
