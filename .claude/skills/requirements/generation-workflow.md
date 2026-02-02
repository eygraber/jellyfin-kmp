# Generation Workflow

Use this workflow when asked to generate or update requirements from code.

## Step-by-Step Process

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
