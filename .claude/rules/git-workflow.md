Branch naming: <issue#>-<brief-description> (e.g., 123-add-user-profile)
First commit in branch should have a clear description of the goal
Commit messages describe the goal/why, not code changes
Focus on what and why, not how in commit messages
First commit in PR includes issue number at front: "#123 Add feature X"
Subsequent commits in same PR do not need issue number (one issue per PR)
Never use git merge - always use git rebase to update branches
Use git rebase <target-branch> to update with base branch changes
Keep commit history linear for easier understanding
Commit messages may contain markdown lists for extraordinary circumstances
Use two newlines to separate main message from list, indent list items with two spaces

# Trunk-Based Development
One PR per GitHub issue - never batch multiple issues into one PR
Short-lived branches - merge frequently, avoid long-lived feature branches
Each subissue in an epic gets its own branch and PR
Epic branches are not allowed - work on subissues individually
PR must reference the issue it closes: "Closes #123"
PR must have an appropriate type label (type:feature, type:task, type:chore, or bug)
Small, discrete PRs are preferred for ease of review

## Automated Enforcement

Claude Code hooks automatically enforce these rules:
- `git merge` commands are blocked (use `git rebase` instead)
- `git reset --hard`, `git clean -f`, `git checkout .` require explicit user confirmation
- `git push --force` to main/master is blocked
- Branch names are validated against the `<issue#>-<brief-description>` convention
- `git push` runs `./check` first and blocks if it fails

### Documentation Reference
For complete patterns: .docs/workflow/git.md
