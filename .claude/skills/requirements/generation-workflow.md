# Generation Workflow

Use this workflow when asked to generate or update requirements from code.

## Determine Workflow Type

Before starting, determine which scenario applies:

| Scenario                | When to Use                                                           | Version Change |
|-------------------------|-----------------------------------------------------------------------|----------------|
| **New Requirement**     | No existing `.docs/requirements/[feature].md` file exists             | Start at 1.0   |
| **New Version**         | Major feature changes, new capabilities, or significant restructuring | x.0 → (x+1).0  |
| **Patch Fix**           | Corrections, clarifications, minor additions, or bug documentation    | x.y → x.(y+1)  |

### How to Identify the Scenario

1. **Check for existing file**: `ls .docs/requirements/[feature].md`
2. **If file exists**, analyze the scope of changes:
   - **New Version**: Adding new user flows, major behavior changes, new integrations, or architectural changes
   - **Patch Fix**: Fixing documentation errors, clarifying ambiguous requirements, adding missed edge cases,
     documenting bug fixes

---

## Workflow A: New Requirement

Use when creating requirements for a feature that has no existing documentation.

### A.1. Identify Feature Scope
- Trace code from the starting point provided
- List all relevant modules/files
- Identify boundaries of the feature

### A.2. Analyze Git History
- Examine `git log` for identified modules
- Extract issue keys from commit messages
- Group commits by issue key
- Create entries under "Related Issues"

```bash
# Search git log for issue keys
git log --all --oneline -- path/to/module
```

### A.3. Synthesize Overview
- Study UI elements and user interactions
- Identify the problem being solved
- Write concise, user-centric overview (1-2 paragraphs)

### A.4. Extract Requirements
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

### A.5. Identify Edge Cases
- Find error handling (`catch`, `else`, null checks)
- Translate into product-focused edge case descriptions
- Group logically

**From the same code, extracted edge cases:**
1. **Empty Phone Number Field**: If the user attempts to proceed without entering a phone number, the system
   must display an error message indicating the field is required.
2. **Invalid Phone Number Format**: If the user enters a phone number in an incorrect format, the system must
   display an error message explaining the correct format.

### A.6. Generate Markdown File
- Create `.docs/requirements/[feature].md`
- Follow template exactly (see [template.md](template.md))
- Set **Version: 1.0**
- Add initial version history entry: `- **Version 1.0** - YYYY-MM-DD: Initial requirements documentation.`

---

## Workflow B: New Version

Use when documenting major changes to an existing feature (e.g., v1.x → v2.0).

### B.1. Read Existing Requirements
- Load current `.docs/requirements/[feature].md`
- Note current version number and structure
- Understand existing requirements and edge cases

### B.2. Identify Changed Scope
- Compare current code against documented requirements
- Focus git history on changes **since the last version date**

```bash
# Get changes since last version date
git log --after="YYYY-MM-DD" --oneline -- path/to/module
```

### B.3. Categorize Changes
For each code change, determine:
- **New requirements**: Entirely new functionality to add
- **Modified requirements**: Existing requirements that need updating
- **Removed requirements**: Functionality that no longer exists
- **New edge cases**: Error conditions or boundaries added

### B.4. Update Document Structure
- Increment major version (e.g., 1.2 → 2.0)
- Update Overview if the feature's purpose has evolved
- Add/modify/remove requirements while preserving requirement numbering where possible
- Add new edge cases; update or remove obsolete ones
- Add version history entry describing the major changes

**Version history example:**
```markdown
- **Version 2.0** - YYYY-MM-DD: Added offline support, restructured user flow for multi-account handling.
```

### B.5. Reconcile Related Issues
- Add new issue references from recent git history
- Keep existing issue references that remain relevant
- Remove or archive references to superseded work

---

## Workflow C: Patch Fix

Use when making minor corrections to existing requirements (e.g., v1.1 → v1.2).

### C.1. Read Existing Requirements
- Load current `.docs/requirements/[feature].md`
- Note current version number

### C.2. Identify Specific Changes
Patch fixes typically address:
- **Documentation errors**: Typos, incorrect descriptions, outdated information
- **Missing details**: Edge cases that were overlooked, clarifications needed
- **Bug documentation**: Recording behavior changes from bug fixes
- **Consistency fixes**: Aligning with template or formatting standards

### C.3. Make Targeted Edits
- Edit only the specific sections that need changes
- Preserve existing requirement and edge case numbering
- Do NOT restructure or rewrite sections unnecessarily

### C.4. Update Metadata
- Increment minor version (e.g., 1.1 → 1.2)
- Add concise version history entry

**Version history example:**
```markdown
- **Version 1.2** - YYYY-MM-DD: Clarified error message behavior for network timeout edge case.
```

### C.5. Update Related Issues (if applicable)
- Add issue references only if the patch relates to tracked issues
- Don't modify unrelated issue entries

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
