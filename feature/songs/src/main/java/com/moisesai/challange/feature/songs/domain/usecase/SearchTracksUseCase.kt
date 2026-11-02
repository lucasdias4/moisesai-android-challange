package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.feature.songs.domain.model.Constants
import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel
import com.moisesai.challange.feature.songs.domain.repository.TrackSearchRepository
import javax.inject.Inject

internal class SearchTracksUseCase @Inject constructor(
    private val trackSearchRepository: TrackSearchRepository,
) {

    suspend operator fun invoke(searchText: String, page: Int): Result<TrackPageDomainModel> =
        trackSearchRepository.searchTracksBySearchText(searchText, page, Constants.SEARCH_PAGE_SIZE)
}
