---
name: write-ui-tests
description: Write functional tests (one Compose screen or component on the emulator, fake data, no Hilt) and end-to-end tests (the whole app, fake backend and fake player) in this project - underscore names, ids from :core:strings, waitUntil never sleep, recorder of callbacks. Use when adding or changing a test of a screen, a component, an animation, the player engine, the Room migration or a user journey.
---

Functional and end-to-end tests are the two upper layers of the pyramid in `ARCHITECTURE.md` §9. Targets: 50% of the functional matrix, 30% of the journeys; write the test in the same commit as the code. Unit tests are the `write-unit-tests` skill.

## Which layer

| It checks | Layer | Where |
|---|---|---|
| One screen state or one interaction, with fake data | Functional | `src/androidTest` of the module of the screen or component |
| The player engine, the Room migration | Functional (no UI) | `:core:player`, `:core:database` |
| A journey across screens with the real ViewModels and navigation | End to end | `:app` `src/androidTest` |

## Functional test of a screen or a component

Screens are stateless, so no Hilt: give one `UiState` and a recorder of callbacks.

```kotlin
@get:Rule val composeTestRule = createComposeRule()

@Test
fun GIVEN_a_search_error_WHEN_the_user_taps_try_again_THEN_the_retry_is_asked_for() {
    // Given
    val callbacks = RecordingSongsCallbacks()
    composeTestRule.setContent { MoisesaiChallangeTheme { SongsScreen(uiState = errorState, callbacks = callbacks.asSongsCallbacks()) } }

    // When
    composeTestRule.onNodeWithText(context.getString(R.string.songs_retry)).performClick()

    // Then
    assertEquals(1, callbacks.retryCount)
}
```

- Names use underscores (`GIVEN_..._WHEN_..._THEN_...`), the keywords uppercase: DEX before version 40 forbids spaces.
- Find nodes by the text or description of `:core:strings` (`context.getString(R.string...)`), never a hard-coded string.
- Wait with `composeTestRule.waitUntil { ... }`, `waitForIdle` or the test clock (`mainClock`), never `Thread.sleep`.
- A test of a state asserts what the user sees (text, a node that exists or not, enabled), and a test of an interaction asserts the callback that was called. Never assert pixels.
- Sample data comes from the module's `preview_helper` (reachable from `androidTest`); fixtures from the real API when the data comes from the API.
- A test of an animation drives the clock: `mainClock.autoAdvance = false`, `advanceTimeBy(...)`, and asserts the state before and after, not the frames.

## End-to-end test of a journey

- `@HiltAndroidTest` with the Hilt test runner of `:app`. `ItunesBaseUrlModule` is replaced (`@TestInstallIn`) so the app calls `FakeBackend`, a MockWebServer that answers `/search` and `/lookup` with the real-API JSON of `core/network/src/test/resources/fixtures` (shared as androidTest assets); `PlayerModule` is replaced by `FakePlayerController`. The backend is started and the database deleted by a rule that runs before the activity opens. The suggested songs are looked up by ids the fixtures do not hold, so the backend names them "Suggested song <id>".
- One test is one journey, in the words of the user: "search, open a track, it plays, go back, it is in the history". Each step asserts what is on screen.
- The fake backend can fail on demand (`isOffline` answers 503, which the app shows as a network error; a search for `zzzz` finds nothing) to cover the error journeys.
- The app shows a title without its "(Live)" suffix: wait for text that does not depend on it.

## Rules

- A test that passes with the code broken is a bug: break the code on purpose, see the test fail, restore it.
- No `sleep`, no retries, no order between tests. Run the suite three times before reporting.
- A functional suite runs in under 4 minutes and the end-to-end suite in under 4 minutes.

## Checklist before reporting

- The test is written in the same commit as the code.
- The name has the three keywords and underscores; one action per test.
- No hard-coded text, no `sleep`, no pixel assertion.
- `./gradlew connectedDebugAndroidTest` is green three times in a row.
