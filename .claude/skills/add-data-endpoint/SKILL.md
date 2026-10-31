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

When the source is a local database, also add Room (runtime, ktx, compiler via KSP), and Turbine for Flow tests.

## Suspend or Flow?

Decide before writing code (see `ARCHITECTURE.md` §2). One-shot request or read/write: `suspend ...: Result<T>`. Anything observed over time, typically a Room query: `fun ...: Flow<Result<T>>`. When unsure, use `suspend`. The steps below show the suspend form first and the Flow variant where it differs.

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

For a Room source instead: a `Db<Name>` entity in `data/local/entity` and a DAO in `data/local/dao` whose observed read returns `Flow<List<Db<Name>>>`, while single reads (get by id) and all writes (insert, update, delete) are `suspend`. Entities map to domain exactly like `Api*` models.

### 4. DataSource (`data/datasource/<Domain>ApiDataSource.kt`)

```kotlin
suspend fun getExercises(lessonId: String): Result<List<ApiExercise>> =
    withContext(Dispatchers.IO) {
        runCatching { api.getExercises(lessonId) }
            .onFailure { if (it is CancellationException) throw it }
    }
```

Flow variant (`runCatching` cannot see errors in later emissions, so use `.catch`):

```kotlin
fun observeExercises(lessonId: String): Flow<Result<List<DbExercise>>> =
    dao.observeByLesson(lessonId)
        .map { Result.success(it) }
        .catch { if (it is CancellationException) throw it else emit(Result.failure(it)) }
        .flowOn(Dispatchers.IO)
```

A Room one-shot (read by id, or a write) uses the suspend form above with the DAO in place of the API, e.g. `runCatching { dao.upsert(exercise) }` returning `Result<Unit>`. For writes, the repository maps domain to `Db*` (`toDb()`) before calling the datasource.

The datasource owns `Dispatchers.IO` (`withContext` for suspend, `.flowOn` for Flow) and returns raw `Api*` / `Db*` models. Any `runCatching`, `catch (e: Exception)` or Flow `.catch` must rethrow `CancellationException` first.

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

Flow variant: map each emission's `Result`, so the mapping stays in the repository.

```kotlin
override fun observeExercises(lessonId: String): Flow<Result<List<ExerciseDomainModel>>> =
    exerciseLocalDataSource.observeExercises(lessonId)
        .map { result -> result.map { entities -> entities.map { it.toDomain() } } }
```

Repositories use `.map`, never `runCatching` or `mapCatching` (`mapCatching` swallows cancellation). To turn a failure into a domain error, use `fold`.

### 7. Use case (`domain/usecase/<Verb><Noun>UseCase.kt`)

One public method, verb name; `Get` never persists. `suspend operator fun invoke(...): Result<T>` for one-shot calls, `operator fun invoke(...): Flow<Result<T>>` for streams. The ViewModel maps domain to `Ui*`; nothing here does.

### 8. Tests

- Use case and repository tests with MockK
- Flow tests with Turbine: assert a success emission, a `Result.failure` emission (the Flow completes without throwing), and that cancellation is not turned into a `Result.failure`
- A serialization test that pins the JSON for requests, asserting the exact JSON string with the same converter configuration the app uses (the `data/remote/dto/` test folder)

### 9. Prove the contract against the API

Unit tests mock the network, so they cannot show that the keys match. Call the real endpoint once (staging or a local server) with the real Retrofit `Service`, e.g. from a JVM test gated on an environment variable so it skips when unset, and check the result matches the specification. Report this as done or blocked.

### 10. Report

Files changed per layer, any audit finding (a swallowed `CancellationException`, a `Result<T>` function that also throws), and whether step 9 was run.

## Hard constraints

- `CancellationException` is never swallowed
- Datasources catch; repositories and use cases propagate `Result<T>` (`Flow<Result<T>>` for streams); ViewModels use `.onSuccess`/`.onFailure`
- A Flow never throws to its collector: failures are emitted as `Result.failure`
- `Flow` only for real streams; one-shot calls stay `suspend`
- Repositories never call the Retrofit interface or a DAO directly
- A failing repository method returns `Result<T>` (`Result<Unit>` for void), never `T?`, and never both returns and throws
