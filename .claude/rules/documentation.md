---
paths:
  - "**/*.md"
---

# Documentation Conventions

## Automated Documentation Generation

**CRITICAL: Do not proactively create summary or index files.**

When working with project documentation (`.docs/`, `.agents/`, `AGENTS.md`, `.gemini/styleguide.md`, `README.md`, etc.),
follow these principles:

### What NOT to Create

❌ **Do NOT** create these files unless explicitly requested:
- `README.md` files in documentation directories
- `index.md` or `INDEX.md` summary files
- `SUMMARY.md` or table-of-contents files
- Any other "meta-documentation" that summarizes existing docs

### Rationale

- Documentation should be **discovered through IDE navigation** and directory structure
- Index files become **stale quickly** and add maintenance burden
- Developers prefer **direct file access** over navigating through summaries
- AI tools can already **search and understand** documentation without indexes

### When Summaries ARE Allowed

✅ **DO** create summary/index files when:
- Developer explicitly requests: "Create a README summarizing..."
- Project convention already established (e.g., existing READMEs in all doc folders)
- Building public-facing documentation (external docs, published guides)

### What TO Do Instead

When updating documentation:
- **Create/update specific topic files** directly (e.g., `architecture.md`, `testing-strategy.md`)
- **Use clear, descriptive filenames** that convey purpose
- **Organize with directory structure** rather than index files
- **Cross-reference related docs** using markdown links within content

### Example Workflow

**BAD** (Unsolicited summary):
1. Create `feature-x.md` with details
2. Create `README.md` listing all features ← ❌ Don't do this

**GOOD** (Direct documentation):
1. Create `feature-x.md` with details
2. Update existing relevant docs with cross-references
3. Stop there unless asked for more ← ✅ Do this
