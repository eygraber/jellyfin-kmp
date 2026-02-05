#!/bin/bash
# Get list of blocked issues (issues that have blockedBy relationships)
# Usage: ./get-blocked-issues.sh [--details]
#
# Options:
#   --details   Include blocking issue details (slower)
#
# Output: JSON array of blocked issue numbers (or full details with --details)

set -e

OWNER="eygraber"
REPO="super-do"
DETAILS=false

while [[ $# -gt 0 ]]; do
  case $1 in
    --details)
      DETAILS=true
      shift
      ;;
    *)
      shift
      ;;
  esac
done

if [[ "$DETAILS" == "true" ]]; then
  gh api graphql -f query='
  {
    repository(owner: "'"$OWNER"'", name: "'"$REPO"'") {
      issues(first: 100, states: OPEN) {
        nodes {
          number
          title
          blockedBy(first: 10) {
            nodes { number title }
          }
        }
      }
    }
  }' | jq '[.data.repository.issues.nodes[] | select(.blockedBy.nodes | length > 0) | {number, title, blockedBy: [.blockedBy.nodes[] | {number, title}]}]'
else
  gh api graphql -f query='
  {
    repository(owner: "'"$OWNER"'", name: "'"$REPO"'") {
      issues(first: 100, states: OPEN) {
        nodes {
          number
          blockedBy(first: 5) {
            nodes { number }
          }
        }
      }
    }
  }' | jq '[.data.repository.issues.nodes[] | select(.blockedBy.nodes | length > 0) | .number]'
fi
