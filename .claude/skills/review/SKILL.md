---
name: review
description: Comprehensive code review covering UI, architecture, data layer, quality, and style. Use when reviewing PRs, code changes, or when asked to review code.
argument-hint: "[focus] [files] - e.g., 'ui', 'data src/...', or blank for auto"
context: fork
agent: Explore
allowed-tools: Read, Glob, Grep, Bash(git diff*, git log*, git show*, git status)
---

# Code Review

Code review with automatic scope detection or explicit focus areas.

## Usage

```
/review                           # Auto-detect scope from staged changes
/review src/main/.../MyFile.kt    # Review specific file (auto-detect focus)
/review ui                        # Focus on UI checklist only
/review data domain               # Focus on data and domain checklists
/review architecture MyModel.kt   # Architecture review of specific file
```

## Focus Areas

When a focus is specified, apply only those checklists:

| Focus          | Description                             |
|----------------|-----------------------------------------|
| `ui`           | Compose, accessibility, UX              |
| `domain`       | Use cases, business logic               |
| `data`         | Repositories, SQLDelight, Ktorfit       |
| `architecture` | MVI/VICE patterns                       |
| `quality`      | Testing, performance, security          |
| `style`        | Kotlin conventions, formatting          |
| `docs`         | KDoc, comments                          |

Multiple focus areas can be combined: `/review ui architecture`

## Process

1. **Parse arguments** - identify focus areas and files/commits
2. **Identify changes** using git diff or specified files
3. **Determine scope** - use explicit focus or auto-detect from file patterns
4. **Apply checklists** for each relevant area
5. **Synthesize findings** into prioritized, actionable feedback
6. **Acknowledge good work** alongside areas for improvement

## Auto-Detection (when no focus specified)

| File Pattern                               | Review Areas          |
|--------------------------------------------|-----------------------|
| `**/screens/**`, `**/*View.kt`, `**/ui/**` | UI, Architecture      |
| `**/*Model.kt`, `**/*Compositor.kt`        | Architecture, Quality |
| `**/data/**`, `**/repository/**`, `*.sq`   | Data, Quality         |
| `**/domain/**`, `**/usecase/**`            | Domain, Architecture  |
| `*.kt` (any)                               | Style, Quality        |

## Output Format

```markdown
## Summary
[1-2 sentence overview of changes and overall impression]

## Review by Area

### [Area Name]
[Key findings from that area's checklist]

## Priority Issues

### 🔴 Blocking (must fix before merge)
- Issue with file:line reference

### 🟡 Important (should address)
- Issue with file:line reference

### 🔵 Suggestions (nice to have)
- Suggestion with context

## Positive Feedback
- Specific things done well

## Recommendations
1. Top priority action
2. Important follow-up
```

## Review Philosophy

- **Constructive**: Focus on helping, not criticizing
- **Specific**: Provide file:line references and concrete suggestions
- **Educational**: Explain reasoning and link to documentation
- **Balanced**: Acknowledge good work alongside issues
- **Prioritized**: Distinguish blocking issues from suggestions

## Documentation

- [.docs/architecture/](/.docs/architecture) - Architecture patterns
- [.docs/compose/](/.docs/compose) - Compose guidelines
- [.docs/testing/](/.docs/testing) - Testing strategies
- [.docs/data/](/.docs/data) - Data layer patterns
