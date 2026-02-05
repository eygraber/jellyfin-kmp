#!/bin/bash
# Create a new GitHub issue
# Usage: ./create-issue.sh --title "Title" --body "Body" [OPTIONS]
#
# Required:
#   --title TITLE       Issue title
#   --body BODY         Issue body (can be multiline)
#
# Options:
#   --type TYPE         Work type: feature, bug, task, chore, epic (adds label)
#   --priority PRIORITY Priority: p0, p1, p2, p3 (adds label)
#   --labels LABELS     Additional labels (comma-separated)
#
# Output: JSON with issue number and URL

set -e

TITLE=""
BODY=""
TYPE=""
PRIORITY=""
LABELS=""

while [[ $# -gt 0 ]]; do
  case $1 in
    --title)
      TITLE="$2"
      shift 2
      ;;
    --body)
      BODY="$2"
      shift 2
      ;;
    --type)
      TYPE="$2"
      shift 2
      ;;
    --priority)
      PRIORITY="$2"
      shift 2
      ;;
    --labels)
      LABELS="$2"
      shift 2
      ;;
    *)
      shift
      ;;
  esac
done

if [[ -z "$TITLE" ]]; then
  echo "Error: --title is required" >&2
  exit 1
fi

if [[ -z "$BODY" ]]; then
  echo "Error: --body is required" >&2
  exit 1
fi

# Build labels list
ALL_LABELS=""

# Add type label
case "$TYPE" in
  feature)
    ALL_LABELS="type:feature"
    ;;
  bug)
    ALL_LABELS="bug"
    ;;
  task)
    ALL_LABELS="type:task"
    ;;
  chore)
    ALL_LABELS="type:chore"
    ;;
  epic)
    ALL_LABELS="epic"
    ;;
esac

# Add priority label
if [[ -n "$PRIORITY" ]]; then
  PRIORITY_LOWER=$(echo "$PRIORITY" | tr '[:upper:]' '[:lower:]')
  if [[ "$PRIORITY_LOWER" =~ ^p[0-3]$ ]]; then
    [[ -n "$ALL_LABELS" ]] && ALL_LABELS="$ALL_LABELS,"
    ALL_LABELS="${ALL_LABELS}priority:$PRIORITY_LOWER"
  fi
fi

# Add custom labels
if [[ -n "$LABELS" ]]; then
  [[ -n "$ALL_LABELS" ]] && ALL_LABELS="$ALL_LABELS,"
  ALL_LABELS="${ALL_LABELS}$LABELS"
fi

# Create the issue
if [[ -n "$ALL_LABELS" ]]; then
  gh issue create --title "$TITLE" --body "$BODY" --label "$ALL_LABELS" --json number,url
else
  gh issue create --title "$TITLE" --body "$BODY" --json number,url
fi
