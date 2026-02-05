app module: contains Application class, Activity, and main app DI graphs
screens/<feature>: feature screen modules with VICE components and navigation
data/<feature>: split into public (interfaces) and impl (implementations) submodules
domain/<feature>: split into public (interfaces) and impl (implementations) submodules
services/<service>: service modules for integrating external libraries
utils/<utility>: utility modules for helpers (phone numbers, locales, etc.)
ui/<component-type>: reusable UI components and design system elements
common: shared resources (strings, drawables)
di: dependency injection scopes and contexts
entity: shared data entities
nav: navigation graph builder and routing
Use public/impl structure for shared data, domain, and services modules
Dependencies must be in alphabetical order: projects first (projects.*), then libs (libs.*)

### Documentation Reference
For complete patterns: .docs/ProjectLayout.md and .docs/architecture/
