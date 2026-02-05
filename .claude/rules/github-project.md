# GitHub Project Configuration

Global GitHub settings for the super-do repository.

## Repository

- **Owner:** eygraber
- **Repo:** super-do
- **Full name:** eygraber/super-do

## GitHub Project Board

- **Project Number:** 5
- **Project ID:** PVT_kwHOABDKXc4BOFIP
- **URL:** https://github.com/users/eygraber/projects/5

## Status Columns

| Status      | Option ID | Description                  |
|-------------|-----------|------------------------------|
| Backlog     | 00d992b2  | Work not yet prioritized     |
| Ready       | f81fe634  | Ready to be picked up        |
| In Progress | 11e3b05f  | Currently being worked on    |
| In Review   | e44e270f  | PR created, awaiting review  |
| Done        | 740920d0  | Completed                    |

**Status Field ID:** PVTSSF_lAHOABDKXc4BOFIPzg84wPs

## Labels

| Label          | Color   | Purpose                    |
|----------------|---------|----------------------------|
| epic           | #8B5CF6 | Epic issue                 |
| highlight      | #F59E0B | Highlight feature          |
| type:feature   | #0E8A16 | New feature                |
| type:task      | #1D76DB | Implementation task        |
| type:chore     | #FBCA04 | Maintenance/tooling        |
| bug            | #d73a4a | Bug fix                    |
| priority:p0    | #B60205 | Critical                   |
| priority:p1    | #D93F0B | High                       |
| priority:p2    | #FBCA04 | Medium                     |
| priority:p3    | #C5DEF5 | Low                        |

## Quick Commands

Commands using project-specific values. For general gh CLI reference, see [github-commands.md](./rules/github-commands.md).

```bash
# List project items
gh project item-list 5 --owner eygraber

# Add issue to project
gh project item-add 5 --owner eygraber --url https://github.com/eygraber/super-do/issues/123

# Get issue node ID
gh api repos/eygraber/super-do/issues/123 --jq '.node_id'

# List subissues of an epic
gh api repos/eygraber/super-do/issues/1/sub_issues
```

## Adding Subissues to Epics

```bash
# Example: Add issue #44 as subissue of Phase 1 epic
TASK_ID=$(gh api repos/eygraber/super-do/issues/44 --jq '.node_id')
gh api graphql -f query="mutation { addSubIssue(input: {issueId: \"I_kwDORGtN0c7nk2Oo\", subIssueId: \"$TASK_ID\"}) { subIssue { number } } }"
```

## Blocked Issues

Use GitHub's native blocking relationships to track dependencies:

```bash
# Mark issue #123 as blocked by issue #45
BLOCKED_ID=$(gh api repos/eygraber/super-do/issues/123 --jq '.node_id')
BLOCKING_ID=$(gh api repos/eygraber/super-do/issues/45 --jq '.node_id')
gh api graphql -f query="mutation { addBlockedBy(input: {issueId: \"$BLOCKED_ID\", blockingIssueId: \"$BLOCKING_ID\"}) { issue { number } } }"

# Remove blocking relationship
gh api graphql -f query="mutation { removeBlockedBy(input: {issueId: \"$BLOCKED_ID\", blockingIssueId: \"$BLOCKING_ID\"}) { issue { number } } }"

# Query blocking relationships for an issue
gh api graphql -f query='
{
  repository(owner: "eygraber", name: "super-do") {
    issue(number: 123) {
      blockedBy(first: 10) { nodes { number title } }
      blocking(first: 10) { nodes { number title } }
    }
  }
}'
```

The `/start-work` skill will skip issues that have blocking relationships when finding work.
