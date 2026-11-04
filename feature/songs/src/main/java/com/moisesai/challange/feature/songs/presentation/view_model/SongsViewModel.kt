package com.moisesai.challange.feature.songs.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel
import com.moisesai.challange.feature.songs.domain.usecase.GetRecentTracksUseCase
import com.moisesai.challange.feature.songs.domain.usecase.GetSuggestedTracksUseCase
import com.moisesai.challange.feature.songs.domain.usecase.RefreshTracksUseCase
import com.moisesai.challange.feature.songs.domain.usecase.SearchTracksUseCase
import com.moisesai.challange.feature.songs.presentation.mapper.toUiError
import com.moisesai.challange.feature.songs.presentation.mapper.toUiModel
import com.moisesai.challange.feature.songs.presentation.model.UiSearchResultsState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsContentState
import com.moisesai.challange.feature.songs.presentation.model.UiSongsError
import com.moisesai.challange.feature.songs.presentation.model.UiSongsUiState
import com.moisesai.challange.feature.songs.presentation.model.UiSuggestionsState
import com.moisesai.challange.feature.songs.presentation.model.UiTrackOptionsState
import com.moisesai.challange.feature.songs.presentation.model.UiTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// iTunes allows about 20 requests a minute: waiting for a pause in typing keeps one request per word, not one per letter.
private const val SEARCH_DEBOUNCE_MILLIS = 400L
private const val FIRST_PAGE = 0

@HiltViewModel
internal class SongsViewModel @Inject constructor(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val refreshTracksUseCase: RefreshTracksUseCase,
    private val getRecentTracksUseCase: GetRecentTracksUseCase,
    private val getSuggestedTracksUseCase: GetSuggestedTracksUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiSongsUiState(
            query = "",
            isSearchFieldVisible = true,
            content = UiSongsContentState.FirstLaunch(UiSuggestionsState.Loading),
            trackOptions = UiTrackOptionsState.Hidden,
        ),
    )
    val uiState: StateFlow<UiSongsUiState> = _uiState.asStateFlow()

    // Typing is slow to settle, so it waits; a submitted search (keyboard action, a suggestion) starts at once.
    private val typedSearchText = MutableStateFlow("")
    private val submittedSearchText = MutableSharedFlow<String>(extraBufferCapacity = 1)

    private var activeSearchText = ""
    private var nextPage = FIRST_PAGE

    // The home needs these two even while a search is shown, so that going back to it finds them up to date.
    private var recentTracks = emptyList<UiTrack>()
    private var suggestions: UiSuggestionsState = UiSuggestionsState.Loading

    // The one request for the list in flight (a search, the next page or a refresh). Starting another cancels it.
    private var contentJob: Job? = null

    init {
        observeRecentTracks()
        loadSuggestedTracks()
        observeSearchText()
    }

    // The text changes only through the field, or when going back clears it, so the field is on screen either way.
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, isSearchFieldVisible = true) }
        typedSearchText.value = query
    }

    // A blank text asks for nothing, so the field stays; otherwise it makes room for the results.
    fun onSearchSubmit() {
        val query = _uiState.value.query
        submittedSearchText.tryEmit(query)
        _uiState.update { it.copy(isSearchFieldVisible = query.isBlank()) }
    }

    fun onSearchIconClick() {
        _uiState.update { it.copy(isSearchFieldVisible = true) }
    }

    fun onTrackOptionsClick(track: UiTrack) {
        _uiState.update { it.copy(trackOptions = UiTrackOptionsState.Visible(track)) }
    }

    fun onTrackOptionsDismiss() {
        _uiState.update { it.copy(trackOptions = UiTrackOptionsState.Hidden) }
    }

    fun loadMore() {
        val results = _uiState.value.content as? UiSongsContentState.SearchResults ?: return
        // Any status but HasMore is busy, failed or at the last page, so a failing page is not requested on every scroll.
        if (results.status != UiSearchResultsState.HasMore) return
        updateResults { it.copy(status = UiSearchResultsState.LoadingMore) }
        contentJob = viewModelScope.launch {
            searchTracksUseCase(activeSearchText, nextPage).fold(onSuccess = ::appendPage, onFailure = ::showError)
        }
    }

    fun refresh() {
        when (val content = _uiState.value.content) {
            is UiSongsContentState.SearchResults -> refreshResults(content)
            is UiSongsContentState.SearchError, is UiSongsContentState.SearchNoResults -> retry()
            is UiSongsContentState.SearchLoading, is UiSongsContentState.Home -> Unit
        }
    }

    fun retry() {
        startSearch(activeSearchText)
    }

    private fun refreshResults(results: UiSongsContentState.SearchResults) {
        if (results.status == UiSearchResultsState.Refreshing) return
        contentJob?.cancel()
        updateResults { it.copy(status = UiSearchResultsState.Refreshing) }
        contentJob = viewModelScope.launch {
            refreshTracksUseCase(activeSearchText).fold(onSuccess = ::showFirstPage, onFailure = ::showError)
        }
    }

    private fun loadSuggestedTracks() {
        viewModelScope.launch {
            getSuggestedTracksUseCase().fold(
                onSuccess = { tracks -> showSuggestions(tracks) },
                onFailure = { showSuggestions(emptyList()) },
            )
        }
    }

    private fun showSuggestions(tracks: List<TrackDomainModel>) {
        suggestions = if (tracks.isEmpty()) UiSuggestionsState.Unavailable else UiSuggestionsState.Loaded(tracks.toUiModels())
        refreshHome()
    }

    private fun observeRecentTracks() {
        getRecentTracksUseCase()
            .onEach { result -> result.onSuccess(::showRecentTracks) }
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchText() {
        // Clearing the field goes back to the history at once; only a non-blank text needs to settle.
        val settledTypedText = typedSearchText.debounce { text -> if (text.isBlank()) 0L else SEARCH_DEBOUNCE_MILLIS }
        merge(settledTypedText, submittedSearchText)
            .map(String::trim)
            .distinctUntilChanged()
            .onEach(::startSearch)
            .launchIn(viewModelScope)
    }

    private fun startSearch(searchText: String) {
        contentJob?.cancel()
        activeSearchText = searchText
        nextPage = FIRST_PAGE
        if (searchText.isBlank()) {
            showContent(homeContent())
            return
        }
        showContent(UiSongsContentState.SearchLoading)
        contentJob = viewModelScope.launch {
            searchTracksUseCase(searchText, FIRST_PAGE).fold(onSuccess = ::showFirstPage, onFailure = ::showError)
        }
    }

    private fun showFirstPage(page: TrackPageDomainModel) {
        nextPage = FIRST_PAGE + 1
        val tracks = page.tracks.toUiModels()
        showContent(
            if (tracks.isEmpty()) {
                UiSongsContentState.SearchNoResults(activeSearchText)
            } else {
                UiSongsContentState.SearchResults(tracks, page.toResultsStatus())
            },
        )
    }

    private fun appendPage(page: TrackPageDomainModel) {
        nextPage++
        updateResults { it.copy(tracks = it.tracks + page.tracks.toUiModels(), status = page.toResultsStatus()) }
    }

    private fun showError(error: Throwable) {
        val uiError = error.toUiError()
        _uiState.update { state -> state.copy(content = state.content.afterFailure(uiError)) }
    }

    private fun showRecentTracks(tracks: List<TrackDomainModel>) {
        recentTracks = tracks.toUiModels()
        refreshHome()
    }

    private fun homeContent(): UiSongsContentState.Home =
        if (recentTracks.isEmpty()) {
            UiSongsContentState.FirstLaunch(suggestions)
        } else {
            UiSongsContentState.History(recentTracks)
        }

    // While a search is on screen the home is not drawn: its data is only remembered, for when the search ends.
    private fun refreshHome() {
        _uiState.update { state -> if (state.content is UiSongsContentState.Home) state.copy(content = homeContent()) else state }
    }

    private fun showContent(content: UiSongsContentState) {
        _uiState.update { it.copy(content = content) }
    }

    // A page can arrive after the results it belongs to were replaced; then there is nothing to update.
    private fun updateResults(transform: (UiSongsContentState.SearchResults) -> UiSongsContentState.SearchResults) {
        _uiState.update { state ->
            val results = state.content as? UiSongsContentState.SearchResults ?: return@update state
            state.copy(content = transform(results))
        }
    }

    /** What is already on screen stays: the error is shown next to the results and not instead of them. */
    private fun UiSongsContentState.afterFailure(error: UiSongsError): UiSongsContentState = when (this) {
        is UiSongsContentState.SearchResults -> copy(status = UiSearchResultsState.Failed(error))
        is UiSongsContentState.Search -> UiSongsContentState.SearchError(error)
        is UiSongsContentState.Home -> this
    }

    private fun TrackPageDomainModel.toResultsStatus() = if (hasNext) UiSearchResultsState.HasMore else UiSearchResultsState.Complete

    private fun List<TrackDomainModel>.toUiModels() = map(TrackDomainModel::toUiModel)
}
