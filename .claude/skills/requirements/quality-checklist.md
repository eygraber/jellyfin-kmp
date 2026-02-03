# Quality Checklist

Before finalizing any requirements documentation, verify:

## Structure
- [ ] Follows exact template structure from [template.md](template.md)
- [ ] Maximum 120 character line length
- [ ] All sections present and in correct order

## Requirements Section
- [ ] All requirements are testable and specific
- [ ] No code implementation details (no class names, methods, APIs)
- [ ] Requirements use "must/should/may" consistently
- [ ] Requirements are grouped logically

## Edge Cases Section
- [ ] Edge cases describe expected behavior, not implementation
- [ ] Error messages are documented where applicable
- [ ] Covers: empty states, errors, boundaries, concurrent operations

## Issue Tracking
- [ ] Issue references include links if using an issue tracker
- [ ] Only relevant issues included (not every commit)
- [ ] Brief description accompanies each issue reference

## Metadata
- [ ] Status is accurate (Not Started | In Progress | Complete | Blocked | On Hold)
- [ ] Version number is appropriate
- [ ] Version history is accurate and up to date

## Version Type Validation

### For New Requirements (v1.0)
- [ ] Version is set to **1.0**
- [ ] Version history has single entry: "Initial requirements documentation"
- [ ] All related issues from git history are included

### For New Versions (x.0 → (x+1).0)
- [ ] Major version incremented (not minor)
- [ ] Version history describes the major changes (new features, restructuring)
- [ ] Overview updated if feature purpose evolved
- [ ] New issue references added from recent git history
- [ ] Removed requirements are actually deleted (not commented out)

### For Patch Fixes (x.y → x.(y+1))
- [ ] Minor version incremented (not major)
- [ ] Version history describes the specific fix/clarification
- [ ] Changes are minimal and targeted
- [ ] Existing requirement/edge case numbering preserved
- [ ] No unnecessary restructuring or rewrites
