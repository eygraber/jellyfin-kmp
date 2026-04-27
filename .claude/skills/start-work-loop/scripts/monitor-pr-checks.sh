#!/bin/bash
# Poll a PR's status checks until they reach a terminal state (all pass, or any fail).
# Usage: ./monitor-pr-checks.sh PR_NUMBER [--interval SECONDS] [--max-wait SECONDS]
#
# Arguments:
#   PR_NUMBER          The PR number to monitor
#
# Options:
#   --interval N       Poll interval in seconds (default: 60)
#   --max-wait N       Maximum total wait time in seconds before giving up (default: 7200)
#
# Output (stdout, single JSON line on terminal state):
#   {"status":"success","pr":N}                              all checks passed
#   {"status":"failure","pr":N,"failed_checks":[...]}        one or more checks failed
#   {"status":"timeout","pr":N,"pending_checks":[...]}       max-wait exceeded
#   {"status":"conflict","pr":N}                             PR has merge conflicts
#
# Progress lines are written to stderr.
#
# Exit codes:
#   0  success (all checks passed)
#   1  failure (one or more checks failed)
#   2  timeout
#   3  merge conflict detected
#   10 usage / unrecoverable error

set -e

PR_NUMBER=""
INTERVAL=60
MAX_WAIT=7200

while [[ $# -gt 0 ]]; do
  case $1 in
    --interval)
      INTERVAL="$2"
      shift 2
      ;;
    --max-wait)
      MAX_WAIT="$2"
      shift 2
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
  echo "Usage: $0 PR_NUMBER [--interval SECONDS] [--max-wait SECONDS]" >&2
  exit 10
fi

START_TIME=$(date +%s)
ITERATION=0

while true; do
  ITERATION=$((ITERATION + 1))
  ELAPSED=$(($(date +%s) - START_TIME))

  if [[ "$ELAPSED" -ge "$MAX_WAIT" ]]; then
    PENDING=$(gh pr checks "$PR_NUMBER" --json name,bucket 2>/dev/null \
      | jq '[.[] | select(.bucket == "pending") | .name]' || echo "[]")
    echo "{\"status\":\"timeout\",\"pr\":$PR_NUMBER,\"pending_checks\":$PENDING}"
    exit 2
  fi

  # Detect merge conflicts early — these block the merge regardless of checks
  MERGEABLE=$(gh pr view "$PR_NUMBER" --json mergeable --jq '.mergeable' 2>/dev/null || echo "UNKNOWN")
  if [[ "$MERGEABLE" == "CONFLICTING" ]]; then
    echo "[$(date +%H:%M:%S)] PR #$PR_NUMBER: merge conflict detected" >&2
    echo "{\"status\":\"conflict\",\"pr\":$PR_NUMBER}"
    exit 3
  fi

  CHECKS_JSON=$(gh pr checks "$PR_NUMBER" --json name,bucket,state,link,workflow 2>/dev/null || echo "[]")
  TOTAL=$(echo "$CHECKS_JSON" | jq 'length')

  if [[ "$TOTAL" -eq 0 ]]; then
    echo "[$(date +%H:%M:%S)] PR #$PR_NUMBER: no checks reported yet (iteration $ITERATION)" >&2
    sleep "$INTERVAL"
    continue
  fi

  PASS=$(echo "$CHECKS_JSON" | jq '[.[] | select(.bucket == "pass" or .bucket == "skipping")] | length')
  FAIL=$(echo "$CHECKS_JSON" | jq '[.[] | select(.bucket == "fail" or .bucket == "cancel")] | length')
  PENDING=$(echo "$CHECKS_JSON" | jq '[.[] | select(.bucket == "pending")] | length')

  echo "[$(date +%H:%M:%S)] PR #$PR_NUMBER: $PASS pass, $FAIL fail, $PENDING pending (of $TOTAL total)" >&2

  if [[ "$FAIL" -gt 0 ]]; then
    FAILED_DETAILS=$(echo "$CHECKS_JSON" | jq '[.[] | select(.bucket == "fail" or .bucket == "cancel") | {name, state, link, workflow}]')
    echo "{\"status\":\"failure\",\"pr\":$PR_NUMBER,\"failed_checks\":$FAILED_DETAILS}"
    exit 1
  fi

  if [[ "$PENDING" -eq 0 ]]; then
    echo "{\"status\":\"success\",\"pr\":$PR_NUMBER}"
    exit 0
  fi

  sleep "$INTERVAL"
done
