package com.moisesai.challange.feature.songs.domain.usecase

import com.moisesai.challange.core.shareddomainmodel.TrackDomainModel
import com.moisesai.challange.feature.songs.domain.repository.SuggestedTracksRepository
import javax.inject.Inject

// The first songs offered to someone with no history. They are picked by hand: well known songs whose covers look good.
private const val SUGGESTED_TRACK_ID_1 = 1440788744L // As (Stevie Wonder)
private const val SUGGESTED_TRACK_ID_2 = 1440899467L // ocean eyes (Billie Eilish)
private const val SUGGESTED_TRACK_ID_3 = 6827658231L // Flower In A Hurricane (Justice)

private val SUGGESTED_TRACK_IDS = listOf(SUGGESTED_TRACK_ID_1, SUGGESTED_TRACK_ID_2, SUGGESTED_TRACK_ID_3)

internal class GetSuggestedTracksUseCase @Inject constructor(private val suggestedTracksRepository: SuggestedTracksRepository) {

    suspend operator fun invoke(): Result<List<TrackDomainModel>> = suggestedTracksRepository.getSuggestedTracks(SUGGESTED_TRACK_IDS)
}
