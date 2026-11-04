package com.moisesai.challange.feature.songdetails.domain.usecase

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songdetails.domain.repository.RecentTracksRepository
import javax.inject.Inject

internal class SaveRecentTrackUseCase @Inject constructor(private val recentTracksRepository: RecentTracksRepository) {

    /** Remembers that [track] was played just now, so the songs screen can list it. */
    suspend operator fun invoke(track: TrackDomainModel): Result<Unit> = recentTracksRepository.saveRecentTrack(track)
}
