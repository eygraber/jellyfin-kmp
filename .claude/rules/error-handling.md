---
paths:
  - "**/*.kt"
---

Use SuperDoResult for operation success/failure states
SuperDoResult extensions: mapSuccessTo/flatMapSuccessTo (transform success), mapToUnit (discard value), flatMap (general mapping), successOrNull/errorDetailOrNull (extractors), isSuccess/isError (type checks)
Use specialized sealed classes for representing API call states
Handle errors gracefully at appropriate layers
Validate user inputs on both client-side and server-side
Never expose sensitive error details to users
Log errors with sufficient context for debugging

## doOnSuccess vs andThen

Use andThen when the callback can fail and you care about propagating that failure:
- andThen wraps the callback in runResult, propagating errors as SuperDoResult.Error
- Use for side effects that can throw (database writes, network calls, file I/O)
- Preferred in most cases for robustness

Use doOnSuccess when the callback cannot fail or you don't care about its failures:
- doOnSuccess ignores any exceptions thrown in the callback
- Use only for logging, metrics, or other non-critical side effects
- Much less common; default to andThen unless you have a specific reason

## Future Plans

We will adopt Kotlin rich errors once they are available and more stabilized
