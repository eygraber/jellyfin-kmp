# GitHub CLI Commands Reference

Quick reference for common GitHub CLI operations used in project management.

## Issue Operations

```bash
# List issues
gh issue list                              # Open issues
gh issue list --state all                  # All issues
gh issue list --label "epic"               # Filter by label
gh issue list --assignee @me               # Assigned to me

# View issue
gh issue view 123                          # View details
gh issue view 123 --comments               # Include comments

# Create issue
gh issue create --title "Title" --body "Description" --label "type:task"

# Update issue
gh issue edit 123 --title "New title"
gh issue edit 123 --body "New description"
gh issue edit 123 --add-label "priority:p1"
gh issue edit 123 --remove-label "priority:p2"
gh issue edit 123 --add-assignee username

# Close/reopen
gh issue close 123
gh issue close 123 --reason "not planned"
gh issue reopen 123

# Comment
gh issue comment 123 --body "Comment text"

# Get node ID (for GraphQL)
gh api repos/{owner}/{repo}/issues/123 --jq '.node_id'
```

## Subissues (GraphQL Required)

```bash
# List subissues
gh api repos/{owner}/{repo}/issues/123/sub_issues

# Add subissue
gh api graphql -f query='
mutation {
  addSubIssue(input: {issueId: "EPIC_NODE_ID", subIssueId: "TASK_NODE_ID"}) {
    issue { number }
    subIssue { number }
  }
}'

# Remove subissue
gh api graphql -f query='
mutation {
  removeSubIssue(input: {issueId: "EPIC_NODE_ID", subIssueId: "TASK_NODE_ID"}) {
    issue { number }
    subIssue { number }
  }
}'

# Batch add subissues
EPIC_ID="I_xxxxx"
for issue in 8 9 10; do
  node_id=$(gh api repos/{owner}/{repo}/issues/$issue --jq '.node_id')
  gh api graphql -f query="mutation { addSubIssue(input: {issueId: \"$EPIC_ID\", subIssueId: \"$node_id\"}) { subIssue { number } } }"
done
```

## Blocking Relationships (GraphQL Required)

```bash
# Add blocking relationship (issue 123 is blocked by issue 45)
BLOCKED_ID=$(gh api repos/{owner}/{repo}/issues/123 --jq '.node_id')
BLOCKING_ID=$(gh api repos/{owner}/{repo}/issues/45 --jq '.node_id')
gh api graphql -f query="mutation { addBlockedBy(input: {issueId: \"$BLOCKED_ID\", blockingIssueId: \"$BLOCKING_ID\"}) { issue { number } } }"

# Remove blocking relationship
gh api graphql -f query="mutation { removeBlockedBy(input: {issueId: \"$BLOCKED_ID\", blockingIssueId: \"$BLOCKING_ID\"}) { issue { number } } }"

# Query blocking relationships for an issue
gh api graphql -f query='
{
  repository(owner: "{owner}", name: "{repo}") {
    issue(number: 123) {
      blockedBy(first: 10) { nodes { number title } }
      blocking(first: 10) { nodes { number title } }
    }
  }
}'

# Find all blocked issues in repo
gh api graphql -f query='
{
  repository(owner: "{owner}", name: "{repo}") {
    issues(first: 50, states: OPEN) {
      nodes {
        number
        blockedBy(first: 5) { nodes { number } }
      }
    }
  }
}' | jq '[.data.repository.issues.nodes[] | select(.blockedBy.nodes | length > 0)]'
```

## Projects

```bash
# List projects
gh project list --owner {owner}

# View project
gh project view {number} --owner {owner}
gh project view {number} --owner {owner} --format json

# List items
gh project item-list {number} --owner {owner}

# Add item to project
gh project item-add {number} --owner {owner} --url {issue_url}

# Get project fields
gh project field-list {number} --owner {owner}
```

## Labels

```bash
# List labels
gh label list

# Create label
gh label create "name" --description "Description" --color "HEXCODE"

# Colors for standard labels:
# type:feature  = 0E8A16 (green)
# type:task     = 1D76DB (blue)
# type:chore    = FBCA04 (yellow)
# type:bug      = d73a4a (red)
# priority:p0   = B60205 (dark red)
# priority:p1   = D93F0B (orange)
# priority:p2   = FBCA04 (yellow)
# priority:p3   = C5DEF5 (light blue)
# epic          = 8B5CF6 (purple)
```

## Pull Requests

```bash
# Create PR
gh pr create --title "Title" --body "Description"
gh pr create --title "Title" --body "Closes #123"

# List PRs
gh pr list
gh pr list --state all

# View PR
gh pr view 123

# Merge PR
gh pr merge 123
gh pr merge 123 --squash
gh pr merge 123 --rebase
```

## Repository Info

```bash
# Get repo info
gh repo view
gh repo view --json nameWithOwner -q '.nameWithOwner'
```

## Tips

1. **Node IDs**: Required for GraphQL mutations (subissues). Get with:
   ```bash
   gh api repos/{owner}/{repo}/issues/{number} --jq '.node_id'
   ```

2. **JSON output**: Add `--json` flag for machine-readable output:
   ```bash
   gh issue list --json number,title,labels
   ```

3. **JQ filtering**: Use `--jq` to extract specific fields:
   ```bash
   gh issue list --json number --jq '.[].number'
   ```

4. **Batch operations**: Loop through issues:
   ```bash
   for issue in $(gh issue list --json number --jq '.[].number'); do
     gh issue edit $issue --add-label "type:task"
   done
   ```
