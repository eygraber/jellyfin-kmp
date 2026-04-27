#!/bin/bash
# Fetch failure details for failed checks on a PR.
# Usage: ./get-pr-check-failures.sh PR_NUMBER [--log-tail N] [--summary-only]
#
# Arguments:
#   PR_NUMBER          The PR number
#
# Options:
#   --log-tail N       Number of trailing log lines to include per failed check (default: 200)
#   --summary-only     Skip log fetching, only emit names + links
#
# Output (stdout): Human-readable report of each failed check, with:
#   - check name, workflow, link
#   - tail of failed-job logs (unless --summary-only)
#
# Exit codes:
#   0  one or more failed checks reported
#   1  no failed checks (nothing to report)
#  10  usage / unrecoverable error

set -e

PR_NUMBER=""
LOG_TAIL=200
SUMMARY_ONLY=false

while [[ $# -gt 0 ]]; do
  case $1 in
    --log-tail)
      LOG_TAIL="$2"
      shift 2
      ;;
    --summary-only)
      SUMMARY_ONLY=true
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
  echo "Usage: $0 PR_NUMBER [--log-tail N] [--summary-only]" >&2
  exit 10
fi

CHECKS_JSON=$(gh pr checks "$PR_NUMBER" --json name,bucket,state,link,workflow 2>/dev/null || echo "[]")
FAILED=$(echo "$CHECKS_JSON" | jq '[.[] | select(.bucket == "fail" or .bucket == "cancel")]')
COUNT=$(echo "$FAILED" | jq 'length')

if [[ "$COUNT" -eq 0 ]]; then
  echo "No failed checks on PR #$PR_NUMBER."
  exit 1
fi

echo "=== PR #$PR_NUMBER: $COUNT failed check(s) ==="
echo ""

# Iterate failed checks
echo "$FAILED" | jq -c '.[]' | while read -r check; do
  NAME=$(echo "$check" | jq -r '.name')
  STATE=$(echo "$check" | jq -r '.state')
  LINK=$(echo "$check" | jq -r '.link')
  WORKFLOW=$(echo "$check" | jq -r '.workflow')

  echo "---"
  echo "Check:    $NAME"
  echo "Workflow: $WORKFLOW"
  echo "State:    $STATE"
  echo "Link:     $LINK"

  if [[ "$SUMMARY_ONLY" == "true" ]]; then
    continue
  fi

  # Extract run ID from link, e.g. .../actions/runs/12345/job/67890
  RUN_ID=$(echo "$LINK" | sed -E 's#.*/runs/([0-9]+)/.*#\1#')
  JOB_ID=$(echo "$LINK" | sed -E 's#.*/job/([0-9]+).*#\1#')

  if [[ -z "$RUN_ID" || "$RUN_ID" == "$LINK" ]]; then
    echo "(could not parse run ID from link; skipping log fetch)"
    continue
  fi

  echo ""
  echo "--- Logs (last $LOG_TAIL lines of failed step) ---"
  if [[ -n "$JOB_ID" && "$JOB_ID" != "$LINK" ]]; then
    gh run view "$RUN_ID" --job "$JOB_ID" --log-failed 2>/dev/null | tail -n "$LOG_TAIL" \
      || echo "(no failed-step log available; logs may be expired or run still finalizing)"
  else
    gh run view "$RUN_ID" --log-failed 2>/dev/null | tail -n "$LOG_TAIL" \
      || echo "(no failed-step log available; logs may be expired or run still finalizing)"
  fi
  echo ""
done

exit 0
