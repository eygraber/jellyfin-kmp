---
name: domain
description: Work with domain layer - create behavior models, validators, formatters, or understand when to use domain vs screen layer.
argument-hint: "[task] - e.g., 'create EmailValidator', 'explain domain vs screen', 'add phone formatter'"
context: fork
allowed-tools: Read, Edit, Write, Bash(./format, ./gradlew *, grep, cat, mkdir), Glob, Grep
---

# Domain Skill

Work with domain layer components - create behavior models, validators, formatters, or understand when to extract logic to the domain layer.

## Common Tasks

```
/domain create EmailValidator              # Create validation behavior
/domain add PhoneNumberFormatter           # Create formatting utility
/domain explain when to use domain layer   # Architecture guidance
/domain move validation from screen        # Extract shared logic
/domain test PasswordStrengthChecker       # Write behavior tests
```

## When to Create Domain Layer

**Create domain components when:**
- Business logic is needed by multiple screens
- Complex validation or transformation logic exists
- Application-level business rules need enforcement

**Don't create domain for:**
- Logic used by only one screen (keep in screen's Model)
- Simple data transformations
- UI-specific logic

## Two Types of Domain Components

| Type                | Purpose                                | Pattern                                                     |
|---------------------|----------------------------------------|-------------------------------------------------------------|
| **Behavior Model**  | Validation, formatting, transformation | `@Inject class`                                             |
| **Reactive Source** | Shared state across screens            | `ViceSource` (see [vice-sources](../vice-sources/SKILL.md)) |

## Quick Reference - Behavior Model

```kotlin
@Inject
class EmailValidator {
  @Immutable
  enum class Result { Valid, Invalid, Required }

  fun validate(email: CharSequence): Result =
    when {
      email.isBlank() -> Result.Required
      !email.matches(ValidEmailRegex) -> Result.Invalid
      else -> Result.Valid
    }

  companion object {
    private val ValidEmailRegex = Regex(
      """[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+""",
    )
  }
}
```

## Module Structure

```
domain/<feature>/
├── public/    # Interfaces (if needed) and models
├── impl/      # Implementations with DI annotations
└── fake/      # Test doubles
```

**Simple behavior models** don't need interface/impl split - put them directly in public or a shared module.

**Use interface/impl pattern when:**
- Testing would be difficult without fakes
- Multiple implementations are expected
- Better encapsulation is needed

## Related Skills

- **[vice-sources](../vice-sources/SKILL.md)** - Reactive state patterns for domain layer
- **[repository](../repository/SKILL.md)** - Data layer that domain consumes
