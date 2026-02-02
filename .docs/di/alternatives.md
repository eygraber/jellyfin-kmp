# Why Metro?

## Metro

[Metro](https://github.com/ZacSweers/metro) is the DI framework used by this project.

**Pros**:
- Combines best of Dagger and kotlin-inject
- Uses Kotlin compiler plugins (faster than KSP)
- Full compile-time safety
- Native Kotlin support
- Simpler API than Dagger
- Anvil-like code generation built-in

## Why Not Dagger/Hilt?

**Cons**:
- Not easy to learn or implement
- Advanced features leak complexity into codebases
- KSP2 support limited
- Java stubs generation causes build instabilities
- Hilt is tightly bound to Android framework

## Why Not Koin?

**Cons**:
- No compile-time safety by default (runtime validation)
- Escape hatches bypass safety even with Koin Annotations
- Runtime performance scales with codebase size
- Can lead to user-perceivable performance issues

## Comparison

| Feature             | Metro   | Dagger | Koin    |
|---------------------|---------|--------|---------|
| Kotlin Native       | Yes     | No     | Yes     |
| Compiler Plugin     | Yes     | No     | No      |
| Compile-time Safety | Yes     | Yes    | Partial |
| API Complexity      | Low     | High   | Low     |
| Runtime Performance | Fast    | Fast   | Slower  |
| Learning Curve      | Low     | High   | Low     |
| Build Performance   | Fast    | Slow   | Fast    |

## Conclusion

Metro provides:
- Native Kotlin support
- Full compile-time safety
- Simple, small API
- Fast builds via compiler plugin
- Anvil-like code generation simplicity
