#!/bin/bash
# Update local master from origin and rebase the current branch onto it.
# Used to resolve conflicts caused by Renovate auto-merges (or any other branch
# advancing master while a feature branch is being worked on).
#
# Usage: ./rebase-on-master.sh [--branch BRANCH] [--push] [--force-with-lease]
#
# Options:
#   --branch BRANCH       Rebase this branch instead of the current one
#   --push                Push the rebased branch back to origin (uses --force-with-lease)
#   --force-with-lease    Implied by --push; included for clarity if used standalone
#
# Output: Status of the rebase and any conflicts detected.
#
# Exit codes:
#   0  rebase succeeded (and pushed if requested)
#   1  rebase had conflicts that need manual resolution (rebase left in-progress)
#   2  uncommitted changes prevented rebase
#  10  usage / unrecoverable error

set -e

BRANCH=""
PUSH=false

while [[ $# -gt 0 ]]; do
  case $1 in
    --branch)
      BRANCH="$2"
      shift 2
      ;;
    --push)
      PUSH=true
      shift
      ;;
    --force-with-lease)
      shift
      ;;
    -h|--help)
      sed -n '2,/^$/p' "$0" | sed 's/^# \{0,1\}//'
      exit 0
      ;;
    *)
      shift
      ;;
  esac
done

# Verify we're inside a git repo
if ! git rev-parse --git-dir >/dev/null 2>&1; then
  echo "Not inside a git repository." >&2
  exit 10
fi

if [[ -z "$BRANCH" ]]; then
  BRANCH=$(git rev-parse --abbrev-ref HEAD)
fi

if [[ "$BRANCH" == "master" || "$BRANCH" == "HEAD" ]]; then
  echo "Refusing to rebase '$BRANCH' onto itself. Switch to a feature branch first." >&2
  exit 10
fi

# Refuse to rebase with uncommitted changes
if ! git diff --quiet || ! git diff --cached --quiet; then
  echo "Uncommitted changes present. Commit or stash before rebasing." >&2
  git status --short
  exit 2
fi

echo "Fetching latest master from origin..."
git fetch origin master

# Make sure we're on the target branch
CURRENT=$(git rev-parse --abbrev-ref HEAD)
if [[ "$CURRENT" != "$BRANCH" ]]; then
  echo "Switching to $BRANCH..."
  git checkout "$BRANCH"
fi

# Update local master to match origin/master (works regardless of current branch).
echo "Updating local master to origin/master..."
git update-ref refs/heads/master refs/remotes/origin/master

# Compute the merge base between this branch and origin/master. Using
# `git rebase --onto master <merge-base>` (instead of plain `git rebase master`)
# replays ONLY the commits unique to this branch, skipping anything already
# reachable from origin/master. That avoids re-applying — and re-conflicting on —
# commits that landed on master via Renovate auto-merge or an earlier rebase.
MERGE_BASE=$(git merge-base HEAD origin/master)
if [[ -z "$MERGE_BASE" ]]; then
  echo "Could not determine merge base between $BRANCH and origin/master." >&2
  exit 10
fi

# If the branch is already up to date with origin/master, nothing to do.
if [[ "$MERGE_BASE" == "$(git rev-parse origin/master)" ]]; then
  echo "$BRANCH is already up to date with origin/master."
  if [[ "$PUSH" == "true" ]]; then
    echo "Skipping push — nothing to update."
  fi
  exit 0
fi

echo "Rebasing $BRANCH onto master (using --onto from merge-base ${MERGE_BASE:0:10})..."
if git rebase --onto master "$MERGE_BASE" "$BRANCH"; then
  echo "Rebase complete."
else
  echo "Rebase produced conflicts. Resolve them and run: git rebase --continue" >&2
  echo "(or 'git rebase --abort' to back out)" >&2
  git status --short
  exit 1
fi

if [[ "$PUSH" == "true" ]]; then
  echo "Pushing $BRANCH with --force-with-lease..."
  git push --force-with-lease origin "$BRANCH"
fi

exit 0
