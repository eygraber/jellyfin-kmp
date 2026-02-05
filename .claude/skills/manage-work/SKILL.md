---
name: manage-work
description: View, update, prioritize, and organize GitHub issues. Modify descriptions, labels, status, and epic relationships.
argument-hint: "[action] [issue] - e.g., 'list ready', 'update #123', 'prioritize #45 p1', 'close #67'"
context: fork
allowed-tools: Bash(gh issue*, gh project*), Read
---

# Manage Work

View and modify existing GitHub issues and project board items.

## Usage

```
/manage-work list                    # List open issues
/manage-work list ready              # List issues in Ready column
/manage-work view #123               # View issue details
/manage-work update #123             # Update issue description/criteria
/manage-work prioritize #123 p1      # Change priority
/manage-work label #123 area:auth    # Add label
/manage-work move #123 ready         # Move to Ready column
/manage-work close #123              # Close issue
/manage-work reopen #123             # Reopen issue
/manage-work link #123 to #100       # Link as subissue to epic
```

## References

- **GitHub project config:** [/.claude/rules/github-project.md](/.claude/rules/github-project.md)
- **gh CLI commands:** [/.claude/rules/github-commands.md](/.claude/rules/github-commands.md)

## Scripts

Helper scripts are located in `scripts/` directory:

| Script                  | Purpose                                  |
|-------------------------|------------------------------------------|
| `list-issues.sh`        | List issues with various filters         |
| `set-issue-priority.sh` | Change priority label on an issue        |
| `set-project-status.sh` | Move issue to a project board status     |
| `add-blocking.sh`       | Add blocking relationship between issues |
| `remove-blocking.sh`    | Remove blocking relationship             |

## Actions

### List Issues

```bash
# All open issues
.claude/skills/manage-work/scripts/list-issues.sh

# With specific label
.claude/skills/manage-work/scripts/list-issues.sh --label "type:bug"

# Assigned to someone
.claude/skills/manage-work/scripts/list-issues.sh --assignee @me

# As JSON
.claude/skills/manage-work/scripts/list-issues.sh --json
```

### View Issue Details

```bash
gh issue view ISSUE_NUMBER
gh issue view ISSUE_NUMBER --comments  # Include comments
```

### Update Issue

```bash
# Update title
gh issue edit ISSUE_NUMBER --title "New title"

# Update body
gh issue edit ISSUE_NUMBER --body "New description"

# Add labels
gh issue edit ISSUE_NUMBER --add-label "priority:p1,area:data"

# Remove labels
gh issue edit ISSUE_NUMBER --remove-label "priority:p2"

# Assign
gh issue edit ISSUE_NUMBER --add-assignee USERNAME
```

### Change Priority

Priority labels: `priority:p0`, `priority:p1`, `priority:p2`, `priority:p3`

```bash
# Set priority
.claude/skills/manage-work/scripts/set-issue-priority.sh ISSUE_NUMBER p1

# Remove priority
.claude/skills/manage-work/scripts/set-issue-priority.sh ISSUE_NUMBER none
```

### Move on Project Board

```bash
# Move to Ready
.claude/skills/manage-work/scripts/set-project-status.sh ISSUE_NUMBER ready

# Move to In Progress
.claude/skills/manage-work/scripts/set-project-status.sh ISSUE_NUMBER in-progress

# Move to Done
.claude/skills/manage-work/scripts/set-project-status.sh ISSUE_NUMBER done
```

### Blocking Relationships

```bash
# Add blocking: issue 123 is blocked by issue 45
.claude/skills/manage-work/scripts/add-blocking.sh 123 45

# Remove blocking relationship
.claude/skills/manage-work/scripts/remove-blocking.sh 123 45
```

### Close/Reopen

```bash
# Close as completed
gh issue close ISSUE_NUMBER

# Close as not planned
gh issue close ISSUE_NUMBER --reason "not planned"

# Reopen
gh issue reopen ISSUE_NUMBER
```

### Link Issues (Subissues)

```bash
# Add subissue relationship (via issue body edit or comment)
# GitHub subissues are managed through the UI or API

# Add reference in body
gh issue edit CHILD_NUMBER --body "Parent: #PARENT_NUMBER\n\nOriginal body..."
```

### Add Comment

```bash
gh issue comment ISSUE_NUMBER --body "Comment text here"
```

## Bulk Operations

### Prioritize Multiple Issues

When asked to prioritize or triage:
1. List relevant issues
2. Present summary to user
3. Apply priority labels as directed

### Sprint Planning

When asked to plan a sprint:
1. List backlog items
2. Help select items for sprint
3. Move selected to "Ready" column
4. Ensure priorities are set

## Common Workflows

### Triage New Issues
```
/manage-work list unlabeled
```
Then for each: add type, priority, and area labels.

### Check Sprint Status
```
/manage-work list in-progress
```
Shows what's actively being worked on.

### Groom Backlog
```
/manage-work list backlog
```
Review and prioritize items in backlog.

## Integration with Local Planning

When closing issues that have local `.projects/` plans:

1. **Closing a phase subissue**: Update the corresponding phase status in `.projects/<feature>/PLAN.md`
2. **Closing an epic**: Verify all local plan phases are complete, then archive (delete) `.projects/<feature>/`

When updating issues, check if there's a local plan that should also be updated:
```bash
# Check for local plan
ls .projects/
```

## Output Format

When listing issues, show:
```
#123 [P1] [feature] Title of the issue
     Labels: area:auth, type:feature, priority:p1
     Assignee: @username (or unassigned)

#124 [P2] [bug] Another issue title
     Labels: area:data, type:bug, priority:p2
     Assignee: unassigned
```

When updating, confirm:
```
Updated #123:
- Priority: P2 → P1
- Added label: area:auth
- Moved to: Ready
```
