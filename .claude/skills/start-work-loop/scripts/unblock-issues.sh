#!/bin/bash
# Check blocked issues and unblock those whose blockers have PRs
# Usage: ./unblock-issues.sh [--scope "<scope>"] [--dry-run]
#
# Options:
#   --scope "<scope>"   Only check issues within this scope
#   --dry-run           Show what would be unblocked without making changes
#
# Output: List of issues that were unblocked (or would be with --dry-run)

set -eo pipefail

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

# Get all open issues that have at least one OPEN blocker. Closed blockers are pre-filtered out
# here — once a blocker closes, the issue is no longer blocked by it (whether it closed via PR
# merge, manual close, or anything else).
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
}' | jq '[.data.repository.issues.nodes[]
  | .blockedBy.nodes |= map(select(.state == "OPEN"))
  | select(.blockedBy.nodes | length > 0)]')

# An open blocker counts as "resolved enough to unblock" if its PR is up — which on this project
# is reflected by the board status sitting in In Review. (Done issues are CLOSED and were already
# filtered out above.)
IN_REVIEW_ITEMS=$("$START_WORK_SCRIPTS/list-project-items.sh" --status in-review 2>/dev/null | jq '[.[].number]' || echo "[]")

RESOLVED_ISSUES="$IN_REVIEW_ITEMS"

# An issue can be unblocked if every remaining (OPEN) blocker has its PR up (In Review).
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
