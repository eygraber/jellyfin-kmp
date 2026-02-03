# Generation Workflow

Use this workflow when asked to generate or update requirements from code.

## Determine Generation Mode

Before starting, determine which mode applies:

### Mode Decision Tree

```
Does a requirement file exist for this feature?
├── NO → Use "New Requirement" workflow
└── YES → What is the scope of changes?
    ├── Significant behavior changes, new features, or structural overhaul → Use "New Version" workflow
    ├── Clarifications, typo fixes, or minor wording improvements → Use "Patch Fix" workflow
    └── Unsure → Compare git history since last version date to determine scope
```

### Mode Indicators

| Mode            | Version Change | When to Use                                                                |
|-----------------|----------------|----------------------------------------------------------------------------|
| **New**         | 1.0            | No existing requirement file; documenting feature for the first time       |
| **New Version** | Major (2.0)    | Adding/removing requirements; changing core behavior; significant rewrites |
| **Patch Fix**   | Minor (1.1)    | Fixing typos; clarifying ambiguous wording; minor edge case additions      |

---

## New Requirement Workflow

Use when creating requirements documentation for a feature that has no existing requirement file.

### 1. Identify Feature Scope
- Trace code from the starting point provided
- List all relevant modules/files
- Identify boundaries of the feature

### 2. Analyze Git History
- Examine `git log` for identified modules
- Extract issue keys from commit messages
- Group commits by issue key
- Create entries under "Related Issues"

```bash
# Search git log for issue keys
git log --all --oneline -- path/to/module
```

### 3. Synthesize Overview
- Study UI elements and user interactions
- Identify the problem being solved
- Write concise, user-centric overview (1-2 paragraphs)

### 4. Extract Requirements
- Translate code behavior into product requirements
- Focus on WHAT the system does, not HOW
- Analyze "happy path" in conditional logic
- Group requirements logically

**Example transformation:**

```kotlin
// Code
fun validatePhoneNumber(input: String) {
  if (input.isEmpty()) {
    state.value = state.value.copy(error = "Phone number required")
    return
  }

  if (!isValidPhoneNumber(input)) {
    state.value = state.value.copy(error = "Invalid phone number format")
    return
  }

  // Send verification code
}
```

**Extracted requirement:**
> The system must validate that the user has entered a phone number before proceeding with verification.

### 5. Identify Edge Cases
- Find error handling (`catch`, `else`, null checks)
- Translate into product-focused edge case descriptions
- Group logically

**From the same code, extracted edge cases:**
1. **Empty Phone Number Field**: If the user attempts to proceed without entering a phone number, the system
   must display an error message indicating the field is required.
2. **Invalid Phone Number Format**: If the user enters a phone number in an incorrect format, the system must
   display an error message explaining the correct format.

### 6. Generate Markdown File
- Assemble into complete `.docs/requirements/[feature].md`
- Follow template exactly (see [template.md](template.md))
- Set version to **1.0**
- Add initial version history entry

---

## New Version Workflow

Use when significant changes require a new major version of existing requirements (e.g., 1.x → 2.0).

### When to Use New Version

- Adding or removing multiple requirements
- Changing core feature behavior
- Restructuring the requirements document
- Feature has undergone significant redesign
- Breaking changes from user perspective

### 1. Read Existing Requirements
- Read the current `.docs/requirements/[feature].md` file
- Note the current version number
- Understand existing requirements structure

### 2. Analyze Changes Since Last Version
```bash
# Find commits since the last version date
git log --since="YYYY-MM-DD" --all --oneline -- path/to/module
```
- Compare current code against documented requirements
- Identify added, removed, and modified behaviors
- Extract new issue keys from recent commits

### 3. Categorize Changes

| Change Type        | Action                                          |
|--------------------|-------------------------------------------------|
| New behavior       | Add new requirement(s)                          |
| Removed behavior   | Remove requirement(s), note in version history  |
| Modified behavior  | Update existing requirement wording             |
| New edge case      | Add to edge cases section                       |
| Removed edge case  | Remove from edge cases, note in version history |

### 4. Update Requirements Document
- Increment to next major version (e.g., 1.2 → 2.0)
- Update all affected sections
- Preserve requirement numbering where possible for traceability
- Re-number if requirements were removed (note in version history)

### 5. Document Version History
Add a detailed version history entry:
```markdown
- **Version 2.0** - YYYY-MM-DD: Major update. Added support for [feature]. Removed deprecated [behavior].
  Restructured requirements for clarity.
```

### 6. Update Related Issues
- Add new issue keys from recent commits
- Keep historical issues that remain relevant
- Remove issues only if they are completely superseded

---

## Patch Fix Workflow

Use for minor corrections that don't change the fundamental requirements (e.g., 1.0 → 1.1).

### When to Use Patch Fix

- Fixing typos or grammatical errors
- Clarifying ambiguous wording
- Adding minor edge cases discovered during implementation
- Correcting inaccurate descriptions
- Improving formatting or organization

### 1. Read Existing Requirements
- Read the current `.docs/requirements/[feature].md` file
- Note the current version number
- Identify the specific issues to fix

### 2. Make Targeted Edits
- Edit only what needs to change
- Preserve existing structure and numbering
- Do not add new requirements (use New Version workflow instead)
- Do not remove requirements (use New Version workflow instead)

### 3. Allowed Patch Changes

| Allowed                              | Not Allowed (Use New Version)         |
|--------------------------------------|---------------------------------------|
| Fix typos                            | Add new requirements                  |
| Clarify wording                      | Remove existing requirements          |
| Add minor edge case                  | Change core behavior descriptions     |
| Fix incorrect descriptions           | Restructure document                  |
| Improve formatting                   | Add significant new functionality     |

### 4. Increment Minor Version
- Increment minor version (e.g., 1.0 → 1.1, 2.3 → 2.4)
- Never increment major version for patches

### 5. Document Version History
Add a concise version history entry:
```markdown
- **Version 1.1** - YYYY-MM-DD: Clarified requirement 3 wording. Added edge case for empty input.
```

### 6. Validate Changes
Before finalizing:
- Ensure no requirements were accidentally added or removed
- Verify version was incremented correctly
- Confirm version history accurately describes the patch

---

## Tips for Good Requirements

1. **Be specific and testable**: "The system must display an error within 500ms" vs "The system should show
   errors quickly"

2. **Use consistent language**:
   - "must" for mandatory requirements
   - "should" for recommended behavior
   - "may" for optional features

3. **Group logically**: By user flow, by component, or by priority

4. **Avoid implementation details**: No class names, method names, or API endpoints in requirements section

5. **Consider all user states**: First-time user, returning user, offline user, admin user
