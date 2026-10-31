---
name: add-data-endpoint
description: Wire a backend endpoint or field through the app's data layers (Api model, Service, datasource, mapper, repository, use case, tests) and prove the JSON matches the API. Use when a new or changed API response/request must reach the app, or when writing or auditing datasource/repository suspend code.
---

Take an endpoint or field to the UI through the layers in `ARCHITECTURE.md` §5 and §6. This skill is the procedure and the coroutine-safety checklist. Layers are packages inside `:app` (`domain/`, `data/`) until modules exist.

## When to use

- A new endpoint, or a new/renamed field on a request or response
- Writing or reviewing a datasource or repository `suspend` function

## When NOT to use

- UI work on data the app already has: use `create-compose-screen`

## Input

The endpoint (method + path), request and response JSON, and which screen needs it. Use the API specification you were given as the source of truth; if there is none, ask for it before writing code.

## Prerequisites

The template has no networking. Before the first endpoint, add to `gradle/libs.versions.toml` and `app/build.gradle.kts`: Retrofit, a JSON converter (Gson, Moshi or kotlinx.serialization), OkHttp, `kotlinx-coroutines-core`, and for tests MockK and `kotlinx-coroutines-test`. Add the `INTERNET` permission to `AndroidManifest.xml`. Pick the converter once and record it in `ARCHITECTURE.md` §1. Until a DI library is added, build the object graph by hand in one place.

## Steps

### 1. Read the contract

From the API specification, copy the exact JSON keys. Note which fields are optional or defaulted: a wrong key on those is often accepted silently.

### 2. Api models (`data/remote/dto/Api<Name>.kt`)

```kotlin
data class ApiStreak(
    val count: Int,
)
```

Prefix `Api`. Keep the property names or serialization annotations matching the JSON keys exactly.

### 3. Service (`data/remote/api/Service.kt`)

A Retrofit interface with a `suspend` function returning the `Api*` model.

### 4. DataSource (`data/datasource/<Domain>ApiDataSource.kt`)

```kotlin
suspend fun getExercises(lessonId: String): Result<List<ApiExercise>> =
    withContext(Dispatchers.IO) {
        runCatching { api.getExercises(lessonId) }
            .onFailure { if (it is CancellationException) throw it }
    }
```

The datasource owns `Dispatchers.IO` (`withContext` for suspend, `.flowOn` for Flow) and returns raw `Api*` models. Any `runCatching` or `catch (e: Exception)` in a `suspend` function must rethrow `CancellationException` first.

### 5. Mapper (`data/mapper/Api<Name>Mapper.kt`)

```kotlin
fun ApiStreak.toDomain(): Int = count
```

A pure extension function with no injected dependencies.

### 6. Domain and repository

- Domain model `<Name>DomainModel` and the repository interface in `domain/repository` (pure Kotlin)
- Implementation in `data/repository/<Domain>RepositoryImpl.kt`:

```kotlin
override suspend fun getExercises(lessonId: String): Result<List<ExerciseDomainModel>> =
    exerciseApiDataSource.getExercises(lessonId).map { dtos -> dtos.map { it.toDomain() } }
```

Repositories use `.map`, never `runCatching` or `mapCatching` (`mapCatching` swallows cancellation). To turn a failure into a domain error, use `fold`.

### 7. Use case (`domain/usecase/<Verb><Noun>UseCase.kt`)

One public method, verb name; `Get` never persists. `suspend operator fun invoke(...)` for one-shot calls, `operator fun invoke(...): Flow<T>` for streams.

### 8. Tests

- Use case and repository tests with MockK
- A serialization test that pins the JSON for requests, asserting the exact JSON string with the same converter configuration the app uses (the `data/remote/dto/` test folder)

### 9. Prove the contract against the API

Unit tests mock the network, so they cannot show that the keys match. Call the real endpoint once (staging or a local server) with the real Retrofit `Service`, e.g. from a JVM test gated on an environment variable so it skips when unset, and check the result matches the specification. Report this as done or blocked.

### 10. Report

Files changed per layer, any audit finding (a swallowed `CancellationException`, a `Result<T>` function that also throws), and whether step 9 was run.

## Hard constraints

- `CancellationException` is never swallowed
- Datasources catch; repositories and use cases propagate `Result<T>`; ViewModels use `.onSuccess`/`.onFailure`
- Repositories never call the Retrofit interface directly
- A failing repository method returns `Result<T>` (`Result<Unit>` for void), never `T?`, and never both returns and throws
