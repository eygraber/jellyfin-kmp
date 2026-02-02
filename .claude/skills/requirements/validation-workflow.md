# Validation Workflow

Use this workflow when asked to validate code against requirements.

## Step-by-Step Process

### 1. Parse Requirements
Read the `.md` file and understand all requirements and edge cases.

### 2. Analyze Git History for Issues
- Examine `git log` for files in "Top-Level Modules"
- Scan commit messages for issue keys
- Only include commits that relate to the requirements
- Create list of unique issue keys with links if available

```bash
# Search git log for issue references
git log --all --oneline -- path/to/module
```

### 3. Analyze Codebase
Review relevant modules based on the feature scope.

### 4. Compare and Identify Gaps
- Verify each requirement has corresponding implementation
- Check edge cases have explicit handling
- Note missing or incomplete implementations

### 5. Generate Validation Report
Use the format below.

---

## Validation Report Format

```markdown
# Validation Report: [Feature Name]

## Overall Status
[Fully Compliant | Partially Compliant | Non-Compliant]

[Summary paragraph]

## Issue Traceability
**Found in git history:**
- Issue-123
- Issue-124

**Listed in requirements:**
- Issue-123

**Missing from requirements:** Issue-124

**Extra in requirements:** None

## Requirements Analysis

### Met Requirements
1. **Requirement 1**: Implementation details
2. **Requirement 2**: Implementation details

### Partially Met Requirements
3. **Requirement 3**: What's implemented and what's missing

### Unmet Requirements
4. **Requirement 4**: What needs to be added

## Edge Cases Analysis

### Handled Edge Cases
1. **Edge Case 1**: How it's handled

### Unhandled Edge Cases
2. **Edge Case 2**: Missing handling, suggested implementation

## Recommended Actions

### High Priority
1. **Fix X**: [Specific code change needed]
   ```kotlin
   // Suggested implementation
   ```

2. **Add Test for Y**: [Test description]
   ```kotlin
   @Test
   fun `test name`() {
     // Skeleton code
   }
   ```

### Medium Priority
3. **Improve Z**: [Suggested enhancement]

## Summary
[Brief summary of validation findings and next steps]
```

---

## Validation Checklist

When validating, check each of these:

- [ ] Every numbered requirement has corresponding code
- [ ] Every edge case has explicit handling (try/catch, if/else, null checks)
- [ ] All issues from git history are listed in requirements
- [ ] No orphaned requirements (requirements without code)
- [ ] No undocumented features (code without requirements)
