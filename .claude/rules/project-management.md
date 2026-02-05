# Project Management with GitHub

## GitHub Projects
- Work is tracked via GitHub Projects board
- Use GitHub subissues to model epics (parent issues with child tasks)
- Current and next work can be found by viewing the GitHub Project board

## Work Intake

### Adding New Work

Use the `/add-work` skill to create new issues. The process:

1. **Determine work type**:
   - **Epic**: Large feature or initiative with multiple tasks
   - **Task**: Standalone implementation work
   - **Bug**: Defect requiring fix
   - **Chore**: Maintenance, refactoring, or tooling work

2. **Create the issue** with:
   - Clear, actionable title
   - Description with context and acceptance criteria
   - Labels for type and area (e.g., `type:feature`, `area:auth`)
   - Link to parent epic if applicable

3. **Add to Project board**:
   - New work goes to "Backlog" column by default
   - Move to "Ready" when it should be picked up soon
   - Set priority via labels (P0-P3)

### Breaking Down Epics

When creating an epic:
1. Create the parent issue describing the high-level goal
2. Create subissues for each implementation task
3. Link subissues to parent using GitHub's subissue feature
4. Order subissues by dependency/priority

### Modifying Existing Work

Use the `/manage-work` skill to:
- Update issue descriptions or acceptance criteria
- Change priority or labels
- Move issues between columns
- Add or remove subissues from epics
- Close or reopen issues

### Priority Levels

| Priority | Label         | Meaning                                |
|----------|---------------|----------------------------------------|
| P0       | `priority:p0` | Critical - drop everything             |
| P1       | `priority:p1` | High - address this sprint             |
| P2       | `priority:p2` | Medium - schedule soon                 |
| P3       | `priority:p3` | Low - backlog, do when capacity allows |

## Starting Work

Use the `/start-work` skill to find and begin working on the next issue:

```
/start-work                    # Find highest priority Todo item
/start-work epic #1            # Find work within a specific epic
/start-work bug                # Find ready bugs
/start-work in progress        # Pick up work already in progress
/start-work auth validation    # Find work matching description keywords
```

The skill will:
1. Query the project board for matching issues
2. Sort by priority (P0 > P1 > P2 > P3 > unlabeled)
3. Present options if multiple issues match
4. Move the selected issue to "In Progress"
5. Display issue details and acceptance criteria

## Work Discovery

To find current work manually:
1. Check the GitHub Project board for items in "Ready" or "In Progress" columns
2. Look at open issues assigned to the current milestone/phase
3. Review parent issues (epics) to see child subissues for detailed tasks

**Project board columns:**
- **Backlog** - Work not yet prioritized, or blocked by other work
- **Ready** - Ready to be picked up (not blocked, all dependencies met)
- **In Progress** - Currently being worked on
- **In Review** - PR created, awaiting review
- **Done** - Completed

## Status Transitions

**Agents MUST keep issue status up to date.** Status transitions:

| From        | To          | When                                                      |
|-------------|-------------|-----------------------------------------------------------|
| Backlog     | Ready       | All blocking issues resolved, work is ready to start      |
| Ready       | In Progress | Work has begun on the issue                               |
| In Progress | In Review   | PR created for the issue                                  |
| In Review   | Done        | PR merged and issue is complete (close issue)             |
| In Review   | In Progress | PR requires changes, back to active development           |
| In Progress | Ready       | Work paused, but issue is still unblocked                 |
| Any         | Backlog     | New blocker discovered, or work is deprioritized          |

**Rules:**
- When starting work: Move to **In Progress** before writing code
- When creating a PR: Move to **In Review**
- When completing work: Close the issue (automatically moves to Done)
- When work is interrupted: Leave in **In Progress** (use `/resume-work` to continue)
- When a blocker is removed: Move unblocked issues from Backlog to **Ready**
- **Never move issues out of Done** - if there's a problem, create a new issue

**Epic status rules:**
- When starting work on any subissue: Move the **parent epic** to **In Progress** (if not already)
- Epic stays **In Progress** while any subissue is being worked on
- When the **last subissue** moves to **In Review**: Move the epic to **In Review**
- When all subissues are **Done**: Close the epic (moves to Done)
- If working on an epic directly (not a subissue): Move epic to In Progress, then work on its first available subissue

**Status checks before starting:**
- Verify issue has no unresolved `blockedBy` relationships
- Check if any issues this work would unblock should move to Ready

## Resuming Interrupted Work

Use the `/resume-work` skill when returning to interrupted work:

```
/resume-work             # Find and continue in-progress work
/resume-work #123        # Resume specific issue
```

## After Completing Work
When finishing a subtask in the project:
1. Mark the GitHub issue/subissue as complete
2. Compact the context to summarize completed work only as it pertains to the next work
3. Update any relevant documentation if patterns changed
4. Push commits and create PR if appropriate

## Issue Structure
- **Epic (Parent Issue)**: High-level feature or phase goal
- **Subissue (Child Task)**: Specific implementation task
- Link subissues to parent using GitHub's subissue feature
- Reference requirements in issue descriptions

## Commit and PR Workflow

### Issue References in Commits
- First commit in PR includes issue number at front: `#123 Add feature X`
- Subsequent commits in same PR do not need issue number
- This creates clear traceability between code and work items

### One PR Per Issue (Trunk-Based Development)
- Each GitHub issue gets its own branch and PR
- PRs should close related issues using keywords (Closes #123)
- Never batch multiple issues into a single PR
- Short-lived branches - merge to main frequently
- Epic branches are NOT allowed - work on subissues individually

### Why One PR Per Issue
- Smaller, easier-to-review PRs
- Faster feedback cycles
- Clear 1:1 mapping for traceability
- Easier rollbacks if needed
- Reduced merge conflicts

## Requirements, Issues, and Plans

Three layers work together:

| Layer                  | Location              | Purpose                               | Owner       |
|------------------------|-----------------------|---------------------------------------|-------------|
| Product Requirements   | `.docs/requirements/` | What to build (user-facing behavior)  | PO/Product  |
| Work Tracking          | GitHub Issues         | What work items to do and when        | PO/Team     |
| Implementation Plans   | `.projects/`          | How to build it (technical execution) | Engineering |

### Product Requirements (`.docs/requirements/`)

**Source of truth** for product behavior and acceptance criteria.

- One file per feature/system (e.g., `authentication.md`, `task-management.md`)
- Numbered, testable requirements describing user-facing behavior
- Edge cases and special conditions
- Version history tracking changes

**Creating requirements:**
- `/requirements define <feature>` - **Preferred.** Define requirements collaboratively before implementation
- `/requirements generate <path>` - Generate from existing code (acceptable but not ideal)
- `/requirements validate <feature>` - Check code against requirements

**Do NOT put in requirements:**
- Technical implementation details (algorithms, data models)
- Architecture decisions
- Success metrics (those go in implementation plans)

### GitHub Issues

**Source of truth** for work tracking and prioritization.

- Epics for major features/phases
- Subissues for implementation tasks
- Reference requirements documents in issue descriptions
- Track priority, assignment, and status

### Implementation Plans (`.projects/`)

**Execution details** for how to implement requirements.

#### Structure: One Phase Per PR

Each phase directory represents ONE GitHub subissue and ONE PR:

```
.projects/<feature>/
├── PLAN.md              # Links to top-level epic, overview
├── phase-1-<name>/      # → GitHub subissue #X → PR #Y
│   ├── SCOPE.md         # Technical scope for this PR
│   └── TASKS.md         # Granular implementation checklist
├── phase-2-<name>/      # → GitHub subissue #Z → PR #W
│   ├── SCOPE.md
│   └── TASKS.md
└── ...
```

#### Phase Contents

- **SCOPE.md**: References requirements documents, technical requirements, UX notes, success metrics, out of scope
- **TASKS.md**: Granular implementation checklist for the single PR

#### Key Principles

- Each phase = one GitHub subissue = one PR = one short-lived branch
- Phases should be small enough to review in a single PR
- If a phase is too large, break it into multiple subissues/phases
- Complete and merge each phase before starting the next

### Relationship Model

```
.docs/requirements/feature.md ──────────────────────────────────────────
  (product requirements - source of truth)                             │
                                                                       │
GitHub Epic (#123) ────────────────────────────────────────────────────│
  │                                                    ←── references ─┤
  ├── Subissue #124 ──→ branch: feature/phase-1-xyz ──→ PR #130       │
  ├── Subissue #125 ──→ branch: feature/phase-2-abc ──→ PR #131       │
  └── Subissue #126 ──→ branch: feature/phase-3-def ──→ PR #132       │
                                                                       │
.projects/feature-name/ ───────────────────────────────────────────────│
  ├── PLAN.md (links to epic #123)                     ←── references ─┤
  ├── phase-1-xyz/
  │   ├── SCOPE.md (links to subissue #124)
  │   └── TASKS.md (implementation checklist for PR #130)
  └── phase-2-abc/
      ├── SCOPE.md (links to subissue #125)
      └── TASKS.md (implementation checklist for PR #131)
```

**Key mapping:** One phase directory = One subissue = One branch = One PR

### Workflow

1. **PO defines requirements** via `/requirements define <feature>` (preferred)
   - Collaborates with agent to define well-thought-out requirements
   - Creates `.docs/requirements/<feature>.md`
   - Requirements drive what will be built

2. **PO creates epic** via `/add-work epic`
   - References requirements document in description
   - Epic represents the high-level feature/initiative

3. **Agent plans implementation** via `plan-feature` agent
   - Reads requirements from `.docs/requirements/`
   - Creates `.projects/<feature>/` with PLAN.md linking to epic
   - Creates phase directories, each representing ONE subissue worth of work
   - Each phase should be small enough for a single PR

4. **Agent/dev creates subissues** for each phase via `/add-work task` linked to epic
   - One subissue per phase directory
   - Subissue description references the phase's SCOPE.md

5. **During implementation** (per phase):
   - Create branch: `feature/<phase-description>`
   - Work through TASKS.md checklist
   - First commit references subissue: `#124 Add X`
   - Create PR: "Closes #124"
   - Merge to main after review
   - Delete branch

6. **After each phase**:
   - Subissue closes when PR merges
   - Update PLAN.md status
   - Start next phase

7. **On feature completion**:
   - All subissues closed → close epic (or auto-closes)
   - Delete `.projects/<feature>/` directory
   - Requirements document remains as permanent record

### What Goes Where

| `.docs/requirements/`            | GitHub Issues            | `.projects/`                    |
|----------------------------------|--------------------------|---------------------------------|
| User-facing requirements         | Work item descriptions   | Technical requirements          |
| Edge cases and validation rules  | Priority and scheduling  | Architecture decisions          |
| Acceptance criteria (behavior)   | Assignment and status    | Implementation approach         |
| Version history                  | Cross-team visibility    | Task dependencies and ordering  |

### Rules

- Product requirements MUST live in `.docs/requirements/`
- Every `.projects/` SCOPE.md MUST reference requirements documents
- Every `.projects/` directory MUST reference a GitHub issue
- GitHub issues are never deleted when local plans are archived
- Subissues should be created before starting each phase
- PRs must reference the GitHub issue they close
- When requirements change, update `.docs/requirements/` first, then update implementation plans

### Requirements-First Warning

**Ideal workflow**: Requirements should be defined BEFORE implementation begins.

When creating a new implementation plan (`.projects/`), check if the relevant requirements exist in `.docs/requirements/`.
If requirements are missing or incomplete:

1. **WARN the user** that requirements should ideally be defined first
2. Suggest running `/requirements generate` to create requirements from existing code (if code exists)
3. Or suggest defining requirements before starting implementation (if greenfield)

It is acceptable to generate requirements after implementation (by analyzing code), but this is not ideal because:
- Implementation decisions may not align with product intent
- Edge cases may be missed that product would have specified
- Rework may be needed if requirements differ from what was built

**When you encounter this scenario, output a warning like:**
```
⚠️ Requirements-first recommended: No requirements found in .docs/requirements/ for this feature.
   Consider defining product requirements before implementation, or run `/requirements generate`
   after implementation to document what was built.
```
