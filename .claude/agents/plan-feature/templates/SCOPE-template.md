# Phase [N]: [Phase Name] - Scope

**GitHub Issue:** [#XXX](https://github.com/OWNER/REPO/issues/XXX)
**Branch:** `feature/[description]`
**Last Updated:** YYYY-MM-DD

## Phase Overview

[2-3 sentence description of what this phase accomplishes and why it's a distinct phase]

## Phase Type

[New Feature | Feature Update | Bug Fix | Refactor | Infrastructure]

## Affected Layers

- [ ] **UI Layer**: [Brief description of changes]
  - Screens: [List screen modules]
  - Components: [List UI components]
  - Navigation: [Navigation changes]

- [ ] **Domain Layer**: [Brief description of changes]
  - Use Cases: [List use cases]
  - Models: [List domain models]

- [ ] **Data Layer**: [Brief description of changes]
  - Repositories: [List repositories]
  - Data Sources: [API/Database changes]
  - Entities: [List entities]

## Architecture Decisions

### New Modules

[List any new modules that will be created with their purpose]

- `screens/[feature-name]`: [Purpose]
- `domain/[feature-name]/public`: [Purpose]
- `domain/[feature-name]/impl`: [Purpose]
- `data/[feature-name]/public`: [Purpose]
- `data/[feature-name]/impl`: [Purpose]

### Modified Modules

[List existing modules that will be modified and why]

- `[module-path]`: [What changes and why]

### Dependencies

[List new dependencies or dependency changes]

- **New Dependencies:**
  - `[library-name]`: [Purpose]

- **Dependency Changes:**
  - `[module-a]` now depends on `[module-b]`: [Reason]

### Database Changes

[If applicable, describe SQLDelight schema changes and migrations needed in detail]

#### New Tables
[For each new table, specify:]
- **Table Name**: `[table_name]` (use backticks if reserved keyword like `` `Case` ``)
  - `[column_name]` [TYPE] [NULL/NOT NULL] [DEFAULT value]: [Purpose]
  - `[column_name]` [TYPE] [NULL/NOT NULL]: [Purpose]
  - **Indexes**: [Describe any indexes for performance; ensure FK references are covered by an index]
  - **Foreign Keys**: [Describe relationships; include ON DELETE clause where warranted]

#### Modified Tables
[For each modified table:]
- **Table Name**: `[table_name]`
  - **Added Columns**:
    - `[column_name]` [TYPE] [NULL/NOT NULL]: [Purpose]
  - **Modified Columns**:
    - `[column_name]`: [What changed and why]
  - **Removed Columns**:
    - `[column_name]`: [Why removed, migration strategy]

#### New/Modified Queries
[List SQLDelight queries that will be added or changed:]
- `[QueryName]`: [Purpose and SQL summary]

#### Migrations
- **Migration File**: `[version].sqm`
  - [Detailed migration steps]
  - [Data transformation strategy if needed]
  - [Rollback considerations]

### API Changes

[If applicable, describe API endpoint changes or additions in detail]

#### New Endpoints
[For each new endpoint:]
- **`[HTTP METHOD] /api/v1/[path]`**
  - **Purpose**: [What this endpoint does]
  - **Authentication**: [Required auth type]
  - **Request Body** (if applicable):
    ```json
    {
      "field": "type",
      "nested": {
        "field": "type"
      }
    }
    ```
  - **Response Body** (success):
    ```json
    {
      "field": "type",
      "data": []
    }
    ```
  - **Error Responses**:
    - `400`: [When this occurs]
    - `401`: [When this occurs]
    - `404`: [When this occurs]
    - `500`: [When this occurs]

#### Modified Endpoints
[For each modified endpoint:]
- **`[HTTP METHOD] /api/v1/[path]`**
  - **Changes**: [Describe what changed]
  - **Backward Compatibility**: [Is this breaking? Migration path?]
  - **Updated Request/Response**: [Show only changed fields]

### Data Models

[Describe new or modified data models/entities]

#### New Models
[For each new model:]
- **`[ModelName]`**
  - `[property]`: `[Type]` - [Purpose]
  - `[property]`: `[Type?]` - [Purpose, nullable because...]
  - **Relationships**: [How it relates to other models]
  - **Validation**: [Any validation rules]
  - **Serialization**: [kotlinx.serialization annotations needed?]

#### Modified Models
- **`[ModelName]`**
  - **Added Properties**: [List new properties]
  - **Modified Properties**: [List changed properties]
  - **Removed Properties**: [List removed properties and migration strategy]

## Technical Approach

[Detailed explanation of the technical implementation strategy, including:
- How different layers interact
- Data flow through the system
- State management approach
- Error handling strategy
- Performance considerations]

## Out of Scope (This Phase)

[Explicitly list what is NOT included in THIS PHASE, even if part of overall feature]

- [Item 1]
- [Item 2]

## Dependencies on Other Phases

**Requires from previous phases:**
- [What must be complete before this phase can start]

**Provides for future phases:**
- [What this phase delivers that future phases will use]

## Open Questions

[List any remaining unknowns or decisions that need to be made for this phase]

- [ ] [Question 1]
- [ ] [Question 2]

## Assumptions

[List any assumptions being made for this phase]

- [Assumption 1]
- [Assumption 2]
