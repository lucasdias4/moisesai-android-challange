package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel
import com.moisesai.challange.feature.songs.domain.repository.TrackSearchRepository
import javax.inject.Inject

internal class SearchTracksUseCase @Inject constructor(
    private val trackSearchRepository: TrackSearchRepository,
) {

    suspend operator fun invoke(searchText: String, page: Int): Result<TrackPageDomainModel> =
        // Nothing to look for: answering here saves a request that could only return noise.
        if (searchText.isBlank()) Result.success(NoTracksPage)
        else trackSearchRepository.searchTracksBySearchText(searchText, page, SEARCH_PAGE_SIZE)
}

internal val NoTracksPage = TrackPageDomainModel(tracks = emptyList(), hasNext = false)
