package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.domain.repository.SuggestedTracksRepository
import javax.inject.Inject

// The first songs offered to someone with no history. They are picked by hand: well known songs whose covers look good.
// iTunes ids of: As (Stevie Wonder), ocean eyes (Billie Eilish) and Flower In A Hurricane (Justice).
private val SUGGESTED_TRACK_IDS = listOf(1440788744L, 1440899467L, 6827658231L)

internal class GetSuggestedTracksUseCase @Inject constructor(
    private val suggestedTracksRepository: SuggestedTracksRepository,
) {

    suspend operator fun invoke(): Result<List<TrackDomainModel>> =
        suggestedTracksRepository.getSuggestedTracks(SUGGESTED_TRACK_IDS)
}
