package com.moisesai.challange.feature.songdetails.domain.usecase

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songdetails.domain.repository.TrackRepository
import javax.inject.Inject

internal class GetTrackUseCase @Inject constructor(
    private val trackRepository: TrackRepository,
) {

    /** The track the user chose, from the saved ones when it is there. It does not wait for its album, so the player can show it at once. */
    suspend operator fun invoke(trackId: Long): Result<TrackDomainModel> = trackRepository.getTrack(trackId)
}
