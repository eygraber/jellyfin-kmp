# Definition Workflow

Use this workflow when asked to define product requirements BEFORE implementation. This is the preferred approach
for new features as it ensures product intent drives development.

## When to Use

- New feature with no existing code
- Significant enhancement to existing feature
- User wants to specify requirements before implementation begins

## Determine Workflow Type

Before starting, check if a requirements document already exists for the feature:

```bash
ls .docs/requirements/<feature>.md 2>/dev/null
```

Based on what exists and the user's intent, choose one of three workflows:

| Scenario                          | Existing Doc? | User Intent                           | Workflow        |
|-----------------------------------|---------------|---------------------------------------|-----------------|
| Brand new feature                 | No            | Define requirements from scratch      | New Requirement |
| Major enhancement or redesign     | Yes           | Significant functional changes        | New Version     |
| Clarification, typo, or minor fix | Yes           | Small corrections, no behavior change | Patch Fix       |

---

## Workflow A: New Requirement

Use when no requirements document exists for the feature.

### 1. Gather Context

Start by understanding what the user wants to build. Ask clarifying questions:

**Core Understanding:**
- What problem does this feature solve for users?
- Who are the target users?
- What is the primary user goal?

**Scope:**
- What are the key capabilities?
- What is explicitly out of scope?
- Are there related features this interacts with?

**Constraints:**
- Are there platform limitations to consider?
- Are there accessibility requirements?
- Are there performance requirements?

Use `AskUserQuestion` to gather this information efficiently. Group related questions together.

### 2. Define User Flows

Work with the user to define the primary user flows:

1. **Happy Path**: The ideal scenario where everything works
2. **Error Cases**: What happens when things go wrong
3. **Edge Cases**: Unusual but valid scenarios

For each flow, identify:
- Entry point (how does the user get here?)
- Steps (what does the user do?)
- Exit point (where does the user go next?)
- State changes (what changes in the system?)

### 3. Draft Requirements

Transform user flows into numbered, testable requirements:

**Good requirement characteristics:**
- Describes WHAT, not HOW
- Is testable (can verify pass/fail)
- Uses consistent language ("must" for mandatory, "should" for recommended)
- Is specific enough to implement unambiguously
- Is user-centric (describes behavior user sees)

**Example transformation:**

User says: "Users should be able to add tasks quickly"

Draft requirements:
1. The system must display a floating action button on the task list screen for creating new tasks.
2. The system must open the task creation screen when the user taps the add button.
3. The system must focus the title input field automatically when the task creation screen opens.
4. The system must allow users to save a task by tapping the save button.
5. The system must return to the task list after successful task creation.

### 4. Identify Edge Cases

For each requirement, consider:

- **Empty/null states**: What if required data is missing?
- **Validation failures**: What if input is invalid?
- **Network errors**: What if the operation fails?
- **Concurrent operations**: What if user acts while operation is in progress?
- **Boundary conditions**: What are the limits (max length, etc.)?
- **Interruptions**: What if user navigates away mid-operation?

Format as numbered list with **bold condition** followed by expected behavior.

### 5. Review with User

Present the draft requirements to the user for review:

1. Show the overview section - does it capture the intent?
2. Walk through requirements - are any missing or incorrect?
3. Review edge cases - are the behaviors acceptable?
4. Confirm scope - is anything included that should be out of scope?

Iterate until the user approves.

### 6. Generate Document

Create the requirements document following [template.md](template.md):

- Set Status to "Not Started"
- Set Version to "1.0"
- Write user-centric Overview
- List all numbered Requirements grouped logically
- List all Edge Cases with expected behaviors
- Add Version History entry
- Related Issues will be empty (no implementation yet)

Save to `.docs/requirements/<feature>.md`

---

## Workflow B: New Version

Use when requirements exist but the user wants to make significant functional changes (new capabilities,
redesigned behavior, major enhancements). This warrants a version bump.

### 1. Read Existing Requirements

Read the current requirements document to understand what exists:

```bash
cat .docs/requirements/<feature>.md
```

Summarize for the user:
- Current version number
- Key existing requirements
- Status of the feature

### 2. Determine Version Increment

Ask the user about the nature of changes to determine version increment:

| Change Type                                            | Version Bump | Example   |
|--------------------------------------------------------|--------------|-----------|
| **Major**: Breaking changes, redesign, or new paradigm | Major (X.0)  | 1.2 → 2.0 |
| **Minor**: New capabilities, non-breaking enhancements | Minor (X.Y)  | 1.2 → 1.3 |

Use `AskUserQuestion` to clarify:
```
What type of changes are you making to <feature>?

1. Major redesign or breaking changes (→ new major version)
2. Adding new capabilities without breaking existing behavior (→ new minor version)
```

### 3. Identify What's Changing

Work with the user to understand:

**Additions:**
- What new requirements are being added?
- What new edge cases need to be covered?

**Modifications:**
- Which existing requirements are changing?
- How are they changing?

**Removals:**
- Are any requirements being removed or deprecated?
- Why are they no longer needed?

Present a change summary before proceeding:
```
## Proposed Changes to <feature> (v1.2 → v2.0)

### New Requirements
- [new requirement 1]
- [new requirement 2]

### Modified Requirements
- Requirement 3: [old] → [new]

### Removed Requirements
- Requirement 7: [reason for removal]
```

### 4. Draft Updated Requirements

Apply the changes to create the new version:

1. Keep unchanged requirements as-is (preserve numbering where possible)
2. Add new requirements (append to existing numbering)
3. Update modified requirements in-place
4. Remove deprecated requirements (renumber if necessary)
5. Update edge cases to reflect new behaviors

### 5. Review with User

Present the complete updated document for review:

1. Highlight what changed from the previous version
2. Confirm new requirements are complete
3. Verify modified requirements are accurate
4. Confirm removed requirements are intentional

### 6. Update Document

Update the requirements document:

- Increment Version appropriately (major.minor)
- Update Status if needed
- Apply all requirement changes
- Add Version History entry describing the changes:
  ```
  - **Version 2.0** - YYYY-MM-DD: Major redesign of authentication flow. Added OAuth support (req 8-12).
    Removed legacy password reset (req 7).
  ```

Save the updated `.docs/requirements/<feature>.md`

---

## Workflow C: Patch Fix

Use when making small corrections to existing requirements that don't change behavior. Examples:
- Fixing typos or grammatical errors
- Clarifying ambiguous wording
- Improving requirement testability without changing meaning
- Adding missing edge case that was always intended
- Fixing incorrect links or references

Patch fixes do NOT warrant a version bump.

### 1. Identify the Fix

Ask the user what needs to be corrected:

```
What needs to be fixed in the <feature> requirements?

1. Typo or grammatical error
2. Clarify ambiguous wording
3. Add missing edge case (behavior was always intended)
4. Fix broken link or reference
5. Other minor correction
```

### 2. Verify It's a Patch (Not a Version)

Confirm the fix does NOT change expected behavior:

**IS a patch fix:**
- "Change 'user must click' to 'user must tap'" (terminology)
- "Add comma for clarity" (grammar)
- "Clarify 'quickly' to mean 'within 2 seconds'" (adding specificity to existing intent)
- "Add edge case for empty list that we forgot" (documenting existing expected behavior)

**IS NOT a patch fix (use Workflow B instead):**
- "Change timeout from 30s to 60s" (behavior change)
- "Add support for biometric login" (new capability)
- "Remove requirement for email verification" (behavior change)

If the change affects behavior, redirect to Workflow B (New Version).

### 3. Apply the Fix

Make the correction directly in the requirements document:

- Do NOT change the version number
- Do NOT add a Version History entry (too minor)
- Ensure the fix doesn't inadvertently change meaning

### 4. Confirm with User

Show the specific change made:

```
Fixed: Requirement 5
- Before: "User must click the save button"
- After: "User must tap the save button"

No version change (patch fix only).
```

---

## Question Templates

Use these templates when gathering information from users:

### Initial Discovery

```
To define requirements for [feature], I need to understand:

1. **Problem**: What problem does this solve for users?
2. **Users**: Who will use this feature?
3. **Goal**: What is the main thing users want to accomplish?
```

### Scope Clarification

```
Let me confirm the scope:

**Included:**
- [capability 1]
- [capability 2]

**Not Included:**
- [out of scope item 1]
- [out of scope item 2]

Does this match your expectations?
```

### Edge Case Discovery

```
For [scenario], what should happen when:

1. [edge case 1]?
2. [edge case 2]?
3. [edge case 3]?
```

---

## Tips for Good Requirements

1. **Start with user goals, not features**: "User wants to track daily tasks" not "System needs a task list"

2. **Be specific about behaviors**: "Display error message below the field" not "Show an error"

3. **Consider all user types**: First-time user, power user, user with accessibility needs

4. **Think about state transitions**: What triggers changes? What are the before/after states?

5. **Don't over-specify**: Leave room for UX decisions that don't affect product behavior

6. **Validate with examples**: "If user enters 'abc' in phone field, system shows 'Invalid phone number'"

---

## Output Checklists

### New Requirement Checklist

Before finalizing a new requirements document:

- [ ] Overview explains the "why" clearly
- [ ] Every requirement is testable
- [ ] No implementation details in requirements
- [ ] Edge cases cover error, empty, and boundary conditions
- [ ] Requirements are grouped logically
- [ ] Language is consistent (must/should/may)
- [ ] Line length under 120 characters
- [ ] Version set to "1.0"
- [ ] Version history has initial entry
- [ ] Status set to "Not Started"

### New Version Checklist

Before finalizing a version update:

- [ ] Version number incremented correctly (major vs minor)
- [ ] Version history entry describes what changed
- [ ] Changed requirements are clearly updated
- [ ] Numbering is consistent (no gaps unless intentional)
- [ ] Edge cases updated to reflect new behaviors
- [ ] No unintentional changes to unaffected requirements
- [ ] User confirmed all additions/modifications/removals

### Patch Fix Checklist

Before finalizing a patch fix:

- [ ] Change does NOT affect expected behavior
- [ ] Version number NOT changed
- [ ] No Version History entry added
- [ ] Fix improves clarity without changing meaning
- [ ] User confirmed the correction
