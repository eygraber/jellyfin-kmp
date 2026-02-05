#!/bin/bash
# List GitHub issues with various filters
# Usage: ./list-issues.sh [OPTIONS]
#
# Options:
#   --state STATE       Issue state: open, closed, all (default: open)
#   --label LABEL       Filter by label
#   --assignee USER     Filter by assignee (@me for current user)
#   --milestone NAME    Filter by milestone
#   --limit N           Limit results (default: 30)
#   --json              Output as JSON
#
# Output: Formatted issue list or JSON

set -e

STATE="open"
LABEL=""
ASSIGNEE=""
MILESTONE=""
LIMIT=30
JSON_OUTPUT=false

while [[ $# -gt 0 ]]; do
  case $1 in
    --state)
      STATE="$2"
      shift 2
      ;;
    --label)
      LABEL="$2"
      shift 2
      ;;
    --assignee)
      ASSIGNEE="$2"
      shift 2
      ;;
    --milestone)
      MILESTONE="$2"
      shift 2
      ;;
    --limit)
      LIMIT="$2"
      shift 2
      ;;
    --json)
      JSON_OUTPUT=true
      shift
      ;;
    *)
      shift
      ;;
  esac
done

# Build command
CMD="gh issue list --state $STATE --limit $LIMIT"

[[ -n "$LABEL" ]] && CMD="$CMD --label \"$LABEL\""
[[ -n "$ASSIGNEE" ]] && CMD="$CMD --assignee $ASSIGNEE"
[[ -n "$MILESTONE" ]] && CMD="$CMD --milestone \"$MILESTONE\""

if [[ "$JSON_OUTPUT" == "true" ]]; then
  CMD="$CMD --json number,title,state,labels,assignees"
fi

eval "$CMD"
