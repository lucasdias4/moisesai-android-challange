package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.feature.songs.domain.model.TrackPageDomainModel
import com.moisesai.challange.feature.songs.domain.repository.TrackSearchRepository
import javax.inject.Inject

internal class RefreshTracksUseCase @Inject constructor(
    private val trackSearchRepository: TrackSearchRepository,
) {

    suspend operator fun invoke(searchText: String): Result<TrackPageDomainModel> =
        if (searchText.isBlank()) Result.success(NoTracksPage)
        else trackSearchRepository.refreshTracksBySearchText(searchText, SEARCH_PAGE_SIZE)
}
