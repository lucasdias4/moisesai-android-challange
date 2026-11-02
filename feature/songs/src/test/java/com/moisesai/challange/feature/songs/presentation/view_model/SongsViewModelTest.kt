package com.moisesai.challange.feature.songs.presentation.view_model

import com.moisesai.challange.core.common.AppError
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel
import com.moisesai.challange.feature.songs.domain.usecase.GetRecentTracksUseCase
import com.moisesai.challange.feature.songs.domain.usecase.GetSuggestedTracksUseCase
import com.moisesai.challange.feature.songs.domain.usecase.RefreshTracksUseCase
import com.moisesai.challange.feature.songs.domain.usecase.SearchTracksUseCase
import com.moisesai.challange.feature.songs.fixture.aTrackDomainModel
import com.moisesai.challange.feature.songs.presentation.mapper.toUiModel
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SongsViewModelTest {

    private val dispatcher: TestDispatcher = StandardTestDispatcher()
    private val searchTracks = mockk<SearchTracksUseCase>()
    private val refreshTracks = mockk<RefreshTracksUseCase>()
    private val getRecentTracks = mockk<GetRecentTracksUseCase>()
    private val getSuggestedTracks = mockk<GetSuggestedTracksUseCase>()

    private val levitating = aTrackDomainModel(id = 1)
    private val otherTrack = aTrackDomainModel(id = 2)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { getRecentTracks() } returns flowOf(Result.success(emptyList()))
        coEvery { getSuggestedTracks() } returns Result.success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN the user types a search text, WHEN 399 milliseconds pass, THEN no search is made`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))

        // When
        viewModel.onQueryChange("levitating")
        advanceTimeBy(399)
        runCurrent()

        // Then
        coVerify(exactly = 0) { searchTracks(any(), any()) }
    }

    @Test
    fun `GIVEN the user types a search text, WHEN 400 milliseconds pass, THEN its results are shown`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))

        // When
        viewModel.onQueryChange("levitating")
        advanceTimeBy(DEBOUNCE_MILLIS)
        runCurrent()

        // Then
        assertEquals(resultsOf(levitating, status = UiSearchResultsState.Complete), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN the user types, WHEN the text changes, THEN the state holds what is typed at once`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))

        // When
        viewModel.onQueryChange("levit")

        // Then
        assertEquals("levit", viewModel.uiState.value.query)
    }

    @Test
    fun `GIVEN a slow search, WHEN it has started, THEN the screen is loading with no results`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(levitating), hasNext = false))
        }

        // When
        viewModel.onQueryChange("levitating")
        advanceTimeBy(DEBOUNCE_MILLIS)
        runCurrent()

        // Then
        assertEquals(UiSongsContentState.SearchLoading, viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN results are shown, WHEN a new search text settles, THEN the old results are cleared while the new ones load`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        searchFor(viewModel, "levitating")
        coEvery { searchTracks("daft punk", 0) } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        }

        // When
        searchFor(viewModel, "daft punk")

        // Then
        assertEquals(UiSongsContentState.SearchLoading, viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN a search is running, WHEN a newer search text settles, THEN only the newer results are shown`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("a", 0) } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(levitating), hasNext = false))
        }
        coEvery { searchTracks("ab", 0) } returns Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        searchFor(viewModel, "a")

        // When
        viewModel.onQueryChange("ab")
        advanceTimeBy(DEBOUNCE_MILLIS + SLOW_SEARCH_MILLIS)
        runCurrent()

        // Then
        assertEquals(resultsOf(otherTrack, status = UiSearchResultsState.Complete), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN results are shown, WHEN the search field is cleared, THEN the screen goes back to the home at once`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        searchFor(viewModel, "levitating")

        // When
        viewModel.onQueryChange("")
        runCurrent()

        // Then
        assertEquals(UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN the user types, WHEN the search is submitted from the keyboard, THEN it starts without waiting`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        viewModel.onQueryChange("daft punk")

        // When
        viewModel.onSearchSubmit()
        runCurrent()

        // Then
        coVerify { searchTracks("daft punk", 0) }
    }

    @Test
    fun `GIVEN a search found no song, WHEN it ends, THEN the screen says nothing was found for the searched text`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(emptyList(), hasNext = false))

        // When
        searchFor(viewModel, "zzzz")

        // Then
        assertEquals(UiSongsContentState.SearchNoResults("zzzz"), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN more pages exist, WHEN the first page arrives, THEN the results can load more`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = true))

        // When
        searchFor(viewModel, "levitating")

        // Then
        assertEquals(resultsOf(levitating, status = UiSearchResultsState.HasMore), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN more pages exist, WHEN loading more, THEN the next page is added to the results`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } returns Result.success(TrackPageDomainModel(listOf(levitating), hasNext = true))
        coEvery { searchTracks("levitating", 1) } returns Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        searchFor(viewModel, "levitating")

        // When
        viewModel.loadMore()
        runCurrent()

        // Then
        assertEquals(
            resultsOf(levitating, otherTrack, status = UiSearchResultsState.Complete),
            viewModel.uiState.value.content,
        )
    }

    @Test
    fun `GIVEN the next page is slow, WHEN loading more, THEN the results stay and show that more is loading`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } returns Result.success(TrackPageDomainModel(listOf(levitating), hasNext = true))
        coEvery { searchTracks("levitating", 1) } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        }
        searchFor(viewModel, "levitating")

        // When
        viewModel.loadMore()
        runCurrent()

        // Then
        assertEquals(resultsOf(levitating, status = UiSearchResultsState.LoadingMore), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN the next page is loading, WHEN loading more again, THEN the page is requested once`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } returns Result.success(TrackPageDomainModel(listOf(levitating), hasNext = true))
        coEvery { searchTracks("levitating", 1) } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        }
        searchFor(viewModel, "levitating")
        viewModel.loadMore()
        runCurrent()

        // When
        viewModel.loadMore()
        runCurrent()

        // Then
        coVerify(exactly = 1) { searchTracks("levitating", 1) }
    }

    @Test
    fun `GIVEN there is no next page, WHEN loading more, THEN nothing is requested`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        searchFor(viewModel, "levitating")

        // When
        viewModel.loadMore()
        runCurrent()

        // Then
        coVerify(exactly = 0) { searchTracks("levitating", 1) }
    }

    @Test
    fun `GIVEN the screen shows the home, WHEN loading more, THEN nothing is requested`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.loadMore()
        runCurrent()

        // Then
        coVerify(exactly = 0) { searchTracks(any(), any()) }
    }

    @Test
    fun `GIVEN loading the next page fails, WHEN loading more again, THEN the results stay and nothing more is requested`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } returns Result.success(TrackPageDomainModel(listOf(levitating), hasNext = true))
        coEvery { searchTracks("levitating", 1) } returns Result.failure(AppError.Unknown())
        searchFor(viewModel, "levitating")
        viewModel.loadMore()
        runCurrent()

        // When
        viewModel.loadMore()
        runCurrent()

        // Then
        assertEquals(
            resultsOf(levitating, status = UiSearchResultsState.Failed(UiSongsError.Unknown)),
            viewModel.uiState.value.content,
        )
        coVerify(exactly = 1) { searchTracks("levitating", 1) }
    }

    @Test
    fun `GIVEN a search fails because of the network, WHEN it ends, THEN the network error is shown with no results`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks(any(), any()) } returns Result.failure(AppError.Network())

        // When
        searchFor(viewModel, "levitating")

        // Then
        assertEquals(UiSongsContentState.SearchError(UiSongsError.Network), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN a failed search, WHEN retrying, THEN the search runs again and its results are shown`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } returnsMany listOf(
            Result.failure(AppError.Network()),
            Result.success(TrackPageDomainModel(listOf(levitating), hasNext = false)),
        )
        searchFor(viewModel, "levitating")

        // When
        viewModel.retry()
        runCurrent()

        // Then
        assertEquals(resultsOf(levitating, status = UiSearchResultsState.Complete), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN results are shown, WHEN refreshing, THEN the refreshed results replace them`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        coEvery { refreshTracks("levitating") } returns Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        searchFor(viewModel, "levitating")

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        assertEquals(resultsOf(otherTrack, status = UiSearchResultsState.Complete), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN results are shown and the refresh is slow, WHEN refreshing, THEN the results stay and show the refresh`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        coEvery { refreshTracks("levitating") } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        }
        searchFor(viewModel, "levitating")

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        assertEquals(resultsOf(levitating, status = UiSearchResultsState.Refreshing), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN a refresh is running, WHEN refreshing again, THEN the refresh is requested once`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        coEvery { refreshTracks("levitating") } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        }
        searchFor(viewModel, "levitating")
        viewModel.refresh()
        runCurrent()

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        coVerify(exactly = 1) { refreshTracks("levitating") }
    }

    @Test
    fun `GIVEN a refresh is running, WHEN loading more, THEN nothing is requested`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = true))
        coEvery { refreshTracks("levitating") } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = true))
        }
        searchFor(viewModel, "levitating")
        viewModel.refresh()
        runCurrent()

        // When
        viewModel.loadMore()
        runCurrent()

        // Then
        coVerify(exactly = 0) { searchTracks("levitating", 1) }
    }

    @Test
    fun `GIVEN results are shown and the network fails, WHEN refreshing, THEN the results stay and the error is shown`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        coEvery { refreshTracks("levitating") } returns Result.failure(AppError.Network())
        searchFor(viewModel, "levitating")

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        assertEquals(
            resultsOf(levitating, status = UiSearchResultsState.Failed(UiSongsError.Network)),
            viewModel.uiState.value.content,
        )
    }

    @Test
    fun `GIVEN a failed update shows an error over the results, WHEN refreshing, THEN the refreshed results replace them`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        coEvery { refreshTracks("levitating") } returnsMany listOf(
            Result.failure(AppError.Network()),
            Result.success(TrackPageDomainModel(listOf(otherTrack), hasNext = false)),
        )
        searchFor(viewModel, "levitating")
        viewModel.refresh()
        runCurrent()

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        assertEquals(resultsOf(otherTrack, status = UiSearchResultsState.Complete), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN a refresh finds no song, WHEN it ends, THEN the screen says nothing was found`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        coEvery { refreshTracks("levitating") } returns Result.success(TrackPageDomainModel(emptyList(), hasNext = false))
        searchFor(viewModel, "levitating")

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        assertEquals(UiSongsContentState.SearchNoResults("levitating"), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN a search failed with nothing to show, WHEN refreshing, THEN the search runs again`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } returnsMany listOf(
            Result.failure(AppError.Network()),
            Result.success(TrackPageDomainModel(listOf(levitating), hasNext = false)),
        )
        searchFor(viewModel, "levitating")

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        assertEquals(resultsOf(levitating, status = UiSearchResultsState.Complete), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN a search found no song, WHEN refreshing, THEN the search runs again`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } returnsMany listOf(
            Result.success(TrackPageDomainModel(emptyList(), hasNext = false)),
            Result.success(TrackPageDomainModel(listOf(levitating), hasNext = false)),
        )
        searchFor(viewModel, "levitating")

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        assertEquals(resultsOf(levitating, status = UiSearchResultsState.Complete), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN a search is loading, WHEN refreshing, THEN nothing is requested`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()
        coEvery { searchTracks("levitating", 0) } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(TrackPageDomainModel(listOf(levitating), hasNext = false))
        }
        searchFor(viewModel, "levitating")

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        coVerify(exactly = 0) { refreshTracks(any()) }
        coVerify(exactly = 1) { searchTracks("levitating", 0) }
    }

    @Test
    fun `GIVEN the screen shows the history, WHEN refreshing, THEN nothing is requested`() = runTest(dispatcher) {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.refresh()
        runCurrent()

        // Then
        coVerify(exactly = 0) { refreshTracks(any()) }
        coVerify(exactly = 0) { searchTracks(any(), any()) }
    }

    @Test
    fun `GIVEN recent tracks exist, WHEN the screen starts, THEN the history is shown with them`() = runTest(dispatcher) {
        // Given
        every { getRecentTracks() } returns flowOf(Result.success(listOf(levitating)))

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(UiSongsContentState.History(listOf(levitating.toUiModel())), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN reading recent tracks fails, WHEN the screen starts, THEN the first launch is shown and no error`() = runTest(dispatcher) {
        // Given
        every { getRecentTracks() } returns flowOf(Result.failure(AppError.Unknown()))

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN no recent tracks, WHEN the screen starts, THEN the first launch is shown`() = runTest(dispatcher) {
        // Given / When
        val viewModel = createViewModel()

        // Then
        assertTrue(viewModel.uiState.value.content is UiSongsContentState.FirstLaunch)
    }

    @Test
    fun `GIVEN the history is shown, WHEN a song is played, THEN the history shows it`() = runTest(dispatcher) {
        // Given
        val recentTracks = MutableStateFlow(Result.success(listOf(levitating)))
        every { getRecentTracks() } returns recentTracks
        val viewModel = createViewModel()

        // When
        recentTracks.value = Result.success(listOf(otherTrack, levitating))
        runCurrent()

        // Then
        assertEquals(
            UiSongsContentState.History(listOf(otherTrack.toUiModel(), levitating.toUiModel())),
            viewModel.uiState.value.content,
        )
    }

    @Test
    fun `GIVEN results are shown, WHEN a song is played, THEN the results stay as they are`() = runTest(dispatcher) {
        // Given
        val recentTracks = MutableStateFlow(Result.success(emptyList<TrackDomainModel>()))
        every { getRecentTracks() } returns recentTracks
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        val viewModel = createViewModel()
        searchFor(viewModel, "levitating")

        // When
        recentTracks.value = Result.success(listOf(otherTrack))
        runCurrent()

        // Then
        assertEquals(resultsOf(levitating, status = UiSearchResultsState.Complete), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN a song was played during a search, WHEN the search field is cleared, THEN the history shows it`() = runTest(dispatcher) {
        // Given
        val recentTracks = MutableStateFlow(Result.success(emptyList<TrackDomainModel>()))
        every { getRecentTracks() } returns recentTracks
        givenSearchReturns(TrackPageDomainModel(listOf(levitating), hasNext = false))
        val viewModel = createViewModel()
        searchFor(viewModel, "levitating")
        recentTracks.value = Result.success(listOf(otherTrack))
        runCurrent()

        // When
        viewModel.onQueryChange("")
        runCurrent()

        // Then
        assertEquals(UiSongsContentState.History(listOf(otherTrack.toUiModel())), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN suggested tracks exist, WHEN the screen starts, THEN the first launch shows them`() = runTest(dispatcher) {
        // Given
        coEvery { getSuggestedTracks() } returns Result.success(listOf(levitating))

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(
            UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(listOf(levitating.toUiModel()))),
            viewModel.uiState.value.content,
        )
    }

    @Test
    fun `GIVEN looking up the suggested tracks fails, WHEN the screen starts, THEN the suggestions are unavailable`() = runTest(dispatcher) {
        // Given
        coEvery { getSuggestedTracks() } returns Result.failure(AppError.Network())

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN no suggested track could be found, WHEN the screen starts, THEN the suggestions are unavailable`() = runTest(dispatcher) {
        // Given
        coEvery { getSuggestedTracks() } returns Result.success(emptyList())

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(UiSongsContentState.FirstLaunch(UiSuggestionsState.Unavailable), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN the lookup has not finished, WHEN the screen starts, THEN the suggestions are loading`() = runTest(dispatcher) {
        // Given
        coEvery { getSuggestedTracks() } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(listOf(levitating))
        }

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(UiSongsContentState.FirstLaunch(UiSuggestionsState.Loading), viewModel.uiState.value.content)
    }

    @Test
    fun `GIVEN the suggestions arrive during a search, WHEN the search field is cleared, THEN the first launch shows them`() = runTest(dispatcher) {
        // Given
        coEvery { getSuggestedTracks() } coAnswers {
            delay(SLOW_SEARCH_MILLIS)
            Result.success(listOf(levitating))
        }
        givenSearchReturns(TrackPageDomainModel(listOf(otherTrack), hasNext = false))
        val viewModel = createViewModel()
        searchFor(viewModel, "daft punk")
        advanceTimeBy(SLOW_SEARCH_MILLIS)
        runCurrent()

        // When
        viewModel.onQueryChange("")
        runCurrent()

        // Then
        assertEquals(
            UiSongsContentState.FirstLaunch(UiSuggestionsState.Loaded(listOf(levitating.toUiModel()))),
            viewModel.uiState.value.content,
        )
    }

    private fun TestScope.createViewModel() =
        SongsViewModel(searchTracks, refreshTracks, getRecentTracks, getSuggestedTracks).also { runCurrent() }

    private fun TestScope.searchFor(viewModel: SongsViewModel, searchText: String) {
        viewModel.onQueryChange(searchText)
        advanceTimeBy(DEBOUNCE_MILLIS)
        runCurrent()
    }

    private fun givenSearchReturns(page: TrackPageDomainModel) {
        coEvery { searchTracks(any(), any()) } returns Result.success(page)
    }

    private fun resultsOf(vararg tracks: TrackDomainModel, status: UiSearchResultsState) =
        UiSongsContentState.SearchResults(tracks.map(TrackDomainModel::toUiModel), status)

    private companion object {
        const val DEBOUNCE_MILLIS = 400L
        const val SLOW_SEARCH_MILLIS = 1_000L
    }
}
