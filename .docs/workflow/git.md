# Git Workflow

Git workflow and conventions for Jellyfin.

## Branching

Development follows [GitHub Flow](https://docs.github.com/en/get-started/using-github/github-flow):

1. Create a branch off of `master`
2. Make your changes
3. Make a PR
4. Get an approval
5. Merge the PR
   - `Rebase and merge` to maintain the branch's commits
   - `Squash and merge` to collapse them

## Branch Naming

Use descriptive branch names:

```
feature/<description>
fix/<description>
refactor/<description>
docs/<description>
```

Examples:
- `feature/add-user-profile`
- `fix/login-crash`
- `refactor/navigation-structure`

## Commit Messages

Write clear, concise commit messages:

- Use imperative mood ("Add feature" not "Added feature")
- First line: summary (50 chars or less)
- Optional: blank line followed by detailed description

```
Add user profile screen

- Implement VICE components for profile
- Add navigation integration
- Include screenshot tests
```

## Pull Requests

- Keep PRs focused on a single change
- Include a description of what changed and why
- Link related issues if applicable
- Ensure all checks pass before requesting review

## Git LFS

Binary files (screenshots) are stored in Git LFS:

```bash
git lfs install --local
git lfs pull
```

## Tags

Version is calculated from tags. Ensure tags are fetched:

```bash
git fetch --tags
```
