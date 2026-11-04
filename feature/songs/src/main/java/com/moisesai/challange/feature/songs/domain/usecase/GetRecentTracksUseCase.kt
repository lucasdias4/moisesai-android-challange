package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.domain.repository.RecentTracksRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

// The home screen shows a short row of cards, not the whole history.
private const val RECENT_TRACKS_LIMIT = 10

internal class GetRecentTracksUseCase @Inject constructor(private val recentTracksRepository: RecentTracksRepository) {

    operator fun invoke(): Flow<Result<List<TrackDomainModel>>> = recentTracksRepository.observeRecentTracks(RECENT_TRACKS_LIMIT)
}
