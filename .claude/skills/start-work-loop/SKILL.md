---
name: start-work-loop
description: Work through a scope of issues continuously until all have PRs. Stacks branches, manages context, and unblocks dependent issues.
argument-hint: "<scope> - e.g., 'epic #1', 'label:area:auth', 'all ready', 'bug'"
user-facing: true
agent-invocable: false
context: fork
allowed-tools: Bash, Read, Write, Edit, Glob, Grep, Task
---

# Start Work Loop

Autonomously work through a scope of issues until all have PRs created. This skill implements a continuous work loop that:

- Finds the next issue in scope
- Implements the solution and creates a PR
- Stacks branches to avoid conflicts
- Manages context between issues
- Unblocks dependent issues after each PR

## Autonomy Principle

**Goal: Minimize blocking on user input.** This loop should run autonomously with minimal user intervention.

- Do NOT ask the user to choose between candidate issues - pick one automatically
- Do NOT ask for confirmation before starting each issue
- Do NOT merge PRs - only create them and leave for user review
- Only block for user input when absolutely necessary (unrecoverable errors, ambiguous requirements)
- Make reasonable decisions and keep moving forward

## Usage

```
/start-work-loop epic #1              # Complete all issues in epic #1
/start-work-loop label:area:auth      # Complete all auth-related issues
/start-work-loop all ready            # Complete all ready issues
/start-work-loop bug                  # Complete all bugs
/start-work-loop type:task priority:p1  # Complete all P1 tasks
```

## References

- **GitHub project config:** [/.claude/rules/github-project.md](/.claude/rules/github-project.md)
- **gh CLI commands:** [/.claude/rules/github-commands.md](/.claude/rules/github-commands.md)
- **Git workflow:** [/.claude/rules/git-workflow.md](/.claude/rules/git-workflow.md)

## Scripts

| Script                    | Purpose                                              |
|---------------------------|------------------------------------------------------|
| `unblock-issues.sh`       | Find and unblock issues whose blockers have PRs      |
| `get-scope-issues.sh`     | Get all issues matching the scope                    |

Also uses scripts from `/start-work`:

| Script                    | Purpose                                         |
|---------------------------|-------------------------------------------------|
| `list-project-items.sh`   | List project items, optionally filter by status |
| `get-epic-subissues.sh`   | Get subissues of an epic                        |
| `get-blocked-issues.sh`   | Get list of blocked issues                      |
| `set-issue-status.sh`     | Move issue to a project board status            |

## Main Loop Process

### 1. Parse Scope and Initialize

Parse the user's scope definition and gather all issues that match:

```bash
# Get issues matching scope
.claude/skills/start-work-loop/scripts/get-scope-issues.sh "<scope>"
```

**Scope types:**
- `epic #N` - All subissues of epic N
- `label:X` or type shorthands (`bug`, `feature`, `task`, `chore`) - Issues with that label
- `all ready` - All issues in Ready status
- `priority:pN` - All issues with that priority
- Combinations: `epic #1 priority:p1` - P1 issues within epic #1

Initialize tracking:
- `completed_issues` - Issues with PRs created this session
- `base_branch` - Starting branch (usually `master`)
- `current_branch` - Current working branch (for stacking)

### 2. Find Next Issue

Query for the next issue to work on within the scope:

1. Get all issues matching scope
2. Exclude issues already completed this session
3. Exclude blocked issues (unless blocker has a PR from this session)
4. Filter to Ready or In Progress status
5. Sort by priority (P0 > P1 > P2 > P3 > unlabeled)
6. **If multiple issues have the same highest priority, pick one at random**
7. Do NOT ask user to choose - just pick and proceed

```bash
# Get blocked issues
.claude/skills/start-work-loop/scripts/../start-work/scripts/get-blocked-issues.sh
```

If no issues are ready but some are blocked:
- Run the unblock check (see step 6)
- Retry finding the next issue

If no issues remain in scope:
- Report completion and exit the loop

### 3. Handle Epics

If the selected issue is an **epic** (has `epic` label):

1. Move the epic to In Progress
2. Get the epic's subissues:
   ```bash
   .claude/skills/start-work/scripts/get-epic-subissues.sh EPIC_NUMBER
   ```
3. Find the first available (Ready, not blocked) subissue
4. Work on that subissue instead (the epic stays In Progress)

If none of the subissues are ready to be worked on, find the next issue in scope.

**Epic status management:**
- When starting work on any subissue, ensure parent epic is In Progress
- After creating a PR for a subissue, check if it's the last subissue
- If all subissues are now In Review or Done, move the epic to In Review

### 4. Start Working on Issue

1. **Move issue to In Progress** (and parent epic if applicable):
   ```bash
   .claude/skills/start-work/scripts/set-issue-status.sh ISSUE_NUMBER in-progress
   # If this is a subissue, also ensure epic is In Progress
   ```

2. **Create stacked branch:**
   ```bash
   # Branch from current working branch (not master) to stack
   git checkout -b <issue#>-<brief-description>
   ```

   The branch name follows the convention: `<issue#>-<brief-description>`

3. **Display issue context:**
   ```bash
   gh issue view ISSUE_NUMBER
   ```

4. **Implement the solution:**
   - Read issue description and acceptance criteria
   - Explore codebase as needed
   - Write code, tests, and documentation
   - Run `./check` to verify

### 5. Create PR

After implementing the solution:

1. **Commit changes:**
   ```bash
   git add <files>
   git commit -m "#ISSUE_NUMBER <description>"
   ```

2. **Push branch:**
   ```bash
   git push -u origin <branch-name>
   ```

3. **Create PR:**
   - Set base branch to `current_branch` (the previous issue's branch, or `master` if first)
   - Title: `#ISSUE_NUMBER <brief description>`
   - Body: Summary, test plan, closes reference
   - Add appropriate label (`type:task`, `type:feature`, `type:chore`, or `bug`)

   ```bash
   gh pr create --base <current_branch> --title "#N Title" --label "type:task" --body "..."
   ```

4. **Move issue to In Review:**
   ```bash
   .claude/skills/start-work/scripts/set-issue-status.sh ISSUE_NUMBER in-review
   ```

5. **Check epic status** (if this was a subissue):
   - Get all subissues of the parent epic
   - If ALL subissues are now In Review or Done, move the epic to In Review
   - Otherwise, epic stays In Progress

6. **Update tracking:**
   - Add issue to `completed_issues`
   - Set `current_branch` to the new branch (for stacking next PR)

### 6. Manage Context

After creating a PR, determine how to handle context for the next issue:

**Determine relationship to next issue:**

Look at the next issue in scope (if any). Consider it "related" if:
- Same epic
- Same area/component (based on labels or file paths touched)
- Sequential dependency (current issue unblocked the next)

**If next issue is related AND context is manageable:**
- Use `/compact` to summarize completed work
- Retain relevant context about architecture, patterns, files modified

**If next issue is unrelated OR context is too large:**
- Use `/clear` to start fresh
- The stacked branch will still be the base for the next PR

**Context size heuristic:**
- If conversation has > 50 turns since last clear/compact, prefer clearing
- If implementation touched > 20 files, prefer clearing
- If next issue is in a completely different area, prefer clearing

### 7. Unblock Dependent Issues

After creating a PR, check if any blocked issues can now be unblocked:

```bash
.claude/skills/start-work-loop/scripts/unblock-issues.sh
```

This script:
1. Gets all blocked issues in the scope
2. For each blocked issue, checks if ALL blocking issues have PRs (In Review or Done)
3. If so, moves the issue from Backlog to Ready

**Important:** An issue is considered "unblocked" when its blocker has a PR created, not just when work has started. This allows parallel work to proceed once the blocking implementation is complete and reviewable.

### 8. Loop or Complete

**Continue loop if:**
- There are remaining issues in scope that are Ready or can be unblocked

**Complete if:**
- All issues in scope have PRs (In Review or Done)
- No more issues can be unblocked (circular dependencies or external blockers)
- All remaining issues failed implementation (skipped)

**On completion:**
```
Completed work loop for scope: <scope>

PRs Created (stacked):
  1. #45 → PR #101 (base: master)
  2. #46 → PR #102 (base: 45-add-validation)
  3. #47 → PR #103 (base: 46-update-tests)

Issues remaining (blocked by external factors):
  - #48 blocked by #30 (not in scope)

To merge: Merge PRs in order starting from #101
```

## Branch Stacking Strategy

PRs are stacked to avoid merge conflicts:

```
master ─────────────────────────────────────────────
    \
     45-add-validation ─────────────────────────────
         \
          46-update-tests ──────────────────────────
              \
               47-add-feature ──────────────────────
```

Each PR's base is the previous branch, creating a stack:
- PR #101: `45-add-validation` → `master`
- PR #102: `46-update-tests` → `45-add-validation`
- PR #103: `47-add-feature` → `46-update-tests`

## Error Handling

**Principle: Attempt to recover automatically. Only ask user when truly stuck.**

### Implementation Fails

If implementation fails (tests don't pass, can't figure out solution):

1. **First, attempt to fix automatically** - review errors, adjust approach
2. **If still failing after reasonable attempts:**
   - Log the failure with details
   - Skip this issue and continue with next
   - Leave issue in In Progress (not completed)
   - Don't add to completed list
   - Continue with next issue (still stack from current branch)
3. **Only ask user if:** The issue is critical (P0) or all remaining issues are failing

### PR Creation Fails

If PR creation fails (push rejected, conflicts):

1. **Attempt to resolve automatically:**
   ```bash
   # Rebase onto the previous branch in the stack (the one this branch was created from)
   git pull --rebase origin <previous-branch>
   ```

2. **If conflicts during rebase:** Attempt to resolve obvious conflicts, abort rebase if complex

3. **Only ask user if:** Conflicts cannot be automatically resolved

### No Ready Issues But Blocked Issues Exist

If all remaining issues are blocked by external factors (issues outside the scope):

1. Log what's blocking each issue
2. **End the loop** - report completion with remaining blocked issues
3. Do NOT ask user whether to continue - just report status and finish

## Example Session

```
User: /start-work-loop epic #2

Claude: Starting work loop for epic #2

Scope: Epic #2 - Phase 2: Essential Task Features
Found 5 issues:
  - #17 [Ready] Create entity modules (P2)
  - #18 [Blocked by #17] Add project repository
  - #19 [Blocked by #17] Add tag repository
  - #21 [Blocked by #18, #19] Update task entity
  - #22 [Blocked by #21] Add task filtering

Starting with #17...

[... implements #17, creates PR ...]

PR #57 created for #17: Create entity modules
Branch: 17-create-entity-modules (base: master)
Issue moved to In Review

Checking for newly unblocked issues...
  - #18 unblocked (blocker #17 has PR)
  - #19 unblocked (blocker #17 has PR)
Moving #18 and #19 to Ready

Context: Next issue #18 is related (same epic, builds on #17)
Compacting context...

[... continues with #18 ...]
```

## Interruption and Resume

If the loop is interrupted (user stops, error, session ends):

- Completed PRs remain as-is
- Current issue stays In Progress
- Use `/resume-work` to continue the interrupted issue
- Use `/start-work-loop <same-scope>` to restart the loop (will skip completed issues)

The loop tracks completion by checking issue status (In Review = has PR), so restarting is safe.
