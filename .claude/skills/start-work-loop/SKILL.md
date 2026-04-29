---
name: start-work-loop
description: Work through a scope of issues continuously, reviewing, merging, and unblocking each before moving to the next.
argument-hint: "<scope> - e.g., 'epic #1', 'label:area:auth', 'all ready', 'bug'"
user-facing: true
agent-invocable: false
context: fork
allowed-tools: Bash, Read, Write, Edit, Glob, Grep, Task
---

# Start Work Loop

Autonomously work through a scope of issues, taking each one all the way from "Ready" to "merged on master" before moving to the next. The loop:

- Finds the next issue in scope
- Implements the solution on a fresh branch off `master`
- Runs the `review` skill in a subagent and addresses findings before opening the PR
- Creates the PR and monitors its status checks every 60 seconds
- Fixes any failing checks, then merges the PR (rebase strategy)
- Resolves Renovate-induced conflicts by rebasing onto an updated `master`
- Unblocks dependent issues after each merge

## Autonomy Principle

**Goal: Minimize blocking on user input.** This loop runs autonomously with minimal user intervention.

- Do NOT ask the user to choose between candidate issues — pick one automatically
- Do NOT ask for confirmation before starting each issue
- Make reasonable decisions and keep moving forward
- Only block for user input when truly stuck (unrecoverable errors, ambiguous requirements, irrecoverable conflicts)

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
- **Project management:** [/.claude/rules/project-management.md](/.claude/rules/project-management.md)
- **Review skill:** [/.claude/skills/review/SKILL.md](/.claude/skills/review/SKILL.md)

## Scripts

This skill's own scripts (`.claude/skills/start-work-loop/scripts/`):

| Script                       | Purpose                                                              |
|------------------------------|----------------------------------------------------------------------|
| `get-scope-issues.sh`        | Get all issues matching the scope                                    |
| `unblock-issues.sh`          | Find and unblock issues whose blockers have PRs                      |
| `monitor-pr-checks.sh`       | Poll PR checks every 60s until success/failure (terminal state)      |
| `get-pr-check-failures.sh`   | Fetch logs/details of failed checks for a PR                         |
| `merge-pr.sh`                | Merge a PR with rebase strategy and delete the branch                |
| `rebase-on-master.sh`        | Update master from origin and rebase the current branch onto it      |

Also uses scripts from `/start-work`:

| Script                    | Purpose                                         |
|---------------------------|-------------------------------------------------|
| `list-project-items.sh`   | List project items, optionally filter by status |
| `get-epic-subissues.sh`   | Get subissues of an epic                        |
| `get-blocked-issues.sh`   | Get list of blocked issues                      |
| `set-issue-status.sh`     | Move issue to a project board status            |

## Main Loop Process

### 1. Parse Scope and Initialize

```bash
.claude/skills/start-work-loop/scripts/get-scope-issues.sh "<scope>"
```

**Scope types:**
- `epic #N` — All subissues of epic N
- `label:X` or shorthands (`bug`, `feature`, `task`, `chore`) — Issues with that label
- `all ready` — All issues in Ready status
- `priority:pN` — All issues with that priority
- Combinations: `epic #1 priority:p1`

Initialize tracking:
- `completed_issues` — Issues whose PRs have been merged this session
- Always start each issue from a fresh `master` (no branch stacking — see below)

### 2. Find Next Issue

1. Get all issues matching scope
2. Exclude issues already completed this session
3. Exclude blocked issues (use `get-blocked-issues.sh`)
4. Filter to Ready or In Progress status
5. Sort by priority (P0 > P1 > P2 > P3 > unlabeled)
6. **If multiple issues tie on priority, pick one** — do not ask the user

If no issues are ready but some are blocked:
- Run unblock check (step 8) and retry
- If still nothing ready, complete the loop

### 3. Handle Epics

If the selected issue has the `epic` label:

1. Move the epic to In Progress
2. List its subissues:
   ```bash
   .claude/skills/start-work/scripts/get-epic-subissues.sh EPIC_NUMBER
   ```
3. Pick the first available (Ready, not blocked) subissue and work on that
4. The epic stays In Progress until all subissues are merged (Done)

### 4. Start Working on the Issue

1. **Move issue (and parent epic, if any) to In Progress:**
   ```bash
   .claude/skills/start-work/scripts/set-issue-status.sh ISSUE_NUMBER in-progress
   ```

2. **Create a fresh branch off master:**
   ```bash
   git checkout master
   git pull --ff-only origin master
   git checkout -b <issue#>-<brief-description>
   ```

   No branch stacking — every PR is merged before the next issue starts, so each new branch always starts from the latest `master`.

3. **Display issue context:**
   ```bash
   gh issue view ISSUE_NUMBER
   ```

4. **Implement the solution:**
   - Read issue description and acceptance criteria
   - Explore codebase as needed
   - Write code, tests, and documentation
   - Run `./check` locally before considering implementation done

### 5. Pre-PR Review (REQUIRED)

**Before creating the PR**, run the `review` skill in a subagent over the staged/unstaged changes. This is mandatory — do not skip.

Spawn a subagent (e.g. via the `Agent` tool with `subagent_type: "general-purpose"`) and instruct it to invoke the `review` skill against the current diff. The subagent should:

- Run `git diff master...HEAD` (and `git status`) to identify the changed surface
- Apply the `review` skill checklists for the relevant focus areas
- Return a prioritized list: 🔴 Blocking, 🟡 Important, 🔵 Suggestions

**Address every 🔴 Blocking finding before opening the PR.** Address 🟡 Important findings unless there's a clear reason to defer (note the reason in the PR body if so). 🔵 Suggestions are optional.

Re-run `./check` after applying review fixes.

### 6. Create the PR

1. **Commit changes** (first commit on a new branch carries the issue number):
   ```bash
   git add <files>
   git commit -m "#ISSUE_NUMBER <description>"
   ```

2. **Push branch:**
   ```bash
   git push -u origin <branch-name>
   ```

3. **Create PR** with `master` as the base (no stacking):
   ```bash
   gh pr create --base master \
     --title "#N <brief description>" \
     --label "type:task" \
     --body "Closes #N

   ## Summary
   <bullet points>

   ## Test plan
   - [ ] <test items>"
   ```
   Use the appropriate type label (`type:task`, `type:feature`, `type:chore`, `bug`).

4. **Move issue to In Review:**
   ```bash
   .claude/skills/start-work/scripts/set-issue-status.sh ISSUE_NUMBER in-review
   ```

### 7. Monitor PR Checks (every 60 seconds)

Poll the PR's checks until a terminal state is reached:

```bash
.claude/skills/start-work-loop/scripts/monitor-pr-checks.sh PR_NUMBER --interval 60
```

The script writes a single JSON line to stdout when it terminates. Possible outcomes:

#### a) `{"status":"success", ...}` — Merge the PR

```bash
.claude/skills/start-work-loop/scripts/merge-pr.sh PR_NUMBER
```

If the merge fails because the branch is out of date with master (Renovate may have merged a dependency PR while you were working), go to **(c) conflict** below, then retry the merge.

After the merge succeeds:
- The remote branch is auto-deleted (`deleteBranchOnMerge=true`)
- Locally: `git checkout master && git pull --ff-only origin master && git branch -D <branch>`
- The closing keyword in the PR body (`Closes #N`) auto-closes the issue → moves to Done. **Verify** the issue landed in Done (`gh issue view <N> --json state,projectItems`). If it's still In Review (no closing keyword fired, or the project field didn't update), move it manually:
  ```bash
  gh issue close <N>
  .claude/skills/start-work/scripts/set-issue-status.sh <N> done
  ```
- Add the issue to `completed_issues`
- Continue to step 8

#### b) `{"status":"failure","failed_checks":[...]}` — Fix the failure

1. **Get failure details:**
   ```bash
   .claude/skills/start-work-loop/scripts/get-pr-check-failures.sh PR_NUMBER
   ```
   This prints each failed check's name, link, and the tail of its failed-step logs.

2. **Diagnose** based on the failure category:
   - `ktlint` / `detekt` / `lint` → run `./format` and/or fix style violations locally; re-run `./check`
   - `assemble` / build → reproduce locally with the gradle task in the log
   - `unit_and_screenshot_tests` → run the failing test locally; for screenshot diffs use `./gradlew recordPaparazziDebug` only if the new rendering is actually correct
   - `dependency_analysis` → adjust module dependencies per the script's recommendation
   - `danger` → read the Danger comment on the PR for the rule violation
   - `license_check` → ensure new dependencies have approved licenses

3. **Fix, commit, push:**
   ```bash
   git add <files>
   git commit -m "<short description of fix>"
   git push origin <branch-name>
   ```
   (No issue number on follow-up commits — only the first commit on the branch references the issue.)

4. **Resume monitoring** by re-running `monitor-pr-checks.sh`.

5. **If the same check keeps failing after 3 fix attempts**, leave the issue In Review with a comment summarizing what's blocking, skip to the next issue in scope, and surface the situation in the final report.

#### c) `{"status":"conflict", ...}` — Rebase onto master

A conflict means `master` advanced while you were working (almost always a Renovate auto-merge). Resolve by rebasing onto the updated master:

```bash
.claude/skills/start-work-loop/scripts/rebase-on-master.sh --push
```

The script:
- Refuses to run with uncommitted changes
- Fetches `origin/master` and fast-forwards local `master`
- Rebases the current branch onto `master`
- Pushes with `--force-with-lease` if `--push` is given

**If the rebase produces conflicts the script can't resolve** (exit code 1):
- Inspect the conflict markers (`git status --short`)
- Resolve obvious conflicts (the kind Renovate causes are usually in `gradle/libs.versions.toml`, lockfiles, or `.idea/` config — accept the master-side version unless your branch deliberately changed the same line)
- `git add <resolved-files> && git rebase --continue`
- `git push --force-with-lease origin <branch-name>`
- If conflicts are non-trivial and outside your changed surface, ask the user before continuing

After a successful rebase, return to step 7 (re-monitor checks).

#### d) `{"status":"timeout", ...}`

Checks took longer than the script's `--max-wait`. Re-invoke `monitor-pr-checks.sh` to keep waiting, or escalate to the user if something looks stuck (e.g. a runner queued for >30 min).

### 8. Unblock Dependent Issues

After each merge, unblock issues whose blockers are now resolved:

```bash
.claude/skills/start-work-loop/scripts/unblock-issues.sh
```

Issues are considered unblocked when their blockers are merged (Done) or have an open PR (In Review). The script moves matching issues from Backlog to Ready.

### 9. Loop or Complete

**Continue** if remaining issues in scope are Ready or can be unblocked.

**Complete** when:
- All issues in scope are Done (or In Review with their PR queued for auto-merge)
- Remaining issues are blocked by external factors not in scope
- Repeated check failures left an issue stuck in In Review (reported in the summary)

**Final report:**
```
Completed work loop for scope: <scope>

Merged this session:
  1. #45 → PR #101 (merged)
  2. #46 → PR #102 (merged)
  3. #47 → PR #103 (merged)

Stuck (left in In Review):
  - #48 → PR #104 — repeated test failure in <test name>

Blocked by external factors:
  - #50 blocked by #30 (not in scope)
```

## Why No Branch Stacking?

Earlier versions of this skill stacked PRs (each branch based on the previous) so multiple in-flight PRs could coexist. With merge-on-success, that's no longer needed:

- Each PR merges to `master` before the next issue begins, so the next branch is always cut from up-to-date `master`
- No risk of cascading rebase pain when an early PR in the stack changes
- Renovate auto-merges interleave cleanly with our merges instead of poisoning a stack

If a future issue truly depends on changes from a not-yet-merged PR, work on it last and pull in the merged base when starting.

## Error Handling

**Principle: Recover automatically when possible. Only block on the user when truly stuck.**

| Scenario                                | Recovery                                                                                          |
|-----------------------------------------|---------------------------------------------------------------------------------------------------|
| Implementation can't pass `./check`     | Iterate up to 3 times. If still failing, skip the issue (leave In Progress) and continue the loop |
| `gh pr create` rejected (out of date)   | `rebase-on-master.sh --push`, then retry                                                          |
| PR merge rejected for being out of date | `rebase-on-master.sh --push`, wait for checks, retry merge                                        |
| Failing check after 3 fix attempts      | Leave PR In Review with a comment, skip to next issue, surface in final report                    |
| Rebase has non-trivial conflicts        | Resolve obvious ones; ask the user only if the conflict is outside your changed surface           |
| All remaining issues blocked externally | End the loop and report status — do not ask the user whether to continue                          |

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
[implementation]
Running review skill via subagent...
  → 1 blocking issue: missing test for empty list case. Fixing.
  → 1 important: rename `EntityModuleX` to follow convention. Fixing.
Creating PR #57 (base: master)...
Issue moved to In Review.

Monitoring PR #57 checks (60s interval)...
[10:14:22] PR #57: 3 pass, 0 fail, 7 pending (of 10 total)
[10:15:22] PR #57: 8 pass, 0 fail, 2 pending (of 10 total)
[10:16:22] PR #57: 10 pass, 0 fail, 0 pending (of 10 total)
All checks passed. Merging...
Merged. Issue #17 closed (Done).

Unblock check: #18 and #19 now Ready.

Continuing with #18...
```

## Interruption and Resume

If the loop is interrupted:

- Merged PRs and closed issues stay as-is
- A current in-flight issue stays In Progress (or In Review if its PR was already created)
- Use `/resume-work` to continue the interrupted issue
- Use `/start-work-loop <same-scope>` to restart the loop (it will skip already-merged issues based on issue status)
