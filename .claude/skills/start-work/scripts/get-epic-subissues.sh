#!/bin/bash
# Get subissues of an epic
# Usage: ./get-epic-subissues.sh EPIC_NUMBER
#
# Arguments:
#   EPIC_NUMBER   The issue number of the epic
#
# Output: JSON array of subissue numbers and titles

set -e

OWNER="eygraber"
REPO="super-do"

if [[ -z "$1" ]]; then
  echo "Usage: $0 EPIC_NUMBER" >&2
  exit 1
fi

EPIC_NUMBER="$1"

gh api "repos/$OWNER/$REPO/issues/$EPIC_NUMBER/sub_issues" \
  --jq '[.[] | {number, title, state}]'
