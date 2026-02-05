---
name: resume-work
description: Resume working on an interrupted GitHub issue. Finds in-progress work and provides context to continue.
argument-hint: "[issue] - e.g., '', '#123'"
context: fork
allowed-tools: Bash(gh issue*, gh project*, gh api*), Read
---

# Resume Work

Continue working on an interrupted issue. This is a lightweight skill for picking up where you left off.

## Usage

```
/resume-work             # Find and resume in-progress work
/resume-work #123        # Resume specific issue #123
```

## Process

### With Issue Number

When the user specifies an issue number:

1. **Fetch issue details:**
   ```bash
   gh issue view ISSUE_NUMBER
   ```

2. **Check current status:**
   - If not In Progress, confirm with user before continuing
   - If closed/Done, inform user and suggest `/start-work` instead

3. **Check for local implementation plan:**
   Look in `.projects/` for plans referencing this issue.

4. **Display context to continue:**
   ```
   Resuming #123: Issue title here

   Status: In Progress
   Priority: P1

   ## Description
   [Issue description]

   ## Acceptance Criteria
   - [ ] Criterion 1
   - [ ] Criterion 2

   Ready to continue. What would you like to work on?
   ```

### Without Issue Number

When no issue is specified:

1. **Find in-progress items:**
   ```bash
   .claude/skills/start-work/scripts/list-project-items.sh --status in-progress
   ```

2. **If one item found:**
   Display it and confirm with user.

3. **If multiple items found:**
   ```
   Found 2 issues in progress:

     1. #48 [P0] Update ViewStatePreviewProviders
        Labels: type:chore, priority:p0

     2. #45 [P2] Integrate Firebase Crashlytics
        Labels: type:feature, priority:p2

   Which issue would you like to continue?
   ```

4. **If no items found:**
   ```
   No in-progress work found.

   Use /start-work to find the next issue to work on.
   ```

## References

- **GitHub project config:** [/.claude/rules/github-project.md](/.claude/rules/github-project.md)
- Uses scripts from `/start-work` skill for project queries
