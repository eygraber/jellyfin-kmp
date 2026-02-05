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
