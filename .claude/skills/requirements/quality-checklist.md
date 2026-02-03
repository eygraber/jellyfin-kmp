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

## Workflow-Specific Checks

### New Requirement
- [ ] Version is set to 1.0
- [ ] Version history has initial entry with "Initial draft" or similar
- [ ] All relevant issues from git history are included

### New Version (Major Update)
- [ ] Major version incremented (e.g., 1.x → 2.0)
- [ ] Version history explains what was added/removed/changed
- [ ] Removed requirements noted in version history
- [ ] New issues since last version are added to Related Issues

### Patch Fix (Minor Update)
- [ ] Minor version incremented (e.g., 1.0 → 1.1)
- [ ] No requirements were added or removed (only clarified/fixed)
- [ ] Version history describes the specific fix
- [ ] Changes are limited to corrections, not new functionality
