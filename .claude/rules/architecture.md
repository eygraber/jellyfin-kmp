# Screen Architecture
Follow MVI/VICE pattern for all screen modules
Use .scripts/generate_module --feature=<FeatureName> to create new screens
Each screen must have: ViewState, Intent, Compositor, View
Each screen can optionally have an Effect if needed
Each screen can optionally have a model package with Model classes if needed
ViceNavEntryProvider is the entry point for each screen
There should be only one top-level ViceNavEntryProvider per screen module
Each ViceNavEntryProvider must have an associated ScreenGraph for DI

# Component Responsibilities
Model: Encapsulates business logic using ViceSource
Compositor: Routes intents to handlers, combines state from sources, only the most simplistic of UI and business logic
Effects: Side effects not directly tied to the UDF

Bad: Business logic in Compositor or View
Good: Business logic in Model, Compositor just routes

Bad: Launching network calls from Compositor
Good: Models handle screen specific domain logic

# Data Flow
Compositor -> ViewState -> View -> Intent -> Compositor

# Domain and Data Layers
Extract Models into domain layer modules when used by multiple modules
Use Repository pattern for data access coordination between local and remote sources
Data module structure: public/ (interfaces), impl/ (implementations), fake/ (test doubles)

# DI Scopes
AppScope -> ActivityScope -> NavScope -> ScreenScope

### Documentation Reference
For complete patterns: .docs/architecture/
