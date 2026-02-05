---
name: start-work
description: Find and start working on the next GitHub issue. Prioritizes ready work by priority, with optional filtering by epic, label, or description.
argument-hint: "[filter] - e.g., '', 'epic #1', 'label:area:auth', 'bug', 'auth validation'"
context: fork
allowed-tools: Bash(gh issue*, gh project*, gh api*), Read, AskUserQuestion
---

# Start Work

Find the next issue to work on from the GitHub project and begin working on it.

## Usage

```
/start-work                          # Find highest priority ready work
/start-work epic #1                  # Find work within a specific epic
/start-work epic #1 in progress      # Find in-progress work within an epic
/start-work label:area:auth          # Find work with specific label
/start-work bug                      # Find ready bugs
/start-work in progress              # Find work that's already in progress
/start-work auth validation          # Find work matching description keywords
/start-work login screen             # Find work related to login screen
```

## References

- **GitHub project config:** [/.claude/rules/github-project.md](/.claude/rules/github-project.md)
- **gh CLI commands:** [/.claude/rules/github-commands.md](/.claude/rules/github-commands.md)

## Scripts

Helper scripts are located in `scripts/` directory:

| Script                    | Purpose                                         |
|---------------------------|-------------------------------------------------|
| `list-project-items.sh`   | List project items, optionally filter by status |
| `get-epic-subissues.sh`   | Get subissues of an epic                        |
| `get-blocked-issues.sh`   | Get list of blocked issues                      |
| `set-issue-status.sh`     | Move issue to a project board status            |

## Process

### 1. Fetch Candidate Issues

Query the project board for issues matching the criteria.

**Default behavior (no filter):**
1. First, look for Ready items (highest priority first)
2. If no Ready items, look at Backlog items (highest priority first)
3. If nothing in Backlog, look at In Progress items (pick up unfinished work)

**Important:** Always exclude issues that have blocking relationships (use `blockedBy` field) unless explicitly requested.

**Priority order:** P0 > P1 > P2 > P3 > unlabeled

```bash
# Get all project items
.claude/skills/start-work/scripts/list-project-items.sh

# Get items in a specific status
.claude/skills/start-work/scripts/list-project-items.sh --status Ready
```

### 2. Apply Filters

Based on user arguments, filter the results:

**Epic filter (`epic #N`):**
```bash
# Get subissues of the epic
.claude/skills/start-work/scripts/get-epic-subissues.sh N
```
Then filter project items to only include those issue numbers.

**Label filter (`label:X` or type shorthand like `bug`, `feature`):**
Filter items where labels array contains the specified label.

**Status filter (`ready`, `backlog`, `in progress`):**
```bash
.claude/skills/start-work/scripts/list-project-items.sh --status Ready
```

**Description filter (free text):**
When the filter doesn't match a known pattern (epic, label, status), treat it as a description search:
- Search issue titles and bodies for matching keywords
- Use case-insensitive substring matching
- Multiple words are treated as AND (all must match)
- Still apply priority sorting to matching results

```bash
# Search for issues matching description
gh issue list --state open --search "auth validation" --json number,title,labels
```

**Check for blocked issues:**
```bash
# Get list of blocked issue numbers
.claude/skills/start-work/scripts/get-blocked-issues.sh

# Get details including what's blocking each issue
.claude/skills/start-work/scripts/get-blocked-issues.sh --details
```
Exclude any issues that appear in this blocked list.

### 3. Sort by Priority

Sort matching issues by priority:
1. `priority:p0` - Critical
2. `priority:p1` - High
3. `priority:p2` - Medium
4. `priority:p3` - Low
5. No priority label - Lowest

Within same priority, prefer:
- Issues with fewer blockers
- Smaller scope (non-epic issues over epics)
- Older issues (lower issue number)

### 4. Present Options to User

If multiple issues match, present them to the user:

```
Found 3 issues ready to work on:

  1. [Recommended] #45 [P1] Implement login validation
     Labels: type:task, area:auth, priority:p1
     Epic: #1 Phase 1: Core Foundation

  2. #47 [P1] Add password strength indicator
     Labels: type:task, area:auth, priority:p1
     Epic: #1 Phase 1: Core Foundation

  3. #52 [P2] Update error messages
     Labels: type:chore, priority:p2

Which issue would you like to start?
```

Use `AskUserQuestion` to let the user choose.

### 5. Start the Selected Issue

Once the user selects an issue:

**If the selected issue is an epic** (has `epic` label):
1. Move the epic to In Progress
2. Get the epic's subissues:
   ```bash
   .claude/skills/start-work/scripts/get-epic-subissues.sh EPIC_NUMBER
   ```
3. Find the first available (Ready, not blocked) subissue
4. Start working on that subissue instead (continue with steps below)
5. The epic stays In Progress until all subissues are In Review

**For regular issues (or the selected subissue):**

1. **Move to In Progress** (REQUIRED - do this BEFORE any implementation work):
   ```bash
   .claude/skills/start-work/scripts/set-issue-status.sh ISSUE_NUMBER in-progress
   ```
   If this is a subissue, also ensure the parent epic is In Progress.

2. **Assign to user** (optional, if requested):
   ```bash
   gh issue edit ISSUE_NUMBER --add-assignee @me
   ```

3. **Display issue details:**
   ```bash
   gh issue view ISSUE_NUMBER
   ```

4. **Check for local implementation plan:**
   Look for existing plans in `.projects/` that reference this issue.

5. **Report ready state:**
   ```
   Started #45: Implement login validation

   Status: In Progress
   Priority: P1
   Epic: #1 Phase 1: Core Foundation

   ## Description
   [Issue description here]

   ## Acceptance Criteria
   - [ ] Criterion 1
   - [ ] Criterion 2

   Ready to begin implementation. What would you like to tackle first?
   ```

## Project Board Reference

| Status      | Option ID  | Meaning                          |
|-------------|------------|----------------------------------|
| Backlog     | 64300155   | Work not yet prioritized         |
| Ready       | 8a7cfffc   | Ready to be picked up            |
| In Progress | a81dc5a2   | Currently being worked on        |
| Done        | 8e2dbd5e   | Completed                        |

**Project ID:** PVT_kwHOABDKXc4BOFIP
**Status Field ID:** PVTSSF_lAHOABDKXc4BOFIPzg84wPs

## Edge Cases

### No Issues Found

If no issues match the criteria:
```
No issues found matching your criteria.

Current project status:
- Ready: 0 items
- Backlog: 3 items
- In Progress: 2 items
- Done: 15 items

Would you like to:
1. View backlog items to prioritize
2. View in-progress work to pick up
3. Check a specific epic
4. Create new work with /add-work
```

### Single Issue Found

If only one issue matches, still confirm with user before starting:
```
Found 1 issue ready to work on:

  #45 [P1] Implement login validation
  Labels: type:task, area:auth, priority:p1

Start working on this issue?
```

### Epic Has No Ready Subissues

If filtering by epic but all subissues are done or in progress:
```
Epic #1 has no Ready items.

Current status:
- Backlog: #48
- In Progress: #45, #47
- Done: #42, #43, #44

Would you like to:
1. Move a backlog item to Ready (#48)
2. Pick up in-progress work (#45 or #47)
3. View epic details
4. Check a different epic
```

## Integration with Implementation

After starting an issue, the user may want to:
- **Plan the implementation:** Suggest using `plan-feature` agent for complex work
- **Read requirements:** Check `.docs/requirements/` for related product requirements
- **Check existing plans:** Look in `.projects/` for implementation details

Provide relevant suggestions based on the issue type and complexity.
