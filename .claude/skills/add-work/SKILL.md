---
name: add-work
description: Create new GitHub issues for features, tasks, bugs, or chores. Supports creating epics with subissues.
argument-hint: "[type] [title] - e.g., 'feature user authentication', 'bug login crash', 'epic payments'"
context: fork
allowed-tools: Bash(gh issue*, gh project*), Read
---

# Add Work

Create new GitHub issues and add them to the project board.

## Usage

```
/add-work feature Add dark mode support
/add-work bug Login button unresponsive on slow networks
/add-work epic Payment processing system
/add-work task Refactor user repository to use Flow
/add-work chore Update Kotlin to 2.0
```

## Work Types

| Type      | Label           | Use For                                      |
|-----------|-----------------|----------------------------------------------|
| `feature` | `type:feature`  | New user-facing functionality                |
| `bug`     | `type:bug`      | Defects, broken behavior                     |
| `epic`    | `type:epic`     | Large initiatives with multiple subtasks     |
| `task`    | `type:task`     | Implementation work, standalone              |
| `chore`   | `type:chore`    | Maintenance, refactoring, tooling, deps      |

## Scripts

Helper scripts are located in `scripts/` directory:

| Script              | Purpose                              |
|---------------------|--------------------------------------|
| `create-issue.sh`   | Create a new issue with labels       |
| `add-to-project.sh` | Add issue to project board           |
| `add-subissue.sh`   | Link issue as subissue to epic       |

## Process

### 1. Gather Information

Ask for any missing details:
- **Title**: Clear, actionable (provided in arguments)
- **Description**: Context, acceptance criteria, technical notes
- **Priority**: P0-P3 (default P2 if not specified)
- **Area labels**: Which part of the app (auth, tasks, ui, data, etc.)
- **Parent epic**: If this is a subtask of an existing epic

### 2. Create the Issue

```bash
# Basic feature issue
.claude/skills/add-work/scripts/create-issue.sh \
  --title "Add dark mode support" \
  --body "## Summary\n..." \
  --type feature \
  --priority p2

# Bug with area label
.claude/skills/add-work/scripts/create-issue.sh \
  --title "Login button unresponsive" \
  --body "## Steps to reproduce\n..." \
  --type bug \
  --priority p1 \
  --labels "area:auth"

# Epic
.claude/skills/add-work/scripts/create-issue.sh \
  --title "User onboarding flow" \
  --body "## Goal\n..." \
  --type epic
```

### 3. Add to Project Board

```bash
# Add to project (defaults to Backlog status)
.claude/skills/add-work/scripts/add-to-project.sh ISSUE_NUMBER

# Add with Ready status
.claude/skills/add-work/scripts/add-to-project.sh ISSUE_NUMBER --status ready
```

### 4. For Epics: Create Subissues

When creating an epic, ask if the user wants to break it down immediately:

1. Create the parent epic issue
2. For each subtask:
   - Create as separate issue
   - Link as subissue to parent using the script
   - Add to project board
3. Report the full structure

```bash
# Link issue 45 as subissue of epic 1
.claude/skills/add-work/scripts/add-subissue.sh 45 1
```

## Issue Body Template

```markdown
## Summary
[1-2 sentence description of what this work accomplishes]

## Context
[Why is this needed? What problem does it solve?]

## Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Criterion 3

## Technical Notes
[Optional: implementation hints, constraints, related code]

## Related
- Parent: #EPIC_NUMBER (if applicable)
- Related: #OTHER_ISSUE (if applicable)
```

## References

- **GitHub project config:** [/.claude/github-project.md](/.claude/rules/github-project.md)
- **gh CLI commands:** [/.claude/rules/github-commands.md](/.claude/rules/github-commands.md)

## Output

After creating, report:
- Issue number and URL
- Labels applied
- Project board status
- Parent epic (if linked)

## Integration with Local Planning

For complex features (epics, large features), suggest using the `plan-feature` agent after creating the GitHub issue:

```
"Epic #123 created. This looks complex enough to benefit from detailed planning.
Would you like me to run the plan-feature agent to create implementation plans in .projects/?"
```

The `plan-feature` agent will:
1. Read the GitHub issue for context
2. Create detailed `.projects/<feature>/` plans
3. Link plans back to the GitHub issue
4. Create subissues for each implementation phase

## Examples

### Simple Feature
```
/add-work feature Add pull-to-refresh on task list
```
Creates issue with `type:feature`, prompts for priority and description.

### Bug with Context
```
/add-work bug App crashes when rotating during login
```
Creates issue with `type:bug`, asks for reproduction steps.

### Epic with Breakdown
```
/add-work epic User onboarding flow
```
Creates epic, then offers to create subissues for each step.
