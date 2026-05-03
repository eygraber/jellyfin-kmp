#!/bin/bash
# Merge a PR using the rebase strategy and delete the branch.
# Usage: ./merge-pr.sh PR_NUMBER [--admin] [--auto]
#
# Arguments:
#   PR_NUMBER          The PR number to merge
#
# Options:
#   --admin            Pass --admin to gh pr merge (bypass branch protection if you have permission)
#   --auto             Use auto-merge (queues the merge to run once checks/required reviews pass)
#
# Repo policy (verified via `gh repo view`):
#   - rebaseMergeAllowed: true
#   - squashMergeAllowed / mergeCommitAllowed: false
#   - deleteBranchOnMerge: true
#
# Output: Merge confirmation, or error.
#
# Exit codes:
#   0  merged successfully (or auto-merge queued)
#   1  merge failed
#  10  usage / unrecoverable error

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
START_WORK_SCRIPTS="$SCRIPT_DIR/../../start-work/scripts"

PR_NUMBER=""
ADMIN_FLAG=""
AUTO_FLAG=""

while [[ $# -gt 0 ]]; do
  case $1 in
    --admin)
      ADMIN_FLAG="--admin"
      shift
      ;;
    --auto)
      AUTO_FLAG="--auto"
      shift
      ;;
    -h|--help)
      sed -n '2,/^$/p' "$0" | sed 's/^# \{0,1\}//'
      exit 0
      ;;
    *)
      if [[ -z "$PR_NUMBER" ]]; then
        PR_NUMBER="$1"
      fi
      shift
      ;;
  esac
done

if [[ -z "$PR_NUMBER" ]]; then
  echo "Usage: $0 PR_NUMBER [--admin] [--auto]" >&2
  exit 10
fi

# Sanity check: PR exists and is open
PR_STATE=$(gh pr view "$PR_NUMBER" --json state --jq '.state' 2>/dev/null || echo "")
if [[ "$PR_STATE" != "OPEN" ]]; then
  echo "PR #$PR_NUMBER is not open (state: $PR_STATE). Cannot merge." >&2
  exit 1
fi

echo "Merging PR #$PR_NUMBER (rebase, delete branch)..."

if ! gh pr merge "$PR_NUMBER" --rebase --delete-branch $ADMIN_FLAG $AUTO_FLAG; then
  echo "Failed to merge PR #$PR_NUMBER." >&2
  exit 1
fi

echo "PR #$PR_NUMBER merged."

# GitHub auto-closes issues referenced by closing keywords (Closes #N), but its built-in project
# automation often fails to flip the project board's Status field from "In Review" to "Done" —
# leaves merged work stuck in In Review. Do the transition explicitly here so the board stays in
# sync. Skipped for --auto since the merge hasn't actually happened yet.
if [[ -z "$AUTO_FLAG" ]]; then
  CLOSED_ISSUES=$(gh pr view "$PR_NUMBER" --json closingIssuesReferences \
    --jq '.closingIssuesReferences[].number' 2>/dev/null || true)
  if [[ -n "$CLOSED_ISSUES" ]]; then
    while read -r ISSUE_NUMBER; do
      [[ -z "$ISSUE_NUMBER" ]] && continue
      "$START_WORK_SCRIPTS/set-issue-status.sh" "$ISSUE_NUMBER" done >/dev/null \
        && echo "Moved #$ISSUE_NUMBER to Done." \
        || echo "Warning: failed to move #$ISSUE_NUMBER to Done on the project board." >&2
    done <<< "$CLOSED_ISSUES"
  fi
fi

exit 0
