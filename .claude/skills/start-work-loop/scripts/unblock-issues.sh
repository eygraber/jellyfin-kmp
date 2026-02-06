#!/bin/bash
# Check blocked issues and unblock those whose blockers have PRs
# Usage: ./unblock-issues.sh [--scope "<scope>"] [--dry-run]
#
# Options:
#   --scope "<scope>"   Only check issues within this scope
#   --dry-run           Show what would be unblocked without making changes
#
# Output: List of issues that were unblocked (or would be with --dry-run)

set -e

OWNER="eygraber"
REPO="jellyfin-kmp"
PROJECT_NUMBER=6
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
START_WORK_SCRIPTS="$SCRIPT_DIR/../../start-work/scripts"

SCOPE=""
DRY_RUN=false

while [[ $# -gt 0 ]]; do
  case $1 in
    --scope)
      SCOPE="$2"
      shift 2
      ;;
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    *)
      shift
      ;;
  esac
done

# Get all blocked issues with details
BLOCKED_ISSUES=$(gh api graphql -f query='
{
  repository(owner: "'"$OWNER"'", name: "'"$REPO"'") {
    issues(first: 100, states: OPEN) {
      nodes {
        number
        title
        blockedBy(first: 10) {
          nodes {
            number
            title
            state
          }
        }
      }
    }
  }
}' | jq '[.data.repository.issues.nodes[] | select(.blockedBy.nodes | length > 0)]')

# Get issues that are In Review or Done (have PRs)
ISSUES_WITH_PRS=$(gh api graphql -f query='
{
  repository(owner: "'"$OWNER"'", name: "'"$REPO"'") {
    issues(first: 100) {
      nodes {
        number
        state
        linkedPullRequests(first: 1) {
          nodes { number }
        }
      }
    }
  }
}' | jq '[.data.repository.issues.nodes[] | select(.linkedPullRequests.nodes | length > 0) | .number]')

# Also check project board status for In Review items
IN_REVIEW_ITEMS=$("$START_WORK_SCRIPTS/list-project-items.sh" --status in-review 2>/dev/null | jq '[.[].number]' || echo "[]")
DONE_ITEMS=$("$START_WORK_SCRIPTS/list-project-items.sh" --status done 2>/dev/null | jq '[.[].number]' || echo "[]")

# Combine: issues with PRs OR in review/done status
RESOLVED_ISSUES=$(echo "[$ISSUES_WITH_PRS, $IN_REVIEW_ITEMS, $DONE_ITEMS]" | jq 'flatten | unique')

# Find issues that can be unblocked
# An issue can be unblocked if ALL its blockers are in RESOLVED_ISSUES
UNBLOCKABLE=$(echo "$BLOCKED_ISSUES" | jq --argjson resolved "$RESOLVED_ISSUES" '
  [.[] | select(
    .blockedBy.nodes | all(.number as $n | $resolved | index($n))
  ) | {number, title, blockedBy: [.blockedBy.nodes[].number]}]
')

UNBLOCKABLE_COUNT=$(echo "$UNBLOCKABLE" | jq 'length')

if [[ "$UNBLOCKABLE_COUNT" -eq 0 ]]; then
  echo "No issues can be unblocked at this time."
  exit 0
fi

echo "Found $UNBLOCKABLE_COUNT issue(s) that can be unblocked:"
echo ""

echo "$UNBLOCKABLE" | jq -r '.[] | "  #\(.number): \(.title) (was blocked by: \(.blockedBy | map("#\(.)") | join(", ")))"'

if [[ "$DRY_RUN" == "true" ]]; then
  echo ""
  echo "(dry-run mode - no changes made)"
  exit 0
fi

echo ""
echo "Moving to Ready status..."

# Unblock each issue
echo "$UNBLOCKABLE" | jq -r '.[].number' | while read -r ISSUE_NUMBER; do
  "$START_WORK_SCRIPTS/set-issue-status.sh" "$ISSUE_NUMBER" ready
done

echo ""
echo "Done. Unblocked issues are now in Ready status."
