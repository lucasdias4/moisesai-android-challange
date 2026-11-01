---
name: write-unit-tests
description: Write or review unit tests in this project using the Given / When / Then pattern in both the name and the body - one action per test, test through public entry points, real-API fixtures, no logic in tests. Use whenever adding or changing a unit test for a datasource, extension, repository, use case, mapper or ViewModel.
---

Every unit test in this project follows **Given / When / Then**, in the name and in the body. The rule is in `ARCHITECTURE.md` §9 (and §8, Tests); this skill is how to apply it. If the two disagree, `ARCHITECTURE.md` wins.

## When to use

- Adding a test for new code, or changing an existing test
- Reviewing tests for structure, naming and value

## The pattern

Every test has the pattern twice: in its **name** and in its **body**, where each part is marked with a comment, always in this order:

```kotlin
@Test
fun `GIVEN an unknown track id, WHEN getting the track, THEN the response is successful and has no results`() = runBlocking {
    // Given
    server.enqueue(jsonResponse(readFixture(Fixtures.EMPTY)))

    // When
    val response = dataSource.getTrack(trackId = 1).getOrThrow()

    // Then
    assertTrue(response.results.isEmpty())
}
```

| Part | What goes in it | What stays out |
|---|---|---|
| `// Given` | The setup: inputs, the fake or mock and what it returns, the response the server will give, the state before the action | Calls to the code under test |
| `// When` | **One** call to the code under test, usually assigned to a `val` | Setup, assertions |
| `// Then` | The assertions on the result, on the request that was sent, or on what the fake received | Further calls to the code under test |

- **One `When` per test.** That is what makes it one concept. Two actions mean two tests.
- **All three markers, every time.** If there is truly no setup, still write `// Given` and put the input there (building the input object is the setup).
- **No logic in the body:** no loops, `if` or `when` around assertions. If you need a loop, the test covers two concepts.
- A test that throws on purpose captures the exception in `When` and asserts in `Then`:

```kotlin
// When
val call = { runBlocking { remoteCall(cancelled) } }

// Then
assertThrows(CancellationException::class.java) { call() }
```

## Naming

The name is one sentence in backticks that states the same three parts. The three keywords are **uppercase**; everything else is lowercase:

```text
GIVEN <state>, WHEN <action>, THEN <outcome>
```

- **GIVEN** describes the situation the test sets up (`a real search response`, `an unreachable server`, `a track missing a required field`)
- **WHEN** names the one action (`searching tracks`, `getting the track`, `converting it to an app error`)
- **THEN** states the observable outcome (`the result is a network error`, `only the tracks remain in their original order`)

Written from the caller's point of view, not as a method name. Examples:

- `` `GIVEN the server answers 500, WHEN getting a track, THEN the result is a network error` ``
- `` `GIVEN a response without an album, WHEN reading the album, THEN it is null` ``

The name and the markers must agree: what the name calls `GIVEN` is what `// Given` sets up, and so on.

## What to test, and where

1. **Test through the public entry points.** A class with its own logic gets a test class; the test package mirrors the production package (`datasource`, `extension`).
2. **No test class for configuration or data holders:** an annotated Retrofit interface, a DTO, the `Json` configuration, Hilt wiring. They are covered through the datasource tests with real fixtures: if a JSON key, a query parameter or a Json option is wrong, those tests fail.
3. **Fake the layer below, not the class under test.** A remote datasource: the real Retrofit, OkHttp and JSON stack against `MockWebServer`. A repository: the datasource mocked with MockK (final Kotlin classes can be mocked). A use case: the repository mocked. A ViewModel: the use cases mocked, the `StateFlow` asserted. A Room datasource: an in-memory database.
4. **Pure helpers get their own tests with plain objects** and no network (`remoteCall`, `toRemoteError`, `ApiResponse.tracks()`).
5. **Fixtures are real captures** under `src/test/resources/fixtures/`, trimmed only by removing list items. Synthetic JSON is only for cases the real API will not produce (a malformed body, a missing required field).
6. **Shared helpers live in a `support` package** and exist only when two or more test classes use them. A helper with one user is private in that test class.
7. **Delete a test that adds no value:** one that restates a constant, or that still passes when the code under test is broken.

## Coroutines and flows

- `runTest` for suspend code with mocks; `runBlocking` when real threads are needed (`MockWebServer`).
- A `@Test` function must return `Unit`. With `= runTest { ... }` or `= runBlocking { ... }` make sure the last expression is `Unit` (assertions are); otherwise use a block body.
- Flows use Turbine (`flow.test { ... }`); assert each emission and that a failure is an emitted `Result.failure`, never a thrown exception.

## Checklist before reporting

- Every test has the three markers, with exactly one action under `When`
- Names follow `GIVEN ..., WHEN ..., THEN ...` (keywords uppercase) and agree with the three markers in the body; no loops or conditionals in test bodies
- Tests go through public entry points; no test class for a DTO, `Json` configuration, service interface or DI module
- Fixtures are real; helpers in `support` have two or more users
- The suite passes from clean (`./gradlew clean :<module>:testDebugUnitTest`) and the counts per class are reported
