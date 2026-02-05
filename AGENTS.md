# Jellyfin - AI Agent Guide

## Quick Start

Load project context from [.claude/CLAUDE.md](/.claude/CLAUDE.md) for project overview and key commands.

## AI Resources

| Resource | Location                                          | Description                   |
|----------|---------------------------------------------------|-------------------------------|
| Memory   | [.claude/CLAUDE.md](/.claude/CLAUDE.md)           | Project overview and commands |
| Rules    | [.claude/rules](/.claude/rules)                   | Context-specific coding rules |
| Agents   | [.claude/agents](/.claude/agents)                 | Specialized agent definitions |
| Skills   | [.claude/skills](/.claude/skills)                 | Reusable skill definitions    |
| Examples | [.claude/rules/examples](/.claude/rules/examples) | Reference implementations     |

## Rules

Rules in `.claude/rules/` use YAML frontmatter with `paths` for conditional loading:

```yaml
---
paths:
  - "**/*.kt"
---
```

Rules without `paths` frontmatter apply globally.

## Firebender Compatibility

[firebender.json](/firebender.json) mirrors the rules configuration for Firebender compatibility.
The `ignore` array contains glob patterns for files agents should skip.
