package com.moisesai.challange.feature.songdetails.domain.usecase

import com.moisesai.challange.feature.songdetails.domain.model.TrackQueueDomainModel
import com.moisesai.challange.feature.songdetails.domain.repository.TrackRepository
import javax.inject.Inject

internal class GetTrackQueueUseCase @Inject constructor(
    private val trackRepository: TrackRepository,
) {

    /**
     * The chosen track with the tracks of its album around it, so the player can go to the previous and the next one.
     * The album is a bonus: when it cannot be loaded, the chosen track still plays alone.
     */
    suspend operator fun invoke(trackId: Long): Result<TrackQueueDomainModel> =
        trackRepository.getTrack(trackId).map { chosenTrack ->
            val albumTracks = trackRepository.getAlbumTracks(chosenTrack.albumId).getOrDefault(emptyList())
            TrackQueueDomainModel.startingAt(chosenTrack, albumTracks)
        }
}
