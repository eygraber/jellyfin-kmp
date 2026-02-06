#!/bin/bash
# Add a blocking relationship between issues
# Usage: ./add-blocking.sh BLOCKED_ISSUE BLOCKING_ISSUE
#
# Arguments:
#   BLOCKED_ISSUE    Issue number that is blocked
#   BLOCKING_ISSUE   Issue number that is doing the blocking
#
# Output: Confirmation message

set -e

OWNER="eygraber"
REPO="jellyfin-kmp"

if [[ -z "$1" ]] || [[ -z "$2" ]]; then
  echo "Usage: $0 BLOCKED_ISSUE BLOCKING_ISSUE" >&2
  echo "Example: $0 123 45  # Issue 123 is blocked by issue 45" >&2
  exit 1
fi

BLOCKED_NUMBER="$1"
BLOCKING_NUMBER="$2"

# Get node IDs
BLOCKED_ID=$(gh api "repos/$OWNER/$REPO/issues/$BLOCKED_NUMBER" --jq '.node_id')
BLOCKING_ID=$(gh api "repos/$OWNER/$REPO/issues/$BLOCKING_NUMBER" --jq '.node_id')

if [[ -z "$BLOCKED_ID" ]] || [[ "$BLOCKED_ID" == "null" ]]; then
  echo "Issue #$BLOCKED_NUMBER not found" >&2
  exit 1
fi

if [[ -z "$BLOCKING_ID" ]] || [[ "$BLOCKING_ID" == "null" ]]; then
  echo "Issue #$BLOCKING_NUMBER not found" >&2
  exit 1
fi

# Add the blocking relationship
gh api graphql -f query="
mutation {
  addBlockedBy(input: {issueId: \"$BLOCKED_ID\", blockingIssueId: \"$BLOCKING_ID\"}) {
    issue { number }
    blockingIssue { number }
  }
}" > /dev/null

echo "Added: #$BLOCKED_NUMBER is now blocked by #$BLOCKING_NUMBER"
