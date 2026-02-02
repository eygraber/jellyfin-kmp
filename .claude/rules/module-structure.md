apps/android: contains Android Application class, Activity, and main app DI graphs
apps/ios: contains iOS-specific entry points
apps/desktop: contains JVM desktop entry point
apps/web: contains WasmJs web entry point
apps/shared: shared multiplatform app logic
screens/<feature>: feature screen modules with VICE components and navigation
data/<feature>: split into public (interfaces) and impl (implementations) submodules
domain/<feature>: split into public (interfaces) and impl (implementations) submodules
services/<service>: service modules for integrating external libraries
ui/<component-type>: reusable UI components and design system elements
common: shared resources (strings, drawables, multiplatform)
di: dependency injection scopes and contexts
nav: navigation graph builder and routing
Use public/impl structure for shared data, domain, and services modules
Dependencies must be in alphabetical order: projects first (projects.*), then libs (libs.*)

### Documentation Reference
For complete patterns: .docs/ProjectLayout.md and .docs/architecture/
